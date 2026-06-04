INSERT INTO
  tenants (name)
VALUES
  ('正常性確認用テナント'),
  ('404確認用テナント');

INSERT INTO
  tenant_memberships (tenant_id, user_id)
SELECT
  t.id,
  u.id
FROM
  tenants t
  JOIN users u ON u.login_id = 'test_taro'
WHERE
  t.name = '正常性確認用テナント';