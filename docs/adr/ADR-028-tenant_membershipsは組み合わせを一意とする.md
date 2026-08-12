# ADR-028: tenant_memberships は (tenant_id, user_id) を一意とする

- Status: Accepted
- Date: 2026-06-04
- 関連: ADR-024（tenant 選択状態） / ADR-019（Flyway による管理） / ADR-015（tenant 境界）

> **決定**: `tenant_memberships` は `tenant_id` と `user_id` を持ち、その組み合わせに一意制約を置く。tenant 選択時の判定は、一覧取得ではなく組み合わせの存在確認で行う。外部キーには暫定的に `ON DELETE CASCADE` を付ける。

## 背景

`POST /tenants/{tenantId}/select`（ADR-024）を実装するには、user と tenant の所属関係を DB で確認できる必要があった。

このとき決めるべきは、所属関係の一意性をどう保証するか、tenant 選択の判定をどう実装するか、tenant や user を削除したときに所属関係をどうするかの3点だった。

## 理由

- `(tenant_id, user_id)` に一意制約を置くことで、同じ user が同じ tenant に重複して所属する状態を DB 側で防げる。アプリケーション側の重複チェックに依存しない
- tenant 選択で必要なのは「この user がこの tenant に所属しているか」の真偽だけである。所属 tenant を一覧取得してアプリ側で比較すると、所属数に比例して無駄な取得が発生する。組み合わせの存在確認に寄せる
- `ON DELETE CASCADE` は、tenant や user を消したときに所属関係だけが残る状態を防ぐ。ただしこれは選択機能の最小実装のための暫定判断であり、削除方針そのものを決めたものではない

## 検討した他の案

- **案A: 一意制約を置かず、アプリケーション側で重複を防ぐ** — 別経路から所属を追加したときに重複を防げない。seed データや将来の管理 API でも同じチェックを繰り返すことになる
- **案B: 所属 tenant を一覧取得し、Service 側で該当 tenant を探す** — 存在確認に必要のないデータを取得する。所属数が増えるほど無駄が大きくなる

## 受け入れる制約

- `ON DELETE CASCADE` により、tenant や user の物理削除で所属関係が消える。`requests`、`approvals`、`audit_logs` を実装した段階で、物理削除を許すのか論理削除に寄せるのかを決め直す必要がある
- 現時点では所属に有効期間や停止状態を持たせていないため、「所属しているが利用停止中」を表せない

## 見直す条件

- 監査ログを実装し、削除された user の操作履歴を保持する必要が出た場合
- request / approval が tenant・user・所属を参照するようになった場合
- tenant や user の削除 API を実装する場合
- 所属に有効期間、利用停止状態、最終選択日時などの属性が必要になった場合
