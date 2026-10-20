package edu.course.taskboard;

import java.time.Clock;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class TaskService {
    private final TaskStore store;
    private final Clock clock;
    private final TitlePolicy titles = new TitlePolicy();

    // передаём хранилище и часы, в тестах подставим свои
    public TaskService(TaskStore store, Clock clock) {
        this.store = store;
        this.clock = clock;
    }

    public TaskItem create(String rawTitle, String owner) {
        // сначала чистим название и проверяем длину
        String title = titles.normalize(rawTitle);
        // Ищем такое же название у этого пользователя
        if (store.exists(owner, title)) throw new IllegalStateException("duplicate title");
        // новой задаче даём UUID, время берём из clock
        return store.save(new TaskItem(UUID.randomUUID(), title, owner, clock.instant()));
    }

    public List<TaskItem> list(String owner) { return store.findAll(owner); }

    public TaskItem get(UUID id, String owner) {
        // чужую задачу не возвращаем
        return store.findById(id).filter(t -> t.owner().equals(owner))
                .orElseThrow(() -> new NoSuchElementException("task not found"));
    }
}
