package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseEntity;

import java.util.List;

public interface AuditService {
   <T extends BaseEntity<?>> List<T> addAuditInfo(List<T> entities);
}
