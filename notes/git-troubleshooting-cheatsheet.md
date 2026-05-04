# Git トラブル対応チートシート

Git 操作で詰まったときに見るためのメモ。

日常操作は `notes/git-cheatsheet.md` を参照する。

---

## 最初に見るコマンド

何かおかしいと思ったら、まず以下を見る。

```bash
git status
git diff
git diff --staged
git branch --show-current
```

### 見る順番

1. 今どのブランチにいるか
2. 未コミット変更があるか
3. ステージ済みの変更があるか
4. これから commit / pull / rebase / merge してよい状態か

---

## push が拒否されたとき

### よくある表示

```bash
git push
# non-fast-forward で拒否
```

### 原因

GitHub 側のブランチが、自分のローカルより先に進んでいる。

つまり、自分が push しようとしている間に、GitHub 側にまだ取り込んでいない commit がある。

### 対応

```bash
git fetch origin
git rebase origin/main
git push
```

### 注意

作業ブランチで作業している場合は、`origin/main` ではなく、対象ブランチを確認する。

```bash
git branch --show-current
```

---

## pull / rebase しようとして止まったとき

### 原因

未コミット変更が残っている状態で、`pull --rebase` や `rebase` をしようとしている可能性が高い。

### まず確認する

```bash
git status
```

### 選択肢

| やりたいこと         | 対応         |
| -------------------- | ------------ |
| 変更を残したい       | commit する  |
| 変更を一時退避したい | stash する   |
| 変更を捨ててよい     | restore する |

---

## 変更を commit してから rebase する

```bash
git status
git add <file>
git diff --staged
git commit -m "<type>(<scope>): <summary>"
git fetch origin
git rebase origin/main
```

---

## 変更を一時退避してから rebase する

```bash
git status
git stash push -u
git fetch origin
git rebase origin/main
git stash pop
```

### 注意

`git stash pop` 後に conflict することがある。
その場合は、ファイルを確認して解消する。

---

## 変更を破棄してよい場合

```bash
git status
git restore <file>
```

ステージ済みの変更を戻す場合:

```bash
git restore --staged <file>
```

ファイル自体を元に戻す場合:

```bash
git restore <file>
```

---

## merge 前に止まったとき

### まず見る

```bash
git status
git diff
git diff --staged
```

### やってはいけないこと

```bash
git add .
```

何が混ざっているか分からない状態で全 add しない。

### 確認すること

- 今いるブランチは正しいか
- merge したいブランチは正しいか
- 未コミット変更が残っていないか
- main は最新か

---

## conflict が起きたとき

### まず確認する

```bash
git status
```

conflict しているファイルが表示される。

### 対応の流れ

1. conflict しているファイルを開く
2. `<<<<<<<` / `=======` / `>>>>>>>` の範囲を確認する
3. 残す内容を決める
4. conflict マーカーを消す
5. 修正後の差分を確認する
6. add する
7. rebase / merge を続ける

### rebase 中の場合

```bash
git add <file>
git rebase --continue
```

### merge 中の場合

```bash
git add <file>
git commit
```

---

## rebase をやめたいとき

rebase 中に「やっぱり戻したい」と思った場合。

```bash
git rebase --abort
```

rebase 開始前の状態に戻る。

---

## merge をやめたいとき

merge 中に「やっぱり戻したい」と思った場合。

```bash
git merge --abort
```

merge 開始前の状態に戻る。

---

## 間違えて add したとき

ステージから外す。

```bash
git restore --staged <file>
```

ファイルの変更自体は残る。

---

## 間違えてファイルを変更したとき

変更を捨てて、最後の commit の状態に戻す。

```bash
git restore <file>
```

### 注意

この操作をすると、未コミットの変更は消える。
必要な変更なら先に commit するか stash する。

---

## 直前の commit に入れ忘れたとき

直前の commit に追加する。

```bash
git add <file>
git commit --amend
```

commit message も修正したい場合:

```bash
git commit --amend -m "<type>(<scope>): <summary>"
```

### 注意

すでに push 済みの commit に対しては、基本的に安易に使わない。

---

## 今いるブランチが分からないとき

```bash
git branch --show-current
```

または:

```bash
git branch
```

`*` が付いているブランチが、今いるブランチ。

---

## main と origin/main の位置がズレているとき

### 意味

- `main`: ローカルの main
- `origin/main`: GitHub 上の main を最後に取得した状態

GitHub の最新状態を取り込むには、まず fetch する。

```bash
git fetch origin
```

その後、ローカル main を更新する。

```bash
git switch main
git rebase origin/main
```

---

## Ahead / Behind の見方

| 状態                   | 意味                                           | 対応                        |
| ---------------------- | ---------------------------------------------- | --------------------------- |
| Ahead 0 / Behind > 0   | main 側に新しい commit がある                  | pull / rebase する          |
| Ahead > 0 / Behind 0   | 自分の commit がまだ push / merge されていない | push または PR 確認         |
| Ahead > 0 / Behind > 0 | 両方進んでいる                                 | 状況確認して rebase / merge |
| Ahead 0 / Behind 0     | 差分なし                                       | 基本的に問題なし            |

---

## PR merge 済みなのにブランチが残っているとき

PR が merge 済みなら削除してよい。

```bash
git switch main
git pull --rebase origin main
git branch -d <branch-name>
git push origin --delete <branch-name>
```

### 注意

削除されるのはブランチ名という参照。
変更履歴そのものは `main` に残る。

---

## ローカルブランチ削除でエラーが出るとき

### 例

```bash
git branch -d <branch-name>
# error: The branch is not fully merged
```

### 意味

Git が「このブランチの commit がまだ main に入っていない可能性がある」と判断している。

### まず確認する

```bash
git log --oneline main..<branch-name>
```

何も出なければ、main に差分はない可能性が高い。

### 強制削除する場合

```bash
git branch -D <branch-name>
```

### 注意

`-D` は強制削除なので、PR merge 済みか、差分が不要であることを確認してから使う。

---

## やってはいけないこと

### 状況不明のまま全 add

```bash
git add .
```

### 状況不明のまま force push

```bash
git push --force
```

### main で大きな作業を始める

```bash
git switch main
# そのまま実装開始
```

---

## 迷ったときの確認セット

```bash
git branch --show-current
git status
git diff
git diff --staged
git log --oneline -5
```

この結果を見ても判断できない場合は、以下を確認する。

- 今いるブランチ
- やりたい操作
- 直前に実行したコマンド
- `git status` の内容
- GitHub の PR 状態
