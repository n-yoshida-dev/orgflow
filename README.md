# OrgFlow

OrgFlow は、B2B ワークフロー SaaS を題材にした学習用プロジェクトです。  
経費申請を中心とした **申請 → 承認 → 監査ログ記録** の業務フローを題材に、  
バックエンド設計、DB 設計、認証・認可、RBAC、監査ログ、OpenAPI、テスト、CI を
説明できる状態を目指しています。

## このリポジトリの目的

このリポジトリの目的は、単に CRUD API を作ることではありません。  
以下を自分の言葉で説明できる成果物を作ることを目的にしています。

- なぜその設計にしたか
- 他案をなぜ採らなかったか
- 業務フローをどうデータ構造と API に落としたか
- 権限、監査、状態遷移をどう整理したか
- 今は何を対象にし、何を後ろに送っているか

## 現在の状態

現在は Phase 1 の途中で、**実装よりも先に設計と判断の整理を進めている段階**です。

現時点では、主に以下を整理しています。

- 業務ルールの文章化
- 設計方針の整理
- ADR の作成
- 概念 ER 図 / 論理 ER 図の整理
- GitHub Pages によるドキュメント公開導線の整備

そのため、この README は「完成済みアプリの使用手順」ではなく、  
**現在の到達点とドキュメントの入口**として記載しています。

## 題材として OrgFlow を選んだ理由

題材は単純な ToDo ではなく、申請・承認アプリを採用しています。  
理由は、以下の論点を 1 つの題材で自然に扱いやすいからです。

- 組織単位のデータ
- ロールと認可
- 承認フロー
- 状態遷移
- 監査ログ
- README / ADR に残すべき設計判断

## 現時点の主要ドキュメント

### GitHub Pages

- 公開ドキュメント入口
  - `https://n-yoshida-dev.github.io/orgflow/`

### 設計関連

- [設計概要](./docs/design-overview.md)
- [業務ルール](./docs/domain-rules.md)

### ER 関連

- [ER メモ](./docs/er/00_notes.md)
- [概念 ER 図](./docs/er/01_concept-er.md)
- [論理 ER 図（DBML）](./docs/er/02_logical-er.dbml)
- [物理 ER 図（DBML）](./docs/er/03_physical-er.dbml)

### ADR

- [ADR 一覧](./docs/adr/)
- [ADR-001: 題材として申請・承認を採用した理由](./docs/adr/ADR-001-なぜ題材をタスク管理ではなく申請・承認にしたか.md)
- [ADR-002: 監査ログを独立した概念として扱う](./docs/adr/ADR-002-監査ログを独立した概念として扱う.md)
- [ADR-003: RBAC の最小方針](./docs/adr/ADR-003-RBAC の最小方針.md)
- [ADR-004: 業務フローの状態遷移をどう表現するか](./docs/adr/ADR-004-業務フローの状態遷移をどう表現するか.md)

## 現時点での設計上の考え方

詳細は ADR と docs に分けていますが、現時点の大きな方針は以下です。

- `request` は、ある時点の申請内容を持つ 1 件の申請版として扱う
- `approval` は、申請に対して行われた 1 回の判断履歴として扱う
- `role` は操作権限、`approval_policy` は承認ルート決定ルールとして分ける
- `audit_log` は他概念の付属情報ではなく、独立した記録として扱う
- README には全設計を書かず、判断理由は ADR、詳細設計は docs に分ける

## Phase 1 の到達目標

Phase 1 では、Java / Spring Boot による OrgFlow API v1 を対象に、  
以下を説明できる状態を目指しています。

- organizations / users / roles / requests / approvals / audit_logs の意味
- 認証済み API
- RBAC
- 申請 → 承認または差し戻し → 監査ログ記録 の一貫した業務フロー
- OpenAPI による API 契約
- 単体テスト / 統合テスト
- GitHub Actions CI

## 想定技術スタック（Phase 1）

- Java 17
- Spring Boot
- PostgreSQL
- OpenAPI
- Docker Compose
- GitHub Actions

※ 現時点では、技術選定の一部は設計・学習段階を含みます。  
※ 実装済み範囲と今後の予定は、今後 README と docs を更新して反映します。

## リポジトリ構成

```text
.
├── README.md
├── docs
│   ├── adr
│   ├── design-overview.md
│   ├── domain-rules.md
│   └── er
└── notes
```

## 今後更新する項目

以下は、実装の進行に合わせて README に追記予定です。

- セットアップ手順
- ローカル起動手順
- テスト実行手順
- API 一覧
- デモ用アカウント / Seed データ
- 実装済み機能一覧
- スクリーンショット / デモ導線

## 補足

この README は、現時点のプロジェクト入口として最小限に保っています。
設計理由の詳細は ADR、業務ルールや構造の詳細は docs を参照してください。
