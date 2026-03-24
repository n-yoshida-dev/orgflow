# ADR-007: requestの版管理方式

## Context

申請（request）は、差し戻し後に内容を修正して再申請される。
このとき、過去の申請内容と修正後の申請内容の両方を保持し、
履歴として説明可能にする必要がある。

## Decision

- requestは「申請版」を表す単位とする
- 差し戻し後の再申請では既存レコードを更新せず、新しいrequestを作成する
- 同一申請の系列は `request_series_id` で表現する
- 各版は `version_no` で識別する
- `(request_series_id, version_no)` に一意制約を設ける
- 参照の簡便性のため `request_id` を主キーとして別に持つ

## Consequences

### Positive

- 申請内容の変更履歴を完全に保持できる
- 差し戻し前後の差分を明確に説明できる
- 監査ログとの整合性が取りやすい

### Negative

- 同一申請を取得する際に系列単位の取得が必要になる
- レコード数が増加する

## Alternatives Considered

### 1. requestを更新し続ける

- 不採用理由
  - 過去の申請内容が失われる
  - 差し戻し履歴の説明が困難になる

### 2. versionテーブルを別に持つ

- 不採用理由
  - Phase1では構造が複雑になりすぎる
