# Plan: Integration Test Strategy cho Pharmacist Role và Profile Feature

## Mục tiêu

Tạo comprehensive integration tests cho role Pharmacist và chức năng Profile, kiểm tra tích hợp giữa các layer (Controller → Service → Repository → Database) với real dependencies. Đảm bảo toàn bộ flow hoạt động chính xác trong môi trường gần giống production.

---

## Phần 1: Phân biệt Integration Test vs Unit Test

### Unit Test (đã có kế hoạch riêng)
- Mock tất cả dependencies
- Test từng class độc lập
- Fast execution (<1s per test)
- Không cần database, external services

### Integration Test (kế hoạch này)
- Sử dụng real dependencies (database, Spring context)
- Test nhiều layer cùng lúc
- Slower execution (2-10s per test)
- Cần test database (H2, TestContainers)
- Verify end-to-end behavior

---

## Phần 2: Scope Integration Tests

### 2.1 Controller Integration Tests (Priority: CAO)

**Test toàn bộ HTTP request → Controller → Service → Repository → Database**

#### PharmacistController.updateProfile()
- Full request/response cycle
- Database persistence verification
- Transaction behavior
- Security/authentication
- Validation at HTTP layer

#### PharmacistController.createInvoice()
- POS workflow end-to-end
- Stock reservation in database
- Invoice creation + detail persistence
- Transaction rollback scenarios

#### PharmacistController.getInvoices()
- Data retrieval from database
- Filtering and pagination
- DTO mapping verification

### 2.2 Service Integration Tests (Priority: CAO)

**Test Service → Repository với real database**

#### UserService
- `updateProfile()` - verify database persistence
- Password encoding in real scenario
- Duplicate checking with real queries
- Transaction rollback verification

#### InvoiceService
- `createInvoice()` - multi-table operations
- Stock update + Invoice insert trong cùng transaction
- Rollback when error occurs
- Concurrent access scenarios

#### InventoryService
- `reserveStock()` - update database
- Concurrent reservation handling
- Optimistic locking behavior

### 2.3 Repository Integration Tests (Priority: TRUNG BÌNH)

**Test custom queries và native queries**

#### UserRepository
- `findByEmailIgnoreCase()`
- `existsByEmailIgnoreCaseAndIdNot()`
- `existsByPhoneNumberAndIdNot()`

#### InvoiceRepository
- `findRevenueShiftByUser()` - complex query
- `findRevenueDetailsByShiftAndUser()` - joins

#### InventoryRepository
- Custom queries with joins
- Aggregate functions

---

## Phần 3: Test Infrastructure cho Integration Tests

### 3.1 Test Database Setup

**Option A: H2 In-Memory Database** (Recommended for speed)

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryIntegrationTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    // Tests here
}
```

**application-test.properties:**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Option B: TestContainers với MySQL** (Recommended for accuracy)

```java
@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("pharma_test")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Tests here
}
```

### 3.2 Base Integration Test Classes

```java
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    
    @Autowired
    protected TestRestTemplate restTemplate;
    
    @Autowired
    protected UserRepository userRepository;
    
    @Autowired
    protected InvoiceRepository invoiceRepository;
    
    @Autowired
    protected InventoryRepository inventoryRepository;
    
    @BeforeEach
    void setUpBase() {
        // Clean database
        cleanDatabase();
    }
    
    protected void cleanDatabase() {
        invoiceRepository.deleteAll();
        inventoryRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    protected User createTestUser(String email, String role) {
        User user = new User();
        user.setEmail(email);
        user.setFullName("Test User");
        user.setPassword("encodedPassword");
        // Set other fields
        return userRepository.save(user);
    }
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public abstract class BaseControllerIntegrationTest extends BaseIntegrationTest {
    
    @LocalServerPort
    protected int port;
    
    protected String createURL(String uri) {
        return "http://localhost:" + port + uri;
    }
    
    protected HttpHeaders createAuthHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }
}
```

### 3.3 Test Data Setup với SQL Scripts

**test-data.sql:**
```sql
-- Insert test roles
INSERT INTO roles (id, name) VALUES (1, 'PHARMACIST');
INSERT INTO roles (id, name) VALUES (2, 'MANAGER');

