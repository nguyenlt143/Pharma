package vn.edu.fpt.pharma.constant;

import java.util.HashMap;
import java.util.Map;

public enum DosageForm {
    VIEN_NEN("Viên nén", "Viên"),
    VIEN_NANG("Viên nang", "Viên"),
    VIEN_NOI("Viên nội", "Viên"),
    SIRO("Siro", "Chai"),
    DUNG_DICH("Dung dịch", "Chai"),
    TIEM("Thuốc tiêm", "Ống"),
    BOT("Thuốc bột", "Gói"),
    COM("Cốm", "Gói"),
    CREAM("Kem/Gel", "Tuýp"),
    MOI("Thuốc mỡ", "Tuýp"),
    NHOM_GIOT("Thuốc nhỏ giọt", "Chai"),
    VITAMIN("Vitamin", "Viên"),
    KHANG_SINH("Kháng sinh", "Viên"),
    KHAC("Khác", "Đơn vị");

    private final String displayName;
    private final String baseUnitName;

    private static final Map<String, DosageForm> BY_DISPLAY_NAME = new HashMap<>();
    private static final Map<DosageForm, String> BASE_UNIT_MAP = new HashMap<>();

    static {
        for (DosageForm form : values()) {
            BY_DISPLAY_NAME.put(form.displayName.toLowerCase(), form);
            BASE_UNIT_MAP.put(form, form.baseUnitName);
        }
    }

    DosageForm(String displayName, String baseUnitName) {
        this.displayName = displayName;
        this.baseUnitName = baseUnitName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBaseUnitName() {
        return baseUnitName;
    }

    public static DosageForm fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return KHAC;
        }
        return BY_DISPLAY_NAME.getOrDefault(displayName.toLowerCase().trim(), KHAC);
    }

    public static String getBaseUnitForForm(DosageForm form) {
        return BASE_UNIT_MAP.getOrDefault(form, "Đơn vị");
    }

    public static boolean isValidBaseUnit(DosageForm form, String unitName) {
        if (form == null || unitName == null) {
            return false;
        }
        return BASE_UNIT_MAP.get(form).equalsIgnoreCase(unitName.trim());
    }
}

