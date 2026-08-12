INSERT INTO
  internal_organizations (tenant_id, internal_organization_name)
SELECT
  t.id,
  '経理部'
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
  AND io.internal_organization_name = '経理部';

INSERT INTO
  internal_organization_roles (role_name)
VALUES
  ('requester');

INSERT INTO
  internal_organization_membership_roles (internal_organization_membership_id, role_id)
SELECT
  iom.id,
  ior.id
FROM
  internal_organization_memberships iom
  JOIN internal_organizations io ON io.id = iom.internal_organization_id
  AND io.tenant_id = iom.tenant_id
  JOIN tenant_memberships tm ON tm.id = iom.tenant_membership_id
  AND tm.tenant_id = iom.tenant_id
  JOIN users u ON u.id = tm.user_id
  JOIN tenants t ON t.id = iom.tenant_id
  JOIN internal_organization_roles ior ON ior.role_name = 'requester'
WHERE
  u.login_id = 'test_taro'
  AND t.name = '正常性確認用テナント'
  AND io.internal_organization_name = '経理部';