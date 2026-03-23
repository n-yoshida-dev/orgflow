# 概念ER図ラフ

## 目的

経費申請アプリで扱う主要な概念と、その関係を確認する。  
この段階では、属性・主キー・外部キーは最小限または未記載とする。

## `AUDIT_LOG` について

`AUDIT_LOG` は、`request`、`approval`、`approval_policy`、`user` などに対する重要操作を記録する独立概念である。  
この概念ER図では、通常の親子関係のように線を引くと誤解を招きやすいため、関係線は描かない。

## `APPLIED_APPROVAL_ROUTE` について

`APPLIED_APPROVAL_ROUTE` は、`request` 作成時に `approval_policy` を評価して確定した承認ルートの保存結果である。  
現時点では、どの単位で保存するかが未確定であるため、この概念ER図では `REQUEST` との関係だけを描き、`APPROVAL_POLICY` や `ORGANIZATION` との直接関係は描かない。

```mermaid
---
config:
  layout: elk
---
erDiagram
  USER
  ORGANIZATION
  ROLE
  REQUEST
  APPROVAL
  APPROVAL_POLICY
  APPLIED_APPROVAL_ROUTE
  AUDIT_LOG
  ORGANIZATION_MEMBERSHIP

  USER ||--o{ REQUEST : 作成する
  USER ||--o{ APPROVAL : 判断を記録する
  USER ||--|{ ORGANIZATION_MEMBERSHIP : 所属情報を持つ
  ORGANIZATION ||--o{ ORGANIZATION_MEMBERSHIP : 所属者情報を持つ
  ORGANIZATION_MEMBERSHIP }o--|{ ROLE : ロールが付与される
  REQUEST }o--|| ORGANIZATION : 所属する
  REQUEST ||--o{ APPROVAL : 判断履歴を持つ
  REQUEST }o--|| APPLIED_APPROVAL_ROUTE : 申請時の承認ルートを参照する
  ORGANIZATION ||--o{ APPROVAL_POLICY : 承認ルールを持つ
```
