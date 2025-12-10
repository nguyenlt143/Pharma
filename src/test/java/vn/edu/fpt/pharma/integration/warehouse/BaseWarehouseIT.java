package vn.edu.fpt.pharma.integration.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all Warehouse Integration Tests.
 * <p>
 * Features:
 * - Uses H2 in-memory database with MySQL mode
 * - Auto-configures MockMvc for web layer testing
 * - Pre-configured with WAREHOUSE role for security
 * - Resets test data before each test method
 * - Logs test evidence to separate file for warehouse tests
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "logging.config=classpath:logback-warehouse.xml"
})
@Sql(
    scripts = {"/schema-test.sql", "/test-data.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
@WithMockUser(username = "warehouse_user", roles = "WAREHOUSE")
public abstract class BaseWarehouseIT {

    private static final Logger evidenceLogger = LoggerFactory.getLogger("WAREHOUSE_EVIDENCE");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEPARATOR = "──────────────────────────────────────────────────────────────────────────────";

    @Autowired
    protected MockMvc mockMvc;

    /**
     * Log test evidence to file with beautiful format.
     *
     * @param testClass    Name of the test class
     * @param testMethod   Name of the test method
     * @param httpMethod   HTTP method (GET, POST, etc.)
     * @param endpoint     The endpoint being tested
     * @param result       MvcResult from the test
     * @param startTime    Start time of the test for calculating response time
     */
    protected void logEvidence(String testClass, String testMethod, String httpMethod,
                               String endpoint, MvcResult result, long startTime) {
        long responseTime = System.currentTimeMillis() - startTime;
        int status = result.getResponse().getStatus();
        String statusText = getStatusText(status);
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);

        StringBuilder sb = new StringBuilder();
        sb.append(SEPARATOR).append("\n");
        sb.append(String.format("[%s] %s.%s%n", timestamp, testClass, testMethod));
        sb.append(String.format("  → Method : %-4s %s%n", httpMethod, endpoint));
        sb.append(String.format("  → Status : %d %s%n", status, statusText));
        sb.append(String.format("  → Time   : %d ms", responseTime));

        evidenceLogger.info(sb.toString());
    }

    /**
     * Log test evidence with response details (for debugging).
     */
    protected void logEvidenceDetailed(String testClass, String testMethod, String httpMethod,
                                       String endpoint, MvcResult result, long startTime) {
        long responseTime = System.currentTimeMillis() - startTime;
        int status = result.getResponse().getStatus();
        String statusText = getStatusText(status);
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);

        StringBuilder sb = new StringBuilder();
        sb.append(SEPARATOR).append("\n");
        sb.append(String.format("[%s] %s.%s%n", timestamp, testClass, testMethod));
        sb.append(String.format("  → Method : %-4s %s%n", httpMethod, endpoint));
        sb.append(String.format("  → Status : %d %s%n", status, statusText));
        sb.append(String.format("  → Time   : %d ms", responseTime));

        // Add response content if available
        try {
            String content = result.getResponse().getContentAsString();
            if (content != null && !content.isEmpty() && content.length() < 1000) {
                sb.append("\n  → Response:\n");
                // Pretty format JSON response
                String formattedContent = formatJsonResponse(content);
                sb.append(formattedContent);
            }
        } catch (Exception e) {
            // Ignore encoding issues
        }

        evidenceLogger.info(sb.toString());
    }

    /**
     * Format JSON response with indentation for readability.
     */
    private String formatJsonResponse(String json) {
        if (json == null || json.isEmpty()) {
            return "";
        }

        StringBuilder formatted = new StringBuilder();
        int indent = 0;
        boolean inString = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
                formatted.append(c);
            } else if (!inString) {
                switch (c) {
                    case '{', '[' -> {
                        formatted.append(c).append("\n");
                        indent++;
                        formatted.append("      ").append("  ".repeat(indent));
                    }
                    case '}', ']' -> {
                        formatted.append("\n");
                        indent--;
                        formatted.append("      ").append("  ".repeat(indent)).append(c);
                    }
                    case ',' -> formatted.append(c).append("\n").append("      ").append("  ".repeat(indent));
                    case ':' -> formatted.append(": ");
                    case ' ', '\n', '\r', '\t' -> { /* skip whitespace */ }
                    default -> formatted.append(c);
                }
            } else {
                formatted.append(c);
            }
        }

        return formatted.toString();
    }

    /**
     * Log separator for test class start.
     */
    protected void logTestClassStart(String testClassName) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("═".repeat(78)).append("\n");
        sb.append(String.format("  WAREHOUSE INTEGRATION TEST - %s%n", testClassName));
        sb.append(String.format("  Started at: %s%n", timestamp));
        sb.append("═".repeat(78));
        evidenceLogger.info(sb.toString());
    }

    /**
     * Log separator for test class end.
     */
    protected void logTestClassEnd(String testClassName) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        StringBuilder sb = new StringBuilder();
        sb.append(SEPARATOR).append("\n");
        sb.append(String.format("  END: %s - %s%n", testClassName, timestamp));
        sb.append("═".repeat(78)).append("\n");
        evidenceLogger.info(sb.toString());
    }

    /**
     * Convert HTTP status code to readable text.
     */
    private String getStatusText(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 302 -> "Found (Redirect)";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> String.valueOf(status);
        };
    }

    /**
     * Get current timestamp for logging.
     */
    protected String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
}

