package vn.edu.fpt.pharma.dto;

import java.util.List;

public record CsvOutputResult<T>(
        List<T> data,
        List<CsvOutputError> errors
) {

    public record CsvOutputError(
            int lineNumber,
            String[] line,
            String message
    ) {
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }
}
