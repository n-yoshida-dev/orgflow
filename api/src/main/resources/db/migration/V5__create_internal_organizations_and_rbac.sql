ALTER TABLE tenant_memberships ADD CONSTRAINT unique_tenant_memberships_id_tenant UNIQUE (id, tenant_id);

CREATE TABLE internal_organizations (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  tenant_id uuid NOT NULL,
  internal_organization_name text NOT NULL,
  CONSTRAINT fk_internal_organizations_tenants FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
  CONSTRAINT unique_id_tenant UNIQUE (id, tenant_id),
  CONSTRAINT unique_tenant_internal_organization_name UNIQUE (tenant_id, internal_organization_name)
);

CREATE TABLE internal_organization_memberships (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  tenant_id uuid NOT NULL,
  tenant_membership_id uuid NOT NULL,
  internal_organization_id uuid NOT NULL,
  CONSTRAINT fk_internal_organization_memberships_tenants FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
  CONSTRAINT fk_internal_organization_memberships_tenant_memberships FOREIGN KEY (tenant_membership_id, tenant_id) REFERENCES tenant_memberships (id, tenant_id) ON DELETE CASCADE,
  CONSTRAINT fk_internal_organization_memberships_internal_organizations FOREIGN KEY (internal_organization_id, tenant_id) REFERENCES internal_organizations (id, tenant_id) ON DELETE CASCADE,
  CONSTRAINT unique_tenant_membership_id_internal_organization_id UNIQUE (tenant_membership_id, internal_organization_id)
);

CREATE TABLE internal_organization_roles (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  role_name text NOT NULL,
  CONSTRAINT unique_role_name UNIQUE (role_name)
);

CREATE TABLE internal_organization_membership_roles (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  internal_organization_membership_id uuid NOT NULL,
  role_id uuid NOT NULL,
  CONSTRAINT fk_iomr_membership FOREIGN KEY (internal_organization_membership_id) REFERENCES internal_organization_memberships (id) ON DELETE CASCADE,
  CONSTRAINT fk_iomr_role FOREIGN KEY (role_id) REFERENCES internal_organization_roles (id) ON DELETE CASCADE,
  CONSTRAINT unique_role_id_internal_organization_membership_id UNIQUE (role_id, internal_organization_membership_id)
);