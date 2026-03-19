# ADR-003: roleとapproval policyの関係性

## ステータス
Accepted

## 日付
2026-03-17

## コンテキスト（背景・課題）
- roleとapproval policyの役割が曖昧であり、どのような責務分解とするか考える必要があった。

## 決定（Decision）
- roleとapproval policyは分離する方針とした。
- roleはあくまで承認権限の区分であり、approval policyは承認ルートや承認段数を決定するルールとする。

## 根拠（Rationale）
- 用語の定義として、役割とルールは分ける方が設計上の誤解が発生しないと判断した。

## 検討した選択肢（Options）
単に私がroleとapproval policyの区別が曖昧だっただけのため、別案は特にない

## 結果（Consequences）
### 良い影響
- 言葉の定義による誤解が発生しない。

### 悪い影響
- 特になし

## 実装方針（Implementation）
- 特になし

## 参考（References）
- 特になし