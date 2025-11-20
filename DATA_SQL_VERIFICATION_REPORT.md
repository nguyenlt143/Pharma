# Data.sql Verification Report

## Date: November 20, 2025

## Overview
This report documents the verification and corrections made to `data.sql` based on the business logic defined in `flow.txt`.

## Tables Verified and Completed

### 1. ✅ BATCHES
- **Status**: Complete
- **Records**: 21 batches (variants 1-21)
- **Verification**: All batches have proper:
  - batch_code
  - mfg_date and expiry_date
  - variant_id and supplier_id
  - batch_status (ACTIVE, EXHAUSTED, EXPIRED, DISPOSED)

### 2. ✅ INVENTORY_MOVEMENTS
- **Status**: Complete with corrections
- **Movement Types Implemented**:
  - **SUP_TO_WARE**: 10 movements (Supplier → Warehouse)
  - **WARE_TO_BR**: 31 movements (Warehouse → Branch)
  - **BR_TO_WARE**: 3 movements (Branch → Warehouse returns)
  - **WARE_TO_SUP**: 2 movements (Warehouse → Supplier returns)
  - **DISPOSAL**: 2 movements (Disposal/destruction)

### 3. ✅ INVENTORY_MOVEMENT_DETAILS
- **Status**: Complete with price corrections
- **Corrections Made**:
  
  #### Price vs Snap_Cost Logic (per flow.txt):
  - **SUP_TO_WARE**: price = snap_cost (both = supplier cost)
  - **WARE_TO_BR**: 
    - price = branch_price (warehouse selling to branch)
    - snap_cost = original supplier cost
  - **BR_TO_WARE**: price = snap_cost (preserve cost)
  - **WARE_TO_SUP**: price = snap_cost (original supplier cost)

  #### Specific Corrections:
  1. **Movement 16** (WARE_TO_BR):
     - Variant 7: snap_cost corrected from 104000 → 70000 (original supplier cost)
     - Variant 8: snap_cost corrected from 155000 → 90000 (original supplier cost)
  
  2. **Movements 25-26** (WARE_TO_BR):
     - Variant 7: snap_cost corrected from 104000 → 70000
     - Variant 8: snap_cost corrected from 155000 → 90000
  
  3. **Movements 27-28** (WARE_TO_BR):
     - Variant 9: snap_cost corrected from 60000 → 40000
     - Variant 10: snap_cost corrected from 30000 → 25000
  
  4. **Movements 35-36** (WARE_TO_BR):
     - Variant 7: snap_cost corrected from 104000 → 70000
     - Variant 8: snap_cost corrected from 155000 → 90000
  
  5. **Movements 37-38** (WARE_TO_BR):
     - Variant 9: snap_cost corrected from 60000 → 40000
     - Variant 10: snap_cost corrected from 30000 → 25000
  
  6. **Movement 45** (WARE_TO_SUP):
     - Variant 10: price and snap_cost corrected from 30000 → 25000

### 4. ✅ INVENTORY
- **Status**: Complete and expanded
- **Warehouse (branch_id=1)**: 22 variants with inventory
- **Branch 3 Hanoi (branch_id=3)**: 16 variants with inventory
- **Key Fields**:
  - unit_price: Set from movement_details.price when receiving (WARE_TO_BR)
  - quantity: Calculated from movements (received - sent)
  - last_movement_id: References last movement affecting this inventory

**Added Inventory Records**:
- Warehouse: Added variants 11-22 (previously only 1-5)
- Branch 3: Added variants 11-12, 18-20, 22 (previously only 1-10)

### 5. ✅ PRICES
- **Status**: Complete and expanded
- **Records**: 22 price entries (variants 1-22)
- **Structure**:
  - branch_price: Price warehouse charges to branch
  - sale_price: Price branch charges to customers
  - Markup pattern: sale_price > branch_price > supplier_cost

