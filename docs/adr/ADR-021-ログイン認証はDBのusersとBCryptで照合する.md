# ADR-021: ログイン認証は DB の users と BCrypt で照合する

- Status: Accepted
- Date: 2026-05-09
- 関連: ADR-020（Security 構成） / ADR-022（認証成功後の token 発行） / ADR-019（seed user と password hash）

> **決定**: `/login` は loginId で `users` を検索し、`PasswordEncoder#matches` で password を照合する。loginId 未存在と password 不一致は区別せず、どちらも 401 と同一のメッセージを返す。

## 背景

`/login` は仮実装で、リクエストの形だけ受け取って固定値を返していた。token の発行に進む前に、DB 上の `users` と seed user を使った本人確認を成立させる必要があった。

あわせて、認証に失敗したときに外部へ何を返すかを決める必要があった。ここは外部から繰り返し試行できる唯一の入口であり、レスポンスの差が情報漏洩になる。

## 理由

- loginId 未存在と password 不一致を別のレスポンスにすると、外部から「その loginId が登録されているか」を判定できてしまう。どちらも同じ 401・同じメッセージに丸めることでアカウント列挙を防ぐ
- 照合を `PasswordEncoder#matches` に任せることで、hash 文字列の直接比較や復号といった誤った実装を避けられる。BCrypt は salt を hash 文字列に含むため、同じ password でも hash は毎回異なり、自前比較は成立しない
- 本人確認の判断は Service の責務とし、Repository は loginId による user 検索に限定する。Repository に認証の合否判断を持たせない

## 検討した他の案

- **案A: loginId 未存在は 404、password 不一致は 401 にする** — HTTP ステータスの使い分けとしては自然に見えるが、レスポンスの差からアカウントの存在を判定できてしまう
- **案B: password hash を取得して自前で比較する** — BCrypt の hash には salt とコストが埋め込まれており、文字列一致では判定できない

## 受け入れる制約

- 認証成功時に返す token はこの段階では仮の値であり、JWT の発行は ADR-022 で扱う
- login の成功 / 失敗を監査ログに記録していない。監査ログの実装時にあらためて扱う
- 連続失敗によるアカウントロックやレート制限は Phase 1 では扱わない。総当たりへの耐性は BCrypt のコストのみに依存する
- user の tenant 所属や role の確認は `/login` では行わない（ADR-024）

## 見直す条件

- login の成功 / 失敗を監査ログへ記録する場合
- password 変更、リセット、アカウントロックを実装する場合
- 外部 IdP へ認証を移す場合
- 多要素認証を導入する場合
