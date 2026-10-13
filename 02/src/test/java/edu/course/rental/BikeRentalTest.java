package edu.course.rental;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BikeRentalTest {
    private final BikeRental rental = new BikeRental();
    private final LocalDateTime dueAt = LocalDateTime.of(2026, 10, 13, 18, 0);

    @ParameterizedTest
    @CsvSource({"1, 120", "5, 600", "10, 1200"})
    void calculatesRentalCost(int hours, int expected) {
        assertThat(rental.rentalCost(hours)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11})
    void rejectsInvalidHours(int hours) {
        assertThatThrownBy(() -> rental.rentalCost(hours))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 10, 25})
    void acceptsNameWithinLengthLimits(int length) {
        String name = "а".repeat(length);
        assertThat(rental.registerName(name)).isEqualTo(name);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "А", "abcdefghijklmnopqrstuvwxyzov"})
    void rejectsInvalidName(String name) {
        assertThatThrownBy(() -> rental.registerName(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void removesSpacesAroundName() {

        assertThat(rental.registerName("  Анна  ")).isEqualTo("Анна");
    }

    @Test
    void keepsSpacesInsideName() {

        assertThat(rental.registerName("  Анна  Мария  ")).isEqualTo("Анна  Мария");
    }

    @Test
    void checksLengthAfterRemovingSpaces() {
        // До удаления пробелов строка длиннее 25 символов.
        String name = "а".repeat(25);
        assertThat(rental.registerName("  " + name + "  ")).isEqualTo(name);
    }

    @Test
    void rejectsShortNameAfterRemovingSpaces() {
        assertThatThrownBy(() -> rental.registerName(" А "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({"-1, false", "0, false", "1, true"})
    void checksReturnTime(long seconds, boolean expected) {
        assertThat(rental.isLate(dueAt, dueAt.plusSeconds(seconds))).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"1, -1, 0", "1, 0, 0", "1, 1, 60", "5, 1, 300", "10, 1, 600"})
    void calculatesLateFee(int hours, long seconds, int expected) {
        assertThat(rental.lateFee(hours, dueAt, dueAt.plusSeconds(seconds))).isEqualTo(expected);
    }
}
