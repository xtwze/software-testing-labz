package edu.course.taskboard;

public class EstimatePolicy {
    public int minutes(int points) {
        if (points < 1 || points > 8) {
            throw new IllegalArgumentException("points must be between 1 and 8");
        }
        return points * 15;
    }
}
