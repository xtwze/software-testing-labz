package edu.course.taskboard;

import java.time.Clock;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class TaskService {
    private final TaskStore store;
    private final Clock clock;
    private final TitlePolicy titles = new TitlePolicy();

    public TaskService(TaskStore store, Clock clock) {
        this.store = store;
        this.clock = clock;
    }

    public TaskItem create(String rawTitle, String owner) {
        String title = titles.normalize(rawTitle);
        if (store.exists(owner, title)) throw new IllegalStateException("duplicate title");
        return store.save(new TaskItem(UUID.randomUUID(), title, owner, clock.instant()));
    }

    public List<TaskItem> list(String owner) { return store.findAll(owner); }

    public TaskItem get(UUID id, String owner) {
        return store.findById(id).filter(t -> t.owner().equals(owner))
                .orElseThrow(() -> new NoSuchElementException("task not found"));
    }
}
