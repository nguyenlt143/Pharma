
-- ============================================================================
-- DATABASE INDEXES - Organized by table dependency hierarchy
-- Run this AFTER table creation but BEFORE data insertion
-- ============================================================================

-- ============================================================================
-- LEVEL 1: Independent/Reference Tables
-- ============================================================================

-- BRANCHES - No foreign keys
-- (No additional indexes needed beyond primary key)

-- ROLES - No foreign keys
-- (No additional indexes needed beyond primary key)

-- CATEGORYS - No foreign keys
-- (No additional indexes needed beyond primary key)

-- UNITS - No foreign keys
-- (No additional indexes needed beyond primary key)

-- SUPPLIERS - No foreign keys
-- (No additional indexes needed beyond primary key)

-- ============================================================================
-- LEVEL 2: Tables with one level of dependency
-- ============================================================================

-- USERS - Depends on: roles, branches
CREATE INDEX idx_invoice_branch_date ON invoices(branch_id, created_at);

-- PRICE
-- MEDICINES - Depends on: categorys

-- SHIFT
-- SHIFTS - Depends on: branches
CREATE INDEX idx_shift_branch ON shifts(branch_id);

-- REQUEST_FORMS - Depends on: branches
CREATE INDEX idx_request_branch ON request_forms(branch_id);

-- ============================================================================
-- LEVEL 3: Tables with multiple levels of dependency
-- ============================================================================

-- MEDICINE_VARIANT - Depends on: medicines, units
CREATE INDEX idx_variant_medicine ON medicine_variant(medicine_id);
CREATE INDEX idx_variant_base_unit ON medicine_variant(base_unit_id);

-- BATCHES - Depends on: medicine_variant, suppliers

CREATE INDEX idx_batch_supplier ON batches(supplier_id);

CREATE INDEX idx_batch_production_date ON batches(expiry_date);

-- PRICES - Depends on: medicine_variant, branches
CREATE INDEX idx_price_variant ON prices(variant_id);
CREATE INDEX idx_price_branch ON prices(branch_id);
CREATE INDEX idx_price_variant_branch ON prices(variant_id, branch_id);

-- INVENTORY_MOVEMENTS - Depends on: branches

CREATE INDEX idx_movement_source_branch ON inventory_movements(source_branch_id);
CREATE INDEX idx_movement_dest_branch ON inventory_movements(destination_branch_id);

CREATE INDEX idx_movement_created_at ON inventory_movements(created_at);

-- ============================================================================
-- LEVEL 4: Tables with deepest dependency levels
-- ============================================================================

-- INVENTORY - Depends on: branches, medicine_variant, batches
CREATE INDEX idx_inventory_branch ON inventory(branch_id);
CREATE INDEX idx_inventory_variant ON inventory(variant_id);
CREATE INDEX idx_inventory_batch ON inventory(batch_id);
CREATE INDEX idx_inventory_branch_variant ON inventory(branch_id, variant_id);
CREATE INDEX idx_inventory_branch_deleted ON inventory(branch_id, deleted);
CREATE INDEX idx_inventory_quantity ON inventory(quantity);

-- INVENTORY_MOVEMENT_DETAILS - Depends on: inventory_movements, batches
