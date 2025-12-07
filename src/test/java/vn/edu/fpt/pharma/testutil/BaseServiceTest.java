package vn.edu.fpt.pharma.testutil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.repository.*;
import vn.edu.fpt.pharma.service.AuditService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for Service layer unit tests
 * Provides common mocks and utilities
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected InvoiceRepository invoiceRepository;

    @Mock
    protected InvoiceDetailRepository invoiceDetailRepository;

    @Mock
    protected InventoryRepository inventoryRepository;

    @Mock
    protected ShiftWorkRepository shiftWorkRepository;

    @Mock
    protected ShiftRepository shiftRepository;

    @Mock
    protected MedicineVariantRepository medicineVariantRepository;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @Mock
    protected AuditService auditService;

    @Mock
    protected RoleRepository roleRepository;

    @Mock
    protected ShiftAssignmentRepository shiftAssignmentRepository;

    @Mock
    protected BranchRepository branchRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Assert that audit fields are properly set
     */
    protected void assertAuditFields(BaseEntity entity) {
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedBy()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getUpdatedBy()).isNotNull();
    }
}

