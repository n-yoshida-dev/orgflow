# ADR-022: JWT の発行と検証を Spring Security の標準機構に寄せる

- Status: Accepted
- Date: 2026-05-12（issuer 検証の追加まで含めた確定は 2026-05-21）
- 関連: ADR-016（Bearer token の採用） / ADR-021（本人確認） / ADR-023（sub の利用）

> **決定**: JWT の発行は `JwtEncoder`、検証は `JwtDecoder` と Spring Security Resource Server で行う。署名は HS256、secret は環境変数から読む。claim は `iss` / `sub` / `iat` / `exp` の4つに絞り、`sub` には `users.id` を入れる。署名・期限・issuer の検証はすべて Controller 到達前に完了させる。

## 背景

Bearer token を採用した（ADR-016）が、token に何を入れるか、どう署名するか、鍵をどこに置くか、検証をどの層で行うかは決まっていなかった。

発行と検証は別の処理だが、署名方式と secret を共有する。片方だけ先に決めると噛み合わなくなるため、まとめて決める必要があった。

## 理由

- 発行に `JwtEncoder`、検証に `JwtDecoder` を使うことで、同じ secret と署名方式を1つの設定から共有できる
- 検証を Resource Server に任せると、署名検証・期限確認・issuer 確認が Controller 到達前に終わる。Controller と Service は「認証済みである」ことを前提にできる
- `sub` に `loginId` ではなく `users.id` を入れることで、loginId の変更やメールアドレスログインへの変更が token の主体識別に影響しない
- JWT の payload は署名されているだけで暗号化されていない。誰でも中身を読めるため、password hash・メールアドレス・権限情報は入れない
- `iss` を claim に入れるだけでは意味がなく、受け取る側で一致を検証して初めて「誰が発行した token か」を保証できる。検証ルールは `SecurityConfig` ではなく、JWT を扱う部品である `JwtDecoder` に持たせる
- 発行処理は「誰に発行してよいか」（本人確認）と「何を入れてどう署名するか」（token 生成）に分ける

## 検討した他の案

- **案A: JWT 専用ライブラリ（JJWT など）で発行・検証する** — 文字列の生成だけなら簡単だが、検証を Resource Server に載せる段階で発行側と検証側の考え方が分断される
- **案B: Controller / Service 内で JWT を検証する** — 認証処理が業務処理層に混ざる。Service は業務ルールと認可を扱う層であり、署名検証を持たせると層が崩れる
- **案C: `withJwkSetUri(...)` で外部から公開鍵を取得する** — token を発行しているのは OrgFlow API 自身であり、参照先の認可サーバーが存在しないため成立しない
- **案D: 発行処理を本人確認と同じクラスに書く** — 本人確認と署名設定の責務が混ざり、鍵の扱いを変えるときに認証処理まで読む必要が出る
- **案E: issuer 検証を行わない** — `iss` を claim に含めている以上、検証しなければ claim を設計した意味がない
- **案F: `sub` に `loginId` を入れる** — loginId は将来変更されうるため、内部の主体識別子には向かない

## 受け入れる制約

- HS256 は共通鍵方式のため、secret を知るものは誰でも有効な token を発行できる。鍵の管理がそのまま認証の強度になる
- secret は現在 shell の環境変数で渡している。API も Docker Compose 管理に寄せる場合は `.env` 経由に見直す。test 側の設定にも同じ値が必要
- issuer は単一の固定値として扱う。複数 issuer や tenant ごとの issuer 分けは扱わない
- refresh token を扱わないため、期限切れは再ログインで対応する
- `NimbusJwtDecoder` や validator の内部実装までは踏み込まず、Resource Server の差し込み口として扱う

## 見直す条件

- 公開鍵方式（RS256）や JWK Set URI 方式へ移行する場合
- 外部 IdP / OIDC を導入する場合
- refresh token を導入する場合
- CI 上で secret をどう扱うか整理する場合
- 複数 issuer の token を受け入れる必要が出た場合
