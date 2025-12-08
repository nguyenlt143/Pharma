# Plan: Unit Test Tối Ưu cho Manager Role - 100% Coverage

Xây dựng bộ unit test với coverage 100%, tuân thủ cấu trúc Arrange-Act-Assert.

**Chiến lược TỐI ƯU**: Test ĐẦY ĐỦ (Happy path, Validation, Business rules, Transaction, Edge cases) cho các method QUAN TRỌNG có logic phức tạp. Các method đơn giản chỉ cần 1 test để đạt coverage 100%.

## Test Metrics Target

- **Total unit tests**: ~78 tests (giảm từ 290 tests)
  - ShiftServiceImpl: 22 tests (1 method đầy đủ, 5 methods coverage only)
  - ShiftAssignmentServiceImpl: 29 tests (2 methods đầy đủ, 6 methods coverage only)
  - UserServiceImpl: 30 tests (3 methods: create/delete đầy đủ, update vừa phải)
  - RevenueReportServiceImpl: 12 tests (1 method đầy đủ)
  - InventoryReportServiceImpl: 5 tests (5 methods coverage only)
- **Line coverage**: 100%
- **Branch coverage**: 100%
- **Method coverage**: 100%
- **Test execution time**: < 20 seconds

## Chiến Lược Test

### Methods TEST ĐẦY ĐỦ (7 methods quan trọng)
1. `ShiftServiceImpl.save()` - 18 tests
2. `ShiftAssignmentServiceImpl.createAssignment()` - 11 tests
3. `ShiftAssignmentServiceImpl.extendShiftWorks()` - 13 tests
4. `UserServiceImpl.create()` - 15 tests
5. `UserServiceImpl.update()` - 8 tests
6. `UserServiceImpl.delete()` - 7 tests
7. `RevenueReportServiceImpl.getRevenueReport()` - 12 tests

### Methods COVERAGE ONLY (1 test per method)
Tất cả các methods còn lại chỉ cần 1 test happy path để đạt 100% coverage.

---

## 1. Setup và Cấu hình

### 1.1. Dependencies (build.gradle)

```groovy
plugins {
    id 'jacoco'
}

dependencies {
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.mockito:mockito-core'
    testImplementation 'org.mockito:mockito-junit-jupiter'
    testImplementation 'org.assertj:assertj-core:3.24.2'
}

test {
    useJUnitPlatform()
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}
```

### 1.2. Test Infrastructure

**File**: `src/test/java/vn/edu/fpt/pharma/BaseServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class BaseServiceTest {
    protected boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equalsIgnoreCase(str2);
    }
}
```

### 1.3. Test Builders

**ShiftTestBuilder.java** - Builder cho Shift entities/DTOs
**UserTestBuilder.java** - Builder cho User entities/DTOs
**ShiftAssignmentTestBuilder.java** - Builder cho ShiftAssignment entities

---

## 2. ShiftServiceImpl Tests (22 tests)

**File**: `src/test/java/vn/edu/fpt/pharma/service/impl/ShiftServiceImplTest.java`

### 2.1. Method `listShifts()` → 1 test (coverage only)

```java
@Test
@DisplayName("Should return shifts with valid parameters")
void listShifts_withValidParams_shouldReturnShifts() {
    // Arrange
    Shift shift = ShiftTestBuilder.create().withId(1L).buildEntity();
    when(repo.search("test", 1L)).thenReturn(List.of(shift));
    
    // Act
    List<ShiftResponse> result = service.listShifts("test", 1L, false);
    
    // Assert
    assertThat(result).hasSize(1);
    verify(repo).search("test", 1L);
}
```

### 2.2. Method `findById()` → 1 test (coverage only)

```java
@Test
@DisplayName("Should return shift when id exists")
void findById_withExistingId_shouldReturnShiftResponse() {
    // Arrange
    Shift shift = ShiftTestBuilder.create().withId(1L).buildEntity();
    when(repo.findById(1L)).thenReturn(Optional.of(shift));
    
    // Act
    Optional<ShiftResponse> result = service.findById(1L);
    
    // Assert
    assertThat(result).isPresent();
}
```

### 2.3. Method `save()` → 18 tests ⭐ TEST ĐẦY ĐỦ

**Happy path (2 tests)**

