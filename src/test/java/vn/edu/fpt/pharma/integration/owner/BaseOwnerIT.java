package vn.edu.fpt.pharma.integration.owner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.entity.Role;
import vn.edu.fpt.pharma.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all Owner Integration Tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "logging.config=classpath:logback-owner.xml"
})
@Sql(
    scripts = {"/schema-test.sql", "/test-data.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
@WithMockUser(username = "owner_user", roles = "OWNER")
public abstract class BaseOwnerIT {

    private static final Logger evidenceLogger = LoggerFactory.getLogger("OWNER_EVIDENCE");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEPARATOR = "──────────────────────────────────────────────────────────────────────────────";

    @Autowired
    protected MockMvc mockMvc;

    protected RequestPostProcessor asOwner() {
        return asOwner(7L, null, "owner_user", "Chủ hệ thống");
    }

    protected RequestPostProcessor asOwner(Long userId, Long branchId, String username, String fullName) {
        Role role = new Role();
        role.setId(2L);
        role.setName("ROLE_OWNER");

        User user = new User();
        user.setId(userId);
        user.setUserName(username);
        user.setFullName(fullName);
        user.setBranchId(branchId);
        user.setRole(role);
        user.setPassword("test_password");
        user.setDeleted(false);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return SecurityMockMvcRequestPostProcessors.user(userDetails);
    }

    protected void logEvidence(String testClass, String testMethod, String httpMethod,
                               String endpoint, MvcResult result, long startTime) {
        long responseTime = System.currentTimeMillis() - startTime;
        int status = result.getResponse().getStatus();
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);

        StringBuilder sb = new StringBuilder();
        sb.append(SEPARATOR).append("\n");
        sb.append(String.format("[%s] %s.%s%n", timestamp, testClass, testMethod));
        sb.append(String.format("  → Method : %-4s %s%n", httpMethod, endpoint));
        sb.append(String.format("  → Status : %d%n", status));
        sb.append(String.format("  → Time   : %d ms", responseTime));

        evidenceLogger.info(sb.toString());
    }

    protected void logEvidenceDetailed(String testClass, String testMethod, String httpMethod,
                                       String endpoint, MvcResult result, long startTime) {
        logEvidence(testClass, testMethod, httpMethod, endpoint, result, startTime);
    }
}

