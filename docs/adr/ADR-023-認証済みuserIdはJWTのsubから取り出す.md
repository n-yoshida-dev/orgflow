# ADR-023: 認証済み userId は JWT の `sub` から取り出し、Service へは UUID で渡す

- Status: Accepted
- Date: 2026-05-24
- 関連: ADR-022（JWT の発行と検証） / ADR-024（current_tenant_id の取得）

> **決定**: Controller が `@AuthenticationPrincipal Jwt` で検証済み JWT を受け取り、`sub` を `UUID` に変換して Service へ渡す。変換処理は専用の部品（`JwtUserIdExtractor`）に切り出す。`sub` が UUID として解釈できない場合は 401 とし、外部向けメッセージは「認証情報が不正です」に丸める。

## 背景

検証済みの JWT は Spring Security が保持しているが、業務処理から「このリクエストを実行しているのは誰か」を扱う手段がなかった。

決めるべきは2点あった。JWT から userId を取り出す位置と、Service へ渡すときの型である。

## 理由

- Controller は HTTP リクエストの入口であり、Spring Security から `Jwt` を受け取る位置として自然
- Service は業務処理の層なので、Spring Security 固有の `Jwt` や `Authentication` に依存させない。渡すのは `UUID userId` だけにする
- `UUID.fromString(jwt.getSubject())` は1行だが、Controller ごとに直書きすると、`sub` が不正だったときの扱いが Controller ごとにばらつく。取り出し方を変えるときの修正箇所も増える
- UUID 変換の失敗は `IllegalArgumentException` になるが、この例外は業務入力値の不正でも発生する。専用の例外型に変換することで、認証情報の不正と業務入力値の不正を型で区別できる
- 変換に失敗した token も署名としては正しい。サーバー内部のエラーではないため、500 ではなく 401 が適切

## 検討した他の案

- **案A: Controller ごとに `UUID.fromString(...)` を直接書く** — 保護 API が増えるほど変換と例外処理が散らばる
- **案B: `Authentication#getName()` から取得する** — 同じ値が返ることは確認したが、コードから「JWT の `sub` を読んでいる」ことが読み取れない
- **案C: Service へ `Jwt` をそのまま渡す** — Service が Spring Security の型を知ることになり、認証方式を変えると業務処理まで影響する
- **案D: `IllegalArgumentException` をそのまま外へ出す** — 認証情報の不正か業務入力値の不正か区別できず、意図しない 500 になる

## 受け入れる制約

- `sub` の userId が DB に実在するかまでは確認していない。署名が正しければ実在するという前提を置いている
- tenant 所属や role の確認は行わない（ADR-024 以降）
- JWT 検証そのものの失敗による 401 と、検証後に `sub` が前提を満たさなかった 401 は処理経路が異なる。外部からは区別できない

## 見直す条件

- `sub` 以外の claim も使って認証済みユーザー情報を組み立てる必要が出た場合
- `JwtAuthenticationConverter` などで principal や authority の変換を Spring Security 側に寄せる場合
- Service へ渡す認証済み利用者の情報が `UUID userId` だけでは足りなくなった場合
