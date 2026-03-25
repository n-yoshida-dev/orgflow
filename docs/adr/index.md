# ADR Index

このディレクトリは、OrgFlow の設計判断を記録した ADR（Architecture Decision Record）の一覧です。
各 ADR には、採用した案・不採用の案・見直す条件を残しています。

## 一覧

| #                                                                       | タイトル                                                    | ステータス | 日付       |
| ----------------------------------------------------------------------- | ----------------------------------------------------------- | ---------- | ---------- |
| [ADR-001](./ADR-001-なぜ題材をタスク管理ではなく申請・承認にしたか.md)  | 題材として申請・承認アプリを採用する                        | Accepted   | 2026-03-19 |
| [ADR-002](./ADR-002-監査ログを独立した概念として扱う.md)                | 監査ログを独立した概念として扱う                            | Accepted   | 2026-03-19 |
| [ADR-003](./ADR-003-RBACの最小方針.md)                                  | RBAC の最小方針                                             | Accepted   | 2026-03-19 |
| [ADR-004](./ADR-004-業務フローの状態遷移をどう表現するか.md)            | 業務フローの状態遷移は明示的な状態モデルで表現する          | Accepted   | 2026-03-19 |
| [ADR-005](./ADR-005-requestの申請者・組織の参照方式.md)                 | request の申請者・組織の参照方式                            | Accepted   | 2026-03-24 |
| [ADR-006](./ADR-006-applied_approval_routeの保存構造.md)                | applied_approval_route の保存構造（3層構造）                | Accepted   | 2026-03-24 |
| [ADR-007](./ADR-007-requestの版管理方式.md)                             | request の版管理方式（request_series_id + version_no）      | Accepted   | 2026-03-24 |
| [ADR-008](./ADR-008-承認ルートの適用結果をrequestに紐づけて保持する.md) | 承認ルートの適用結果を request に紐づけて保持する           | Accepted   | 2026-03-24 |
| [ADR-009](./ADR-009-applied_approval_routeを親子構造で保持する.md)      | applied_approval_route を親子構造で保持する                 | Accepted   | 2026-03-24 |
| [ADR-010](./ADR-010-同一承認段に複数承認者がいる場合のルール.md)        | 同一承認段に複数承認者がいる場合のルール（いずれか1人承認） | Accepted   | 2026-03-24 |
| [ADR-011](./ADR-011-audit_logの操作対象参照方式.md)                     | audit_log の操作対象参照方式（target_type + target_id）     | Accepted   | 2026-03-25 |
| [ADR-012](./ADR-012-approval_policyとapproval_routeの参照方向.md)       | approval_policy と approval_route の参照方向                | Accepted   | 2026-03-25 |

## テンプレート

新しい ADR を作成するときは [ひな形](./ひな形-ADR-xxx-___.md) を使ってください。
