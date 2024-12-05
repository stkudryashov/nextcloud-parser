package com.example.controller;

import com.example.model.UserData;
import com.example.service.UserService;
import com.example.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * REST контроллер для работы с данными пользователей
 */
@RestController
@RequestMapping("/api/users")
public class UserDataController {
    private static final Logger logger = LoggerFactory.getLogger(UserDataController.class);
    private final UserService userService;

    public UserDataController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получает данные пользователя по ID
     * @param userId ID пользователя
     * @return данные пользователя или ошибку, если пользователь не найден
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserData(@PathVariable String userId) {
        logger.info("Получен запрос на получение данных пользователя с ID: {}", userId);
        
        try {
            UserData userData = userService.getUserById(userId);
            logger.info("Успешно получены данные пользователя: {}", userId);
            return ResponseEntity.ok(userData);
        } catch (UserNotFoundException e) {
            logger.warn("Пользователь не найден: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении данных пользователя: {}", userId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Не удалось получить данные пользователя: " + e.getMessage()));
        }
    }
}
