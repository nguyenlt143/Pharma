# Decision Table Test Plan - Validation Coverage

## Objective

Apply Decision Table Testing methodology (similar to Test Requirement 12) to current validation logic in the project. This plan expands validation test coverage from 40% to 100% using systematic condition coverage.

## Current State Analysis

### Existing Validation Tests
Total Tests: 216 tests (100% passing)
- Password validation: 3 tests (44% coverage)
- Phone validation: 4 tests (20% coverage)
- Other validations: 0 tests (0% coverage)
Total Validation Coverage: ~15%
Target: 100%
### Validation Sources
**DTO Layer - Jakarta Bean Validation:**
- UserRequest: userName, fullName, phoneNumber, email, roleId, password
- SupplierRequest: supplierName, phone, address
- CategoryRequest: name, description
- MedicineRequest: name, ingredients, prescriptionRequired
- PriceRequest: variantId, price, effectiveDate
**Service Layer - Business Logic Validation:**
- Duplicate checking (username, email, phone)
- Entity existence verification
- Business rule enforcement
## Decision Table Matrices for All Functions

### TR_USER_01: UserService.create() - User Creation

**Function Signature:** `UserDto create(UserRequest req)`

**Preconditions:**
- Database connection available
- PasswordEncoder configured
- Role repository accessible

**Conditions:**
- A: userName (unique, @Size 4-30)
- B: email (unique, valid format)
- C: phoneNumber (unique, valid format)
- D: password (@Size 6-100)
- E: roleId (exists in DB)
- F: branchId (optional, valid if provided)
- G: Manager constraint (1 per branch if roleId=3)

**Decision Table Matrix (Simplified):**

| TC ID | userName | email | phoneNumber | password | roleId | branchId | Expected | Type | Status |
|-------|----------|-------|-------------|----------|--------|----------|----------|------|--------|
| TC_01 | Valid_new | Valid_new | Valid_new | Valid | Valid | Valid | SUCCESS | N | ✅ |
| TC_02 | Duplicate | Valid | Valid | Valid | Valid | Valid | EXCEPTION | A | ✅ |
| TC_03 | Valid_new | Valid_new | Valid_new | Valid | Invalid | Valid | EXCEPTION | A | ✅ |

**Coverage:** 3/3 core scenarios (100% simplified) ✅
**Note:** Full 15 test cases implemented in code, showing only key scenarios here

---

### TR_USER_02: UserService.update() - User Update

**Function Signature:** `UserDto update(Long id, UserRequest req)`

**Preconditions:**
- User exists in database
- User is not deleted

**Conditions:**
- A: userId (exists)
- B: userName (unique if changed)
- C: email (unique if changed)
- D: phoneNumber (unique if changed)
- E: password (optional update)
- F: roleId change (from non-Manager to Manager)
- G: branchId change (for Manager role)
- H: Target branch has Manager

**Decision Table Matrix (Simplified):**

| TC ID | userId | userName | email | password | Expected | Type | Status |
|-------|--------|----------|-------|----------|----------|------|--------|
| TC_01 | Valid | Changed_new | Changed_new | Valid | SUCCESS | N | ⏳ |
| TC_02 | Valid | Changed_dup | Unchanged | null | EXCEPTION | A | ⏳ |
| TC_03 | Invalid | Any | Any | Any | EXCEPTION | A | ⏳ |

**Coverage:** 0/3 core scenarios

---

### TR_USER_03: UserService.delete() - User Deletion

**Function Signature:** `void delete(Long id)`

**Preconditions:**
- User exists in database

**Conditions:**
- A: userId (exists)
- B: hasActiveShiftAssignment
- C: isManager (roleId = 3)
- D: hasBranch (branchId not null)

**Decision Table Matrix (Simplified):**

| TC ID | userId | hasActiveShift | Expected | Type | Status |
|-------|--------|----------------|----------|------|--------|
| TC_01 | Valid | false | SUCCESS | N | ✅ |
| TC_02 | Valid | true | EXCEPTION | A | ✅ |
| TC_03 | Invalid | Any | EXCEPTION | A | ⏳ |

**Coverage:** 2/3 core scenarios

---

### TR_SUPPLIER_01: SupplierService.createSupplier() - Supplier Creation

**Function Signature:** `SupplierResponse createSupplier(SupplierRequest request)`

**Conditions:**
- A: supplierName (@NotBlank)
- B: phone (optional, valid format if provided)
- C: address (optional)

**Decision Table Matrix (Simplified):**

| TC ID | supplierName | phone | Expected | Type | Status |
|-------|--------------|-------|----------|------|--------|
| TC_01 | Valid | Valid | SUCCESS | N | ✅ |
| TC_02 | null | Valid | EXCEPTION | A | ⏳ |
| TC_03 | Valid | Invalid | EXCEPTION | A | ⏳ |

**Coverage:** 1/3 core scenarios

---

### TR_SUPPLIER_02: SupplierService.updateSupplier() - Supplier Update

**Decision Table Matrix (Simplified):**

| TC ID | supplierId | supplierName | Expected | Type | Status |
|-------|------------|--------------|----------|------|--------|
| TC_01 | Valid | Changed | SUCCESS | N | ✅ |
| TC_02 | Invalid | Any | EXCEPTION | A | ✅ |

**Coverage:** 2/2 core scenarios ✅

---

### TR_SUPPLIER_03: SupplierService.deleteById() - Supplier Deletion

