# Thêm Nút "Thêm vào đơn" cho Inventory Items trong POS

## Những thay đổi đã thực hiện:

### 1. Cập nhật HTML structure trong pos.js (dòng ~97)
- Thay đổi từ div clickable thành div với nút riêng biệt
- Thêm nút "Thêm vào đơn" với style Bootstrap-like
- Disable nút khi hết hàng (quantity <= 0)
- Thêm data attributes cần thiết

### 2. Thêm function addInventoryToCart() (dòng ~239)
- Xử lý click nút "Thêm vào đơn" 
- Extract data từ inventory item
- Validate dữ liệu (inventory ID, quantity, price)
- Kiểm tra item đã tồn tại trong prescription
- Thêm mới hoặc tăng quantity
- Visual feedback với màu và text thay đổi

### 3. Thêm CSS styling (dòng ~970)
- Style cho nút "Thêm vào đơn"
- Hover effects
- Disabled state styling
- Inventory item hover effects
- Transition animations

### 4. Global scope access (dòng ~1010)
- Export addInventoryToCart lên window object
- Cho phép onclick từ HTML gọi function

## Tính năng mới:

1. **Nút riêng biệt**: Mỗi inventory item có nút "Thêm vào đơn" rõ ràng
2. **Visual feedback**: Nút thay đổi màu và text khi click ("Đã thêm!")
3. **Validation**: Kiểm tra hết hàng, giá bán, quantity tối đa
4. **Responsive design**: Nút full-width, hover effects
5. **Disabled state**: Nút hiển thị "Hết hàng" khi quantity = 0

## Cách sử dụng:

1. Tìm kiếm thuốc trong POS
2. Click để xem chi tiết variants
3. Mỗi số lô (inventory item) sẽ có nút "Thêm vào đơn"
4. Click nút để thêm vào prescription
5. Nút sẽ hiển thị "Đã thêm!" và đổi màu tạm thời

## Code structure:

```javascript
// HTML được tạo trong pos.js
<div class="inventory-item" data-*="...">
    <div>Thông tin số lô...</div>
    <button class="add-to-cart-btn" onclick="addInventoryToCart(this)">
        Thêm vào đơn
    </button>
</div>

// Function handler
function addInventoryToCart(button) {
    // Extract data từ parent inventory-item
    // Validate data
    // Add to prescriptionItems array
    // Update UI
}
```

## Testing:
- Kiểm tra nút hiển thị đúng
- Test click functionality  
- Verify visual feedback
- Check disabled state cho hết hàng
- Test với các loại thuốc khác nhau
