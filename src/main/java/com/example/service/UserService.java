package com.example.service;

import com.example.model.UserData;
import com.example.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Сервис для работы с данными пользователей
 * Обеспечивает доступ к данным пользователей из Excel файла
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final NextcloudExcelService nextcloudExcelService;

    public UserService(NextcloudExcelService nextcloudExcelService) {
        this.nextcloudExcelService = nextcloudExcelService;
    }

    /**
     * Получает данные пользователя по его ID
     * @param userId ID пользователя
     * @return данные пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    public UserData getUserById(String userId) {
        List<List<String>> data = nextcloudExcelService.getExcelData();
        
        if (data.isEmpty()) {
            logger.warn("Excel файл пуст");
            throw new UserNotFoundException(userId);
        }

        List<String> headers = data.get(0);
        logger.debug("Заголовки Excel: {}", headers);

        // Поиск пользователя по ID перебором (надо думать)
        for (int i = 1; i < data.size(); i++) {
            List<String> row = data.get(i);
            if (row.isEmpty()) continue;

            String rowId = normalizeId(row.get(0));
            if (rowId.equals(userId)) {
                return createUserData(userId, headers, row);
            }
        }
        
        logger.warn("Пользователь с ID {} не найден", userId);
        throw new UserNotFoundException(userId);
    }

    /**
     * Нормализует ID, убирая десятичную часть .0 если она есть
     */
    private String normalizeId(String id) {
        return id.endsWith(".0") ? id.substring(0, id.length() - 2) : id;
    }

    /**
     * Создает объект UserData из строки данных Excel
     */
    private UserData createUserData(String userId, List<String> headers, List<String> row) {
        UserData userData = new UserData();
        userData.setUserId(userId);
        
        for (int j = 0; j < Math.min(headers.size(), row.size()); j++) {
            userData.addAttribute(headers.get(j), row.get(j));
        }
        
        return userData;
    }
}
