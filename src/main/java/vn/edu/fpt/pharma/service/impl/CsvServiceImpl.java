package vn.edu.fpt.pharma.service.impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.pharma.dto.CsvOutputResult;
import vn.edu.fpt.pharma.service.CsvService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class CsvServiceImpl implements CsvService {

    @Override
    public <T> CsvOutputResult<T> read(
            Class<T> clazz,
            MultipartFile file,
            boolean isSkipHeader,
            Function<String[], T> lineMapper
    ) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            if (isSkipHeader) {
                reader.readLine(); // skip header
            }

            List<T> data = new ArrayList<>();
            List<CsvOutputResult.CsvOutputError> errors = new ArrayList<>();

            String line;
            int lineNumber = isSkipHeader ? 2 : 1;

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",", -1);
                try {
                    T item = lineMapper.apply(columns);
                    data.add(item);
                } catch (Exception ex) {
                    errors.add(new CsvOutputResult.CsvOutputError(
                            lineNumber,
                            columns,
                            ex.getMessage()
                    ));
                }
                lineNumber++;
            }

            return new CsvOutputResult<>(data, errors);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> byte[] write(List<T> data, String[] headers, Function<T, String[]> lineMapper) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            if (headers != null && headers.length > 0) {
                csvWriter.writeNext(headers);
            }

            for (T item : data) {
                String[] line = lineMapper.apply(item);
                csvWriter.writeNext(line);
            }

            writer.flush();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to write CSV file: " + e.getMessage(), e);
        }
    }

}
