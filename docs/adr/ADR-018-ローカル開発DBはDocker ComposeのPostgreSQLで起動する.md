# ADR-018: ローカル開発DBは Docker Compose の PostgreSQL で起動する

- Status: Accepted
- Date: 2026-04-29

> **決定**: ローカル開発DBは Docker Compose の PostgreSQL（DB名 / ユーザー名 / パスワードとも `orgflow`）で起動し、Spring Boot はローカル実行で `localhost:5432` へ接続する。

## 背景

OrgFlow Phase 1 の Spring Boot 実装に入り、まず `/login` を含む認証系 API の最小土台を作り始めた。

Spring Boot プロジェクトには Spring Data JPA と PostgreSQL Driver を含めているため、アプリ起動時点で DataSource 設定が必要になる。最初に DB 接続設定が未定義だったため、Spring Boot 起動時に DataSource の自動設定で失敗した。

この段階で、ローカル開発用 DB をどう用意するかを決める必要があった。

候補は次の3つ。

- ローカルPCに PostgreSQL を直接インストールする
- H2 を一時的に使う
- Docker Compose で PostgreSQL を起動する

## 採用した案

ローカル開発DBは、Docker Compose で PostgreSQL を起動する。

現時点では、`compose.yaml` はリポジトリルートに置き、まずは `db` サービスだけを起動する。

Spring Boot アプリは当面ローカル実行し、Docker 上の PostgreSQL へ `localhost:5432` 経由で接続する。

DB 名、ユーザー名、パスワードはローカル開発用として次の値を使う。

- DB 名: `orgflow`
- DB ユーザー名: `orgflow`
- DB パスワード: `orgflow`

## 採用理由

ローカルPCへ PostgreSQL を直接インストールすると、環境差分が出やすく、セットアップ手順も属人化しやすい。

H2 は起動確認だけなら手軽だが、OrgFlow Phase 1 では PostgreSQL 前提で DB 設計、Repository、認証、認可、将来の Docker Compose 再現性を扱う必要がある。H2 を一時的に入れると、後で捨てる依存・設定・差分理解が増える。

Docker Compose で PostgreSQL を起動すれば、ローカル環境を再現しやすく、README にも起動手順として残しやすい。また、将来的に `api`、`web`、`nginx` などを追加する場合も、リポジトリルートの `compose.yaml` に統合しやすい。

Phase 1 では AWS 本番構成に進む前に、まずローカルで API と DB の再現性を確保することを優先する。

## 不採用案

- 案A: ローカルPCに PostgreSQL を直接インストールする
  - 手元では動いても、他環境での再現性が弱い。
  - README に書くセットアップ手順が環境依存になりやすい。
  - 将来の Docker Compose 化と二重管理になりやすい。

- 案B: H2 を一時的に使う
  - 起動確認だけなら速いが、PostgreSQL との差分が残る。
  - 後で削除する依存関係や設定が増える。
  - OrgFlow Phase 1 の PostgreSQL 前提の DB 設計・認証実装に対して遠回りになる。

## 受け入れる制約

- Docker が起動できる環境であることが前提になる。
- 現時点では DB だけを Compose で起動し、API はローカル実行するため、将来 API も Compose に入れるときに `spring.datasource.url` の host を `localhost` から `db` へ切り替える必要がある。
- ローカル開発用のユーザー名・パスワードは簡易な値を使うため、本番相当の認証情報管理とは分けて扱う。
- `postgres:17.6` のようにバージョンを固定し、`latest` は使わない。

## 見直す条件

- `api` も Docker Compose の service として起動する段階に進むとき。
- React フロントエンドや nginx を Compose に追加するとき。
- CI で PostgreSQL を使った統合テストを回す段階に進むとき。
- AWS RDS へ接続する構成に進むとき。