```java
@Test
@DisplayName("Should create new shift successfully")
void save_withValidNewShift_shouldSaveAndReturnResponse() {
    // Arrange
    ShiftRequest request = ShiftTestBuilder.create()
        .withName("Ca sáng")
        .withStartTime("08:00")
        .withEndTime("16:00")
        .buildRequest();
    when(repo.findOverlappingShifts(any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());
    when(repo.save(any())).thenReturn(savedShift);
    
    // Act
    ShiftResponse result = service.save(request, branchId);
    
    // Assert
    assertThat(result).isNotNull();
    verify(repo).save(any());
}

@Test
@DisplayName("Should update existing shift successfully")
void save_withValidExistingShift_shouldUpdateAndReturnResponse() {
    // Arrange
    ShiftRequest request = ShiftTestBuilder.create()
        .withId(1L)
        .withName("Ca sáng Updated")
        .buildRequest();
    when(repo.findById(1L)).thenReturn(Optional.of(existingShift));
    when(repo.findOverlappingShifts(any(), any(), any(), eq(1L)))
        .thenReturn(Collections.emptyList());
    when(repo.save(any())).thenReturn(updatedShift);
    
    // Act
    ShiftResponse result = service.save(request, branchId);
    
    // Assert
    assertThat(result).isNotNull();
    verify(repo).findById(1L);
}
```

**Validation - Input validation (5 tests)**

```java
@Test
@DisplayName("Should use default midnight when startTime is null")
void save_whenStartTimeNull_shouldUseDefaultMidnight() {
    // Arrange - startTime = null → fallback to 00:00
    
    // Act & Assert - verify fallback behavior
}

@Test
@DisplayName("Should use default midnight when endTime is null")
void save_whenEndTimeNull_shouldUseDefaultMidnight() {
    // Arrange - endTime = null → fallback to 00:00
    
    // Act & Assert - will throw because 00:00 < startTime
}

@Test
@DisplayName("Should parse time with HH:mm format")
void save_withValidTimeFormatHHmm_shouldParse() {
    // Arrange - time = "08:00" → parse success
    
    // Act & Assert
}

@Test
@DisplayName("Should parse time with HH:mm:ss format")
void save_withValidTimeFormatHHmmss_shouldParse() {
    // Arrange - time = "08:00:00" → parse success
    
    // Act & Assert
}

@Test
@DisplayName("Should fallback to midnight with invalid time format")
void save_withInvalidTimeFormat_shouldFallbackToMidnight() {
    // Arrange - time = "abc" → fallback to 00:00
    
    // Act & Assert
}
```

**Business rules - Time validation (3 tests)**

```java
@Test
@DisplayName("Should throw when end time is before start time")
void save_whenEndTimeBeforeStartTime_shouldThrowIllegalArgumentException() {
    // Arrange
    ShiftRequest request = ShiftTestBuilder.create()
        .withStartTime("16:00")
        .withEndTime("08:00")
        .buildRequest();
    
    // Act & Assert
    assertThatThrownBy(() -> service.save(request, branchId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Giờ kết thúc phải lớn hơn giờ bắt đầu");
}

@Test
@DisplayName("Should throw when end time equals start time")
void save_whenEndTimeEqualsStartTime_shouldThrowIllegalArgumentException() {
    // Similar structure
}

@Test
@DisplayName("Should succeed when end time is after start time")
void save_whenEndTimeAfterStartTime_shouldSucceed() {
    // Arrange - et > st by 1 minute → success
    
    // Act & Assert
}
```

**Business rules - Shift overlapping (4 tests)**

```java
@Test
@DisplayName("Should throw when shift overlaps with existing shift")
void save_whenShiftOverlapsExisting_shouldThrowWithOverlappingNames() {
    // Arrange
    Shift overlapping = ShiftTestBuilder.create()
        .withName("Ca trưa")
        .withStartTime("10:00")
        .withEndTime("18:00")
        .buildEntity();
    when(repo.findOverlappingShifts(any(), any(), any(), any()))
        .thenReturn(List.of(overlapping));
    
    // Act & Assert
    assertThatThrownBy(() -> service.save(request, branchId))
        .hasMessageContaining("Ca làm việc bị trùng thời gian với: Ca trưa");
}

@Test
@DisplayName("Should list all overlapping shifts when multiple detected")
void save_whenShiftOverlapsMultiple_shouldListAllOverlapping() {
    // Similar - multiple overlaps
}

@Test
@DisplayName("Should succeed when no overlap detected")
void save_whenNoOverlap_shouldSucceed() {
    // Arrange - empty overlapping list → success
}

@Test
@DisplayName("Should exclude self from overlap check when updating")
void save_whenUpdateSameShift_shouldExcludeSelfFromOverlapCheck() {
    // Arrange - verify findOverlappingShifts called with id parameter
    
    // Act & Assert - verify(repo).findOverlappingShifts(..., eq(1L))
}
```

