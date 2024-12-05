package com.example.service;

import com.example.properties.NextcloudProperties;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с Excel файлами в Nextcloud
 * Обеспечивает загрузку и парсинг Excel файлов через WebDAV протокол
 */
@Service
public class NextcloudExcelService {
    private static final Logger logger = LoggerFactory.getLogger(NextcloudExcelService.class);
    private final NextcloudProperties properties;

    public NextcloudExcelService(NextcloudProperties properties) {
        this.properties = properties;
    }

    /**
     * Получает данные из Excel файла, хранящегося в Nextcloud
     * @return список строк с данными из Excel
     */
    public List<List<String>> getExcelData() {
        try {
            Sardine sardine = createWebDavClient();
            String fullUrl = buildNextcloudUrl();
            
            logger.info("Загрузка файла из Nextcloud: {}", fullUrl);
            
            try (InputStream excelStream = sardine.get(fullUrl)) {
                return parseExcelFile(excelStream);
            }
        } catch (IOException e) {
            logger.error("Ошибка при получении данных из Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось получить данные из Excel файла", e);
        }
    }

    /**
     * Создает WebDAV клиент для доступа к Nextcloud
     */
    private Sardine createWebDavClient() {
        return SardineFactory.begin(properties.getUsername(), properties.getPassword());
    }

    /**
     * Формирует URL для доступа к файлу в Nextcloud
     * Каждая часть пути кодируется отдельно для сохранения структуры
     */
    private String buildNextcloudUrl() {
        String[] pathParts = properties.getExcel().getPath().split("/");
        String encodedPath = Arrays.stream(pathParts)
                .map(this::encodeUrlPart)
                .collect(Collectors.joining("/"));
        return properties.getUrl() + encodedPath;
    }

    /**
     * Кодирует часть URL, заменяя пробелы на %20
     */
    private String encodeUrlPart(String part) {
        try {
            return URLEncoder.encode(part, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Ошибка при кодировании URL", e);
        }
    }

    /**
     * Парсит Excel файл из входного потока
     * @param inputStream поток с данными Excel файла
     * @return список строк с данными
     */
    private List<List<String>> parseExcelFile(InputStream inputStream) throws IOException {
        List<List<String>> result = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            logger.info("Обработка листа Excel: {}", sheet.getSheetName());

            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    rowData.add(getCellValueAsString(cell));
                }
                result.add(rowData);
            }
            
            logger.info("Успешно обработано {} строк", result.size());
        }
        
        return result;
    }

    /**
     * Преобразует значение ячейки Excel в строку
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                // Убираем .0 для целых чисел
                double value = cell.getNumericCellValue();
                if (value == Math.floor(value)) {
                    return String.valueOf((long)value);
                }
                return String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