-- Insert test branch
INSERT INTO branches (id, name, address) VALUES (1, 'Hà Nội', '123 Test Street');

-- Insert test users
INSERT INTO users (id, email, full_name, password, role_id, branch_id, deleted) 
VALUES (1, 'pharmacist@test.com', 'Test Pharmacist', 'encoded', 1, 1, 0);

-- Insert test medicines
-- ...
```

**Usage:**
```java
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clean-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MyIntegrationTest {
    // Tests
}
```

---

## Phần 4: Chi tiết Test Cases Integration

### 4.1 UserService Integration Tests

**Target:** 10-12 test cases
**Duration:** ~5-10s per test

#### Test Full Update Profile Flow

```java
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void updateProfile_withValidData_shouldPersistToDatabase() {
        // Arrange
        User existingUser = createTestUser();
        Long userId = existingUser.getId();
        
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Updated Name")
            .email("updated@test.com")
            .phone("0987654321")
            .build();
        
        // Act
        userService.updateProfile(userId, request);
        
        // Assert
        entityManager.flush();
        entityManager.clear();
        
        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertThat(updatedUser.getFullName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
        assertThat(updatedUser.getPhoneNumber()).isEqualTo("0987654321");
    }
    
    @Test
    void updateProfile_withPasswordChange_shouldEncodeAndPersist() {
        // Arrange
        String oldPassword = "oldPass123";
        String newPassword = "newPass456";
        
        User existingUser = createTestUser();
        existingUser.setPassword(passwordEncoder.encode(oldPassword));
        existingUser = userRepository.save(existingUser);
        Long userId = existingUser.getId();
        
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("Test User")
            .email(existingUser.getEmail())
            .currentPassword(oldPassword)
            .password(newPassword)
            .confirmPassword(newPassword)
            .build();
        
        // Act
        userService.updateProfile(userId, request);
        
        // Assert
        entityManager.flush();
        entityManager.clear();
        
        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword()))
            .isTrue();
        assertThat(passwordEncoder.matches(oldPassword, updatedUser.getPassword()))
            .isFalse();
    }
    
    @Test
    void updateProfile_withDuplicateEmail_shouldThrowExceptionAndRollback() {
        // Arrange
        User user1 = createTestUser("user1@test.com", "User 1");
        User user2 = createTestUser("user2@test.com", "User 2");
        
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("User 2")
            .email("user1@test.com") // Duplicate
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(user2.getId(), request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Email đã được sử dụng");
        
        // Verify no changes in database
        entityManager.clear();
        User unchangedUser = userRepository.findById(user2.getId()).orElseThrow();
        assertThat(unchangedUser.getEmail()).isEqualTo("user2@test.com");
    }
    
    @Test
    void updateProfile_transactionRollback_whenExceptionOccurs() {
        // Test rollback behavior with real database
    }
    
    @Test
    void updateProfile_concurrentUpdate_shouldHandleOptimisticLocking() {
        // Test concurrent updates with version field
    }
    
    @Test
    void updateProfile_withImageUpload_shouldStoreBase64InDatabase() {
        // Test image data persistence
    }
    
    @Test
    void updateProfile_withNullableFields_shouldUpdateCorrectly() {
        // Test nullable field handling
    }
    
    @Test
    void updateProfile_auditFields_shouldBeUpdatedAutomatically() {
        // Test updatedAt, updatedBy fields
    }
    
    @Test
    void findById_withExistingUser_shouldLoadAllRelations() {
        // Test lazy loading, relations
    }
    
    @Test
    void findById_withNonExistingUser_shouldReturnEmpty() {
        // Test not found scenario
    }
    
    private User createTestUser() {
        return createTestUser("test@example.com", "Test User");
    }
    
    private User createTestUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(name);
        user.setPassword("encodedPassword");
        user.setPhoneNumber("0123456789");
        // Set other required fields
        return userRepository.save(user);
    }
}
```

---

### 4.2 InvoiceService Integration Tests

**Target:** 15-18 test cases
**Duration:** ~10-15s per test

```java
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class InvoiceServiceIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void createInvoice_withValidItems_shouldPersistInvoiceAndDetails() {
        // Arrange
        Inventory inventory = createTestInventory(100); // 100 units available
        User pharmacist = createTestUser();
        ShiftWork shiftWork = createTestShiftWork(pharmacist);
        
        InvoiceCreateRequest request = InvoiceCreateRequest.builder()
            .customerName("John Doe")
            .phoneNumber("0123456789")
            .paymentMethod("cash")
            .totalAmount(100000.0)
            .items(List.of(
                InvoiceItemRequest.builder()
                    .inventoryId(inventory.getId())
                    .quantity(5)
                    .unitPrice(20000.0)
                    .selectedMultiplier(1.0)
                    .build()
            ))
            .build();
        
        UserContext userContext = new UserContext(
            pharmacist.getId(), 
            "PHARMACIST", 
            pharmacist.getBranch().getId(), 
            shiftWork.getId()
        );
        
        // Act
        InvoiceResponse response = invoiceService.createInvoice(request, userContext);
        
        // Assert - Flush and clear to force database read
        entityManager.flush();
        entityManager.clear();
        
        // Verify Invoice persisted
        Invoice savedInvoice = invoiceRepository.findById(response.getInvoiceId())
            .orElseThrow();
        assertThat(savedInvoice.getCustomerName()).isEqualTo("John Doe");
        assertThat(savedInvoice.getTotalPrice()).isEqualTo(100000.0);
        assertThat(savedInvoice.getPaymentMethod()).isEqualToIgnoringCase("cash");
        
        // Verify InvoiceDetails persisted
        List<InvoiceDetail> details = invoiceDetailRepository
            .findByInvoiceId(savedInvoice.getId());
        assertThat(details).hasSize(1);
        assertThat(details.get(0).getQuantity()).isEqualTo(5);
        assertThat(details.get(0).getPrice()).isEqualTo(20000.0);
        
        // Verify Inventory updated
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId())
            .orElseThrow();
        assertThat(updatedInventory.getQuantity()).isEqualTo(95); // 100 - 5
    }
    
    @Test
    void createInvoice_withMultipleItems_shouldUpdateAllInventories() {
        // Arrange
        Inventory inv1 = createTestInventory("BATCH001", 100);
        Inventory inv2 = createTestInventory("BATCH002", 50);
        User pharmacist = createTestUser();
        ShiftWork shiftWork = createTestShiftWork(pharmacist);
        
        InvoiceCreateRequest request = InvoiceCreateRequest.builder()
            .customerName("Test Customer")
            .paymentMethod("cash")
            .totalAmount(150000.0)
            .items(List.of(
                InvoiceItemRequest.builder()
                    .inventoryId(inv1.getId())
                    .quantity(10)
                    .unitPrice(10000.0)
                    .selectedMultiplier(1.0)
                    .build(),
                InvoiceItemRequest.builder()
                    .inventoryId(inv2.getId())
                    .quantity(5)
                    .unitPrice(10000.0)
                    .selectedMultiplier(1.0)
                    .build()
            ))
            .build();
        
        UserContext userContext = new UserContext(
            pharmacist.getId(), "PHARMACIST", 
            pharmacist.getBranch().getId(), shiftWork.getId()
        );
        
        // Act
        invoiceService.createInvoice(request, userContext);
        
        // Assert
        entityManager.flush();
        entityManager.clear();
        
        Inventory updated1 = inventoryRepository.findById(inv1.getId()).orElseThrow();
        Inventory updated2 = inventoryRepository.findById(inv2.getId()).orElseThrow();
        
        assertThat(updated1.getQuantity()).isEqualTo(90);  // 100 - 10
        assertThat(updated2.getQuantity()).isEqualTo(45);  // 50 - 5
    }
    
    @Test
    @Transactional
    void createInvoice_whenInsufficientStock_shouldRollbackTransaction() {
        // Arrange
        Inventory inventory = createTestInventory(10); // Only 10 available
        User pharmacist = createTestUser();
        ShiftWork shiftWork = createTestShiftWork(pharmacist);
        
        InvoiceCreateRequest request = InvoiceCreateRequest.builder()
            .items(List.of(
                InvoiceItemRequest.builder()
                    .inventoryId(inventory.getId())
                    .quantity(20) // Request more than available
                    .unitPrice(1000.0)
                    .selectedMultiplier(1.0)
                    .build()
            ))
            .build();
        
        UserContext userContext = new UserContext(
            pharmacist.getId(), "PHARMACIST", 
            pharmacist.getBranch().getId(), shiftWork.getId()
        );
        
        Integer initialInventoryQuantity = inventory.getQuantity();
        
        // Act & Assert
        assertThatThrownBy(() -> invoiceService.createInvoice(request, userContext))
            .isInstanceOf(InsufficientInventoryException.class);
        
        // Verify rollback
        entityManager.flush();
        entityManager.clear();
        
        // No invoice should be created
        List<Invoice> invoices = invoiceRepository.findAll();
        assertThat(invoices).isEmpty();
        
        // Inventory should remain unchanged
        Inventory unchangedInventory = inventoryRepository.findById(inventory.getId())
            .orElseThrow();
        assertThat(unchangedInventory.getQuantity()).isEqualTo(initialInventoryQuantity);
    }
    
    @Test
    void createInvoice_withUnitMultiplier_shouldCalculateCorrectly() {
        // Test multiplier logic with database
    }
    
    @Test
    void createInvoice_auditFields_shouldBeSetCorrectly() {
        // Test createdBy, createdAt from UserContext
    }
    
    @Test
    void createInvoice_generateInvoiceCode_shouldBeUnique() {
        // Test invoice code generation and uniqueness
    }
    
    @Test
    void createInvoice_withDefaultCustomer_shouldUseKhachLe() {
        // Test default customer name
    }
}
```

---

### 4.3 Controller Integration Tests

**Target:** 12-15 test cases per controller
**Duration:** ~15-20s per test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PharmacistControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    @WithMockUser(username = "pharmacist@test.com", roles = "PHARMACIST")
    void updateProfile_endToEnd_shouldPersistToDatabase() throws Exception {
        // Arrange
        User existingUser = createTestPharmacist("pharmacist@test.com");
        
        // Act
        mockMvc.perform(post("/pharmacist/profile/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fullName", "Updated Name")
                .param("email", "updated@test.com")
                .param("phone", "0987654321")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/pharmacist/profile"))
            .andExpect(flash().attributeExists("success"));
        
        // Assert - Verify database
        User updatedUser = userRepository.findByEmail("updated@test.com")
            .orElseThrow();
        assertThat(updatedUser.getFullName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getPhoneNumber()).isEqualTo("0987654321");
    }
    
    @Test
    @WithMockUser(username = "pharmacist@test.com", roles = "PHARMACIST")
    void updateProfile_withValidationError_shouldReturnToForm() throws Exception {
        // Arrange
        createTestPharmacist("pharmacist@test.com");
        
        // Act & Assert
        mockMvc.perform(post("/pharmacist/profile/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fullName", "Test")
                .param("email", "invalid-email") // Invalid email
                .param("phone", "123") // Invalid phone
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("pages/pharmacist/profile"))
            .andExpect(model().attributeHasFieldErrors("profileUpdateRequest", "email", "phone"));
        
        // Verify no changes in database
        User unchangedUser = userRepository.findByEmail("pharmacist@test.com")
            .orElseThrow();
        assertThat(unchangedUser.getFullName()).isNotEqualTo("Test");
    }
    
    @Test
    @WithMockUser(username = "pharmacist@test.com", roles = "PHARMACIST")
    void createInvoice_fullWorkflow_shouldCreateInvoiceAndUpdateStock() throws Exception {
        // Arrange
        User pharmacist = createTestPharmacist("pharmacist@test.com");
        ShiftWork shiftWork = createTestShiftWork(pharmacist);
        Inventory inventory = createTestInventory(100);
        
        String requestBody = """
            {
                "customerName": "John Doe",
                "phoneNumber": "0123456789",
                "paymentMethod": "cash",
                "totalAmount": 100000.0,
                "items": [
                    {
                        "inventoryId": %d,
                        "quantity": 5,
                        "unitPrice": 20000.0,
                        "selectedMultiplier": 1.0
                    }
                ]
            }
            """.formatted(inventory.getId());
        
        // Act
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.invoiceCode").exists())
            .andExpect(jsonPath("$.totalAmount").value(100000.0));
        
        // Assert - Verify database
        List<Invoice> invoices = invoiceRepository.findAll();
        assertThat(invoices).hasSize(1);
        
        Invoice invoice = invoices.get(0);
        assertThat(invoice.getCustomerName()).isEqualTo("John Doe");
        assertThat(invoice.getTotalPrice()).isEqualTo(100000.0);
        
        // Verify inventory updated
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId())
            .orElseThrow();
        assertThat(updatedInventory.getQuantity()).isEqualTo(95);
    }
    
    @Test
    void createInvoice_withoutAuthentication_shouldReturn401() throws Exception {
        // Test without @WithMockUser
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "manager@test.com", roles = "MANAGER")
    void createInvoice_withWrongRole_shouldReturn403() throws Exception {
        // Test with wrong role
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .with(csrf()))
            .andExpect(status().isForbidden());
    }
    
    private User createTestPharmacist(String email) {
        // Helper method
    }
    
    private Inventory createTestInventory(int quantity) {
        // Helper method
    }
    
    private ShiftWork createTestShiftWork(User user) {
        // Helper method
    }
}
```

