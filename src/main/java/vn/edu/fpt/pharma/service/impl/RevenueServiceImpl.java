package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.reveuce.RevenueShiftVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueVM;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.RevenueService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class RevenueServiceImpl extends BaseServiceImpl<Invoice, Long, InvoiceRepository> implements RevenueService {

    public RevenueServiceImpl(InvoiceRepository repository, AuditService auditService) {
        super(repository, auditService);
    }

    public DataTableResponse<RevenueVM> findAllRevenues(DataTableRequest reqDto, Long userId) {
        // Lấy toàn bộ dữ liệu từ DB
        List<Object[]> allData = repository.findRevenueByUser(userId);

        // Map Object[] -> RevenueVM
        List<RevenueVM> revenueList = allData.stream()
                .map(r -> new RevenueVM(
                        (String) r[0],
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).doubleValue()
                ))
                .toList();

        // Sort theo cột được client request
        Comparator<RevenueVM> comparator;
        switch (reqDto.orderColumn()) {
            case "period":
                comparator = Comparator.comparing(RevenueVM::period);
                break;
            case "totalInvoice":
                comparator = Comparator.comparing(RevenueVM::totalInvoice);
                break;
            case "totalCustomer":
                comparator = Comparator.comparing(RevenueVM::totalCustomer);
                break;
            case "totalRevenue":
                comparator = Comparator.comparing(RevenueVM::totalRevenue);
                break;
            default:
                comparator = Comparator.comparing(RevenueVM::period); // default sort
        }

        if ("desc".equalsIgnoreCase(reqDto.orderDir())) {
            comparator = comparator.reversed();
        }

        revenueList = revenueList.stream()
                .sorted(comparator)
                .toList();

        // Pagination
        int start = reqDto.start();
        int length = reqDto.length();
        List<RevenueVM> pageData = revenueList.stream()
                .skip(start)
                .limit(length)
                .toList();

        return new DataTableResponse<>(
                reqDto.draw(),
                revenueList.size(),
                revenueList.size(),
                pageData
        );
    }


    @Override
    public DataTableResponse<RevenueShiftVM> getRevenueShiftSummary(DataTableRequest reqDto, Long userId) {
        List<Object[]> allData = repository.findRevenueShiftByUser(userId);

        List<RevenueShiftVM> revenueList = allData.stream()
                .map(r -> new RevenueShiftVM(
                        (String) r[0],                  // shiftName
                        ((Number) r[1]).longValue(),    // orderCount
                        ((Number) r[2]).doubleValue(),  // cashTotal
                        ((Number) r[3]).doubleValue(),  // transferTotal
                        ((Number) r[4]).doubleValue()   // totalRevenue
                ))
                .toList();

        Comparator<RevenueShiftVM> comparator;
        switch (reqDto.orderColumn()) {
            case "shiftName":
                comparator = Comparator.comparing(RevenueShiftVM::shiftName);
                break;
            case "orderCount":
                comparator = Comparator.comparing(RevenueShiftVM::orderCount);
                break;
            case "cashTotal":
                comparator = Comparator.comparing(RevenueShiftVM::cashTotal);
                break;
            case "transferTotal":
                comparator = Comparator.comparing(RevenueShiftVM::transferTotal);
                break;
            case "totalRevenue":
                comparator = Comparator.comparing(RevenueShiftVM::totalRevenue);
                break;
            default:
                comparator = Comparator.comparing(RevenueShiftVM::shiftName); // mặc định sort theo shiftName
        }

        if ("desc".equalsIgnoreCase(reqDto.orderDir())) {
            comparator = comparator.reversed();
        }

        revenueList = revenueList.stream()
                .sorted(comparator)
                .toList();

        // Pagination
        int start = reqDto.start();
        int length = reqDto.length();
        List<RevenueShiftVM> pageData = revenueList.stream()
                .skip(start)
                .limit(length)
                .toList();

        return new DataTableResponse<>(
                reqDto.draw(),
                revenueList.size(),
                revenueList.size(),
                pageData
        );
    }


}
