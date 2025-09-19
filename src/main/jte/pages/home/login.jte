@param String error = null
@param String logout = null
@param org.springframework.security.web.csrf.CsrfToken csrf

@template.layouts.main(content = @`
    <div class="d-flex justify-content-center align-items-center min-vh-100">
        <div>
            <div class="text-center mb-4">
                <h2 class="fw-bold">Sign in to your account</h2>
            </div>

            @if(error != null)
                <div class="alert alert-danger" role="alert">
                    Invalid username or password
                </div>
            @endif

            <form action="/login" method="POST">
                @if(csrf != null)
                    <input type="hidden" name="${csrf.getParameterName()}" value="${csrf.getToken()}">
                @endif

                <div class="mb-3">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" name="username" id="username" required
                           class="form-control" placeholder="Enter your username">
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" name="password" id="password" required
                           class="form-control" placeholder="Enter your password">
                </div>

                <div class="d-grid">
                    <button type="submit" class="btn btn-primary">
                        Sign in
                    </button>
                </div>
            </form>
        </div>
    </div>
`)
