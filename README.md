# OrgFlow

OrgFlow は、申請・承認フローを題材にした業務ワークフローアプリケーションです。

現在は Phase 1 として、Java / Spring Boot による API 実装を中心に進めています。

## この README の位置づけ

この README は、リポジトリ全体の入口として、以下を確認するための文書です。

- プロジェクト概要
- ローカル起動手順
- テスト実行手順
- 主要ドキュメントへのリンク
- リポジトリ構成

設計判断の詳細は README には詰め込まず、`docs/` と ADR に分けて管理します。

## 技術スタック

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Security OAuth2 Resource Server
- Spring Security OAuth2 JOSE
- Spring Data JPA
- Maven

### Database

- PostgreSQL
- Flyway

### API / Documentation

- OpenAPI
- ADR
- DBML

### Local Development

- Docker Compose

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

## 主要ドキュメント

### ドキュメント入口

- [Docs 入口](./docs/index.md)

### 設計関連

- [設計概要](./docs/design-overview.md)
- [業務ルール](./docs/domain-rules.md)
- [実装対応表](./docs/design/implementation-mapping.tsv)

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

### ADR

- [ADR 一覧](./docs/adr/index.md)

### メモ

- [Git チートシート](./notes/git-cheatsheet.md)
- [Git ブランチ命名ルール](./notes/git-branch-naming-rule.md)
- [Git コミットメッセージルール](./notes/git-commit-message-rule.md)
- [Git トラブルシューティングチートシート](./notes/git-troubleshooting-cheatsheet.md)
- [Docker チートシート](./notes/docker-cheatsheet.md)
- [HTTP ステータスコードチートシート](./notes/http-status-code-cheatsheet.md)

## ローカル起動手順

### 1. PostgreSQL を起動する

リポジトリルートで以下を実行します。

```bash
docker compose up -d
```

PostgreSQL が起動していることを確認します。

```bash
docker compose ps
```

### 2. JWT secret を設定する

API 起動時には `ORGFLOW_JWT_SECRET` が必要です。

WSL / Linux / macOS の例:

```bash
export ORGFLOW_JWT_SECRET='change-this-secret-to-a-long-random-value'
```

PowerShell の例:

```powershell
$env:ORGFLOW_JWT_SECRET = "change-this-secret-to-a-long-random-value"
```

### 3. API を起動する

```bash
cd api
./mvnw spring-boot:run
```

Windows の場合:

```powershell
cd api
.\mvnw.cmd spring-boot:run
```

## テスト実行

```bash
cd api
./mvnw test
```

Windows の場合:

```powershell
cd api
.\mvnw.cmd test
```

## 動作確認

### 疎通確認

```bash
curl http://localhost:8080/hello
```

### ログイン

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "<seed user loginId>",
    "password": "<seed user password>"
  }'
```

ログイン確認用の seed user は、以下を参照してください。

```text
api/src/main/resources/db/migration/V2__seed_users.sql
```

### Bearer token を付けて保護 API を呼び出す

`/login` のレスポンスに含まれる `accessToken` を使います。

```bash
curl http://localhost:8080/hello-auth \
  -H "Authorization: Bearer <accessToken>"
```

## データベース初期化

DB migration は Flyway で管理しています。

migration ファイルは以下に配置しています。

```text
api/src/main/resources/db/migration/
```

ローカル DB を作り直したい場合は、Docker volume を削除してから PostgreSQL を再起動します。

```bash
docker compose down -v
docker compose up -d
```

その後、API を起動すると Flyway migration が実行されます。

## 環境変数

| 変数名               | 用途                        |
| -------------------- | --------------------------- |
| `ORGFLOW_JWT_SECRET` | JWT 署名・検証に使う secret |

## 補足

このリポジトリでは、設計判断、業務ルール、API 契約、ER 図を `docs/` 配下で管理しています。

README は、ローカルで起動・確認するための入口として最小限に保ちます。
