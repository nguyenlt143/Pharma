package vn.edu.fpt.pharma.testutil;

import vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest;
import vn.edu.fpt.pharma.dto.invoice.InvoiceItemRequest;
import vn.edu.fpt.pharma.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Simple factory for creating test data objects
 * Provides default valid test data for common entities
 */
public class TestDataFactory {

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("0123456789");
        user.setPassword("encodedPassword");
        user.setUserName("testuser");
        user.setBranchId(1L);
        user.setDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(1L);
        return user;
    }

    public static Inventory createInventory() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(100L);
        inventory.setDeleted(false);

        // Set variant and batch to avoid NullPointerException
        MedicineVariant variant = new MedicineVariant();
        variant.setId(1L);

        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Test Medicine");
        variant.setMedicine(medicine);

        Batch batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH001");
        batch.setExpiryDate(LocalDate.now().plusYears(1));

        inventory.setVariant(variant);
        inventory.setBatch(batch);

        return inventory;
    }

    public static InvoiceCreateRequest createInvoiceRequest() {
        return InvoiceCreateRequest.builder()
                .customerName("John Doe")
                .phoneNumber("0123456789")
                .paymentMethod("cash")
                .totalAmount(100000.0)
                .items(List.of(createInvoiceItem()))
                .build();
    }

    public static InvoiceItemRequest createInvoiceItem() {
        return InvoiceItemRequest.builder()
                .inventoryId(1L)
                .quantity(5L)
                .unitPrice(20000.0)
                .selectedMultiplier(1.0)
                .build();
    }

    public static Invoice createInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceCode("INV-001");
        invoice.setTotalPrice(100000.0);
        invoice.setPaymentMethod("cash");
        invoice.setInvoiceType(InvoiceType.PAID);
        invoice.setUserId(1L);
        invoice.setBranchId(1L);
        invoice.setDeleted(false);
        invoice.setCreatedAt(LocalDateTime.now());
        return invoice;
    }

    public static MedicineVariant createMedicineVariant() {
        MedicineVariant variant = new MedicineVariant();
        variant.setId(1L);

        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Test Medicine");
        medicine.setActiveIngredient("Paracetamol");

        variant.setMedicine(medicine);
        variant.setFormulation("Viên nén");
        variant.setDosage("500mg");
        variant.setDeleted(false);

        return variant;
    }

    public static Shift createShift() {
        Shift shift = new Shift();
        shift.setId(1L);
        shift.setName("Ca sáng");
        shift.setBranchId(1L);
        shift.setDeleted(false);
        return shift;
    }

    public static ShiftWork createShiftWork() {
        ShiftWork shiftWork = new ShiftWork();
        shiftWork.setId(1L);
        shiftWork.setUserId(1L);
        shiftWork.setShiftId(1L);
        shiftWork.setWorkDate(LocalDate.now());
        shiftWork.setDeleted(false);
        return shiftWork;
    }

    public static Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setPhoneNumber("0123456789");
        customer.setDeleted(false);
        return customer;
    }
}

