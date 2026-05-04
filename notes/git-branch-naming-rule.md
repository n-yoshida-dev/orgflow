# Git ブランチ命名ルール

## 基本ルール

- 空白は使わない
- 小文字を基本にする
- 単語区切りは `-`
- prefix は `docs/` `feature/` `fix/` `test/` `chore/` を使う
- 長すぎる名前は避ける

## 基本形

```text
<prefix>/<what-to-change>
```

例:

```text
docs/logical-er-tenant-internal-organization
```

## prefix

| prefix     | 用途             |
| ---------- | ---------------- |
| `feature/` | 機能追加         |
| `fix/`     | バグ修正         |
| `docs/`    | ドキュメント修正 |
| `test/`    | テスト追加・修正 |
| `chore/`   | 雑多な整理・保守 |

## 命名例

```text
feature/add-request-create-api
feature/add-login-api
feature/add-auth-service
fix/request-status-null
fix/login-error-response
docs/logical-er-diagram-initial
docs/concept-er-tenant-internal-organization
docs/update-adr-auth-method
test/request-service
test/auth-service-login
chore/update-git-cheatsheet
chore/remove-unused-notes
```

## OrgFlow 用の命名例

```text
docs/update-auth-design-notes
docs/update-flyway-learning-log
feature/add-user-repository
feature/add-auth-service-login
feature/add-login-error-handling
fix/flyway-seed-user-password
test/auth-service-login
chore/update-readme-public-expression
```

## 自分用ルール

- ブランチ名は「作業内容」を表す
- 1ブランチ1テーマにする
- `work` や `update` だけの曖昧な名前にしない
- 作業が広がりすぎたら、別ブランチに分ける
