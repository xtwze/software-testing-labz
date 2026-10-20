package edu.course.taskboard;

public class TitlePolicy {
    public String normalize(String raw) {
        if (raw == null) throw new IllegalArgumentException("title is required");
        String title = raw.strip();
        if (title.isEmpty() || title.length() > 80) {
            throw new IllegalArgumentException("title length must be between 1 and 80");
        }
        return title;
    }
}
