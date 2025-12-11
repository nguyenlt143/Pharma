package vn.edu.fpt.pharma.integration.manager;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mock a Manager user with CustomUserDetails for integration tests.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockManagerUserSecurityContextFactory.class)
public @interface WithMockManagerUser {

    /**
     * The username of the manager user.
     */
    String username() default "manager_user";

    /**
     * The full name of the manager user.
     */
    String fullName() default "Quản lý Chi nhánh";

    /**
     * The user ID of the manager.
     */
    long userId() default 2L;

    /**
     * The branch ID the manager belongs to.
     */
    long branchId() default 2L;

    /**
     * The role ID (3 = MANAGER).
     */
    long roleId() default 3L;

    /**
     * The role name (without ROLE_ prefix).
     */
    String role() default "MANAGER";
}

