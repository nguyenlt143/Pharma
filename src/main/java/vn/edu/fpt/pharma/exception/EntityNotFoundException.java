package vn.edu.fpt.pharma.exception;

public class EntityNotFoundException extends RuntimeException {
    private final String entityName;
    private final Object entityId;

    public EntityNotFoundException(String entityName, Object entityId) {
        super(String.format("%s không tìm thấy với ID: %s", entityName, entityId));
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String entityName) {
        super(String.format("%s không tìm thấy", entityName));
        this.entityName = entityName;
        this.entityId = null;
    }

    public String getEntityName() {
        return entityName;
    }

    public Object getEntityId() {
        return entityId;
    }
}

