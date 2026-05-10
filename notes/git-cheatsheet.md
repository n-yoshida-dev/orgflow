# Git チートシート

## まず押さえること

- `main` は本線。基本は直接作業しない
- 通常は **作業ブランチを切って PR を出す**
- `push` は GitHub に送る操作。main に反映されるとは限らない
- `merge` はブランチを統合する操作。ローカル merge の場合は push が別途必要
- PR が merge 済みのブランチは、変更履歴が `main` に入っているので削除してよい
- 迷ったら、まず `git status` / `git diff` / `git diff --staged` を見る
- 状況を把握していないときは `git add .` を使わない

---

## よく使う確認コマンド

```bash
git status
git diff
git diff --staged
git branch --show-current
```

### 用途

| コマンド                    | 用途                     |
| --------------------------- | ------------------------ |
| `git status`                | 今の状態を見る           |
| `git diff`                  | 未ステージの差分を見る   |
| `git diff --staged`         | commit直前の差分を見る   |
| `git branch --show-current` | 今いるブランチを確認する |

---

## 標準ルート：作業ブランチを切って PR を出す

### 1. main を最新化する

```bash
git switch main
git status
git fetch origin
git rebase origin/main
```

### 2. 作業ブランチを作る

```bash
git switch -c <branch-name>
```

ブランチ名ルールは `notes/git-branch-naming-rule.md` を参照する。

### 3. 編集後、差分を確認する

```bash
git status
git diff
```

### 4. 必要なファイルだけ add する

```bash
git add <file>
```

### 5. commit する

```bash
git diff --staged
git commit -m "<type>(<scope>): <summary>"
```

commit message ルールは `notes/git-commit-message-rule.md` を参照する。

### 6. GitHub に push する

初回:

```bash
git push -u origin <branch-name>
```

2回目以降:

```bash
git push
```

### 7. GitHub で PR を作る

- base: `main`
- compare: `<branch-name>`

### 8. GitHub 上で merge する

通常は GitHub の PR 画面から merge する。
ローカルで `git merge` しなくてよい。

### 9. merge 後に main を更新し、ブランチを削除する

```bash
git switch main
git pull --rebase origin main
git branch -d <branch-name>
git push origin --delete <branch-name>
```

---

## 軽微修正ルート：main に直接 commit する場合

### 対象例

- `notes/` の個人メモ修正
- typo 修正
- リンク修正
- レビュー価値がほぼない変更

### 非対象

- ER図
- README
- ADR
- 設計資料
- コード
- テスト
- CI

### 手順

```bash
git switch main
git status
git fetch origin
git rebase origin/main

git diff

git add <file>
git diff --staged
git commit -m "docs: indexのリンク修正"
git push origin main
```

> 軽微修正ルートでは PR を省略できるが、使いすぎないこと。

---

## PR merge 済みブランチの削除

PR が merge 済みなら、ブランチは削除してよい。

削除されるのはブランチ名という参照だけ。
変更履歴そのものは `main` 側に残る。

```bash
git switch main
git pull --rebase origin main
git branch -d <branch-name>
git push origin --delete <branch-name>
```

---

## push が拒否されたとき

### 典型例

```bash
git push
# non-fast-forward で拒否
```

### 対応

```bash
git fetch origin
git rebase origin/main
git push
```

### 意味

GitHub 側の `main` が先に進んでいる。
先にその差分を取り込んでから push する必要がある。

---

## pull / rebase の前に確認すること

`git pull --rebase origin main` や `git rebase origin/main` の前に、作業ツリーが汚れていないか確認する。

```bash
git status
```

未コミット変更がある場合は、次のどれかを選ぶ。

| やりたいこと | コマンド                        |
| ------------ | ------------------------------- |
| 変更を残す   | `git add <file>` → `git commit` |
| 一時退避する | `git stash push -u`             |
| 破棄する     | `git restore <file>`            |

---

## ローカル merge を使う場合

基本は **PR merge を優先** する。
ただし、ローカルで merge する場合は以下。

```bash
git switch main
git status
git fetch origin
git rebase origin/main
git merge <branch-name>
git push origin main
```

### 注意

- `merge` しただけでは GitHub は更新されない
- GitHub に反映するには `git push origin main` が必要
- 実務想定なら、通常は GitHub PR 上で merge した方がよい

---

## Ahead / Behind の見方

| 状態                   | 意味                                                               |
| ---------------------- | ------------------------------------------------------------------ |
| Ahead 0 / Behind > 0   | そのブランチ独自のコミットはもう無い。役目を終えている可能性が高い |
| Ahead > 0 / Behind 0   | まだ main に入っていない変更がある                                 |
| Ahead > 0 / Behind > 0 | main と branch が両方進んでいて、追従や整理が必要                  |

### 注意

- Ahead / Behind はコミット差を表す
- 内容が同じでも、merge 方法次第では Ahead が残ることがある
- 迷ったら PR 状態と diff を確認する

---

## やってはいけないこと

```bash
git add .
```

状況を把握していないときに全 add しない。
まず次を見る。

```bash
git status
git diff
git diff --staged
```

---

## 詳細ルールの参照先

- commit message ルール: `notes/git-commit-message-rule.md`
- ブランチ命名ルール: `notes/git-branch-naming-rule.md`
