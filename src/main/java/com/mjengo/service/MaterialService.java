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
    private final PersistentStoreService store;

    public MaterialService(PersistentStoreService store) {
        this.store = store;
        materials.addAll(store.loadList("materials", Material.class));
        idCounter.set(materials.stream().map(Material::getId).max(Long::compareTo).orElse(0L) + 1L);
    }

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
        save();
        return m;
    }

    public void delete(long id) {
        materials.removeIf(m -> m.getId() == id);
        save();
    }

    public long countByStatus(String status) {
        return materials.stream().filter(m -> m.getStatus().equals(status)).count();
    }

    private void save() {
        store.saveList("materials", materials);
    }
}
