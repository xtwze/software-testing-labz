package edu.course.taskboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskStore {
    boolean exists(String owner, String title);
    TaskItem save(TaskItem item);
    List<TaskItem> findAll(String owner);
    Optional<TaskItem> findById(UUID id);
}
