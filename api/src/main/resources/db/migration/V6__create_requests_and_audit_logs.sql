CREATE TYPE request_status AS ENUM (
  'draft',
  'in_progress',
  'approved',
  'returned',
  'rejected',
  'cancelled'
);

CREATE TABLE requests (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  tenant_id uuid NOT NULL,
  request_series_id uuid NOT NULL,
  version_no integer NOT NULL,
  applicant_user_id uuid NOT NULL,
  internal_organization_id uuid NOT NULL,
  status request_status NOT NULL,
  current_applied_approval_route_step_id uuid,
  request_type text NOT NULL,
  title varchar(100) NOT NULL,
  description text,
  amount integer NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now (),
  updated_at timestamptz NOT NULL DEFAULT now (),
  CONSTRAINT fk_requests_tenants FOREIGN KEY (tenant_id) REFERENCES tenants (id),
  CONSTRAINT check_version_no CHECK (version_no >= 1),
  CONSTRAINT check_request_type CHECK (
    request_type IN (
      'transportation_expenses',
      'travel_expenses',
      'equipment_purchase_expenses'
    )
  ),
  CONSTRAINT check_amount CHECK (amount >= 0),
  CONSTRAINT fk_requests_internal_organizations FOREIGN KEY (internal_organization_id, tenant_id) REFERENCES internal_organizations (id, tenant_id),
  CONSTRAINT fk_requests_tenant_memberships FOREIGN KEY (tenant_id, applicant_user_id) REFERENCES tenant_memberships (tenant_id, user_id),
  CONSTRAINT unique_tenant_request_series_version UNIQUE (tenant_id, request_series_id, version_no)
);

CREATE TYPE operation_type AS ENUM (
  'create',
  'update',
  'delete',
  'submit',
  'approve',
  'return',
  'reject',
  'cancel',
  'login',
  'logout'
);

CREATE TABLE audit_logs (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  actor_tenant_id uuid NOT NULL,
  actor_user_id uuid NOT NULL,
  actor_user_display_name text NOT NULL,
  actor_internal_organization_id uuid,
  actor_internal_organization_name text,
  actor_user_mail_address text NOT NULL,
  target_type varchar(100) NOT NULL,
  target_id uuid NOT NULL,
  operation_type operation_type NOT NULL,
  occurred_at timestamptz NOT NULL,
  CONSTRAINT fk_audit_logs_tenants FOREIGN KEY (actor_tenant_id) REFERENCES tenants (id),
  CONSTRAINT fk_audit_logs_users FOREIGN KEY (actor_user_id) REFERENCES users (id),
  CONSTRAINT fk_audit_logs_internal_organizations FOREIGN KEY (actor_internal_organization_id, actor_tenant_id) REFERENCES internal_organizations (id, tenant_id)
);