---

### 4.4 Repository Integration Tests

**Target:** 8-10 test cases per repository
**Duration:** ~2-5s per test

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryIntegrationTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void findByEmailIgnoreCase_shouldReturnUser() {
        // Arrange
        User user = new User();
        user.setEmail("Test@Example.COM");
        user.setFullName("Test User");
        // Set other fields
        entityManager.persist(user);
        entityManager.flush();
        
        // Act
        Optional<User> found = userRepository.findByEmailIgnoreCase("test@example.com");
        
        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualToIgnoringCase("test@example.com");
    }
    
    @Test
    void existsByEmailIgnoreCaseAndIdNot_shouldReturnTrueForDuplicate() {
        // Arrange
        User user1 = createUser("user1@test.com");
        User user2 = createUser("user2@test.com");
        
        // Act
        boolean exists = userRepository.existsByEmailIgnoreCaseAndIdNot(
            "user1@test.com", user2.getId());
        
        // Assert
        assertThat(exists).isTrue();
    }
    
    @Test
    void existsByPhoneNumberAndIdNot_shouldDetectDuplicate() {
        // Similar to email test
    }
    
    @Test
    void findById_shouldLoadWithRelations() {
        // Test eager/lazy loading
    }
    
    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFullName("Test User");
        // Set other required fields
        return entityManager.persistAndFlush(user);
    }
}

