# Plan: Unit Test Strategy cho Pharmacist Role và Profile Feature

## Mục tiêu

Tạo comprehensive unit tests cho role Pharmacist và chức năng Profile, tập trung vào validation logic, business rules, và các trường hợp fail. Áp dụng cấu trúc AAA (Arrange-Act-Assert) với coverage cao cho Service layer, Validators, Controllers, và Mappers.

---

## Phần 1: Phân tích Scope và Ưu tiên

### 1.1 Service Classes cần test (Priority: RẤT CAO)

#### UserService
- `updateProfile(Long id, ProfileUpdateRequest request)`
- `updateProfile(Long id, ProfileVM profileVM)`
- `findById(Long id)`
- `changePassword(Long id, String currentPassword, String newPassword)`

#### InvoiceService
- `createInvoice(InvoiceCreateRequest request)`
- `findRevenueShiftByUser(Long userId)`
- `findRevenueDetailsByShiftAndUser(Long shiftId, Long userId)`

#### InventoryService
- `reserveStock(Long inventoryId, Integer quantity)`
- `releaseStock(Long inventoryId, Integer quantity)`
- `checkAvailability(Long inventoryId, Integer quantity)`

#### ShiftService
- `getCurrentUserShift(Long userId)`
- `validateShiftWork(Long userId, LocalDateTime time)`

#### MedicineVariantService
- `searchMedicines(String keyword)`
- `getVariantsWithInventory(Long medicineId)`

### 1.2 Validators cần test (Priority: CAO)

- `PhoneNumberValidator`
- `PasswordValidator`
- `QuantityValidator`
- `EmailValidator`
- `PrescriptionValidator`

### 1.3 Mappers/DTOs cần test (Priority: VỪA CAO)

- `ProfileUpdateRequest` → `User`
- `User` → `ProfileVM`
- `InvoiceCreateRequest` → `Invoice`
- `Inventory` → `VariantInventoryDTO`

### 1.4 Controllers cần test (Priority: TRUNG BÌNH - CAO)

- `PharmacistController.profile()`
- `PharmacistController.update()`
- `PharmacistController.createInvoice()`
- `PharmacistController.getInvoices()`

---

## Phần 2: Chi tiết Test Cases theo Service

### 2.1 UserService.updateProfile() Tests

**Complexity:** HIGH (LOC ~50-100, nhiều branches, validation, transaction)
**Target:** 15-20 test cases

#### Happy Path Tests (5 tests)

```java
@Test
void updateProfile_withValidData_shouldUpdateSuccessfully() {
    // Arrange
    Long userId = 1L;
    User existingUser = UserTestDataBuilder.aUser()
        .withId(userId)
        .withEmail("old@example.com")
        .withPhone("0123456789")
        .build();
    
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("New Name")
        .email("new@example.com")
        .phone("0987654321")
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), userId))
        .thenReturn(false);
    when(userRepository.existsByPhoneNumberAndIdNot(request.getPhone(), userId))
        .thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    
    // Act
    userService.updateProfile(userId, request);
    
    // Assert
    verify(userRepository).save(argThat(user -> 
        user.getFullName().equals("New Name") &&
        user.getEmail().equals("new@example.com") &&
        user.getPhone().equals("0987654321")
    ));
}

@Test
void updateProfile_withOnlyNameChange_shouldNotUpdatePassword() {
    // Arrange
    Long userId = 1L;
    User existingUser = UserTestDataBuilder.aUser()
        .withId(userId)
        .withPassword("encodedOldPassword")
        .build();
    
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("New Name")
        .email(existingUser.getEmail())
        .phone(existingUser.getPhoneNumber())
        // No password fields
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
        .thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    
    // Act
    userService.updateProfile(userId, request);
    
    // Assert
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository).save(argThat(user -> 
        user.getPassword().equals("encodedOldPassword")
    ));
}

@Test
void updateProfile_withPasswordChange_shouldEncodeAndUpdate() {
    // Arrange
    Long userId = 1L;
    String oldPassword = "oldPass123";
    String newPassword = "newPass456";
    String encodedOld = "encodedOldPassword";
    String encodedNew = "encodedNewPassword";
    
    User existingUser = UserTestDataBuilder.aUser()
        .withId(userId)
        .withPassword(encodedOld)
        .build();
    
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName(existingUser.getFullName())
        .email(existingUser.getEmail())
        .phone(existingUser.getPhoneNumber())
        .currentPassword(oldPassword)
        .password(newPassword)
        .confirmPassword(newPassword)
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
        .thenReturn(false);
    when(passwordEncoder.matches(oldPassword, encodedOld)).thenReturn(true);
    when(passwordEncoder.encode(newPassword)).thenReturn(encodedNew);
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    
    // Act
    userService.updateProfile(userId, request);
    
    // Assert
    verify(passwordEncoder).matches(oldPassword, encodedOld);
    verify(passwordEncoder).encode(newPassword);
    verify(userRepository).save(argThat(user -> 
        user.getPassword().equals(encodedNew)
    ));
}

@Test
void updateProfile_withEmptyPhone_shouldAcceptAndStoreEmpty() {
    // Arrange
    Long userId = 1L;
    User existingUser = UserTestDataBuilder.aUser()
        .withId(userId)
        .withPhone("0123456789")
        .build();
    
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("Test User")
        .email("test@example.com")
        .phone("") // Empty phone
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
        .thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    
    // Act
    userService.updateProfile(userId, request);
    
    // Assert
    verify(userRepository).save(argThat(user -> 
        user.getPhoneNumber().isEmpty()
    ));
}

@Test
void updateProfile_withAvatarData_shouldUpdateImageUrl() {
    // Arrange
    Long userId = 1L;
    String base64Avatar = "data:image/png;base64,iVBORw0KGgoAAAANS...";
    
    User existingUser = UserTestDataBuilder.aUser()
        .withId(userId)
        .withImageUrl(null)
        .build();
    
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("Test User")
        .email("test@example.com")
        .avatarData(base64Avatar)
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
        .thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    
    // Act
    userService.updateProfile(userId, request);
    
    // Assert
    verify(userRepository).save(argThat(user -> 
        user.getImageUrl().equals(base64Avatar)
    ));
}
```

