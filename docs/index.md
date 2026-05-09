# OrgFlow Docs

OrgFlow の設計・判断記録をまとめたドキュメントです。
README はリポジトリ全体の入口、この `docs/` は設計の正本を置く場所として扱います。

## まず読む順序

1. [設計概要](./design-overview.md)
2. [業務ルール](./domain-rules.md)
3. [ER 設計メモ](./er/00_notes.md)
4. [概念 ER 図](./er/01_concept-er.md)
5. [論理 ER 図（DBML）](./er/02_logical-er.dbml)
6. [物理 ER 図（DBML）](./er/03_physical-er.dbml)
7. [ADR 一覧](./adr/index.md)

## 設計

- [設計概要](./design-overview.md)
  概念の責務分担と、どの概念を分けて扱うかを説明する
- [業務ルール](./domain-rules.md)
  用語、状態遷移、権限境界、業務上の固定ルールを説明する

## ER

- [ER 設計メモ](./er/00_notes.md)
  ER 図を読むための補足事項と、未確定論点だけを置く
- [概念 ER 図](./er/01_concept-er.md)
  主要概念とその関係を表す
- [論理 ER 図（DBML）](./er/02_logical-er.dbml)
  主キー、外部キー、一意制約、参照の向きを含む論理設計
- [物理 ER 図（DBML）](./er/03_physical-er.dbml)
  型、制約、インデックスを含む物理設計

## ADR

- [ADR 一覧](./adr/index.md)
  設計判断の一覧と各 ADR への入口
- [ADR テンプレート](./adr/ひな形-ADR-xxx-___.md)

## このディレクトリに置かないもの

次のものは `docs/` の正本に置かない方針とします。

- AI ツール名を冠した一時メモ
- 試行錯誤そのままの思考ログ
- すでに別文書へ反映済みの重複メモ
- 図と同内容の重複ファイル
