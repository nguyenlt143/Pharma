package vn.edu.fpt.pharma.dto;

public class PagingRequest {
    private int page = 0;
    private int size = 10;

    public PagingRequest() {
    }

    public PagingRequest(int page, int size) {
        if (page >= 0) {
            this.page = page;
        }
        if (size > 0) {
            this.size = size;
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if (page >= 0) {
            this.page = page;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size > 0) {
            this.size = size;
        }
    }
}