@DataJpaTest
class InvoiceRepositoryIntegrationTest {
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void findRevenueShiftByUser_shouldReturnCorrectAggregates() {
        // Arrange
        User user = createTestUser();
        Shift shift = createTestShift();
        ShiftWork shiftWork = createTestShiftWork(user, shift);
        
        // Create invoices with different payment methods
        createInvoice(shiftWork, 100000.0, "cash");
        createInvoice(shiftWork, 50000.0, "transfer");
        createInvoice(shiftWork, 75000.0, "cash");
        
        entityManager.flush();
        entityManager.clear();
        
        // Act
        List<Object[]> results = invoiceRepository
            .findRevenueShiftByUser(user.getId());
        
        // Assert
        assertThat(results).isNotEmpty();
        Object[] row = results.get(0);
        
        // Verify aggregated values
        // row[0] = shiftName
        // row[1] = orderCount
        // row[2] = cashTotal
        // row[3] = transferTotal
        // row[4] = totalRevenue
        
        assertThat(row[1]).isEqualTo(3L); // 3 orders
        assertThat(row[2]).isEqualTo(175000.0); // 100k + 75k cash
        assertThat(row[3]).isEqualTo(50000.0); // 50k transfer
        assertThat(row[4]).isEqualTo(225000.0); // Total
    }
    
