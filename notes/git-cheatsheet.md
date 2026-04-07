# Git チートシート

## まず押さえること

- `main` は本線。基本は直接作業しない
- 通常は **作業ブランチを切って PR を出す**
- `push` は「GitHubに送る」だけ。**mainに反映されるとは限らない**
- `merge` は「ブランチを統合する」操作。**ローカル merge の場合は push が別途必要**
- `PR` は「この変更を main に入れたい」という提案・レビューの場
- `main` はローカルブランチ、`origin/main` は GitHub 上の main の追跡先
- `pull --rebase` や `merge` の前に、作業ツリーが clean か `git status` で確認する
- PR が merge 済みのブランチは、変更履歴が `main` に入っているので削除してよい
- GitHub の Ahead は「そのブランチにだけあるコミット数」、Behind は「main にあってそのブランチにないコミット数」
- 迷ったら `git status` / `git diff` / `git diff --staged` を先に見る
- 状況を把握していないときは `git add .` を使わない

---

## 用語の最小整理

- **add**: 変更をステージする
- **commit**: ローカル履歴として確定する
- **push**: ローカルのコミットを GitHub に送る
- **fetch**: GitHub の最新情報を取り込むだけ。作業ツリーは変えない
- **pull --rebase**: `fetch` してから、自分のコミットを最新の土台の上に積み直す
- **merge**: ブランチを統合する
- **PR**: GitHub 上で差分確認・レビュー・CI確認をしてから main に取り込むための場
- **Ahead**: そのブランチにあって `main` にまだ入っていないコミット数
- **Behind**: `main` にあって、そのブランチにまだ入っていないコミット数

---

## main と origin/main の違い

- `main`: ローカルの main
- `origin/main`: GitHub 上の main の追跡先

### 重要

- `git show HEAD:<path>` は、今のローカルブランチの先頭にあるファイルを見る
- `git show origin/main:<path>` は、GitHub 上の最新 main にあるファイルを見る
- Git Graph では、`main` と `origin/main` の位置がズレていると、ローカルと GitHub の状態が違う

---

## ブランチ名ルール

- 空白は使わない
- 小文字を基本にする
- 単語区切りは `-`
- prefix は `docs/` `feature/` `fix/` `test/` `chore/` を使う
- 長すぎる名前は避ける

### 例

- `docs/logical-er-tenant-internal-organization`
- `feature/add-request-create-api`
- `fix/request-status-null`
- `test/request-service`
- `chore/update-git-cheatsheet`

---

## 標準ルート（実務想定 / 推奨）

### 1. 作業開始前に main を最新化

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

### 3. 編集後、差分を確認する

```bash
git status
git diff
git diff --staged
```

### 4. 必要なファイルだけ add する

```bash
git add <file>
```

### 5. commit する

```bash
git commit -m "docs(er): 概念ER図のテナント前提を修正"
```

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

### 8. review / CI 確認後、GitHub 上で merge する

- 通常は GitHub の PR 画面から merge
- ローカルで `git merge` しなくてよい

### 9. 作業後に main を更新し、ブランチを掃除する

```bash
git switch main
git pull --rebase origin main
git branch -d <branch-name>
git push origin --delete <branch-name>
```

---

## 例外ルート（軽微修正のみ）

**対象例**

- `notes/` の個人メモ修正
- typo 修正
- リンク修正
- レビュー価値がほぼない変更

