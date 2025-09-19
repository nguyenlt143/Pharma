package vn.edu.fpt.pharma.service;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.pharma.dto.CsvOutputResult;

import java.util.List;
import java.util.function.Function;

public interface CsvService {
    <T> CsvOutputResult<T> read(Class<T> clazz, MultipartFile file, boolean isSkipHeader, Function<String[], T> lineMapper);
    <T> byte[] write(List<T> data, String[] headers, Function<T, String[]> lineMapper);
}