#### Boundary Cases (5 tests)

```java
@Test
void updateProfile_withMinLengthName_shouldAccept() {
    // fullName = 6 characters (minimum)
    // Assert success
}

@Test
void updateProfile_withMaxLengthName_shouldAccept() {
    // fullName = 100 characters (maximum)
    // Assert success
}

@Test
void updateProfile_withMinPasswordLength_shouldAccept() {
    // password = 6 characters (minimum)
    // Assert success and password encoded
}

@Test
void updateProfile_withMaxPasswordLength_shouldAccept() {
    // password = 100 characters (maximum)
    // Assert success and password encoded
}

@Test
void updateProfile_withExactlyMaxPhoneLength_shouldAccept() {
    // phone = 11 digits (with +84)
    // Assert success
}
```

#### Abnormal/Error Cases (10 tests)

```java
@Test
void updateProfile_withNullUserId_shouldThrowIllegalArgumentException() {
    // Arrange
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("Test")
        .email("test@example.com")
        .build();
    
    // Act & Assert
    assertThatThrownBy(() -> userService.updateProfile(null, request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User ID cannot be null");
}

@Test
void updateProfile_whenUserNotFound_shouldThrowRuntimeException() {
    // Arrange
    Long userId = 999L;
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("Test")
        .email("test@example.com")
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.empty());
    
    // Act & Assert
    assertThatThrownBy(() -> userService.updateProfile(userId, request))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found");
}

@Test
void updateProfile_withDuplicateEmail_shouldThrowRuntimeException() {
    // Arrange
    Long userId = 1L;
    User existingUser = UserTestDataBuilder.aUser().withId(userId).build();
    
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("Test")
        .email("duplicate@example.com")
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(
        "duplicate@example.com", userId)).thenReturn(true);
    
    // Act & Assert
    assertThatThrownBy(() -> userService.updateProfile(userId, request))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Email đã được sử dụng");
}

@Test
void updateProfile_withDuplicatePhone_shouldThrowRuntimeException() {
    // Arrange
    Long userId = 1L;
    User existingUser = UserTestDataBuilder.aUser().withId(userId).build();
    
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("Test")
        .email("test@example.com")
        .phone("0123456789")
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
        .thenReturn(false);
    when(userRepository.existsByPhoneNumberAndIdNot("0123456789", userId))
        .thenReturn(true);
    
    // Act & Assert
    assertThatThrownBy(() -> userService.updateProfile(userId, request))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Số điện thoại đã được sử dụng");
}

@Test
void updateProfile_withWrongCurrentPassword_shouldThrowRuntimeException() {
    // Arrange
    Long userId = 1L;
    String wrongPassword = "wrongPass";
    String newPassword = "newPass456";
    String encodedOld = "encodedOldPassword";
    
    User existingUser = UserTestDataBuilder.aUser()
        .withId(userId)
        .withPassword(encodedOld)
        .build();
    
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("Test")
        .email("test@example.com")
        .currentPassword(wrongPassword)
        .password(newPassword)
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
        .thenReturn(false);
    when(passwordEncoder.matches(wrongPassword, encodedOld)).thenReturn(false);
    
    // Act & Assert
    assertThatThrownBy(() -> userService.updateProfile(userId, request))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Mật khẩu hiện tại không đúng");
}

@Test
void updateProfile_withTooShortPassword_shouldThrowRuntimeException() {
    // password length < 6
}

@Test
void updateProfile_withTooLongPassword_shouldThrowRuntimeException() {
    // password length > 100
}

@Test
void updateProfile_withNullPassword_shouldNotChangePassword() {
    // password = null, should skip password update
}

@Test
void updateProfile_withEmptyPassword_shouldNotChangePassword() {
    // password = "", should skip password update
}

@Test
void updateProfile_withBlankPassword_shouldNotChangePassword() {
    // password = "   ", should skip password update after trim
}
```

