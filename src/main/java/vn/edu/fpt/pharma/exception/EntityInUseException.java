package vn.edu.fpt.pharma.exception;

public class EntityInUseException extends RuntimeException {
    private final String entityName;
    private final String usageMessage;

    public EntityInUseException(String entityName, String usageMessage) {
        super(String.format("%s đang được sử dụng trong %s. Không thể xóa bản ghi này.", entityName, usageMessage));
        this.entityName = entityName;
        this.usageMessage = usageMessage;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getUsageMessage() {
        return usageMessage;
    }
}