**Transaction behavior (2 tests)**

```java
@Test
@DisplayName("Should rollback when repo.save fails")
void save_whenRepoSaveFails_shouldRollbackAndThrow() {
    // Arrange
    when(repo.save(any())).thenThrow(new RuntimeException("Database error"));
    
    // Act & Assert
    assertThatThrownBy(() -> service.save(request, branchId))
        .isInstanceOf(RuntimeException.class);
}

@Test
@DisplayName("Should rollback when findOverlapping fails")
void save_whenFindOverlappingFails_shouldRollbackAndThrow() {
    // Similar structure
}
```

**Edge cases (2 tests)**

```java
@Test
@DisplayName("Should handle boundary times 00:00 to 23:59")
void save_withBoundaryTimes_shouldSucceed() {
    // Arrange - min/max time values
    
    // Act & Assert
}

@Test
@DisplayName("Should validate cross-midnight shift")
void save_withCrossMidnightShift_shouldValidate() {
    // Arrange - night shift 22:00 - 02:00
    
    // Act & Assert - current implementation will throw
}
```

### 2.4. Method `delete()` → 1 test (coverage only)

```java
@Test
@DisplayName("Should call repo.deleteById")
void delete_withExistingShift_shouldCallRepoDeleteById() {
    // Arrange & Act
    service.delete(1L);
    
    // Assert
    verify(repo).deleteById(1L);
}
```

### 2.5. Method `restore()` → 1 test (coverage only)

```java
@Test
@DisplayName("Should call repo.restoreById")
void restore_withDeletedShift_shouldCallRepoRestoreById() {
    // Arrange & Act
    service.restore(1L);
    
    // Assert
    verify(repo).restoreById(1L);
}
```

### 2.6. Method `getCurrentShift()` → 1 test (coverage only)

```java
@Test
@DisplayName("Should return shift when user in active shift")
void getCurrentShift_withActiveShift_shouldReturnShift() {
    // Arrange
    Shift shift = ShiftTestBuilder.create().withId(1L).buildEntity();
    when(repo.findCurrentShift(any(), any(), any(), any()))
        .thenReturn(Optional.of(shift));
    
    // Act
    Optional<Shift> result = service.getCurrentShift(1L, 1L);
    
    // Assert
    assertThat(result).isPresent();
}
```

**Total ShiftServiceImpl: 22 tests**

---

## 3. ShiftAssignmentServiceImpl Tests (29 tests)

**File**: `src/test/java/vn/edu/fpt/pharma/service/impl/ShiftAssignmentServiceImplTest.java`

### 3.1. Method `createAssignment()` → 11 tests ⭐ TEST ĐẦY ĐỦ

**Happy path (2 tests)**

```java
@Test
@DisplayName("Should create and return assignment successfully")
void createAssignment_withValidParams_shouldCreateAndReturnAssignment() {
    // Arrange
    when(shiftRepo.findById(1L)).thenReturn(Optional.of(shift));
    when(repo.findByShiftIdAndUserId(1L, 10L)).thenReturn(Optional.empty());
    when(repo.save(any())).thenReturn(savedAssignment);
    when(repo.findById(100L)).thenReturn(Optional.of(savedAssignment));
    when(shiftWorkRepo.saveAll(anyList())).thenReturn(Collections.emptyList());
    
    // Act
    ShiftAssignment result = service.createAssignment(1L, 10L);
    
    // Assert
    assertThat(result).isNotNull();
    verify(repo).save(any());
}

@Test
@DisplayName("Should auto-generate 30 days shift works")
void createAssignment_shouldAutoGenerateShiftWorks() {
    // Arrange - similar setup
    ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
    when(shiftWorkRepo.saveAll(captor.capture())).thenReturn(Collections.emptyList());
    
    // Act
    service.createAssignment(1L, 10L);
    
    // Assert
    assertThat(captor.getValue()).hasSize(30);
}
```

**Validation - Input validation (3 tests)**