#### Transaction Behavior Tests (2 tests)

```java
@Test
@Transactional
void updateProfile_whenRepositorySaveFails_shouldRollback() {
    // Arrange
    Long userId = 1L;
    User existingUser = UserTestDataBuilder.aUser().withId(userId).build();
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("Test")
        .email("test@example.com")
        .build();
    
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong()))
        .thenReturn(false);
    when(userRepository.save(any(User.class)))
        .thenThrow(new RuntimeException("Database error"));
    
    // Act & Assert
    assertThatThrownBy(() -> userService.updateProfile(userId, request))
        .isInstanceOf(RuntimeException.class);
    
    // Verify rollback behavior
    verify(userRepository).save(any(User.class));
}

@Test
@Transactional
void updateProfile_whenPasswordEncoderFails_shouldRollback() {
    // Similar to above but encoder throws exception
}
```

---

### 2.2 InvoiceService.createInvoice() Tests

**Complexity:** VERY HIGH (LOC >150, nhiều branches, transaction, external calls)
**Target:** 20-25 test cases

#### Happy Path Tests (6 tests)

```java
@Test
void createInvoice_withValidItems_shouldCreateSuccessfully() {
    // Arrange
    Long userId = 1L;
    Long shiftWorkId = 10L;
    
    InvoiceCreateRequest request = InvoiceCreateRequest.builder()
        .customerName("John Doe")
        .phoneNumber("0123456789")
        .paymentMethod("cash")
        .totalAmount(100000.0)
        .items(List.of(
            InvoiceItemRequest.builder()
                .inventoryId(1L)
                .quantity(5)
                .unitPrice(20000.0)
                .selectedMultiplier(1.0)
                .build()
        ))
        .build();
    
    UserContext userContext = new UserContext(userId, "pharmacist", 1L, shiftWorkId);
    Inventory inventory = InventoryTestDataBuilder.anInventory()
        .withId(1L)
        .withQuantity(100)
        .build();
    
    when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
    when(invoiceRepository.save(any(Invoice.class)))
        .thenAnswer(inv -> {
            Invoice saved = inv.getArgument(0);
            saved.setId(1L);
            saved.setInvoiceCode("INV001");
            return saved;
        });
    
    // Act
    InvoiceResponse response = invoiceService.createInvoice(request, userContext);
    
    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getInvoiceCode()).isEqualTo("INV001");
    assertThat(response.getTotalAmount()).isEqualTo(100000.0);
    
    verify(inventoryRepository).save(argThat(inv -> 
        inv.getQuantity() == 95 // 100 - 5
    ));
    verify(invoiceRepository).save(any(Invoice.class));
    verify(invoiceDetailRepository).saveAll(anyList());
}

@Test
void createInvoice_withCashPayment_shouldCalculateCorrectTotal() {
    // Similar to above with cash payment method
}

@Test
void createInvoice_withTransferPayment_shouldCreateWithQRCode() {
    // payment method = "transfer"
    // Assert QR code generated
}

@Test
void createInvoice_withDefaultCustomer_shouldUseKhachLe() {
    // customerName = null or empty
    // Assert defaults to "Khách lẻ"
}

@Test
void createInvoice_withMultipleItems_shouldCalculateTotalCorrectly() {
    // Multiple items with different quantities and prices
}

@Test
void createInvoice_withUnitMultiplier_shouldCalculateQuantityCorrectly() {
    // selectedMultiplier = 10 (package unit)
    // quantity = 2 packages
    // Should reserve 20 base units
}
```