**非対象**

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
git commit -m "docs: indexのリンク修正"
git push origin main
```

> 例外ルートでは PR を省略できるが、使いすぎないこと

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

## pull / rebase の前提

`git pull --rebase origin main` の前に、作業ツリーが汚れていないか確認する。

```bash
git status
```

未コミット変更がある場合は、次のどれかを選ぶ。

- 残す → commit する
- 一時退避 → `git stash push -u`
- 破棄してよい → `git restore <file>`

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

- GitHub 側の `main` が先に進んでいる
- 先にその差分を取り込んでから push する必要がある

---

## merge 前に止まったとき

### まずやること

```bash
git status
git diff
git diff --staged
```

### やってはいけないこと

```bash
git add .
```

> 何が混ざっているか分からない状態で全 add しない

---

## Ahead / Behind の見方

### 目安

- Ahead 0 / Behind > 0  
  そのブランチ独自のコミットはもう無い。役目を終えている可能性が高い

- Ahead > 0 / Behind 0  
  まだ main に入っていない変更がある

- Ahead > 0 / Behind > 0  
  main と branch が両方進んでいて、追従や整理が必要

### 注意

- Ahead / Behind は **コミット差** を表す
- 内容が同じでも、merge 方法次第では Ahead が残ることがある
- 迷ったら PR 状態と diff を確認する

---

## merge済みブランチの削除

PR が merge 済みなら、ブランチは削除してよい。

- 消えるのはブランチ名という参照
- 変更履歴そのものは `main` 側に残る

### 片付け手順

```bash
git switch main
git pull --rebase origin main
git branch -d <branch-name>
git push origin --delete <branch-name>
```

---

## 差分確認

```bash
git status
git diff
git diff --staged
git show --stat --name-only HEAD
```

### 用途

- `git status`: 変更の全体像を見る
- `git diff`: 未ステージ差分を見る
- `git diff --staged`: commit直前の差分を見る
- `git show --stat --name-only HEAD`: 直前commitに何を入れたか確認する

---

## 位置確認・履歴確認

```bash
git branch --show-current
git log --oneline --all -- <path>
git show origin/main:<path>
```

### 用途

- `git branch --show-current`: 今どのブランチにいるか確認する
- `git log --oneline --all -- <path>`: そのファイルがどの履歴に存在したか確認する
- `git show origin/main:<path>`: GitHub 上の最新 main にあるファイル内容を確認する

---

## Git Graph の最低限の見方

- **丸** = コミット
- **線** = 履歴のつながり
- **ラベル (`main`, `origin/main`)** = そのブランチが今どのコミットを指しているか
- **上にある方が新しい**
- **右ペイン** = 選択したコミット間の差分

### 注意

- Git Graph で見えているファイルは、**今の作業フォルダの状態**とは限らない
- 過去コミット、`main`、`origin/main` のどの地点を見ているかを意識する

---

## よく使う接頭辞

- `feat:` 機能追加
- `fix:` バグ修正
- `docs:` 文書修正
- `test:` テスト追加・修正
- `refactor:` 振る舞いを変えない整理
- `chore:` 雑多な保守作業
- `ci:` CI設定
- `build:` ビルド設定

### scope 付き例

- `docs(er): 概念ER図のテナント前提を修正`
- `docs(adr): applied_approval_routeの判断理由を追記`
- `fix(api): request作成時の組織境界チェックを修正`
- `test(service): 承認時の監査ログ記録を追加`

---

## ブランチ命名例

- `feature/add-request-create-api`
- `fix/request-status-null`
- `docs/logical-er-diagram-initial`
- `docs/concept-er-tenant-internal-organization`
- `test/request-service`
- `chore/update-git-cheatsheet`

---

## よくある勘違い

- `push` しただけで main に入る  
  → 入らない。branch を GitHub に送っただけ

- PR を作っただけで main に入る  
  → 入らない。merge されて初めて入る

- `merge` したら GitHub も変わる  
  → ローカル merge なら `push` が必要

- ブランチを削除すると履歴が消える  
  → merge 済みなら履歴は `main` に残る

- Git Graph で見えている内容 = 今の作業フォルダ  
  → そうとは限らない。どの地点を見ているかを確認する

---

## 自分用ルール

- `main` で直接作業するのは軽微修正だけ
- 設計変更・ER図・README・ADR・コード変更は PR ルート
- ブランチ名は **小文字 + ハイフン区切り + 空白禁止**
- commit 前に `git diff --staged` を必ず見る
- pull / merge / rebase の前に `git status` を見る
- merge / push で詰まったら、先に `git status` を見る
- 「pushしたのにmainに入っていない」は、PR未作成 or 未merge を疑う
- 「mergeしたのにGitHubが変わらない」は、push忘れを疑う
- 「ファイルが消えた？」と思ったら、`main` / `origin/main` / 過去コミットのどこを見ているか確認する
- merge 済みブランチは remote / local ともに削除する
