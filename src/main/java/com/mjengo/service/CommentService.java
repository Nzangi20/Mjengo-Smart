package com.mjengo.service;

import com.mjengo.model.EntityComment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final List<EntityComment> comments = new ArrayList<>();
    private final PersistentStoreService store;

    public CommentService(PersistentStoreService store) {
        this.store = store;
        comments.addAll(store.loadList("comments", EntityComment.class));
        EntityComment.syncIdGenerator(comments.stream().map(EntityComment::getId).max(Long::compareTo).orElse(0L) + 1L);
    }

    public void add(EntityComment c) {
        comments.add(c);
        save();
    }

    public List<EntityComment> getByTargetKey(String targetKey) {
        return comments.stream()
                .filter(c -> c.getTargetKey().equals(targetKey))
                .collect(Collectors.toList());
    }

    private void save() {
        store.saveList("comments", comments);
    }
}
