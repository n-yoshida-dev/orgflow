# ADR-005: request の申請者・組織の参照方式

- Status: Accepted
- Date: 2026-03-24

## 背景

- request は「誰が」「どの組織に対して」提出した申請かを保持する必要がある
- 参照先として organization_membership_id を1本持つ案と、user_id と organization_id を直接持つ案があった
- 決めないと、申請後に申請者が異動した場合に過去 request の帰属が追えなくなるリスクがある

## 採用した案

- request に user_id と organization_id を直接持つ

## 採用理由

- organization_membership は「現在の所属関係」を表すため、異動・削除によって変化しうる
- request が保持すべきは申請時点の帰属という不変の事実であり、現在の所属関係への参照ではない
- user_id と organization_id を直接持つことで、申請後の組織変更に関わらず申請時点の帰属を追跡できる

## 不採用案

- organization_membership_id を1本持つ案: 所属関係が変更・削除された場合に参照が壊れるか意味が変わる

## 受け入れる制約

- request のカラムが1本増える
- 「申請時点の所属」と「現在の所属」が乖離していても検知できない

## 見直す条件

- 代理申請を導入する場合、「操作した人」と「申請の帰属組織」を分けて持つ必要が生じる可能性がある
