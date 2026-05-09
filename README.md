# OrgFlow

OrgFlow は、申請・承認フローを題材にした業務ワークフローアプリケーションです。

経費申請を中心とした **申請 → 承認 → 監査ログ記録** の流れを題材に、
REST API、DB 設計、認証・認可、RBAC、監査ログ、OpenAPI、テスト、CI などを
段階的に実装・整理することを目的としています。

## このリポジトリの目的

このリポジトリの目的は、単に CRUD API を作ることではありません。

申請・承認のような状態遷移を伴う業務フローを、データ構造、API、認証・認可、監査ログと結びつけて整理し、後から設計意図を説明できる状態にすることを目的としています。

主に以下を重視します。

- 業務フローをデータ構造と API に落とし込むこと
- 認証と認可を分けて扱うこと
- ロールごとの操作権限を整理すること
- 監査ログを後付けではなく設計対象として扱うこと
- API 契約を OpenAPI として管理すること
- 実装判断を README / docs / ADR に残すこと

## 現在の状態

現在は Phase 1 の途中で、設計資料と OpenAPI 契約を前提に、Spring Boot API の最小実装へ進んでいる段階です。

現時点では、主に以下を整理・実装しています。

- 業務ルールの文章化
- 設計方針の整理
- ADR の作成
- 概念 ER 図 / 論理 ER 図 / 物理 ER 図の整理
- OpenAPI による API 契約の整理
- Spring Boot API プロジェクトの作成
- Docker Compose による PostgreSQL 起動
- Spring Security の最小設定
- `/login` の DTO / Controller / Service の外形実装
- `@Valid` 失敗時に 422 を返す例外ハンドリング

そのため、この README は「完成済みアプリケーションの利用手順」ではなく、
**現在の到達点とドキュメントの入口**として記載しています。

## 題材として申請・承認フローを扱う理由

OrgFlow では、単純な ToDo ではなく、申請・承認フローを題材にしています。

理由は、以下の論点を 1 つの題材で扱いやすいからです。

- tenant をまたぐデータ分離
- tenant 内の internal organization ごとの差分
- ロールと認可
- 承認フロー
- 状態遷移
- 監査ログ
- 設計判断の記録

## 現時点の主要ドキュメント

### GitHub Pages

- 公開ドキュメント入口
  - `https://n-yoshida-dev.github.io/orgflow/`

### 設計関連

- [Docs 入口](./docs/index.md)
- [設計概要](./docs/design-overview.md)
- [業務ルール](./docs/domain-rules.md)

### ER 関連

- [ER 設計メモ](./docs/er/00_notes.md)
- [概念 ER 図](./docs/er/01_concept-er.md)
- [論理 ER 図（DBML）](./docs/er/02_logical-er.dbml)
- [物理 ER 図（DBML）](./docs/er/03_physical-er.dbml)

### OpenAPI 関連

- [業務操作カタログ](./docs/openapi/00-operation-catalog.md)
- [API グループ整理](./docs/openapi/01-api-groups.md)
- [path 候補整理](./docs/openapi/02-path-candidates.md)
- [OpenAPI 定義](./docs/openapi/openapi.yaml)

### 実装対応表

- [実装対応表](./docs/design/implementation-mapping.tsv)

### ADR

- [ADR 一覧](./docs/adr/index.md)

## 現時点での設計ドキュメントの見方

README には設計詳細を詰め込まず、全体像の入口だけを置いています。
設計の詳細は `docs/` と `ADR` に分けて管理しています。

- 概念の責務分担: `./docs/design-overview.md`
- 固定する業務ルール: `./docs/domain-rules.md`
- ER 図の補足・概念図・論理図・物理図: `./docs/er/`
- API 契約: `./docs/openapi/openapi.yaml`
- 実装対応表: `./docs/design/implementation-mapping.tsv`
- 設計判断の理由: `./docs/adr/`
- ドキュメント全体の入口: `./docs/index.md`

## Phase 1 の到達目標

Phase 1 では、Java / Spring Boot による OrgFlow API v1 を対象に、以下を説明できる状態を目指しています。

- tenant / internal organization / membership の意味
- tenant スコープ権限と internal organization スコープ権限の違い
- request / approval / approval_policy / applied_approval_route / audit_log の意味
- 認証済み API
- 認証と認可の違い
- Spring Security による粗い認可
- Service / Policy 側で行う細かい認可
- RBAC
- 申請 → 承認または差し戻し → 監査ログ記録 の一貫した業務フロー
- OpenAPI による API 契約
- 単体テスト / 統合テスト
- GitHub Actions CI

## 想定技術スタック（Phase 1）

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- OpenAPI
- Docker Compose
- GitHub Actions

※ 現時点では、技術選定の一部は設計・検証段階を含みます。
※ 実装済み範囲と今後の予定は、今後 README と docs を更新して反映します。

## 現在の実装状況

現時点では、OrgFlow API の認証系最小土台を実装中です。

完了済みの内容は次のとおりです。

- Spring Boot API プロジェクトを `api/` 配下に作成
- Docker Compose で PostgreSQL を起動
- `application.yaml` に PostgreSQL 接続設定を追加
- Spring Security の最小設定を追加
  - `/login`、`/hello`、`/error` は `permitAll`
  - それ以外は `authenticated`
  - CSRF 無効
  - セッションは stateless
