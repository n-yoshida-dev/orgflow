# ADR-005: request の申請者・組織の参照方式

- Status: Accepted
- Date: 2026-03-24

## 背景

- request は「誰が」「どの組織に対して」提出した申請かを保持する必要がある
- 参照先として internal_organization_membership.id を1本持つ案と、 users.id と internal_organization.id を直接持つ案があった
- 決めないと、申請後に申請者が異動した場合に過去 request の帰属が追えなくなるリスクがある

## 採用した案

- request に users.id と internal_organization.id を直接持つ

## 採用理由

- internal_organization_membership は「現在の所属関係」を表すため、異動・削除によって変化しうる
- request が保持すべきは申請時点の帰属という不変の事実であり、現在の所属関係への参照ではない
- users.id と internal_organization.id を直接持つことで、申請後の組織変更に関わらず申請時点の帰属を追跡できる

## 不採用案

- internal_organization_membership.id を1本持つ案: 所属関係が変更・削除された場合に参照が壊れるか意味が変わる

## 受け入れる制約

- request のカラムが1本増える
- 「申請時点の所属」と「現在の所属」が乖離していても検知できない

## 見直す条件

- 代理申請を導入する場合、「操作した人」と「申請の帰属組織」を分けて持つ必要が生じる可能性がある

## 追記: request 作成時の申請可能条件と tenant 一致制約（2026-04-08）

### 決定

- `request` は引き続き `applicant_user_id` と `internal_organization_id` を直接持つ
- ただし、request 作成時に選べる `internal_organization` は、**current tenant 内で、その user が `internal_organization_membership` を持ち、かつ request 作成を許可する `membership_role` が付与されている所属先** に限る
- したがって、`request` は申請時点の不変な帰属を直接保持する一方、**申請可能条件の判定は current tenant / membership / membership_role を通して行う**
- physical ER では、`request.tenant_id` を持たせ、申請者と申請先 internal organization が同一 tenant に属することを DB 制約で補助的に保証する

### 理由

- `request` が保持すべきなのは、申請時点の不変な事実であり、現在の所属関係そのものではない
- 一方で、誰でも current tenant 内の任意の internal organization に申請できるとすると、業務権限の境界が曖昧になる
- `membership_role` を通して申請可能条件を判定すれば、同じ user でも internal organization ごとに request 作成可否を変えられる
- 兼務している internal organization に対してのみ申請できる、という業務ルールも自然に表現できる

### 受け入れる制約

- `request` 自体は `internal_organization_membership` を直接参照しないため、申請時点でどの membership によって権限判定したかまでは `request` 単体では保持しない
- 申請可能条件は DB の単独 FK だけでは表現しきれず、API / 認可ロジック側の判定が必要になる

### 見直す条件

- 代理申請を導入する場合
- 「申請者の所属先」と「経費計上先 internal organization」を分離して保持する必要が生じた場合
- 申請時点の membership 自体を監査対象として固定保存したくなった場合