```java
@Test
@DisplayName("Should throw when shiftId is null")
void createAssignment_withNullShiftId_shouldThrowIllegalArgumentException() {
    // Arrange
    when(shiftRepo.findById(null)).thenReturn(Optional.empty());
    
    // Act & Assert
    assertThatThrownBy(() -> service.createAssignment(null, 10L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ca làm việc không tồn tại");
}

@Test
@DisplayName("Should throw when shift does not exist")
void createAssignment_withNonExistingShiftId_shouldThrowIllegalArgumentException() {
    // Similar structure
}

@Test
@DisplayName("Should allow creation with null userId")
void createAssignment_withNullUserId_shouldAllowCreation() {
    // Edge case - null userId is allowed
}
```

**Business rules (3 tests)**

```java
@Test
@DisplayName("Should return existing assignment when duplicate detected")
void createAssignment_withDuplicateAssignment_shouldReturnExisting() {
    // Arrange
    when(repo.findByShiftIdAndUserId(1L, 10L))
        .thenReturn(Optional.of(existingAssignment));
    
    // Act
    ShiftAssignment result = service.createAssignment(1L, 10L);
    
    // Assert
    assertThat(result).isEqualTo(existingAssignment);
    verify(repo, never()).save(any());
}

@Test
@DisplayName("Should set correct userId and shift")
void createAssignment_shouldSetCorrectUserIdAndShift() {
    // Verify entity fields
}

@Test
@DisplayName("Should still allow assignment to deleted shift")
void createAssignment_withDeletedShift_shouldStillAllow() {
    // Edge case - soft deleted shift
}
```

**Transaction behavior (2 tests)**

```java
@Test
@DisplayName("Should rollback when repo.save fails")
void createAssignment_whenRepoSaveFails_shouldRollback() {
    // Arrange
    when(repo.save(any())).thenThrow(new RuntimeException("Database error"));
    
    // Act & Assert
    assertThatThrownBy(() -> service.createAssignment(1L, 10L))
        .isInstanceOf(RuntimeException.class);
}

@Test
@DisplayName("Should rollback when shift work generation fails")
void createAssignment_whenShiftWorkGenerationFails_shouldRollback() {
    // Similar structure
}
```

**Edge cases (1 test)**

```java
@Test
@DisplayName("Should handle optimistic lock exception")
void createAssignment_concurrentCreation_shouldHandleOptimisticLock() {
    // Concurrent creation scenario
}
```

### 3.2. Method `removeAssignment()` → 1 test (coverage only)

```java
@Test
@DisplayName("Should delete assignment when exists")
void removeAssignment_withExistingAssignment_shouldDelete() {
    // Arrange
    ShiftAssignment assignment = ShiftAssignmentTestBuilder.create().buildEntity();
    when(repo.findByShiftIdAndUserId(1L, 10L))
        .thenReturn(Optional.of(assignment));
    
    // Act
    service.removeAssignment(1L, 10L);
    
    // Assert
    verify(repo).delete(assignment);
}
```

### 3.3. Method `extendShiftWorks()` → 13 tests ⭐ TEST ĐẦY ĐỦ

**Happy path (2 tests)**

```java
@Test
@DisplayName("Should create shift works for specified days")
void extendShiftWorks_withValidParams_shouldCreateShiftWorks() {
    // Arrange
    when(repo.findById(100L)).thenReturn(Optional.of(assignment));
    when(shiftWorkRepo.findLastWorkDateByAssignmentId(100L)).thenReturn(null);
    ArgumentCaptor<List<ShiftWork>> captor = ArgumentCaptor.forClass(List.class);
    
    // Act
    service.extendShiftWorks(100L, 30);
    
    // Assert
    verify(shiftWorkRepo).saveAll(captor.capture());
    assertThat(captor.getValue()).hasSize(30);
}

@Test
@DisplayName("Should start from last work date plus one")
void extendShiftWorks_shouldStartFromLastWorkDatePlusOne() {
    // Arrange
    LocalDate lastDate = LocalDate.of(2024, 12, 1);
    when(shiftWorkRepo.findLastWorkDateByAssignmentId(100L))
        .thenReturn(lastDate);
    
    // Act & Assert - verify first date is lastDate + 1
}
```

**Validation - Input validation (2 tests)**

```java
@Test
@DisplayName("Should throw when assignment does not exist")
void extendShiftWorks_withNonExistingAssignmentId_shouldThrowIllegalArgumentException() {
    // Arrange
    when(repo.findById(999L)).thenReturn(Optional.empty());
    
    // Act & Assert
    assertThatThrownBy(() -> service.extendShiftWorks(999L, 30))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Phân công ca không tồn tại");
}

@Test
@DisplayName("Should throw when assignmentId is null")
void extendShiftWorks_withNullAssignmentId_shouldThrow() {
    // Similar structure
}
```

