# Git チートシート

## 操作の流れ

編集
→ git add
→ git commit
→ git push
→ PR
→ review / CI
→ merge

## よく使う接頭辞

- feat: 機能追加
- fix: バグ修正
- docs: 文書修正
- test: テスト追加・修正
- refactor: 振る舞いを変えない整理
- chore: 雑多な保守作業
- ci: CI設定
- build: ビルド設定

## 新規作業開始

git switch main
git pull --rebase origin main
git switch -c <branch-name>

## 差分確認

git status
git diff
git diff --staged

## ステージング

git add <file>

## コミット

git commit -m "docs: 論理ER図を追加"

## 初回push

git push -u origin <branch-name>

## 2回目以降

git push

## 作業終了後

git switch main
git pull --rebase origin main
git branch -d <branch-name>

## 命名例

- feature/add-request-create-api
- fix/request-status-null
- docs/logical-er-diagram-initial
- test/request-service
