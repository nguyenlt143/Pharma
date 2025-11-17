package vn.edu.fpt.pharma.service;

import vn.edu.fpt.pharma.base.BaseService;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.category.CategoryRequest;
import vn.edu.fpt.pharma.dto.category.CategoryResponse;
import vn.edu.fpt.pharma.entity.Category;

public interface CategoryService extends BaseService<Category, Long> {
    DataTableResponse<CategoryResponse> getCategories(DataTableRequest request);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
}
