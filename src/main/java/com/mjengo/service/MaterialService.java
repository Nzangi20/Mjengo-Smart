package com.mjengo.service;

import com.mjengo.model.Material;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MaterialService {
    private final List<Material> materials = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Material> getAll() {
        return materials;
    }

    public Optional<Material> getById(long id) {
        return materials.stream().filter(m -> m.getId() == id).findFirst();
    }

    public Material add(Material m) {
        m.setId(idCounter.getAndIncrement());
        m.recalculateStatus();
        materials.add(m);
        return m;
    }

    public void delete(long id) {
        materials.removeIf(m -> m.getId() == id);
    }

    public long countByStatus(String status) {
        return materials.stream().filter(m -> m.getStatus().equals(status)).count();
    }
}
