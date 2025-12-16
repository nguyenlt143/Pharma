I have enough read-only context (MovementType includes BR_TO_WARE2 and INVENTORY_ADJUSTMENT; repository and controller patterns; frontend JS for import-export). I'll draft a concise plan for adding a new page to store adjustments and expired returns, mirroring import-export behavior. I'll include backend endpoints, repository queries, controller changes, frontend files, and templates. I will not implement—just present the plan and ask for feedback.
## Plan: Add "Adjustments & Expired Returns" screen

TL;DR: Create a new manager page that lists InventoryMovement records where movementType is BR_TO_WARE2 or INVENTORY_ADJUSTMENT. Reuse backend patterns from `ImportExportApiController` and frontend patterns from `src/main/resources/static/assets/js/manager/import-export.js` to provide summary, chart, activities list, and a detail modal. This will mirror import-export both in API shapes and UI behavior.

### Steps
1. Add API controller endpoints in `vn.edu.fpt.pharma.controller.manager` (new `AdjustmentApiController` or extend `ImportExportApiController`) to expose:
   - GET `/api/manager/adjustments/summary` — returns totalValue, formatted string, counts (lowStock/pending as needed).
   - GET `/api/manager/adjustments/movements?range=` — returns labels/imports/exports (same shape as `/import-export/movements` but filtered by movementType IN (BR_TO_WARE2, INVENTORY_ADJUSTMENT)).
   - GET `/api/manager/adjustments/activities?limit=` — recent movements filtered to those types where the user's branch is involved.
   - GET `/api/manager/adjustments/request/{id}` — detail for a specific movement id (use `InventoryMovementRepository.findByIdWithDetails(id)`).
   Note: follow authorization checks used in `ImportExportApiController` (check `CustomUserDetails` and branch involvement). Use `importExportService` helper methods for formatting and labels.

2. Reuse/extend repository methods:
   - Use existing `InventoryMovementRepository.findMovementsSinceByBranch(fromDate, branchId)` and filter results by movementType in controller.
   - Add a new repository query if preferred: `findMovementsSinceByBranchAndTypes(fromDate, branchId, List<MovementType>)` to offload filtering to SQL (optional).

3. Backend DTO/response shape:
   - Keep identical shapes to `ImportExportApiController` responses so the frontend logic can be mirrored (keys: labels, imports, exports; activities array with id, code, type, typeClass, totalMoney, totalValueFormatted, timeAgo, detailUrl; request detail with code, type, branchName, totalMoney, totalValueFormatted, totalQty, details).

4. Frontend assets:
   - Create `src/main/resources/static/assets/js/manager/adjustments.js` copying most logic from `import-export.js` but switching fetch URLs to `/api/manager/adjustments/*` and adjusting labels (e.g., "Điều chỉnh & Trả hàng hết hạn").
   - Add a template page JTE/HTML under `src/main/resources/templates/manager/adjustments.jte` or similar (match the project's templating convention). Copy structure from import-export view (charts, summary cards, activities table, modal). Hook the new JS file in the template (script tag).

5. UI/UX consistency:
   - Keep the same chart components (Chart.js configuration), modal detail layout, and list filtering buttons. Use `movementTypeLabel` and `movementTypeClass` from `importExportService` for consistent labels and CSS classes.
   - Add a menu/link to the manager sidebar/navigation to access `/manager/adjustments`.

6. Tests and integration:
   - Add integration tests mirroring `ImportExportReportControllerIT` to validate the new endpoints (summary, movements, activities, request detail).
   - Unit tests for any new repository query if added.

### Further Considerations
1. Data filtering approach: Filter in controller (fast to implement) / add repository method with IN clause (more efficient for large datasets).
2. UI scope: Option A — exact copy of import-export UI and behavior; Option B — simplified list-only view first, add charts later.
3. Reuse vs new code: Prefer creating a new controller `AdjustmentApiController` to keep responsibilities separate; or extend existing controller if you want fewer files.

Please review this draft plan and tell me:
- Prefer new controller or extend existing `ImportExportApiController`?
- Do you want server-side filtering (new repository queries) or client-side filtering for now?
- Do you want me to also generate the JTE template and menu entry in this change set?

Once you confirm choices I’ll produce a step-by-step implementation plan or the exact file edits to apply.

