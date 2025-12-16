@import vn.edu.fpt.pharma.dto.common.PageResponse

@param PageResponse<?> pagination
@param String baseUrl

@if(pagination.totalElements() > pagination.pageSize())
<div class="pagination-container">
    <div class="pagination-info">
        Hiển thị ${pagination.currentPage() * pagination.pageSize() + 1} - ${Math.min((pagination.currentPage() + 1) * pagination.pageSize(), pagination.totalElements())} / ${pagination.totalElements()} bản ghi
    </div>

    <div class="pagination-controls">
        <!-- Previous Button -->
        @if(pagination.hasPrevious())
            <button class="pagination-btn" data-page="${pagination.currentPage() - 1}">
                <span>‹</span>
            </button>
        @else
            <button class="pagination-btn disabled" disabled>
                <span>‹</span>
            </button>
        @endif

        <!-- Page Numbers -->
        @if(pagination.totalPages() <= 7)
            @for(int i = 0; i < pagination.totalPages(); i++)
                @if(i == pagination.currentPage())
                    <button class="pagination-btn active" data-page="${i}">
                        ${i + 1}
                    </button>
                @else
                    <button class="pagination-btn" data-page="${i}">
                        ${i + 1}
                    </button>
                @endif
            @endfor
        @else
            @if(pagination.currentPage() < 4)
                @for(int i = 0; i < 5; i++)
                    @if(i == pagination.currentPage())
                        <button class="pagination-btn active" data-page="${i}">
                            ${i + 1}
                        </button>
                    @else
                        <button class="pagination-btn" data-page="${i}">
                            ${i + 1}
                        </button>
                    @endif
                @endfor
                <span class="pagination-ellipsis">...</span>
                <button class="pagination-btn" data-page="${pagination.totalPages() - 1}">
                    ${pagination.totalPages()}
                </button>
            @elseif(pagination.currentPage() >= pagination.totalPages() - 4)
                <button class="pagination-btn" data-page="0">1</button>
                <span class="pagination-ellipsis">...</span>
                @for(int i = pagination.totalPages() - 5; i < pagination.totalPages(); i++)
                    @if(i == pagination.currentPage())
                        <button class="pagination-btn active" data-page="${i}">
                            ${i + 1}
                        </button>
                    @else
                        <button class="pagination-btn" data-page="${i}">
                            ${i + 1}
                        </button>
                    @endif
                @endfor
            @else
                <button class="pagination-btn" data-page="0">1</button>
                <span class="pagination-ellipsis">...</span>
                @for(int i = pagination.currentPage() - 1; i <= pagination.currentPage() + 1; i++)
                    @if(i == pagination.currentPage())
                        <button class="pagination-btn active" data-page="${i}">
                            ${i + 1}
                        </button>
                    @else
                        <button class="pagination-btn" data-page="${i}">
                            ${i + 1}
                        </button>
                    @endif
                @endfor
                <span class="pagination-ellipsis">...</span>
                <button class="pagination-btn" data-page="${pagination.totalPages() - 1}">
                    ${pagination.totalPages()}
                </button>
            @endif
        @endif

        <!-- Next Button -->
        @if(pagination.hasNext())
            <button class="pagination-btn" data-page="${pagination.currentPage() + 1}">
                <span>›</span>
            </button>
        @else
            <button class="pagination-btn disabled" disabled>
                <span>›</span>
            </button>
        @endif
    </div>

    <div class="pagination-size">
        <label for="page-size-select">Hiển thị:</label>
        <select id="page-size-select" class="page-size-select">
            @if(pagination.pageSize() == 10)
                <option value="10" selected>10</option>
            @else
                <option value="10">10</option>
            @endif
            @if(pagination.pageSize() == 20)
                <option value="20" selected>20</option>
            @else
                <option value="20">20</option>
            @endif
            @if(pagination.pageSize() == 50)
                <option value="50" selected>50</option>
            @else
                <option value="50">50</option>
            @endif
        </select>
    </div>
</div>
@endif

