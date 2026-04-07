package com.mjengo.service;

import com.mjengo.model.ConsultationRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationService {

    private final List<ConsultationRequest> requests = new ArrayList<>();
    private final PersistentStoreService store;

    public ConsultationService(PersistentStoreService store) {
        this.store = store;
        requests.addAll(store.loadList("consultations", ConsultationRequest.class));
        ConsultationRequest.syncIdGenerator(
                requests.stream().map(ConsultationRequest::getId).max(Long::compareTo).orElse(0L) + 1L);
    }

    public void add(ConsultationRequest r) {
        requests.add(r);
        save();
    }

    public List<ConsultationRequest> getAll() {
        return requests.stream()
                .sorted(Comparator.comparingLong(ConsultationRequest::getId).reversed())
                .collect(Collectors.toList());
    }

    public List<ConsultationRequest> getForClient(String email) {
        if (email == null) {
            return List.of();
        }
        return requests.stream()
                .filter(r -> email.equalsIgnoreCase(r.getClientEmail()))
                .sorted(Comparator.comparingLong(ConsultationRequest::getId).reversed())
                .collect(Collectors.toList());
    }

    private void save() {
        store.saveList("consultations", requests);
    }
}
