# ADR-020: API を stateless 前提の Spring Security 構成にする

- Status: Accepted
- Date: 2026-04-29
- 関連: ADR-016（Bearer token の採用）

> **決定**: 既定を `authenticated` とし、`/login`・`/hello`・`/error` だけを `permitAll` にする。セッションは作成せず（`STATELESS`）、CSRF 対策とフォームログインは無効にする。

## 背景

Spring Security は、フォームログイン・セッション・CSRF トークンが有効な状態を既定値として起動する。これはサーバーが HTML を返す Web アプリを想定した構成で、React から呼ばれる JSON API には合わない。

認証の中身（本人確認、token の発行と検証）を実装する前に、どの API を保護するか、セッションを持つかどうかという入口の構成を先に決める必要があった。

## 理由

- Bearer token を採用したため（ADR-016）、認証状態をサーバー側に保存する必要がない。`STATELESS` にすることで、意図しないセッション生成が起きない
- CSRF 攻撃は Cookie がブラウザによって自動送信されることで成立する。Bearer token は `Authorization` ヘッダで明示的に付けるため自動送信されず、CSRF トークンの往復が不要になる
- 既定を `authenticated` とするホワイトリスト方式にすると、API を追加したときに保護指定を忘れても未認証で公開されることがない
- `/error` を `permitAll` にしないと、バリデーションエラーなどの例外処理が Security に遮られ、意図した HTTP ステータスを返せない

## 検討した他の案

- **案A: 既定を `permitAll` にし、保護が必要な API だけ `authenticated` にする** — API を追加するたびに保護指定が必要になり、忘れても正常に動いてしまうため気づけない
- **案B: セッションを併用する** — Bearer token とセッションで認証状態が二重になり、どちらを正とするかの説明がつかなくなる

## 受け入れる制約

- サーバー側に認証状態を持たないため、特定の token だけを無効化できない（ADR-016 と同じ制約）
- 疎通確認用の `/hello` を `permitAll` に置いている。本番機能ではないため、不要になった時点で削除する
- CSRF を無効にしているため、将来 token を Cookie に保存する方式へ変えるなら CSRF 対策を入れ直す必要がある

## 見直す条件

- token を Cookie に保存する方式へ変更する場合
- IP 制限やレート制限など、認証以外の入口制御を Security 層に持たせる場合
- `@PreAuthorize` などメソッド単位の認可を導入する場合
