package com.mjengo.service;

import com.mjengo.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    private final PersistentStoreService store;

    public TaskService(PersistentStoreService store) {
        this.store = store;
        tasks.addAll(store.loadList("tasks", Task.class));
        idCounter.set(tasks.stream().map(Task::getId).max(Long::compareTo).orElse(0L) + 1L);
    }

    public List<Task> getAll() {
        return tasks;
    }

    public Optional<Task> getById(long id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst();
    }

    public Task add(Task t) {
        t.setId(idCounter.getAndIncrement());
        tasks.add(t);
        save();
        return t;
    }

    public void delete(long id) {
        tasks.removeIf(t -> t.getId() == id);
        save();
    }

    public void markDone(long id) {
        getById(id).ifPresent(t -> {
            t.setStatus("DONE");
            save();
        });
    }

    public long countByStatus(String status) {
        return tasks.stream().filter(t -> t.getStatus().equals(status)).count();
    }

    public List<Task> getByAssignee(String name) {
        return tasks.stream().filter(t -> t.getAssignedTo().equals(name)).toList();
    }

    private void save() {
        store.saveList("tasks", tasks);
    }
}