    @Test
    void findRevenueDetailsByShiftAndUser_shouldReturnAllInvoiceDetails() {
        // Test complex join query
    }
}
```

---

## Phần 5: Test Scenarios đặc biệt

### 5.1 Transaction Rollback Tests

```java
@Test
@Transactional
void createInvoice_whenDetailSaveFails_shouldRollbackEverything() {
    // Arrange
    Inventory inventory = createTestInventory(100);
    // ... setup
    
    // Inject fault to cause failure
    // (có thể dùng AOP hoặc custom repository)
    
    // Act & Assert
    assertThatThrownBy(() -> invoiceService.createInvoice(request, userContext))
        .isInstanceOf(RuntimeException.class);
    
    // Verify complete rollback
    assertThat(invoiceRepository.count()).isZero();
    assertThat(invoiceDetailRepository.count()).isZero();
    
    Inventory unchanged = inventoryRepository.findById(inventory.getId()).orElseThrow();
    assertThat(unchanged.getQuantity()).isEqualTo(100); // Unchanged
}
```

### 5.2 Concurrent Access Tests

```java
@Test
void createInvoice_concurrentAccess_shouldHandleOptimisticLocking() throws Exception {
    // Arrange
    Inventory inventory = createTestInventory(100);
    
    ExecutorService executor = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(2);
    
    List<Exception> exceptions = new CopyOnWriteArrayList<>();
    
    // Act - 2 threads trying to reserve same inventory
    for (int i = 0; i < 2; i++) {
        executor.submit(() -> {
            try {
                InvoiceCreateRequest request = createRequest(inventory.getId(), 60);
                invoiceService.createInvoice(request, userContext);
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                latch.countDown();
            }
        });
    }
    
    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();
    
    // Assert - One should succeed, one should fail
    assertThat(exceptions).hasSize(1);
    assertThat(exceptions.get(0))
        .isInstanceOfAny(
            OptimisticLockingFailureException.class,
            InsufficientInventoryException.class
        );
    
    // Verify final state
    Inventory finalInventory = inventoryRepository.findById(inventory.getId())
        .orElseThrow();
    assertThat(finalInventory.getQuantity()).isEqualTo(40); // 100 - 60 (one succeeded)
}
```

### 5.3 Security Integration Tests

```java
@Test
void pharmacistEndpoint_withoutAuthentication_shouldRedirectToLogin() throws Exception {
    mockMvc.perform(get("/pharmacist/profile"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
}

@Test
@WithMockUser(username = "pharmacist@test.com", roles = "PHARMACIST")
void pharmacistEndpoint_withPharmacistRole_shouldAllow() throws Exception {
    mockMvc.perform(get("/pharmacist/profile"))
        .andExpect(status().isOk());
}

@Test
@WithMockUser(username = "manager@test.com", roles = "MANAGER")
void pharmacistEndpoint_withManagerRole_shouldDeny() throws Exception {
    mockMvc.perform(get("/pharmacist/profile"))
        .andExpect(status().isForbidden());
}
```

---

## Phần 6: Performance và Optimization

### 6.1 Test Execution Speed

**Mục tiêu:**
- Repository tests: < 5s each
- Service tests: < 10s each
- Controller tests: < 20s each
- Full suite: < 5 minutes

**Strategies:**
- Use H2 for faster tests (not TestContainers)
- @DirtiesContext only when necessary
- Reuse test containers across tests
- Parallel test execution

### 6.2 Database Cleanup

```java
@AfterEach
void cleanUp() {
    invoiceDetailRepository.deleteAll();
    invoiceRepository.deleteAll();
    inventoryRepository.deleteAll();
    shiftWorkRepository.deleteAll();
    shiftAssignmentRepository.deleteAll();
    userRepository.deleteAll();
}
```

**Hoặc dùng @DirtiesContext:**
```java
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
```

---

## Phần 7: Dependencies

### 7.1 Maven Dependencies

```xml
<!-- Integration Test Dependencies -->
<dependencies>
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- H2 Database for testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- TestContainers (optional, for MySQL testing) -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.19.3</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Rest Assured (optional, for API testing) -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Phần 8: Execution Plan

### Timeline: 2 weeks (sau khi hoàn thành unit tests)

**Week 1: Repository + Service Integration Tests**
- Day 1-2: Setup infrastructure (base classes, test config, H2/TestContainers)
- Day 3: UserRepository + UserService integration tests
- Day 4: InvoiceRepository + InvoiceService integration tests
- Day 5: InventoryRepository + InventoryService integration tests

**Week 2: Controller + Advanced Scenarios**
- Day 1-2: PharmacistController integration tests (all endpoints)
- Day 3: Transaction rollback scenarios
- Day 4: Concurrent access tests, security tests
- Day 5: Performance optimization, cleanup, documentation

---

## Phần 9: Success Metrics

### Coverage Targets (Integration Tests riêng)

- [ ] Service layer: ≥70% coverage (phần còn lại do unit tests)
- [ ] Controller layer: ≥80% coverage
- [ ] Repository custom queries: 100% coverage
- [ ] Critical workflows: 100% coverage (create invoice, update profile)

### Quality Metrics

- [ ] All integration tests pass consistently
- [ ] No flaky tests (tests pass 100% of time)
- [ ] Test execution time < 5 minutes for full suite
- [ ] Database cleanup works correctly (no test pollution)
- [ ] Tests can run in parallel
- [ ] Tests work with both H2 and MySQL (via TestContainers)

---

## Phần 10: Best Practices

### 10.1 Test Isolation

```java
// BAD: Tests depend on each other
@Test
@Order(1)
void test1_createUser() { ... }

@Test
@Order(2)
void test2_updateUser() { ... } // Depends on test1

// GOOD: Each test is independent
@Test
void updateUser_shouldWork() {
    User user = createTestUser(); // Setup in same test
    // ... test logic
}
```

### 10.2 Use @Transactional for Auto Rollback

```java
@SpringBootTest
@Transactional // Auto rollback after each test
class MyIntegrationTest {
    
    @Test
    void myTest() {
        // Any database changes are rolled back automatically
    }
}
```

### 10.3 Clear EntityManager

```java
@Test
void myTest() {
    // Act
    service.updateUser(userId, request);
    
    // Force flush and clear to test real database state
    entityManager.flush();
    entityManager.clear();
    
    // Assert - read from database
    User user = userRepository.findById(userId).orElseThrow();
    assertThat(user.getFullName()).isEqualTo("Updated");
}
```

### 10.4 Separate Test Profiles

**application-test.properties:**
```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## Summary

Kế hoạch integration test này bao gồm:

- **60-80 integration test cases** across all layers
- **Repository tests** - Verify custom queries work với real database
- **Service tests** - Test business logic với real repositories
- **Controller tests** - Full HTTP request/response cycle
- **Transaction tests** - Verify rollback behavior
- **Concurrent access tests** - Test locking mechanisms
- **Security tests** - Authentication/authorization
- **Performance targets** - < 5 minutes for full suite
- **2 week execution plan** - After unit tests complete

Integration tests complement unit tests by verifying that all components work together correctly in a real-world scenario with actual database operations.