- `/login` 用の request / response DTO を作成
  - `LoginRequest`: `loginId`, `password`
  - `LoginResponse`: `accessToken`, `currentTenantId`
- `AuthController` を作成
- `AuthService` を作成し、Controller から Service を呼ぶ形に分離
- `@Valid` 失敗時に 422 を返す `GlobalExceptionHandler` を作成
- Postman で `POST /login` の正常系とバリデーションエラー系を確認

`POST /login` では、DB 上の `users` テーブルに登録された user を使い、loginId / password による最小限の本人確認を行う。

現在の処理は次のとおり。

- `LoginRequest` で `loginId` と `password` を受け取る
- `UserRepository#findByLoginId` で `users` テーブルから user を検索する
- user が存在しない場合は 401 を返す
- user が存在する場合、入力 password と `users.hashed_password` を `PasswordEncoder#matches` で照合する
- password が一致しない場合も 401 を返す
- user 未存在と password 不一致は、外部には同じメッセージとして返す
- 成功時は、現時点では `dummy-token` と `currentTenantId = null` を返す

まだ未実装の内容は次のとおり。

- JWT 生成
- Bearer token filter
- SecurityContext への認証情報設定
- `POST /tenants/{tenantId}/select`
- current tenant 入り token の再発行
- login 成功 / 失敗の監査ログ記録

## ローカル起動の現状

ローカル開発では、PostgreSQL を Docker Compose で起動します。

```bash
docker compose up -d db
```

Spring Boot API は、現時点ではローカルで起動し、Docker 上の PostgreSQL へ `localhost:5432` で接続します。

将来的に API も Docker Compose に含める場合、DB 接続先 host は `localhost` ではなく Compose の service 名である `db` に変更します。

## 現時点の主要エンドポイント

現時点で実装済みまたは確認用に使っている endpoint は次のとおりです。

| メソッド | path     | 目的                     | 状態              |
| -------- | -------- | ------------------------ | ----------------- |
| POST     | `/login` | ログイン API の外形確認  | 仮実装            |
| GET      | `/hello` | 疎通確認                 | 一時的            |
| -        | `/error` | Spring Boot のエラー処理 | Security 上は許可 |

`/login` は未認証で呼べる API として扱います。
ただし、現時点では dummy token を返す段階であり、DB 上の user 検索や JWT 生成はまだ実装していません。

## 認証・認可の現在方針

Phase 1 では、Bearer token を使う認証済み API として設計します。

現時点の Spring Security 設定では、次の方針を採っています。

- `/login` は未認証で呼べる
- `/hello` は疎通確認用として未認証で呼べる
- `/error` はバリデーションエラーなどの処理が Security に遮られないように未認証で呼べる
- その他の API は、まず `authenticated` を基本とする
- CSRF は無効化する
- セッションは使わず stateless にする
- Spring Security のフォームログインは使わない

request 系 / approval 系の細かい認可は、SecurityConfig に早く詰め込まず、Service / Policy 側で判定する方針です。

ここでいう細かい認可とは、たとえば次のような DB や業務状態を見ないと判断できない条件です。

- 対象 request が current tenant に属しているか
- user が対象 internal organization に所属しているか
- user が申請作成権限を持っているか
- user が現在の承認者か
- request が編集可能な状態か

## `/login` の現在仕様

現時点の `/login` は、次の形を前提にしています。

### Request

```json
{
  "loginId": "user001",
  "password": "password"
}
```

### Response

```json
{
  "accessToken": "dummy-token",
  "currentTenantId": null
}
```

`currentTenantId` は `/login` では確定させません。
current tenant の選択は、後続で実装する `POST /tenants/{tenantId}/select` の責務とします。

## バリデーションエラーの扱い

`@Valid` による入力値検証でエラーが発生した場合、`GlobalExceptionHandler` で `MethodArgumentNotValidException` を受け、422 を返します。

これは、OpenAPI 上で入力値不正を 422 として扱う方針に合わせたものです。

現時点では、主に `/login` の `loginId` や `password` が空の場合に確認しています。

## リポジトリ構成

```text
.
├── README.md
├── api
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src
├── compose.yaml
├── docs
│   ├── adr
│   ├── design
│   ├── design-overview.md
│   ├── domain-rules.md
│   ├── er
│   └── openapi
└── notes
```

## 今後更新する項目

以下は、実装の進行に合わせて README に追記予定です。

- セットアップ手順
- API 起動手順
- テスト実行手順
- API 一覧
- デモ用アカウント / Seed データ
- 実装済み機能一覧
- 認証・認可の実装方針
- request / approval 系 API の動作確認手順
- スクリーンショット / デモ導線

## 次に実装する予定

次は、DB 上の user を使ったログイン認証に進みます。

最初にやるべきことは、JWT 生成ではなく、DB 上の user を使った本人確認です。

予定している順番は次のとおりです。

1. `users` テーブル定義の確認
2. `User` Entity の最小作成
3. `UserRepository` の作成
4. ログイン確認用 seed データの作成
5. `AuthService#login` を「ユーザー検索 + パスワード照合」に変更
6. 認証失敗時の 401 エラー方針整理
7. その後に TokenService / JWT 発行へ進む

## 補足

この README は、現時点のプロジェクト入口として最小限に保っています。
設計理由の詳細は ADR、業務ルールや構造の詳細は docs を参照してください。

現時点では、実装途中の内容も含まれます。
そのため、「実装済み」と「設計済み・未実装」は区別して記載します。
