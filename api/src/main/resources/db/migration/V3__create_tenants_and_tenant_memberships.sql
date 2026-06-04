CREATE TABLE tenants (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  name varchar(100) NOT NULL UNIQUE
);

CREATE TABLE tenant_memberships (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  tenant_id uuid NOT NULL,
  user_id uuid NOT NULL,
  CONSTRAINT fk_tenant_memberships_tenants FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
  CONSTRAINT fk_tenant_memberships_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT unique_tenant_user UNIQUE (tenant_id, user_id)
);