**Business rules - Days calculation (5 tests)**

```java
@Test
@DisplayName("Should not create works when days is zero")
void extendShiftWorks_withZeroDays_shouldNotCreateWorks() {
    // days = 0 → no works created
}

@Test
@DisplayName("Should handle gracefully when days is negative")
void extendShiftWorks_withNegativeDays_shouldHandleGracefully() {
    // days = -5 → no works created
}

@Test
@DisplayName("Should create one work when days is one")
void extendShiftWorks_withOneDay_shouldCreateOneSingleWork() {
    // days = 1 → exactly 1 work
}

@Test
@DisplayName("Should create all works when days is 365")
void extendShiftWorks_withLargeDays_shouldCreateAll() {
    // days = 365 → 365 works
}

@Test
@DisplayName("Should start from today when no existing works")
void extendShiftWorks_whenNoExistingWorks_shouldStartFromToday() {
    // lastDate = null → start from LocalDate.now()
}
```

**Edge cases (2 tests)**

```java
@Test
@DisplayName("Should default to today when last date is null")
void extendShiftWorks_whenLastDateIsNull_shouldDefaultToToday() {
    // Verify fallback logic
}

@Test
@DisplayName("Should call saveAll once for batch operation")
void extendShiftWorks_shouldCallSaveAllOnce() {
    // Performance verification
    verify(shiftWorkRepo, times(1)).saveAll(anyList());
}
```

**Transaction behavior (2 tests)**

```java
@Test
@DisplayName("Should rollback when saveAll fails")
void extendShiftWorks_whenRepoSaveAllFails_shouldRollback() {
    // Arrange
    when(shiftWorkRepo.saveAll(anyList()))
        .thenThrow(new RuntimeException("Batch save failed"));
    
    // Act & Assert
    assertThatThrownBy(() -> service.extendShiftWorks(100L, 30))
        .isInstanceOf(RuntimeException.class);
}

@Test
@DisplayName("Should throw when findById fails")
void extendShiftWorks_whenFindByIdFails_shouldThrow() {
    // Similar structure
}
```

### 3.4-3.8. Finder Methods → 1 test each (5 tests total)

```java
// findByShiftIdAndUserId
@Test
void findByShiftIdAndUserId_withExisting_shouldReturn() {
    // Basic happy path coverage
}

// findByShiftId
@Test
void findByShiftId_withMultipleAssignments_shouldReturnFirst() {
    // Basic happy path coverage
}

// findAllByShiftId
@Test
void findAllByShiftId_shouldReturnAllAssignments() {
    // Basic happy path coverage
}

// getLastWorkDate
@Test
void getLastWorkDate_withExistingWorks_shouldReturnMaxDate() {
    // Basic happy path coverage
}

// getRemainingWorkDays
@Test
void getRemainingWorkDays_withFutureWorks_shouldReturnCount() {
    // Basic happy path coverage
}
```

**Total ShiftAssignmentServiceImpl: 29 tests**

---

## 4. UserServiceImpl Tests (30 tests)

**File**: `src/test/java/vn/edu/fpt/pharma/service/impl/UserServiceImplTest.java`

### 4.1. Method `create()` → 15 tests ⭐ TEST ĐẦY ĐỦ

**Happy path (2 tests)**

```java
@Test
@DisplayName("Should create user with valid request")
void create_withValidRequest_shouldCreateUser() {
    // Arrange
    UserRequest request = UserTestBuilder.create().buildRequest();
    when(roleRepo.findById(4L)).thenReturn(Optional.of(role));
    when(userRepo.existsByUserNameIgnoreCase(any())).thenReturn(false);
    when(userRepo.save(any())).thenReturn(savedUser);
    
    // Act
    UserDto result = service.create(request);
    
    // Assert
    assertThat(result).isNotNull();
    verify(passwordEncoder).encode(any());
}

@Test
@DisplayName("Should update branch userId when creating manager")
void create_withManagerRole_shouldUpdateBranchUserId() {
    // Arrange - roleId = 3L (Manager)
    
    // Act & Assert - verify branchRepo.save() called
}
```

**Validation - Duplicate checking (4 tests)**

