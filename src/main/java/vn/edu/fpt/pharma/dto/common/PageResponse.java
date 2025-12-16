package vn.edu.fpt.pharma.dto.common;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
    public static <T> PageResponse<T> of(List<T> content, long totalElements, int currentPage, int pageSize) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new PageResponse<>(content, totalElements, totalPages, currentPage, pageSize);
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    public boolean hasPrevious() {
        return currentPage > 0;
    }

    public boolean hasNext() {
        return currentPage < totalPages - 1;
    }

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
}

