package com.mjengo.service;

import com.mjengo.model.Incident;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IncidentService {
    private final List<Incident> incidents = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Incident> getAll() {
        return incidents;
    }

    public Optional<Incident> getById(long id) {
        return incidents.stream().filter(i -> i.getId() == id).findFirst();
    }

    public Incident add(Incident i) {
        i.setId(idCounter.getAndIncrement());
        incidents.add(i);
        return i;
    }

    public void delete(long id) {
        incidents.removeIf(i -> i.getId() == id);
    }
}
