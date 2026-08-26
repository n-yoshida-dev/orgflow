# ADR-019: DB マイグレーションと seed データを Flyway で管理する

- Status: Accepted
- Date: 2026-05-03
- 関連: ADR-018（ローカル開発DB） / ADR-029（seed password の保存形式） / ADR-028（tenant_memberships の構造） / ADR-034（seed の置き場所と適用範囲を更新する判断）

> **決定**: DB のテーブル作成と seed データ投入は Flyway の versioned migration で管理し、`schema.sql` / `data.sql` は使わない。適用済みの migration ファイルは編集せず、新しい migration を追加して前に進める。

## 背景

`/login` を本物のログイン認証にするために、`users` テーブルとログイン確認用の seed user が必要になった。

ローカルの PostgreSQL や Docker volume に手作業でテーブルとデータを作る方法もあるが、その場合 DB の状態が自分の環境に閉じ、CI や再構築時に同じ状態を再現できない。

また、この先 `tenants`、`internal_organizations`、`requests`、`approvals`、`audit_logs` を段階的に追加していくため、DB の変更を順番付きの履歴として管理できる方式が必要だった。

## 理由

- DB の構造変更と seed 投入がリポジトリ上の SQL ファイルとして追跡でき、環境を作り直しても同じ状態を再現できる
- Docker volume は現在のデータを永続化する仕組みであって、DB をどう作るかという手順を記録するものではない。volume に残った状態に依存しない
- 変更が V1、V2、V3 と順番に積み上がるため、どのタイミングで何を変えたかが履歴として残る
- DB 初期化の責務が `schema.sql` / `data.sql` と Flyway に分散しない

## 適用済み migration を編集しない理由

一度 DB に適用した versioned migration は、原則として後から編集しない。

SQL の意味が変わっていなくても、空白や改行が変わるだけで Flyway の checksum が変わり、`validate` が失敗する。実際、SQL formatter が既存の V1 / V2 に触れただけで checksum mismatch が発生した。

ローカル開発でデータを捨ててよい場合は、`docker compose down -v` で volume を削除し、V1 から再適用する。本番 DB や消してはいけない検証 DB では、volume 削除も migration の編集も行わず、新しい migration を追加して前に進める。

## 検討した他の案

- **案A: ローカル PostgreSQL や Docker volume に手動でテーブル・データを作る** — 動作確認はできるが、DB の状態が自分の環境に閉じ、別環境や CI で再現できない
- **案B: `schema.sql` / `data.sql` を使う** — Spring Boot の標準的な初期化方法で分かりやすいが、DB の変更を順番付きの履歴として管理する用途には向かない。テーブルを段階的に増やす前提と合わない

## 受け入れる制約

- 適用済みの migration を直せないため、誤りに気づいても打ち消す migration を追加することになる
- ローカルで migration を作り直したい場合は volume を削除して DB を再作成する必要がある
- Flyway の適用履歴と checksum の仕組みを理解していないと、mismatch の原因を追えない

## 見直す条件

- アプリ起動時の自動 migration ではなく、デプロイパイプライン上で明示的に migration したくなった場合
- seed データが増え、開発用 seed とテスト用 seed を分ける必要が出た場合
- 環境ごとに migration / seed の適用方針を分ける必要が出た場合