```java
@Test
@DisplayName("Should throw when username exists")
void create_whenUserNameExists_shouldThrowException() {
    // Arrange
    when(userRepo.existsByUserNameIgnoreCase("admin"))
        .thenReturn(true);
    
    // Act & Assert
    assertThatThrownBy(() -> service.create(request))
        .hasMessage("Tên đăng nhập đã tồn tại");
}

@Test
@DisplayName("Should throw when email exists")
void create_whenEmailExists_shouldThrowException() {
    // Similar - email duplicate
}

@Test
@DisplayName("Should throw when phone number exists")
void create_whenPhoneNumberExists_shouldThrowException() {
    // Similar - phone duplicate
}

@Test
@DisplayName("Should detect case-insensitive username duplicate")
void create_withCaseInsensitiveUserName_shouldDetectDuplicate() {
    // "Admin" vs "admin" → throw
}
```

**Business rule - One Manager per Branch (4 tests)**

```java
@Test
@DisplayName("Should throw when branch already has manager")
void create_whenBranchHasManager_shouldThrowException() {
    // Arrange
    when(userRepo.existsByRoleIdAndBranchIdAndDeletedFalse(3L, 1L))
        .thenReturn(true);
    
    // Act & Assert
    assertThatThrownBy(() -> service.create(managerRequest))
        .hasMessage("Chi nhánh này đã có Manager");
}

@Test
@DisplayName("Should allow manager creation when branch has no manager")
void create_whenBranchHasNoManager_shouldAllowManagerCreation() {
    // No existing manager → allow
}

@Test
@DisplayName("Should allow manager creation without branch")
void create_managerWithNullBranchId_shouldAllowCreation() {
    // Manager with branchId = null → allow
}

@Test
@DisplayName("Should allow non-manager with existing manager")
void create_nonManagerWithExistingManager_shouldAllow() {
    // Non-manager role → không check manager constraint
}
```

**Validation - Role and Password (3 tests)**

```java
@Test
@DisplayName("Should throw when role does not exist")
void create_withNonExistingRoleId_shouldThrowException() {
    // Arrange
    when(roleRepo.findById(999L)).thenReturn(Optional.empty());
    
    // Act & Assert
    assertThatThrownBy(() -> service.create(request))
        .hasMessage("Vai trò không tồn tại");
}

@Test
@DisplayName("Should encode password")
void create_shouldEncodePassword() {
    // Verify passwordEncoder.encode() called
}

@Test
@DisplayName("Should handle null password gracefully")
void create_withNullPassword_shouldHandleGracefully() {
    // password = null → handle without error
}
```

**Transaction behavior (2 tests)**

```java
@Test
@DisplayName("Should rollback when repo save fails")
void create_whenRepoSaveFails_shouldRollback() {
    // Arrange
    when(userRepo.save(any()))
        .thenThrow(new RuntimeException("Database error"));
    
    // Act & Assert
    assertThatThrownBy(() -> service.create(request))
        .isInstanceOf(RuntimeException.class);
}

@Test
@DisplayName("Should rollback user when branch update fails")
void create_whenBranchUpdateFails_shouldRollbackUser() {
    // Transaction rollback test
}
```

### 4.2. Method `update()` → 8 tests (business rules quan trọng)

**Happy path (1 test)**

```java
@Test
@DisplayName("Should update user with valid changes")
void update_withValidChanges_shouldUpdateUser() {
    // Basic update flow
}
```

**Validation - Duplicate checking (2 tests)**

```java
@Test
@DisplayName("Should throw when username changed to duplicate")
void update_whenUserNameChangedToDuplicate_shouldThrow() {
    // Change to existing username → throw
}

@Test
@DisplayName("Should allow update when keeping same username")
void update_whenKeepingSameUsername_shouldAllowUpdate() {
    // Same username → exclude self from check
}
```

**Business rule - Manager role change (2 tests)**

```java
@Test
@DisplayName("Should throw when changing to manager role and branch has manager")
void update_changingToManagerRole_whenBranchHasManager_shouldThrow() {
    // Change role to Manager + branch has manager → throw
}

@Test
@DisplayName("Should allow changing to manager role when branch has no manager")
void update_changingToManagerRole_whenBranchNoManager_shouldSucceed() {
    // Change to Manager + no existing manager → allow
}
```

**Business rule - Manager branch change (2 tests)**

```java
@Test
@DisplayName("Should throw when manager changes branch and target has manager")
void update_managerChangingBranch_whenTargetHasManager_shouldThrow() {
    // Manager changes branch + target has manager → throw
}

@Test
@DisplayName("Should allow manager changing branch when target has no manager")
void update_managerChangingBranch_whenTargetNoManager_shouldSucceed() {
    // Manager changes branch + no existing manager → allow
}
```

**Edge cases (1 test)**

