package com.yoshida.orgflow.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoshida.orgflow.entity.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
