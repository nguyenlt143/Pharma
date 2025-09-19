package vn.edu.fpt.pharma.dto;

import java.util.Map;
import java.util.stream.Collectors;

public record DataTableRequest(
        int draw,
        int start,
        int length,
        String searchValue,
        String orderColumn,
        String orderDir
) {
    public static DataTableRequest fromParams(Map<String, String[]> requestParams) {
        Map<String, String> params = requestParams.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()[0]
                ));
        int draw = Integer.parseInt(params.get("draw"));
        int start = Integer.parseInt(params.get("start"));
        int length = Integer.parseInt(params.get("length"));

        String searchValue = params.get("search[value]");

        String orderColumnIndex = params.get("order[0][column]");
        String orderDir = params.get("order[0][dir]");
        String orderColumn = params.get("columns[" + orderColumnIndex + "][data]");

        return new DataTableRequest(draw, start, length, searchValue, orderColumn, orderDir);
    }
}