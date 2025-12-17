package vn.edu.fpt.pharma.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.reveuce.RevenueDetailVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueShiftVM;
import vn.edu.fpt.pharma.dto.reveuce.RevenueVM;
import vn.edu.fpt.pharma.entity.Invoice;
import vn.edu.fpt.pharma.repository.InvoiceDetailRepository;
import vn.edu.fpt.pharma.repository.InvoiceRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.RevenueService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class RevenueServiceImpl extends BaseServiceImpl<Invoice, Long, InvoiceRepository> implements RevenueService {

    private final InvoiceDetailRepository invoiceDetailRepository;
    public RevenueServiceImpl(InvoiceRepository repository, AuditService auditService, InvoiceDetailRepository invoiceDetailRepository) {
        super(repository, auditService);
        this.invoiceDetailRepository = invoiceDetailRepository;
    }

    public DataTableResponse<RevenueVM> findAllRevenues(DataTableRequest reqDto, Long userId) {
        List<Object[]> allData = repository.findRevenueByUser(userId);
        List<RevenueVM> revenueList = allData.stream()
                .map(r -> new RevenueVM(
                        (String) r[0],
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).doubleValue()
                ))
                .toList();

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
    public DataTableResponse<RevenueDetailVM> ViewRevenuesDetail(DataTableRequest reqDto, Long userId, Integer year, Integer month) {
        List<Object[]> allData = invoiceDetailRepository.getMedicineRevenueByMonth(userId, year, month);
        List<RevenueDetailVM> detailList = allData.stream()
                .map(r -> new RevenueDetailVM(
                        safeString((String) r[0]),
                        safeString((String) r[1]),
                        safeString((String) r[2]),
                        safeString((String) r[3]),
                        safeString((String) r[4]),
                        r[5] == null ? 0L : ((Number) r[5]).longValue(),
                        r[6] == null ? 0D : ((Number) r[6]).doubleValue(),
                        r[7] == null ? 0D : ((Number) r[7]).doubleValue()
                ))
                .toList();

        String sortCol = reqDto.orderColumn();
        Comparator<RevenueDetailVM> comparator = null;

        if (sortCol != null) {
            switch (sortCol) {
                case "drugName" ->
                        comparator = Comparator.comparing(v -> safeString(v.drugName()), String.CASE_INSENSITIVE_ORDER);
                case "unit" ->
                        comparator = Comparator.comparing(v -> safeString(v.unit()), String.CASE_INSENSITIVE_ORDER);
                case "batch" ->
                        comparator = Comparator.comparing(v -> safeString(v.batch()), String.CASE_INSENSITIVE_ORDER);
                case "manufacturer" ->
                        comparator = Comparator.comparing(v -> safeString(v.manufacturer()), String.CASE_INSENSITIVE_ORDER);
                case "country" ->
                        comparator = Comparator.comparing(v -> safeString(v.country()), String.CASE_INSENSITIVE_ORDER);
                case "quantity" ->
                        comparator = Comparator.comparing(RevenueDetailVM::quantity);
                case "price" ->
                        comparator = Comparator.comparing(RevenueDetailVM::price);
                case "totalAmount" ->
                        comparator = Comparator.comparing(RevenueDetailVM::totalAmount);
            }
        }

        // Sort nếu có comparator
        if (comparator != null) {
            detailList = detailList.stream().sorted(comparator).toList();
        }
        int start = reqDto.start();
        int length = reqDto.length();

        List<RevenueDetailVM> pageData = detailList.stream()
                .skip(start)
                .limit(length)
                .toList();

        return new DataTableResponse<>(
                reqDto.draw(),
                detailList.size(),
                detailList.size(),
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

        return buildDataTableResponse(reqDto, revenueList);
    }

    @Override
    public DataTableResponse<RevenueShiftVM> getRevenueShiftSummary(DataTableRequest reqDto, Long userId, String workDate) {
        List<Object[]> allData = repository.findRevenueShiftByUserAndDate(userId, workDate);

        List<RevenueShiftVM> revenueList = allData.stream()
                .map(r -> new RevenueShiftVM(
                        (String) r[0],                  // shiftName
                        ((Number) r[1]).longValue(),    // orderCount
                        ((Number) r[2]).doubleValue(),  // cashTotal
                        ((Number) r[3]).doubleValue(),  // transferTotal
                        ((Number) r[4]).doubleValue()   // totalRevenue
                ))
                .toList();

        return buildDataTableResponse(reqDto, revenueList);
    }

    private DataTableResponse<RevenueShiftVM> buildDataTableResponse(DataTableRequest reqDto, List<RevenueShiftVM> revenueList) {

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

    @Override
    public DataTableResponse<RevenueDetailVM> ViewShiftDetail(DataTableRequest reqDto, Long userId, String shiftName, String workDate) {
        List<Object[]> allData = invoiceDetailRepository.getMedicineRevenueByShift(userId, shiftName, workDate);
        List<RevenueDetailVM> detailList = allData.stream()
                .map(r -> new RevenueDetailVM(
                        safeString((String) r[0]),
                        safeString((String) r[1]),
                        safeString((String) r[2]),
                        safeString((String) r[3]),
                        safeString((String) r[4]),
                        r[5] == null ? 0L : ((Number) r[5]).longValue(),
                        r[6] == null ? 0D : ((Number) r[6]).doubleValue(),
                        r[7] == null ? 0D : ((Number) r[7]).doubleValue()
                ))
                .toList();

        String sortCol = reqDto.orderColumn();
        Comparator<RevenueDetailVM> comparator = null;

        if (sortCol != null) {
            switch (sortCol) {
                case "drugName" ->
                        comparator = Comparator.comparing(v -> safeString(v.drugName()), String.CASE_INSENSITIVE_ORDER);
                case "unit" ->
                        comparator = Comparator.comparing(v -> safeString(v.unit()), String.CASE_INSENSITIVE_ORDER);
                case "batch" ->
                        comparator = Comparator.comparing(v -> safeString(v.batch()), String.CASE_INSENSITIVE_ORDER);
                case "manufacturer" ->
                        comparator = Comparator.comparing(v -> safeString(v.manufacturer()), String.CASE_INSENSITIVE_ORDER);
                case "country" ->
                        comparator = Comparator.comparing(v -> safeString(v.country()), String.CASE_INSENSITIVE_ORDER);
                case "quantity" ->
                        comparator = Comparator.comparing(RevenueDetailVM::quantity);
                case "price" ->
                        comparator = Comparator.comparing(RevenueDetailVM::price);
                case "totalAmount" ->
                        comparator = Comparator.comparing(RevenueDetailVM::totalAmount);
            }
        }

        // Sort nếu có comparator
        if (comparator != null) {
            detailList = detailList.stream().sorted(comparator).toList();
        }
        int start = reqDto.start();
        int length = reqDto.length();

        List<RevenueDetailVM> pageData = detailList.stream()
                .skip(start)
                .limit(length)
                .toList();

        return new DataTableResponse<>(
                reqDto.draw(),
                detailList.size(),
                detailList.size(),
                pageData
        );
    }

    @Override
    public List<String> getDatesWithShifts(Long userId) {
        List<String> dates = repository.findDatesWithShiftsByUser(userId);
        return dates;
    }

    private String safeString(String v) {
        return v == null ? "" : v;
    }
}
