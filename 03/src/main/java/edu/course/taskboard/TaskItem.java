package edu.course.taskboard;

import java.time.Instant;
import java.util.UUID;

public record TaskItem(UUID id, String title, String owner, Instant createdAt) {}
