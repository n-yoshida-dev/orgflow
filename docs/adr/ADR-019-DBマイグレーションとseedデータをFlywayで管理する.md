# ADR-019: DBマイグレーションと seed データを Flyway で管理する

- Status: Accepted
- Date: 2026-05-03
- 最終追記: 2026-06-04

> **決定**: DB のテーブル作成と seed データ投入は Flyway migration で管理し、`schema.sql` / `data.sql` は使わない。

## 背景

OrgFlow Phase 1 では、`/login` を dummy-token 返却から本物のログイン認証へ近づける前段として、DB 上の `users` テーブルとログイン確認用 seed user が必要になった。

当初は、ローカル PostgreSQL や Docker volume に手動でテーブル・データを残す案も考えられた。
しかし、手動作成や volume 依存では、DB の構造と初期データをリポジトリ上で再現しにくい。

また、Spring Boot の `schema.sql` / `data.sql` を使う案もあったが、Phase 1 では今後も users 以外のテーブルや初期データを段階的に追加していくため、DB 変更履歴を順番付きで管理できる方式が必要だった。

## 比較した案

### 案A: ローカル PostgreSQL や Docker volume に手動でテーブル・データを作る

ローカル環境では動作確認しやすいが、DB の状態が自分の環境に閉じる。
別環境、CI、将来の再構築時に、同じテーブル定義と seed データを再現しにくい。

### 案B: `schema.sql` / `data.sql` を使う

Spring Boot の標準的な初期化方法として分かりやすい。
ただし、DB 変更を V1、V2、V3 のような履歴として管理する用途には弱い。

### 案C: Flyway migration で管理する

`db/migration` 配下に versioned migration を置き、DB 変更を順番に適用する。
テーブル作成、seed データ投入、将来のカラム追加や制約追加を、リポジトリ上の SQL ファイルとして追跡できる。

## 採用した案

DB のテーブル作成と seed データ投入は Flyway で管理する。

現時点では次の migration を作成する。

- `V1__create_users.sql`
  - `users` テーブルを作成する
- `V2__seed_users.sql`
  - ログイン確認用 seed user を投入する

`schema.sql` / `data.sql` は使用せず、DB 初期化の正は Flyway migration に寄せる。

## 採用理由

Flyway により、DB の構造変更と seed データ投入をリポジトリ上で管理できる。

Docker volume は、現在の DB データをローカルに永続化するための仕組みであり、DB をどのように作るかという変更手順を説明するものではない。
そのため、volume に残った状態へ依存するのではなく、Flyway migration から DB を再現できる状態を優先する。

また、今後 `tenants`、`internal_organizations`、`requests`、`approvals`、`audit_logs` などを追加していく場合にも、migration を追加して DB を段階的に前へ進められる。

これは、Phase 1 で重視している「README / ADR / SQL / テストを通じて、なぜその設計にしたか説明できる状態」に合う。

## seed password の扱い

`users.hashed_password` には平文 password を保存しない。

ログイン確認用 seed user についても、DB には `{bcrypt}` prefix 付きの BCrypt ハッシュ文字列を保存する。

アプリケーション側では、`PasswordConfig` で `PasswordEncoderFactories.createDelegatingPasswordEncoder()` による `PasswordEncoder` Bean を定義する。
これにより、`{bcrypt}` prefix を含む保存済み password を `PasswordEncoder#matches` で照合できる。

## 採用しなかった案と理由

### 案A: 平文 password を seed に保存する

`hashed_password` というカラム名と実際の保存内容が矛盾する。
また、後続の `PasswordEncoder#matches` を使った実装とつながらないため、不採用とした。

### 案B: `BCryptPasswordEncoder` を直接 Bean 化し、DB には `$2a$...` のみ保存する

最小実装としては成立する。
ただし、今回は `{bcrypt}` prefix を含む形式で seed password を保存し、Spring Security の `DelegatingPasswordEncoder` の考え方も学習対象に含めたい。
そのため、`PasswordEncoderFactories.createDelegatingPasswordEncoder()` を採用した。

### 案C: `schema.sql` / `data.sql` による初期化を使う