#### Boundary Cases (5 tests)

```java
@Test
void createInvoice_withSingleItem_shouldCalculateCorrectly() {
    // items.size() = 1
}

@Test
void createInvoice_withMaxQuantity_shouldReserveAllStock() {
    // quantity = inventory.getQuantity() (max available)
}

@Test
void createInvoice_withMinimumPrice_shouldAccept() {
    // unitPrice = 1.0 (minimum valid price)
}

@Test
void createInvoice_withLargeQuantity_shouldHandleCorrectly() {
    // quantity = 999999
}

@Test
void createInvoice_withMultiplierOne_shouldNotMultiply() {
    // selectedMultiplier = 1.0 (base unit)
}
```

#### Abnormal/Error Cases (10 tests)

```java
@Test
void createInvoice_withEmptyItems_shouldThrowException() {
    // items = []
    // Assert InsufficientInventoryException or similar
}

@Test
void createInvoice_withNullItems_shouldThrowException() {
    // items = null
}

@Test
void createInvoice_whenInsufficientStock_shouldThrowException() {
    // Arrange
    InvoiceCreateRequest request = InvoiceCreateRequest.builder()
        .items(List.of(
            InvoiceItemRequest.builder()
                .inventoryId(1L)
                .quantity(100) // Request more than available
                .unitPrice(1000.0)
                .selectedMultiplier(1.0)
                .build()
        ))
        .build();
    
    Inventory inventory = InventoryTestDataBuilder.anInventory()
        .withId(1L)
        .withQuantity(50) // Only 50 available
        .build();
    
    when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
    
    // Act & Assert
    assertThatThrownBy(() -> invoiceService.createInvoice(request, userContext))
        .isInstanceOf(InsufficientInventoryException.class)
        .hasMessageContaining("Tồn kho không đủ");
}

@Test
void createInvoice_whenInventoryNotFound_shouldThrowException() {
    // inventoryId not exists
}

@Test
void createInvoice_withInvalidMultiplier_shouldThrowException() {
    // selectedMultiplier = 0 or negative
}

@Test
void createInvoice_withZeroQuantity_shouldThrowException() {
    // quantity = 0
}

@Test
void createInvoice_withNegativeQuantity_shouldThrowException() {
    // quantity < 0
}

@Test
void createInvoice_whenUserNotInShift_shouldThrowException() {
    // userContext.shiftWorkId = null
}

@Test
void createInvoice_withNegativeTotalAmount_shouldThrowException() {
    // totalAmount < 0
}

@Test
void createInvoice_withNullCustomerName_shouldUseDefault() {
    // customerName = null → "Khách lẻ"
}
```

#### Transaction Behavior Tests (4 tests)

```java
@Test
@Transactional
void createInvoice_whenStockReservationFails_shouldRollback() {
    // Arrange
    InvoiceCreateRequest request = createValidRequest();
    Inventory inventory = InventoryTestDataBuilder.anInventory().build();
    
    when(inventoryRepository.findById(anyLong()))
        .thenReturn(Optional.of(inventory));
    when(inventoryRepository.save(any(Inventory.class)))
        .thenThrow(new RuntimeException("DB error"));
    
    // Act & Assert
    assertThatThrownBy(() -> invoiceService.createInvoice(request, userContext))
        .isInstanceOf(RuntimeException.class);
    
    // Verify invoice not saved
    verify(invoiceRepository, never()).save(any(Invoice.class));
}

@Test
@Transactional
void createInvoice_whenInvoiceDetailSaveFails_shouldRollbackStockAndInvoice() {
    // Similar but invoiceDetailRepository.save throws
}

@Test
@Transactional
void createInvoice_whenInvoiceSaveFails_shouldRollbackStock() {
    // invoiceRepository.save throws
    // Verify inventory quantity not changed
}

@Test
@Transactional
void createInvoice_withConcurrentStockUpdate_shouldHandleOptimisticLock() {
    // Simulate optimistic locking exception
    // Should retry or throw appropriate exception
}
```

---

### 2.3 InventoryService Tests

**Complexity:** MEDIUM-HIGH (transaction, concurrency concerns)
**Target:** 12-15 test cases

