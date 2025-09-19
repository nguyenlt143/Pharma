@import vn.edu.fpt.pharma.constant.UserRole
@param UserRole role

@template.layouts.dashboard(content = @`
    <div class="mt-4">
        @if (role == UserRole.SYSTEM_ADMIN)
            <div class="row row-cols-1 row-cols-md-3 g-4  justify-content-center">
                <div class="col">
                    <div class="card h-100">
                        <div class="card-body text-center">
                            <h5 class="card-title fw-bold mb-4">
                                Master Data Management
                            </h5>
                            <div class="d-grid gap-2">
                                <a href="/dashboard/stores"
                                   class="btn btn-outline-secondary w-100 py-3">
                                    Stores Management
                                </a>
                                <a href="/dashboard/users"
                                   class="btn btn-outline-secondary w-100 py-3">
                                    Users Management
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        @endif
    </div>
`)