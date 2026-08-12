# ADR-017: OpenAPIをAPI契約としてv1に絞って先に管理する理由

- Status: Accepted
- Date: 2026-04-19

> **決定**: Phase 1 では OpenAPI を API 契約として先に整備し、v1 は重要 endpoint に絞って管理する。

## 背景

- OrgFlow Phase 1 では、実装より先に API の責務整理と OpenAPI の骨子作成を行ってきた。
- このチャットでは、`00-operation-catalog`、`01-api-groups`、`02-path-candidates` を経て、`openapi.yaml` を API 契約として整備した。
- 一方で、全 endpoint を一気に完全化しようとすると、重要 endpoint と反復 CRUD が混ざり、学習効率が落ちる問題があった。

## 採用した案

- Phase 1 では、重要 endpoint を優先して OpenAPI を API 契約として先に整備し、v1 では必要な endpoint に絞って管理する。

## 採用理由

- 実装より先に API 契約を置くことで、後続の Spring Boot 実装やフロント接続時に責務がぶれにくくなる。
- `request`、`approval`、`tenant membership role`、`internal organization membership role` など、業務フローや RBAC に効く endpoint を優先して詰める方が、Phase 1 の説明可能性に直結する。
- 反復 CRUD まで同じ密度で先に埋めると、学習効果より作業負荷が勝ちやすい。
- v1 で今契約として持つ endpoint を先に固定し、残りは実装進行に合わせて戻す方が合理的である。

## 不採用案

- 案A: 実装先行で後から OpenAPI を整備する
  実装都合で API がぶれやすく、React 接続や Go 置換時の説明可能性が落ちるため採らなかった。

- 案B: 全 endpoint を同じ密度で先に OpenAPI 化する
  反復 CRUD の作業負荷が高く、Phase 1 で本来重視すべき業務フロー、認可、監査ログの論点が薄くなるため採らなかった。

## 受け入れる制約

- v1 時点では、反復 CRUD の一部を後回しにする。
- OpenAPI は最終的に育てる前提であり、最初から全 endpoint を完成版にはしない。
- 一覧 API の query 絞り込みや 200 / 204 の使い分けなど、一部の設計判断は実装段階で微調整が残る。

## 見直す条件

- Spring Boot 実装を進める中で、未記載 endpoint が主要業務フローに影響し始めたとき。
- React 接続や Go 置換の段階で、契約不足により実装が詰まるとき。
- OpenAPI から型生成や自動テスト連携を強める段階に進むとき。