#### reserveStock() Tests

```java
@Test
void reserveStock_withAvailableQuantity_shouldDecreaseStock() {
    // Happy path
}

@Test
void reserveStock_withExactQuantity_shouldSetStockToZero() {
    // Boundary: quantity = inventory.quantity
}

@Test
void reserveStock_whenInsufficientStock_shouldThrowException() {
    // Error case
}

@Test
void reserveStock_withNegativeQuantity_shouldThrowException() {
    // Abnormal
}

@Test
void reserveStock_whenInventoryNotFound_shouldThrowException() {
    // Error case
}

@Test
@Transactional
void reserveStock_withConcurrentReservation_shouldHandleCorrectly() {
    // Concurrency test
}
```

#### releaseStock() Tests

```java
@Test
void releaseStock_shouldIncreaseStock() {
    // Happy path
}

@Test
void releaseStock_withLargeQuantity_shouldNotExceedMaxCapacity() {
    // Boundary
}

@Test
void releaseStock_withNegativeQuantity_shouldThrowException() {
    // Abnormal
}
```

#### checkAvailability() Tests

```java
@Test
void checkAvailability_withSufficientStock_shouldReturnTrue() {
    // Happy path
}

@Test
void checkAvailability_withInsufficientStock_shouldReturnFalse() {
    // Boundary
}

@Test
void checkAvailability_withExactQuantity_shouldReturnTrue() {
    // Boundary
}
```

---

## Phần 3: Validator Tests

### 3.1 PhoneNumberValidator Tests

**Complexity:** LOW (simple regex validation)
**Target:** 11 test cases (100% coverage)

```java
@ParameterizedTest
@ValueSource(strings = {
    "0123456789",    // Valid 10 digits
    "0987654321",    // Valid 10 digits
    "01234567890",   // Valid 11 digits
    "+84123456789",  // Valid with +84
    "+841234567890"  // Valid with +84 (11 digits)
})
void validatePhoneNumber_withValidFormats_shouldReturnTrue(String phone) {
    // Act
    boolean result = phoneNumberValidator.validate(phone);
    
    // Assert
    assertThat(result).isTrue();
}

@ParameterizedTest
@ValueSource(strings = {
    "123456789",      // Too short (9 digits)
    "012345678901",   // Too long (12 digits)
    "1123456789",     // Invalid prefix (1)
    "0123456abc",     // Contains letters
    "012-345-6789",   // Contains dashes
    "+85123456789",   // Invalid country code
    "012 345 6789",   // Contains spaces
})
void validatePhoneNumber_withInvalidFormats_shouldReturnFalse(String phone) {
    // Act
    boolean result = phoneNumberValidator.validate(phone);
    
    // Assert
    assertThat(result).isFalse();
}

@Test
void validatePhoneNumber_withNull_shouldReturnFalse() {
    assertThat(phoneNumberValidator.validate(null)).isFalse();
}

@Test
void validatePhoneNumber_withEmpty_shouldReturnFalse() {
    assertThat(phoneNumberValidator.validate("")).isFalse();
}

@Test
void validatePhoneNumber_withBlank_shouldReturnFalse() {
    assertThat(phoneNumberValidator.validate("   ")).isFalse();
}
```

### 3.2 PasswordValidator Tests

**Complexity:** LOW
**Target:** 7 test cases

```java
@Test
void validatePassword_withValidLength_shouldReturnTrue() {
    assertThat(passwordValidator.validate("password123")).isTrue();
}

@Test
void validatePassword_withMinLength_shouldReturnTrue() {
    assertThat(passwordValidator.validate("pass12")).isTrue(); // 6 chars
}

@Test
void validatePassword_withMaxLength_shouldReturnTrue() {
    String maxPassword = "a".repeat(100);
    assertThat(passwordValidator.validate(maxPassword)).isTrue();
}

@Test
void validatePassword_withTooShort_shouldReturnFalse() {
    assertThat(passwordValidator.validate("pass1")).isFalse(); // 5 chars
}

@Test
void validatePassword_withTooLong_shouldReturnFalse() {
    String tooLong = "a".repeat(101);
    assertThat(passwordValidator.validate(tooLong)).isFalse();
}

@Test
void validatePassword_withNull_shouldReturnFalse() {
    assertThat(passwordValidator.validate(null)).isFalse();
}

@Test
void validatePassword_withEmpty_shouldReturnFalse() {
    assertThat(passwordValidator.validate("")).isFalse();
}
```