小規模な学習では分かりやすい。
しかし、Phase 1 では DB 変更履歴を ADR や README と合わせて説明できる状態を目指すため、Flyway に寄せることにした。

## この判断で得られるもの

- DB の構造変更を SQL ファイルとして追跡できる
- ローカル環境を作り直しても、Flyway から users テーブルと seed user を再現できる
- DB 初期化の責務が `schema.sql` / `data.sql` と Flyway に分散しない
- seed user の password 保存方式と、後続の `PasswordEncoder#matches` 実装がつながる
- 将来のテーブル追加、制約追加、seed データ追加を同じ流れで扱える

## この判断で受け入れる制約

- migration ファイルを一度適用した後は、原則として直接編集せず、新しい migration を追加して前に進める必要がある
- ローカル開発中に migration を作り直したい場合は、Docker volume を削除して DB を再作成する必要がある
- Flyway の適用履歴や checksum の考え方を理解する必要がある
- seed password を更新する場合、平文 password と DB 保存用ハッシュ値を混同しないように管理する必要がある

## 見直す条件

- 本番運用で、アプリ起動時に自動 migration するのではなく、デプロイパイプライン上で明示的に migration したくなった場合
- 外部 IdP や Cognito 等を導入し、アプリ側で password を保持しない方式に移行する場合
- seed データの種類が増え、開発用 seed とテスト用 seed を分ける必要が出た場合
- Phase 2 以降で、環境ごとに migration / seed の適用方針を分ける必要が出た場合

## 追記: tenants / tenant_memberships の追加とFlyway checksum mismatchの扱い（2026-06-04）

### 背景

`POST /tenants/{tenantId}/select` を実装するためには、user と tenant の所属関係をDBで確認できる必要があった。

そのため、Flyway migrationとして `tenants` と `tenant_memberships` を追加した。

また、動作確認用に、正常性確認用tenantと404確認用tenantをseedデータとして追加した。

実装途中でSQL formatterにより既存のV1/V2 migrationファイルの空白や改行が変わり、Flywayのchecksum mismatchが発生した。

### 決定

`V3__create_tenants_and_tenant_memberships.sql` で、以下のテーブルを追加する。

- `tenants`
- `tenant_memberships`

`tenant_memberships` は、`tenant_id` と `user_id` を持ち、`(tenant_id, user_id)` に一意制約を置く。

`V4__seed_tenants_and_tenant_memberships.sql` で、以下の確認用データを追加する。

- 正常性確認用テナント
- 404確認用テナント
- `test_taro` が正常性確認用テナントに所属するmembership

### 採用理由

`selectTenant` で必要なのは、認証済みuserIdと指定tenantIdの組み合わせに対応するmembershipが存在するかどうかである。

そのため、Serviceではtenant一覧を取得して比較するのではなく、Repositoryで `userId` と `tenantId` の組み合わせの存在確認を行う。

DB側で `(tenant_id, user_id)` に一意制約を置くことで、同じuserが同じtenantに重複所属する不整合を防ぐ。

### Flyway checksum mismatch から得たルール

一度DBに適用済みのversioned migrationファイルは、原則として後から編集しない。

SQLの意味が変わっていなくても、空白や改行が変わるだけでFlywayのchecksumが変わり、validateで失敗する。

ローカル開発DBでデータを捨ててよい場合は、`docker compose down -v` によりvolumeを削除し、V1から再適用する。

一方、本番DBや消してはいけない検証DBでは、安易にvolume削除やmigration編集を行わない。

### 受け入れる制約

現時点では、`tenant_memberships` の外部キーに `ON DELETE CASCADE` を付ける。

ただし、これは `selectTenant` 最小実装のための暫定判断である。

将来、`requests`、`approvals`、`audit_logs` を実装する段階で、tenant / user / membership の物理削除を本当に許すか、論理削除に寄せるかを再検討する。

### 見直す条件

監査ログ実装時。

申請・承認データがtenant / user / membershipを参照するようになった時。

tenantやuserの削除APIを実装する時。

本番相当DBでmigrationを運用する段階に入った時。
