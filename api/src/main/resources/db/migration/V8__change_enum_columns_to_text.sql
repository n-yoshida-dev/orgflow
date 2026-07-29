ALTER TABLE requests ALTER COLUMN status TYPE text USING status::text;
ALTER TABLE requests ADD CONSTRAINT requests_status_check CHECK (status IN ('draft', 'in_progress', 'approved', 'returned', 'rejected', 'cancelled'));
ALTER TABLE audit_logs ALTER COLUMN operation_type TYPE text USING operation_type::text;
ALTER TABLE audit_logs ADD CONSTRAINT audit_logs_operation_type_check CHECK (operation_type IN ('create', 'update', 'delete', 'submit', 'approve', 'return', 'reject', 'cancel', 'login', 'logout'));
DROP TYPE request_status;
DROP TYPE operation_type;