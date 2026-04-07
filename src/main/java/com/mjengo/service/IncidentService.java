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
    private final PersistentStoreService store;

    public IncidentService(PersistentStoreService store) {
        this.store = store;
        incidents.addAll(store.loadList("incidents", Incident.class));
        idCounter.set(incidents.stream().map(Incident::getId).max(Long::compareTo).orElse(0L) + 1L);
    }

    public List<Incident> getAll() {
        return incidents;
    }

    public Optional<Incident> getById(long id) {
        return incidents.stream().filter(i -> i.getId() == id).findFirst();
    }

    public Incident add(Incident i) {
        i.setId(idCounter.getAndIncrement());
        incidents.add(i);
        save();
        return i;
    }

    public void delete(long id) {
        incidents.removeIf(i -> i.getId() == id);
        save();
    }

    private void save() {
        store.saveList("incidents", incidents);
    }
}