### 3.3 QuantityValidator Tests

**Complexity:** LOW
**Target:** 6 test cases

```java
@Test
void validateQuantity_withinStock_shouldReturnTrue() {
    assertThat(quantityValidator.validate(50, 100)).isTrue();
}

@Test
void validateQuantity_atMaxStock_shouldReturnTrue() {
    assertThat(quantityValidator.validate(100, 100)).isTrue();
}

@Test
void validateQuantity_exceedsStock_shouldReturnFalse() {
    assertThat(quantityValidator.validate(101, 100)).isFalse();
}

@Test
void validateQuantity_withZero_shouldReturnFalse() {
    assertThat(quantityValidator.validate(0, 100)).isFalse();
}

@Test
void validateQuantity_withNegative_shouldReturnFalse() {
    assertThat(quantityValidator.validate(-1, 100)).isFalse();
}

@Test
void validateQuantity_withOne_shouldReturnTrue() {
    assertThat(quantityValidator.validate(1, 100)).isTrue();
}
```

---

## Phần 4: Mapper/DTO Tests

### 4.1 Profile Mapping Tests

**Complexity:** LOW-MEDIUM
**Target:** 7 test cases

```java
@Test
void mapProfileUpdateRequestToUser_shouldMapAllFields() {
    // Arrange
    ProfileUpdateRequest request = ProfileUpdateRequest.builder()
        .fullName("John Doe")
        .email("john@example.com")
        .phone("0123456789")
        .avatarData("base64data")
        .build();
    
    User user = new User();
    
    // Act
    profileMapper.updateUserFromRequest(request, user);
    
    // Assert
    assertThat(user.getFullName()).isEqualTo("John Doe");
    assertThat(user.getEmail()).isEqualTo("john@example.com");
    assertThat(user.getPhoneNumber()).isEqualTo("0123456789");
    assertThat(user.getImageUrl()).isEqualTo("base64data");
}

@Test
void mapProfileUpdateRequestToUser_withNullPhone_shouldMapAsNull() {
    // phone = null
}

@Test
void mapProfileUpdateRequestToUser_withNullPassword_shouldNotMapPassword() {
    // password = null, should not change user.password
}

@Test
void mapUserToProfileVM_shouldMapAllFields() {
    // Arrange
    User user = UserTestDataBuilder.aUser()
        .withFullName("John Doe")
        .withEmail("john@example.com")
        .withPhone("0123456789")
        .withRole("PHARMACIST")
        .withBranch("Hà Nội")
        .build();
    
    // Act
    ProfileVM profileVM = profileMapper.toProfileVM(user);
    
    // Assert
    assertThat(profileVM.getFullName()).isEqualTo("John Doe");
    assertThat(profileVM.getEmail()).isEqualTo("john@example.com");
    assertThat(profileVM.getPhoneNumber()).isEqualTo("0123456789");
    assertThat(profileVM.getRole()).isEqualTo("PHARMACIST");
    assertThat(profileVM.getBranchName()).isEqualTo("Hà Nội");
}

@Test
void mapUserToProfileVM_withNullPhone_shouldMapCorrectly() {
    // phone = null
}

@Test
void mapUserToProfileVM_withNullImageUrl_shouldMapAsNull() {
    // imageUrl = null
}

@Test
void roundTripMapping_shouldPreserveData() {
    // User → ProfileVM → ProfileUpdateRequest → User
    // Assert all data preserved
}
```

---

## Phần 5: Controller Tests (Unit-level với Mock Service)

### 5.1 PharmacistController.updateProfile() Tests

**Complexity:** MEDIUM
**Target:** 7 test cases