```java
@Test
@DisplayName("Should not change password when password is null")
void update_withNullPassword_shouldNotChangePassword() {
    // password = null → keep old password
}
```

### 4.3. Method `delete()` → 7 tests ⭐ TEST ĐẦY ĐỦ

**Happy path (1 test)**

```java
@Test
@DisplayName("Should soft delete regular user")
void delete_withRegularUser_shouldSoftDelete() {
    // Non-manager deletion
}
```

**Validation - ShiftAssignment business rule (2 tests)**

```java
@Test
@DisplayName("Should throw when user has active shift assignment")
void delete_whenUserHasActiveShiftAssignment_shouldThrow() {
    // Arrange
    when(shiftAssignmentRepo.existsByUserIdAndDeletedFalse(1L))
        .thenReturn(true);
    
    // Act & Assert
    assertThatThrownBy(() -> service.delete(1L))
        .hasMessage("Nhân viên đang trong một ca làm việc, không thể xóa");
}

@Test
@DisplayName("Should allow deletion when user has no shift assignment")
void delete_whenUserHasNoShiftAssignment_shouldSucceed() {
    // No shift assignment → allow delete
}
```

**Business rule - Manager specific (3 tests)**

```java
@Test
@DisplayName("Should remove from branch when manager deleted")
void delete_whenManagerDeleted_shouldRemoveFromBranch() {
    // Manager (roleId=3) → clear branch.userId
    verify(branchRepo).save(argThat(b -> b.getUserId() == null));
}

@Test
@DisplayName("Should still delete manager not in branch")
void delete_whenManagerNotInBranch_shouldStillDelete() {
    // Manager without branch → still delete
}

@Test
@DisplayName("Should not affect branch when deleting non-manager")
void delete_nonManagerUser_shouldNotAffectBranch() {
    // Non-manager → branch not touched
    verify(branchRepo, never()).save(any());
}
```

**Edge cases (1 test)**

```java
@Test
@DisplayName("Should still delete user when branch not found")
void delete_whenBranchNotFound_shouldStillDeleteUser() {
    // Branch missing → still delete user
}
```

**Total UserServiceImpl: 30 tests**

---

## 5. RevenueReportServiceImpl Tests (12 tests)

**File**: `src/test/java/vn/edu/fpt/pharma/service/impl/RevenueReportServiceImplTest.java`

### 5.1. Method `getRevenueReport()` → 12 tests (business logic chính)

**Happy path - Mode variations (3 tests)**

```java
@Test
@DisplayName("Should return daily report with mode=day")
void getRevenueReport_withModeDay_shouldReturnDailyReport() {
    // mode = "day" → daily date range (today 00:00 to 23:59)
}

@Test
@DisplayName("Should return weekly report with mode=week")
void getRevenueReport_withModeWeek_shouldReturnWeeklyReport() {
    // mode = "week" → weekly range (Monday to Sunday)
}

@Test
@DisplayName("Should return monthly report with mode=month")
void getRevenueReport_withModeMonth_shouldReturnMonthlyReport() {
    // mode = "month" → monthly range (first to last day)
}
```

**Validation - Date parsing (3 tests)**

```java
@Test
@DisplayName("Should parse valid date correctly")
void getRevenueReport_withValidDate_shouldParseCorrectly() {
    // date = "2024-12-05" → parse success
}

@Test
@DisplayName("Should use today when date is null")
void getRevenueReport_withNullDate_shouldUseToday() {
    // date = null → fallback to LocalDate.now()
}

@Test
@DisplayName("Should fallback to today with invalid date")
void getRevenueReport_withInvalidDate_shouldFallbackToToday() {
    // date = "invalid" → fallback to today
}
```

**Business rules - Filters (2 tests)**

```java
@Test
@DisplayName("Should pass shift filter to repository")
void getRevenueReport_withShiftFilter_shouldPassToRepo() {
    // Verify repo called with shift parameter
}

@Test
@DisplayName("Should pass employee filter to repository")
void getRevenueReport_withEmployeeFilter_shouldPassToRepo() {
    // Verify repo called with employeeId parameter
}
```

**Business rules - KPI calculations (2 tests)**

```java
@Test
@DisplayName("Should calculate KPIs correctly")
void getRevenueReport_shouldCalculateKPIs() {
    // Verify totalInvoices, totalRevenue, totalProfit from KPI
}

@Test
@DisplayName("Should include top categories with percentages")
void getRevenueReport_shouldIncludeTopCategories() {
    // Top 6 categories with percentage calculation
}
```

