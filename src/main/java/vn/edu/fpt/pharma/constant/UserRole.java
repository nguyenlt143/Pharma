package vn.edu.fpt.pharma.constant;

public enum UserRole {
    SYSTEM_ADMIN("System Administrator"),
    BRANCH_MANAGER("Branch Manager"),
    BRANCH_WAREHOUSE("Branch Warehouse Staff"),
    GENERAL_WAREHOUSE("General Warehouse Staff"),
    BUSINESS_OWNER("Business Owner"),
    PHARMACIST("Pharmacist");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}