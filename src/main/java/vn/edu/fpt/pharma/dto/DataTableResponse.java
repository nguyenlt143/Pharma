package vn.edu.fpt.pharma.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record DataTableResponse<T>(
        int draw,
        long recordsTotal,
        long recordsFiltered,
        List<T> data
) {
    public <R> DataTableResponse<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mapped = data.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new DataTableResponse<>(draw, recordsTotal, recordsFiltered, mapped);
    }

    public <R> DataTableResponse<R> transform(Function<? super List<T>, ? extends List<R>> transformer) {
        List<R> transformed = transformer.apply(data);
        return new DataTableResponse<>(draw, recordsTotal, recordsFiltered, transformed);
    }

}
