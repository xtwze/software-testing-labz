package edu.course.taskboard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstimatePolicyTest {
    @Test
    void onePointTakesFifteenMinutes() {
        EstimatePolicy policy = new EstimatePolicy();
        int actual = policy.minutes(1);
        assertEquals(15, actual);
    }

    @Test
    void zeroPointsAreRejected() {
        EstimatePolicy policy = new EstimatePolicy();
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> policy.minutes(0));
        assertEquals("points must be between 1 and 8", error.getMessage());
    }

    // Проверяем примеры с занятия
    @Test
    void twoPointsTakeThirtyMinutes() {
        // Создаем объект для теста
        EstimatePolicy policy = new EstimatePolicy();

        // Считаем время для 2 баллов
        int actual = policy.minutes(2);

        // Должно получиться 30 минут: 2 * 15
        assertEquals(30, actual);
    }

    @Test
    void ninePointsAreRejected() {
        EstimatePolicy policy = new EstimatePolicy();

        // Можно передать максимум 8, поэтому для 9 ждем ошибку
        assertThrows(IllegalArgumentException.class, () -> policy.minutes(9));
    }

    // Второй вариант
    @Test
    void fourPointsTakeSixtyMinutes() {
        // Создаем объект для теста
        EstimatePolicy policy = new EstimatePolicy();

        // Передаем 4 балла и получаем время в минутах
        int actual = policy.minutes(4);

        // В задании написано и 45, и 60. Берем 60, потому что 4 * 15 = 60
        assertEquals(60, actual);
    }

    @Test
    void sevenPointsTakeOneHundredFiveMinutes() {
        // Создаем объект для теста
        EstimatePolicy policy = new EstimatePolicy();

        // Считаем время для 7 баллов
        int actual = policy.minutes(7);

        // Проверяем результат: 7 * 15 = 105 минут
        assertEquals(105, actual);
    }

    @Test
    void minusTwoPointsAreRejected() {
        EstimatePolicy policy = new EstimatePolicy();

        // Отрицательные баллы нельзя передавать, метод должен выбросить ошибку
        assertThrows(IllegalArgumentException.class, () -> policy.minutes(-2));
    }

    @Test
    void elevenPointsAreRejectedWithMessage() {
        EstimatePolicy policy = new EstimatePolicy();

        // 11 выходит за диапазон. Сохраняем исключение, чтобы проверить его текст
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> policy.minutes(11));
        // Проверяем, что сообщение совпадает с заданием
        assertEquals("points must be between 1 and 8", error.getMessage());
    }
}
