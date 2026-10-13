package edu.course.rental;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class BikeRentalTest {
    private final BikeRental rental = new BikeRental();
    private final LocalDateTime dueAt = LocalDateTime.of(2026, 10, 13, 18, 0);

    @ParameterizedTest
    @CsvSource({"1, 120", "5, 600", "10, 1200"})
    void calculatesRentalCost(int hours, int expected) {
        assertEquals(expected, rental.rentalCost(hours));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11})
    void rejectsInvalidHours(int hours) {
        assertThrows(IllegalArgumentException.class, () -> rental.rentalCost(hours));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 10, 25})
    void acceptsNameWithinLengthLimits(int length) {
        String name = "а".repeat(length);
        assertEquals(name, rental.registerName(name));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "А", "abcdefghijklmnopqrstuvwxyz"})
    void rejectsInvalidName(String name) {
        assertThrows(IllegalArgumentException.class, () -> rental.registerName(name));
    }

    @Test
    void removesSpacesAroundName() {
        assertEquals("Анна", rental.registerName("  Анна  "));
    }

    @Test
    void keepsSpacesInsideName() {
        assertEquals("Анна  Мария", rental.registerName("  Анна  Мария  "));
    }

    @Test
    void checksLengthAfterRemovingSpaces() {
        // До удаления пробелов строка длиннее 25 символов.
        String name = "а".repeat(25);
        assertEquals(name, rental.registerName("  " + name + "  "));
    }

    @Test
    void rejectsShortNameAfterRemovingSpaces() {
        assertThrows(IllegalArgumentException.class, () -> rental.registerName(" А "));
    }

    @ParameterizedTest
    @CsvSource({"-1, false", "0, false", "1, true"})
    void checksReturnTime(long seconds, boolean expected) {
        assertEquals(expected, rental.isLate(dueAt, dueAt.plusSeconds(seconds)));
    }

    @ParameterizedTest
    @CsvSource({"1, -1, 0", "1, 0, 0", "1, 1, 60", "5, 1, 300", "10, 1, 600"})
    void calculatesLateFee(int hours, long seconds, int expected) {
        assertEquals(expected, rental.lateFee(hours, dueAt, dueAt.plusSeconds(seconds)));
    }
}
