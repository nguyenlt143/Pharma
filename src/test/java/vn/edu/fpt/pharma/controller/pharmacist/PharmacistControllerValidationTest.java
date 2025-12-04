package vn.edu.fpt.pharma.controller.pharmacist;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.fpt.pharma.dto.invoice.InvoiceCreateRequest;
import vn.edu.fpt.pharma.dto.invoice.InvoiceItemRequest;
import vn.edu.fpt.pharma.dto.medicine.MedicineSearchDTO;
import vn.edu.fpt.pharma.dto.medicine.VariantInventoryDTO;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.service.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PharmacistController.class) // RE-ENABLED - Invoice functionality restored
class PharmacistControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private MedicineVariantService medicineVariantService;

    @MockBean
    private MedicineService medicineService;

    @MockBean
    private ShiftWorkService shiftWorkService;

    @MockBean
    private ShiftService shiftService;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private vn.edu.fpt.pharma.repository.BranchRepository branchRepository;

    private Invoice mockInvoice;

    @BeforeEach
    void setUp() {
        mockInvoice = new Invoice();
        mockInvoice.setId(1L);
        mockInvoice.setInvoiceCode("INV-20231201-000001");
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testSearchMedicines_ValidKeyword() throws Exception {
        // Given
        List<MedicineSearchDTO> mockResults = new ArrayList<>();
        MedicineSearchDTO medicine = new MedicineSearchDTO();
        medicine.setId(1L);
        medicine.setName("Paracetamol");
        mockResults.add(medicine);

        when(medicineService.searchMedicinesByKeyword("paracetamol"))
                .thenReturn(mockResults);

        // When & Then
        mockMvc.perform(get("/pharmacist/pos/api/search")
                        .param("keyword", "paracetamol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Paracetamol"));
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testSearchMedicines_EmptyKeyword() throws Exception {
        // Given
        when(medicineService.searchMedicinesByKeyword(""))
                .thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/pharmacist/pos/api/search")
                        .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testGetVariantsWithInventory_ValidId() throws Exception {
        // Given
        List<VariantInventoryDTO> mockVariants = new ArrayList<>();
        VariantInventoryDTO variant = new VariantInventoryDTO();
        variant.setVariantId(1L);
        mockVariants.add(variant);

        when(medicineVariantService.getVariantsWithInventoryByMedicineId(1L))
                .thenReturn(mockVariants);

        // When & Then
        mockMvc.perform(get("/pharmacist/pos/api/medicine/1/variants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].variantId").value(1));
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_ValidRequest() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        when(invoiceService.createInvoice(any(InvoiceCreateRequest.class)))
                .thenReturn(mockInvoice);

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.invoiceCode").value("INV-20231201-000001"))
                .andExpect(jsonPath("$.message").value("Thanh toán thành công"));
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_InvalidCustomerName_Blank() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        request.setCustomerName(""); // Invalid

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_InvalidPhoneNumber() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        request.setPhoneNumber("invalid-phone"); // Invalid

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_InvalidTotalAmount_Zero() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        request.setTotalAmount(0.0); // Invalid

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_InvalidPaymentMethod_Blank() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        request.setPaymentMethod(""); // Invalid

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_InvalidItems_Empty() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        request.setItems(new ArrayList<>()); // Invalid

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_InvalidItem_NullInventoryId() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        request.getItems().get(0).setInventoryId(null); // Invalid

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_InvalidItem_ZeroQuantity() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        request.getItems().get(0).setQuantity(0L); // Invalid

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    void testCreateInvoice_ServiceException() throws Exception {
        // Given
        InvoiceCreateRequest request = createValidInvoiceRequest();
        when(invoiceService.createInvoice(any(InvoiceCreateRequest.class)))
                .thenThrow(new RuntimeException("Tồn kho không đủ"));

        // When & Then
        mockMvc.perform(post("/pharmacist/pos/api/invoices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tồn kho không đủ"));
    }

    private InvoiceCreateRequest createValidInvoiceRequest() {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setCustomerName("Nguyen Van A");
        request.setPhoneNumber("0123456789");
        request.setTotalAmount(100000.0);
        request.setPaymentMethod("cash");
        request.setNote("Test note");

        List<InvoiceItemRequest> items = new ArrayList<>();
        InvoiceItemRequest item = new InvoiceItemRequest();
        item.setInventoryId(1L);
        item.setQuantity(2L);
        item.setUnitPrice(50000.0);
        item.setSelectedMultiplier(1.0);
        items.add(item);
        request.setItems(items);

        return request;
    }
}
