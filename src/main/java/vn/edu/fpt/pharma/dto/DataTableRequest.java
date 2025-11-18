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
    public static DataTableRequest fromParams(Map<String, ?> requestParams) {
        Map<String, String> params = requestParams.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            Object value = e.getValue();
                            if (value == null) {
                                return null;
                            }
                            if (value instanceof String[]) {
                                String[] arr = (String[]) value;
                                return arr.length > 0 ? arr[0] : null;
                            }
                            if (value instanceof String) {
                                return (String) value;
                            }
                            return value.toString();
                        }
                ));
        int draw = Integer.parseInt(params.getOrDefault("draw", "0"));
        int start = Integer.parseInt(params.getOrDefault("start", "0"));
        int length = Integer.parseInt(params.getOrDefault("length", "10"));

        String searchValue = params.get("search[value]");

        String orderColumnIndex = params.get("order[0][column]");
        String orderDir = params.getOrDefault("order[0][dir]", "asc");
        String orderColumn = null;
        if (orderColumnIndex != null) {
            orderColumn = params.get("columns[" + orderColumnIndex + "][data]");
        }

        return new DataTableRequest(draw, start, length, searchValue, orderColumn, orderDir);
    }
}