**Added Price Records**:
- Variants 11-22 with appropriate markup:
  - Variant 11: sale=35000, branch=33000
  - Variant 12: sale=53000, branch=51000
  - Variant 13: sale=60000, branch=58000
  - Variant 14: sale=50000, branch=48000
  - Variant 15: sale=40000, branch=38000
  - Variant 16: sale=45000, branch=43000
  - Variant 17: sale=42000, branch=40000
  - Variant 18: sale=50000, branch=48000
  - Variant 19: sale=20000, branch=18000
  - Variant 20: sale=32000, branch=30000
  - Variant 21: sale=45000, branch=43000
  - Variant 22: sale=41000, branch=39000

### 6. ✅ INVOICES
- **Status**: Complete
- **Records**: 20 invoices
- **Distribution**:
  - Branch 3 (Hanoi): 18 invoices
  - Warehouse (internal): 2 draft invoices
- **Invoice Types**: PAID and DRAFT
- **Payment Methods**: Cash, Card, Transfer

### 7. ✅ INVOICE_DETAILS
- **Status**: Complete
- **Records**: 20 invoice detail records
- **Verification**: 
  - Prices match sale_price from prices table
  - Quantities deducted from branch inventory
  - Batch_id tracked for FIFO/FEFO compliance

## Business Logic Compliance (per flow.txt)

### ✅ Phase 1: Supplier → Warehouse
- Movement type: SUP_TO_WARE
- Creates batches
- Records inventory at warehouse
- price = snap_cost = supplier cost

### ✅ Phase 2: Warehouse → Branch
- Movement type: WARE_TO_BR
- price = branch_price (warehouse markup)
- snap_cost = original supplier cost (for profit tracking)
- Updates inventory at branch with unit_price = movement_details.price

### ✅ Phase 3: Inventory Lifecycle
- unit_price at branch = movement_details.price from WARE_TO_BR
- Tracks last_movement_id for audit
- Maintains quantity balances

### ✅ Phase 4: Sales (Invoice Creation)
- Uses sale_price from prices table
- Branch profit = sale_price - branch_price
- Warehouse profit = branch_price - supplier_cost

### ✅ Returns and Disposals
- BR_TO_WARE: Returns from branch to warehouse
- WARE_TO_SUP: Returns from warehouse to supplier
- DISPOSAL: Destruction/disposal movements

## Summary of Changes

### Total Corrections Made: 11
1. Fixed snap_cost in 10 WARE_TO_BR movement details
2. Fixed price/snap_cost in 1 WARE_TO_SUP movement detail

### Total Additions Made:
1. Added 12 price records (variants 11-22)
2. Added 17 warehouse inventory records (variants 6-22)
3. Added 6 branch 3 inventory records (variants 11-12, 18-20, 22)

## Data Integrity Verification

### ✅ Referential Integrity
- All movement_details reference valid movements
- All movements reference valid batches and variants
- All inventory records reference valid batches
- All invoice_details reference valid invoices and batches

### ✅ Business Rule Compliance
- Price hierarchy: sale_price > branch_price > supplier_cost ✓
- snap_cost preserves original supplier cost ✓
- unit_price in inventory = branch_price from movements ✓
- Movement balances match inventory quantities ✓

## Recommendations

1. **Consider Adding**: More inventory records for branches 4 and 5 (HCMC and Da Nang)
2. **Consider Adding**: More invoice data for branches 4 and 5
3. **Monitor**: Stock levels for variants with low inventory
4. **Review**: Expired/exhausted batches (variants 6, 11, 12, 14) for cleanup

## Conclusion

The data.sql file has been thoroughly verified and corrected according to the business logic defined in flow.txt. All seven critical tables (batches, inventory_movement, inventory_movement_details, inventory, prices, invoices, invoice_details) are now properly populated with consistent data that follows the defined pricing and movement flows.

The corrections ensure that:
- Profit margins are accurately trackable at each level
- Original supplier costs are preserved for audit purposes
- Inventory valuations reflect actual acquisition costs
- Price hierarchies are maintained throughout the supply chain

