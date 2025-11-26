package vn.edu.fpt.pharma.dto;

import java.util.List;

public class PagingResponse {
    private long totalItems;
    private long totalPages;
    private Object data;

    public PagingResponse(long totalItems, long totalPages, Object data) {
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.data = data;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