```java
@WebMvcTest(PharmacistController.class)
class PharmacistControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    @WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
    void updateProfile_withValidRequest_shouldReturn200() throws Exception {
        // Arrange
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
            .fullName("John Doe")
            .email("john@example.com")
            .phone("0123456789")
            .build();
        
        doNothing().when(userService)
            .updateProfile(anyLong(), any(ProfileUpdateRequest.class));
        
        // Act & Assert
        mockMvc.perform(post("/pharmacist/profile/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fullName", "John Doe")
                .param("email", "john@example.com")
                .param("phone", "0123456789"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("success"))
            .andExpect(model().attribute("success", "Cập nhật thành công!"));
    }
    
    @Test
    @WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
    void updateProfile_withInvalidPhone_shouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/pharmacist/profile/update")
                .param("fullName", "John Doe")
                .param("email", "john@example.com")
                .param("phone", "invalid"))
            .andExpect(status().isOk()) // Returns to form with errors
            .andExpect(model().attributeHasFieldErrors(
                "profileUpdateRequest", "phone"));
    }
    
    @Test
    @WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
    void updateProfile_withInvalidPassword_shouldReturn400() {
        // password too short
    }
    
    @Test
    @WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
    void updateProfile_withDuplicateEmail_shouldReturnError() {
        // Service throws RuntimeException
        // Assert error message displayed
    }
    
    @Test
    @WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
    void updateProfile_whenServiceThrows_shouldReturn500() {
        // Generic exception from service
    }
    
    @Test
    void updateProfile_whenNotAuthenticated_shouldReturn401() {
        // No @WithMockUser
        // Assert redirect to login
    }
    
    @Test
    @WithMockUser(username = "manager1", roles = "MANAGER")
    void updateProfile_withWrongRole_shouldReturn403() {
        // Wrong role accessing pharmacist endpoint
    }
}
```

### 5.2 PharmacistController.createInvoice() Tests

```java
@Test
@WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
void createInvoice_withValidRequest_shouldReturn200() throws Exception {
    // Arrange
    InvoiceResponse response = InvoiceResponse.builder()
        .invoiceCode("INV001")
        .totalAmount(100000.0)
        .build();
    
    when(invoiceService.createInvoice(any(), any()))
        .thenReturn(response);
    
    // Act & Assert
    mockMvc.perform(post("/pharmacist/pos/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "customerName": "John Doe",
                    "phoneNumber": "0123456789",
                    "paymentMethod": "cash",
                    "totalAmount": 100000.0,
                    "items": [
                        {
                            "inventoryId": 1,
                            "quantity": 5,
                            "unitPrice": 20000.0,
                            "selectedMultiplier": 1.0
                        }
                    ]
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.invoiceCode").value("INV001"))
        .andExpect(jsonPath("$.totalAmount").value(100000.0));
}

@Test
@WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
void createInvoice_withEmptyItems_shouldReturn400() {
    // items = []
}

@Test
@WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
void createInvoice_whenInsufficientStock_shouldReturn400() {
    // Service throws InsufficientInventoryException
}

@Test
@WithMockUser(username = "pharmacist1", roles = "PHARMACIST")
void createInvoice_whenServiceThrows_shouldReturn500() {
    // Generic exception
}
```

---

## Phần 6: Test Infrastructure

### 6.1 Test Data Builders

```java
public class UserTestDataBuilder {
    private Long id = 1L;
    private String fullName = "Test User";
    private String email = "test@example.com";
    private String phoneNumber = "0123456789";
    private String password = "encodedPassword";
    private String imageUrl = null;
    private Role role;
    private Branch branch;
    
    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }
    
    public UserTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public UserTestDataBuilder withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
    
    // ... other builder methods
    
    public User build() {
        User user = new User();
        user.setId(id);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        user.setImageUrl(imageUrl);
        user.setRole(role);
        user.setBranch(branch);
        return user;
    }
}

public class InventoryTestDataBuilder {
    private Long id = 1L;
    private Integer quantity = 100;
    private Double salePrice = 10000.0;
    private String batchNumber = "BATCH001";
    private LocalDate expiryDate = LocalDate.now().plusYears(1);
    private MedicineVariant variant;
    
    public static InventoryTestDataBuilder anInventory() {
        return new InventoryTestDataBuilder();
    }
    
    // Builder methods...
    
    public Inventory build() {
        Inventory inventory = new Inventory();
        inventory.setId(id);
        inventory.setQuantity(quantity);
        inventory.setSalePrice(salePrice);
        inventory.setBatchNumber(batchNumber);
        inventory.setExpiryDate(expiryDate);
        inventory.setVariant(variant);
        return inventory;
    }
}

public class InvoiceTestDataBuilder {
    // Similar pattern
}
```

### 6.2 Base Test Class

```java
@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {
    
    @Mock
    protected UserRepository userRepository;
    
    @Mock
    protected InvoiceRepository invoiceRepository;
    
    @Mock
    protected InventoryRepository inventoryRepository;
    
    @Mock
    protected PasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    protected void assertAuditFields(BaseEntity entity) {
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedBy()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getUpdatedBy()).isNotNull();
    }
}
```

### 6.3 Custom Assertions

