-- requester ロールを意図的に付与しない（403 確認用）
INSERT INTO
  internal_organizations (tenant_id, internal_organization_name)
SELECT
  t.id,
  '403確認用組織'
FROM
  tenants t
WHERE
  t.name = '正常性確認用テナント';

INSERT INTO
  internal_organization_memberships (
    tenant_id,
    tenant_membership_id,
    internal_organization_id
  )
SELECT
  tm.tenant_id,
  tm.id,
  io.id
FROM
  tenant_memberships tm
  JOIN users u ON u.id = tm.user_id
  JOIN tenants t ON t.id = tm.tenant_id
  JOIN internal_organizations io ON io.tenant_id = tm.tenant_id
WHERE
  u.login_id = 'test_taro'
  AND t.name = '正常性確認用テナント'
  AND io.internal_organization_name = '403確認用組織';