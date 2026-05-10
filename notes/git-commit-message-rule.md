# Git commit message ルール

## 基本形

```bash
git commit -m "<type>(<scope>): <summary>"
```

例:

```bash
git commit -m "docs(er): 概念ER図のテナント前提を修正"
```

## type

| type       | 用途                   |
| ---------- | ---------------------- |
| `feat`     | 機能追加               |
| `fix`      | バグ修正               |
| `docs`     | 文書修正               |
| `test`     | テスト追加・修正       |
| `refactor` | 振る舞いを変えない整理 |
| `chore`    | 雑多な保守作業         |
| `ci`       | CI設定                 |
| `build`    | ビルド設定             |

## scope

scope は、変更対象の範囲を短く書く。

例:

| scope        | 用途           |
| ------------ | -------------- |
| `api`        | API実装        |
| `auth`       | 認証・認可     |
| `db`         | DB / migration |
| `er`         | ER図           |
| `openapi`    | OpenAPI        |
| `adr`        | ADR            |
| `readme`     | README         |
| `service`    | Service層      |
| `repository` | Repository層   |
| `controller` | Controller層   |

## 例

```bash
git commit -m "docs(er): 概念ER図のテナント前提を修正"
git commit -m "docs(adr): applied_approval_routeの判断理由を追記"
git commit -m "fix(api): request作成時の組織境界チェックを修正"
git commit -m "test(service): 承認時の監査ログ記録を追加"
git commit -m "chore(git): Gitチートシートを整理"
```

## 自分用ルール

- summary は日本語でよい
- 何をしたかが一目で分かる文にする
- 「修正」「更新」だけで終わらせない
- 1 commit に複数テーマを混ぜない
- commit 前に `git diff --staged` を見る
