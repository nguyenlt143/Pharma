# VietQR Integration Documentation

## Overview
Successfully integrated VietQR API for generating QR codes with Vietnamese banking standards. The system now uses the free VietQR API from `https://img.vietqr.io/` to generate real QR codes for bank transfers.

## VietQR API Integration

### API Endpoint
```
https://img.vietqr.io/{bank}/{account}?amount={money}&addInfo={note}
```

### Parameters
- `{bank}`: Bank code (e.g., VCB, TCB, VTB, BIDV, ACB, MB, TPB, STB)
- `{account}`: Bank account number
- `{money}`: Transfer amount in VND (integer)
- `{note}`: Transfer note/description (URL encoded)

### Example Usage
**Request:** Bank: VCB, Account: 0123456789, Amount: 70000, Note: HD23001
**URL:** `https://img.vietqr.io/VCB/0123456789?amount=70000&addInfo=HD23001`

## Implementation Details

### JavaScript Functions

#### updateQRCode()
```javascript
function updateQRCode() {
    const totalAmount = getTotalAmount();
    const invoiceCode = generateInvoiceCode();
    
    // VietQR configuration
    const bankCode = 'VCB'; // Configurable
    const accountNumber = '0123456789'; // Configurable
    const amount = Math.round(totalAmount);
    const addInfo = invoiceCode;
    
    // Generate VietQR URL
    const vietQRUrl = `https://img.vietqr.io/${bankCode}/${accountNumber}?amount=${amount}&addInfo=${encodeURIComponent(addInfo)}`;
    
    // Update QR image with fallback
    qrCodeImage.src = vietQRUrl;
    qrCodeImage.onerror = function() {
        // Fallback to placeholder if API fails
        this.src = "placeholder-image-base64";
    };
}
```

#### generateInvoiceCode()
```javascript
function generateInvoiceCode() {
    const now = new Date();
    const year = now.getFullYear().toString().slice(-2);
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    
    return `HD${year}${month}${day}${hours}${minutes}${seconds}`;
}
```

### HTML Structure

#### QR Code Section
```html
<div id="qrCodeSection" class="qr-code-section">
    <div class="qr-header">
        <h4 class="qr-title">Vui lòng quét mã QR bên dưới để thanh toán</h4>
        <p class="qr-subtitle">Số tiền và nội dung chuyển khoản đã được điền sẵn</p>
    </div>
    
    <div class="qr-code-container">
        <img id="qrCodeImage" src="placeholder" alt="QR Code" class="qr-code-img">
    </div>
    
    <div class="qr-bank-info">
        <div><strong>Ngân hàng:</strong> Vietcombank (VCB)</div>
        <div><strong>Số TK:</strong> <span id="qrAccountNumber">0123456789</span></div>
        <div><strong>Tên TK:</strong> Nhà Thuốc ABC</div>
        <div><strong>Nội dung:</strong> <span id="qrInvoiceCode">HD...</span></div>
    </div>
    
    <div class="qr-amount-display">
        <strong>Tổng tiền: <span id="qrDisplayAmount">0</span> VNĐ</strong>
    </div>
</div>
```

## Features

### 1. Dynamic QR Generation
- Automatically generates QR codes when "Chuyển khoản" is selected
- Updates in real-time when prescription total changes
- Includes unique invoice code for tracking

### 2. Bank Information Display
- Shows bank name, account number, and account holder
- Displays transfer note/invoice code
- Shows formatted amount in Vietnamese currency

### 3. Error Handling
- Fallback to placeholder image if VietQR API fails
- Console warnings for debugging
- Graceful degradation

### 4. Invoice Code Generation
- Format: `HD{YY}{MM}{DD}{HH}{MM}{SS}`
- Example: `HD251205143022` (Dec 5, 2025, 14:30:22)
- Unique per transaction

## Supported Banks

| Code | Bank Name | Full Name |
|------|-----------|-----------|
| VCB | Vietcombank | Ngân hàng TMCP Ngoại thương Việt Nam |
| TCB | Techcombank | Ngân hàng TMCP Kỹ thương Việt Nam |
| VTB | Vietinbank | Ngân hàng TMCP Công Thương Việt Nam |
| BIDV | BIDV | Ngân hàng TMCP Đầu tư và Phát triển Việt Nam |
| ACB | ACB | Ngân hàng TMCP Á Châu |
| MB | MBBank | Ngân hàng TMCP Quân đội |
| TPB | TPBank | Ngân hàng TMCP Tiên Phong |
| STB | Sacombank | Ngân hàng TMCP Sài Gòn Thương Tín |

## Configuration

### Current Settings
```javascript
const bankCode = 'VCB'; // Vietcombank
const accountNumber = '0123456789'; // Test account
```

### Production Configuration
For production, these should be moved to:
1. **Environment variables**
2. **Database configuration**
3. **Backend API endpoint**

## Test Files

### 1. vietqr-test.html
- Standalone VietQR API tester
- Supports multiple banks
- Real-time QR generation
- Parameter customization

### 2. qr-integration-test.html
- Full integration test with prescription simulation
- VietQR integration with payment flow
- Bank information display testing

## Benefits

### 1. Standard Compliance
- Uses VietQR Payload v2 standard
- Compatible with all Vietnamese banking apps
- Follows national QR payment guidelines

### 2. User Experience
- Pre-filled transfer information
- No manual data entry required
- Clear visual feedback

### 3. Reliability
- Free API with good uptime
- Fallback mechanism for failures
- Error handling and logging

### 4. Security
- No sensitive data stored locally
- HTTPS API calls
- Unique transaction codes

## Production Considerations

### 1. Security
- Move bank credentials to secure configuration
- Implement API rate limiting
- Add request validation

### 2. Error Handling
- Implement retry mechanism for API failures
- Add user notification for QR generation errors
- Log failed transactions for monitoring

### 3. Performance
- Consider caching QR codes for repeated amounts
- Implement lazy loading for QR images
- Add loading indicators

### 4. Compliance
- Ensure compliance with banking regulations
- Implement transaction logging
- Add audit trails

## API Limitations

### 1. Free Tier Restrictions
- Rate limiting may apply
- No SLA guarantees
- Dependency on third-party service

### 2. Parameter Constraints
- Amount must be integer (VND)
- Note length limitations
- Bank code must be supported

## Future Enhancements

### 1. Multi-Bank Support
- Allow users to select preferred bank
- Store customer banking preferences
- Support for multiple payment accounts

### 2. Advanced Features
- QR code expiration
- Transaction status verification
- Automated payment confirmation

### 3. Analytics
- QR code usage statistics
- Payment method preferences
- Success/failure rate tracking

## Conclusion

The VietQR integration successfully provides a modern, standard-compliant QR code payment solution for the pharmacy POS system. It enhances user experience by eliminating manual data entry and follows Vietnamese banking standards for maximum compatibility.
