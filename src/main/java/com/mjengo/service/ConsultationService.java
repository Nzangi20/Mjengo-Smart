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

    public void add(ConsultationRequest r) {
        requests.add(r);
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
}
