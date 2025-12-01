// filepath: c:\Users\duy\Desktop\PRM\Pharma\src\test\java\vn\edu\fpt\pharma\testutil\BaseDataJpaTest.java
package vn.edu.fpt.pharma.testutil;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import vn.edu.fpt.pharma.config.JpaAuditingConfig;

/**
 * Base class for repository tests with H2 database.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
public abstract class BaseDataJpaTest {
}