**Edge cases (2 tests)**

```java
@Test
@DisplayName("Should handle gracefully when branchId is null")
void getRevenueReport_withNullBranchId_shouldHandleGracefully() {
    // branchId = null → return zero stats
}

@Test
@DisplayName("Should return zero stats when no data")
void getRevenueReport_withNoData_shouldReturnZeroStats() {
    // Empty result → zero values for all KPIs
}
```

**Total RevenueReportServiceImpl: 12 tests**

---

## 6. InventoryReportServiceImpl Tests (5 tests)

**File**: `src/test/java/vn/edu/fpt/pharma/service/impl/InventoryReportServiceImplTest.java`

### Coverage Only Tests (1 test per method)

```java
@Test
@DisplayName("Should return inventory summary")
void getInventorySummary_withValidBranchId_shouldReturnSummary() {
    // Basic happy path coverage
}

@Test
@DisplayName("Should filter inventory by medicine name")
void searchInventory_withValidQuery_shouldFilterByMedicineName() {
    // Basic happy path coverage
}

@Test
@DisplayName("Should return all categories with statistics")
void getCategoryStatistics_shouldReturnAllCategories() {
    // Basic happy path coverage
}

@Test
@DisplayName("Should return all inventory fields")
void getInventoryDetails_shouldReturnAllFields() {
    // Basic happy path coverage
}

@Test
@DisplayName("Should return category id and name")
void getAllCategories_shouldReturnIdAndName() {
    // Basic happy path coverage
}
```

**Total InventoryReportServiceImpl: 5 tests**

---

## 7. Utility Class Tests (Optional - for 100% coverage)

### DateTimeFormatterUtils Test

**File**: `src/test/java/vn/edu/fpt/pharma/util/DateTimeFormatterUtilsTest.java`

```java
@Test
@DisplayName("Should format LocalDateTime correctly")
void formatDateTime_withValidDateTime_shouldFormatCorrectly() {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.of(2024, 12, 7, 10, 30);
    
    // Act
    String result = DateTimeFormatterUtils.formatDateTime(dateTime);
    
    // Assert
    assertThat(result).isEqualTo("2024/12/07 10:30");
}

@Test
@DisplayName("Should return empty string when datetime is null")
void formatDateTime_withNull_shouldReturnEmptyString() {
    // Act
    String result = DateTimeFormatterUtils.formatDateTime(null);
    
    // Assert
    assertThat(result).isEmpty();
}
```

---

## Test Organization Best Practices

### Nested Classes by Method

```java
@DisplayName("ShiftServiceImpl Tests")
class ShiftServiceImplTest extends BaseServiceTest {
    
    @Nested
    @DisplayName("save() tests")
    class SaveTests {
        // All save-related tests here
    }
    
    @Nested
    @DisplayName("listShifts() tests")
    class ListShiftsTests {
        // All listShifts-related tests here
    }
}
```

### Test Naming Convention

- Method pattern: `methodName_condition_expectedBehavior`
- Use `@DisplayName` for Vietnamese descriptions
- Group by: Happy path → Validation → Business rules → Transaction → Edge cases

### Assertions

Use AssertJ for fluent assertions:

```java
assertThat(result).isNotNull();
assertThat(result.getName()).isEqualTo("Ca sáng");
assertThat(list).hasSize(5).extracting("name").contains("Ca sáng");
assertThatThrownBy(() -> service.save(...))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessage("Giờ kết thúc phải lớn hơn giờ bắt đầu");
```

---

## Summary

**Total Tests: ~78 tests**

| Service | Methods | Tests | Strategy |
|---------|---------|-------|----------|
| ShiftServiceImpl | 6 | 22 | 1 đầy đủ, 5 coverage |
| ShiftAssignmentServiceImpl | 8 | 29 | 2 đầy đủ, 6 coverage |
| UserServiceImpl | 3 | 30 | 3 đầy đủ/vừa phải |
| RevenueReportServiceImpl | 1 | 12 | 1 đầy đủ |
| InventoryReportServiceImpl | 5 | 5 | 5 coverage only |

**Ưu điểm:**
- ✅ Đạt 100% coverage với số lượng tests tối ưu
- ✅ Tập trung test kỹ các business logic quan trọng
- ✅ Giảm 73% effort so với test toàn bộ
- ✅ Dễ maintain và extend
- ✅ Fast execution (<20s)

**Execution:**
```bash
./gradlew test jacocoTestReport
```

