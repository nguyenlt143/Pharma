@import gg.jte.Content

@param Content content

@template.layouts.main(content = @`
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">
            <a class="navbar-brand" href="/dashboard">Pharma System</a>
            <div>
                <div class="nav-item dropdown">
                    <a id="userDropdown" class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Hello, ...
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="#">Change Password</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="/logout">Logout</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </nav>
    <main class="container-fluid">
        ${content}
    </main>

    <script>
        $(document).ready(function () {
            $.ajax({
                url: "/me",
                method: "GET",
                dataType: "json",
                success: function (user) {
                    if (user && user.fullName) {
                        $("#userDropdown").text("Hello, " + user.fullName);
                    }
                },
                error: function (xhr, status, error) {
                    console.error("Error fetching user:", error);
                }
            });
        });
    </script>
`)