**Decision Table Matrix (Simplified):**

| TC ID | supplierId | hasBatches | Expected | Type | Status |
|-------|------------|------------|----------|------|--------|
| TC_01 | Valid | 0 | SUCCESS | N | ✅ |
| TC_02 | Valid | >0 | EXCEPTION | A | ✅ |
| TC_03 | Invalid | Any | EXCEPTION | A | ⏳ |

**Coverage:** 2/3 core scenarios

---

### TR_PRICE_01: PriceService.createPrice() - Price Creation

**Function Signature:** `PriceResponse createPrice(PriceRequest request)`

**Conditions:**
- A: variantId (exists in DB)
- B: price (positive, valid format)
- C: effectiveDate (valid date)

**Decision Table Matrix (Simplified):**

| TC ID | variantId | price | Expected | Type | Status |
|-------|-----------|-------|----------|------|--------|
| TC_01 | Valid | Valid | SUCCESS | N | ✅ |
| TC_02 | Invalid | Valid | EXCEPTION | A | ✅ |
| TC_03 | Valid | 0 | EXCEPTION | B | ⏳ |

**Coverage:** 2/3 core scenarios

---

### TR_PRICE_02: PriceService.updatePrice() - Price Update

**Decision Table Matrix (Simplified):**

| TC ID | priceId | price | Expected | Type | Status |
|-------|---------|-------|----------|------|--------|
| TC_01 | Valid | Changed | SUCCESS | N | ✅ |
| TC_02 | Invalid | Any | EXCEPTION | A | ✅ |
| TC_03 | Valid | 0 | EXCEPTION | B | ⏳ |

**Coverage:** 2/3 core scenarios

---

### TR_SHIFT_01: ShiftService.create() - Shift Creation

**Decision Table Matrix (Simplified):**

| TC ID | name | startTime | endTime | Expected | Type | Status |
|-------|------|-----------|---------|----------|------|--------|
| TC_01 | Valid_new | Valid | Valid_after | SUCCESS | N | ✅ |
| TC_02 | Duplicate | Valid | Valid_after | EXCEPTION | A | ✅ |
| TC_03 | Valid | Valid | Before_start | EXCEPTION | B | ⏳ |

**Coverage:** 2/3 core scenarios

---

### TR_SHIFT_ASSIGNMENT_01: ShiftAssignmentService.assignUserToShift() - Assign User

**Decision Table Matrix (Simplified):**

| TC ID | shiftId | userId | alreadyAssigned | Expected | Type | Status |
|-------|---------|--------|-----------------|----------|------|--------|
| TC_01 | Valid | Valid | false | SUCCESS | N | ✅ |
| TC_02 | Valid | Valid | true | EXCEPTION | A | ✅ |
| TC_03 | Invalid | Valid | false | EXCEPTION | A | ✅ |

**Coverage:** 3/3 core scenarios ✅

---

### TR_INVOICE_01: InvoiceService.createInvoice() - Invoice Creation

**Decision Table Matrix (Simplified):**

| TC ID | customerName | items | quantity | Expected | Type | Status |
|-------|--------------|-------|----------|----------|------|--------|
| TC_01 | Valid | Non-empty | Valid_inStock | SUCCESS | N | ✅ |
| TC_02 | null | Non-empty | Valid | EXCEPTION | A | ⏳ |
| TC_03 | Valid | Non-empty | Exceed_stock | EXCEPTION | A | ⏳ |

**Coverage:** 1/3 core scenarios

---

## Summary of All Decision Tables

### Total Coverage Across All Functions

| Function | Test Cases | Covered | Remaining | Coverage % |
|----------|-----------|---------|-----------|------------|
| UserService.create() | 15 | 15 ✅ | 0 | 100% ✅ |
| UserService.update() | 11 | 0 | 11 | 0% |
| UserService.delete() | 7 | 5 | 2 | 71% |
| SupplierService.createSupplier() | 7 | 3 | 4 | 43% |
| SupplierService.updateSupplier() | 5 | 3 | 2 | 60% |
| SupplierService.deleteById() | 5 | 4 | 1 | 80% |
| PriceService.createPrice() | 7 | 3 | 4 | 43% |
| PriceService.updatePrice() | 6 | 3 | 3 | 50% |
| ShiftService.create() | 8 | 3 | 5 | 38% |
| ShiftAssignmentService.assign() | 7 | 5 | 2 | 71% |
| InvoiceService.createInvoice() | 8 | 1 | 7 | 13% |
| **TOTAL** | **86** | **45** | **41** | **52%** |

**🎉 Phase 1 Complete: UserService.create() achieved 100% coverage (+9 tests)**

### Priority Implementation Order

**High Priority (Critical Business Functions):**
1. UserService.create() - 9 tests needed
2. InvoiceService.createInvoice() - 7 tests needed
3. UserService.update() - 11 tests needed

**Medium Priority (Important Operations):**
4. ShiftService.create() - 5 tests needed
5. SupplierService.createSupplier() - 4 tests needed
6. PriceService.createPrice() - 4 tests needed

**Low Priority (Good to Have):**
7. PriceService.updatePrice() - 3 tests needed
8. UserService.delete() - 2 tests needed
9. ShiftAssignmentService.assign() - 2 tests needed
10. SupplierService.updateSupplier() - 2 tests needed
11. SupplierService.deleteById() - 1 test needed

---

## Test Requirement: TR_USER_01 - UserRequest Validation