```java
public class UserAssertions {
    
    public static void assertUserEquals(User expected, User actual) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getFullName()).isEqualTo(expected.getFullName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
        // ... other fields
    }
    
    public static void assertUserHasValidAuditFields(User user) {
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getCreatedBy()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getUpdatedBy()).isNotNull();
    }
}
```

---

## Phần 7: Coverage và Quality Gates

### 7.1 Coverage Targets

```xml
<!-- pom.xml - JaCoCo configuration -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.75</minimum>
                    </limit>
                </limits>
            </rule>
            <!-- Higher requirements for service layer -->
            <rule>
                <element>CLASS</element>
                <includes>
                    <include>vn.edu.fpt.pharma.service.impl.*</include>
                </includes>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.90</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.85</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

### 7.2 Test Execution Strategy

**Phase 1: Critical Services (Week 1)**
- UserService.updateProfile()
- InvoiceService.createInvoice()
- InventoryService (all methods)

**Phase 2: Validators (Week 1)**
- All validator classes (quick wins for coverage)

**Phase 3: Controllers (Week 2)**
- PharmacistController (all endpoints)

**Phase 4: Mappers (Week 2)**
- All mapper classes

**Phase 5: Edge Cases & Refactor (Week 3)**
- Add missing edge cases
- Refactor tests for maintainability
- Add integration tests if needed

---

## Phần 8: Naming Conventions

### 8.1 Test Method Naming

Pattern: `methodName_stateUnderTest_expectedBehavior()`

Examples:
- `updateProfile_withValidData_shouldUpdateSuccessfully()`
- `updateProfile_withDuplicateEmail_shouldThrowException()`
- `createInvoice_whenInsufficientStock_shouldRollback()`

### 8.2 Test Class Naming

Pattern: `ClassNameTest`

Examples:
- `UserServiceTest`
- `InvoiceServiceTest`
- `PhoneNumberValidatorTest`
- `PharmacistControllerTest`

### 8.3 Test Data Naming

Pattern: `EntityTestDataBuilder` or `MockDataFactory`

Examples:
- `UserTestDataBuilder.aUser()`
- `InventoryTestDataBuilder.anInventory()`
- `InvoiceTestDataBuilder.anInvoice()`

---

## Phần 9: Dependencies và Setup

### 9.1 Maven Dependencies

```xml
<!-- Testing dependencies -->
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Security Test -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 9.2 Test Configuration

```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public Clock clock() {
        return Clock.fixed(
            Instant.parse("2025-12-07T00:00:00Z"),
            ZoneId.of("UTC")
        );
    }
}
```

---

## Phần 10: Execution Plan

### Timeline: 2-3 weeks (Unit Tests only)

**Week 1: Foundation + Critical Services**
- Day 1-2: Setup test infrastructure, builders, base classes
- Day 3-4: UserService tests (all 20+ cases)
- Day 5: InvoiceService tests (start with happy path + boundaries)

**Week 2: Complete Service + Validators + Controllers**
- Day 1-2: InvoiceService tests (error cases + transactions)
- Day 3: InventoryService tests
- Day 4: All Validator tests (quick wins)
- Day 5: Controller tests

**Week 3: Mappers + Edge Cases + Refinement**
- Day 1: Mapper tests
- Day 2-3: Add missing edge cases based on coverage report
- Day 4: Refactor and improve test maintainability
- Day 5: Final review, documentation, CI/CD integration

**Note:** Integration tests sẽ bắt đầu sau Week 3 (xem `plan-pharmacistIntegrationTest.prompt.md`)

### Success Metrics

- [ ] ≥90% line coverage for Service layer
- [ ] ≥85% branch coverage for Service layer
- [ ] 100% coverage for Validators
- [ ] ≥80% coverage for Controllers
- [ ] ≥85% coverage for Mappers
- [ ] All tests pass in CI/CD pipeline
- [ ] Test execution time < 30 seconds for all unit tests
- [ ] Zero flaky tests

---

## Summary

This plan provides comprehensive unit test coverage for Pharmacist role and Profile functionality with:

- **120+ test cases** across all layers
- **Focus on validation and error handling** (60% of tests are abnormal/error cases)
- **Transaction behavior testing** for critical operations
- **Test data builders** for maintainability
- **Clear naming conventions** for readability
- **Phased execution plan** for manageable implementation
- **Coverage targets** with enforcement via JaCoCo

The plan prioritizes Service layer (highest business logic) and Validators (100% coverage target), with comprehensive error case coverage to ensure robustness of the Pharmacist features.

