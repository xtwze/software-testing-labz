package edu.course.rental;

import java.time.LocalDateTime;
import java.util.Objects;

public class BikeRental {
    public int rentalCost(int hours) {
        if (hours < 1 || hours > 10) {
            throw new IllegalArgumentException("hours must be between 1 and 10");
        }
        return hours * 120;
    }

    public String registerName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is required");
        }
        String cleanedName = name.trim();
        if (cleanedName.length() < 2 || cleanedName.length() > 25) {
            throw new IllegalArgumentException("name length must be between 2 and 25");
        }
        return cleanedName;
    }

    public boolean isLate(LocalDateTime dueAt, LocalDateTime returnedAt) {
        Objects.requireNonNull(dueAt, "dueAt is required");
        Objects.requireNonNull(returnedAt, "returnedAt is required");
        return returnedAt.isAfter(dueAt);
    }

    public int lateFee(int hours, LocalDateTime dueAt, LocalDateTime returnedAt) {
        int cost = rentalCost(hours);
        return isLate(dueAt, returnedAt) ? cost / 2 : 0;
    }
}
