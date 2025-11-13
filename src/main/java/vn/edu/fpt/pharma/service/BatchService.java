package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.entity.Batch;

import java.util.List;

public interface BatchService extends BaseService<Batch, Long> {
        Batch create(Batch batch);
        Batch findById(Long id);
        List<Batch> findAll();
}
