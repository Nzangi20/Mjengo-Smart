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

    public List<Task> getAll() {
        return tasks;
    }

    public Optional<Task> getById(long id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst();
    }

    public Task add(Task t) {
        t.setId(idCounter.getAndIncrement());
        tasks.add(t);
        return t;
    }

    public void delete(long id) {
        tasks.removeIf(t -> t.getId() == id);
    }

    public void markDone(long id) {
        getById(id).ifPresent(t -> t.setStatus("DONE"));
    }

    public long countByStatus(String status) {
        return tasks.stream().filter(t -> t.getStatus().equals(status)).count();
    }

    public List<Task> getByAssignee(String name) {
        return tasks.stream().filter(t -> t.getAssignedTo().equals(name)).toList();
    }
}
