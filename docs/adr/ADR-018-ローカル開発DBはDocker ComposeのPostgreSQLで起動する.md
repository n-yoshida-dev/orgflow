# ADR-018: ローカル開発 DB は Docker Compose の PostgreSQL で起動する

- Status: Accepted
- Date: 2026-04-29
- 関連: ADR-019（Flyway による管理）

> **決定**: ローカル開発 DB は Docker Compose で PostgreSQL を起動する。Spring Boot はローカル実行のまま `localhost:5432` へ接続する。DB 名・ユーザー名・パスワードはいずれも `orgflow` とし、イメージのバージョンは固定する。

## 背景

Spring Boot プロジェクトに Spring Data JPA と PostgreSQL Driver を含めているため、起動時に DataSource の設定が必要になる。DB 接続設定が未定義のまま起動して失敗したところで、ローカル開発用の DB をどう用意するかを決める必要が生じた。

候補は、ローカル PC へ PostgreSQL を直接インストールする案、H2 を一時的に使う案、Docker Compose で PostgreSQL を起動する案の3つだった。

## 理由

- `compose.yaml` をリポジトリに置くことで、DB のバージョンと設定がコードとして残り、環境を作り直しても同じ状態を再現できる
- Phase 1 は PostgreSQL 前提で DB 設計・Repository・認証を進めるため、本番と同じ RDBMS をローカルでも使う
- 将来 `api`、`web`、`nginx` を追加する場合も、同じ `compose.yaml` に統合できる
- イメージのバージョンを固定することで、あとから環境を作り直しても DB の挙動が変わらない

## 検討した他の案

- **案A: ローカル PC に PostgreSQL を直接インストールする** — 手元では動くが、インストール手順とバージョンが環境依存になる。将来 Docker Compose 化すると二重管理になる
- **案B: H2 を一時的に使う** — 起動確認は速いが、SQL の方言や型の扱いが PostgreSQL と異なる。後で捨てる依存と設定が増える

## 受け入れる制約

- Docker が起動できる環境であることが前提になる
- 現時点では DB だけを Compose で起動し API はローカル実行しているため、API も Compose に入れる際は接続先 host を `localhost` から `db` へ切り替える必要がある
- ローカル用のユーザー名・パスワードは簡易な値のため、本番の認証情報管理とは分けて扱う

## 見直す条件

- `api` も Docker Compose の service として起動する段階に進むとき
- React フロントエンドや nginx を Compose に追加するとき
- CI で PostgreSQL を使った統合テストを回す段階に進むとき
- AWS RDS へ接続する構成に進むとき
