package com.mjengo.service;

import com.mjengo.model.EntityComment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final List<EntityComment> comments = new ArrayList<>();

    public void add(EntityComment c) {
        comments.add(c);
    }

    public List<EntityComment> getByTargetKey(String targetKey) {
        return comments.stream()
                .filter(c -> c.getTargetKey().equals(targetKey))
                .collect(Collectors.toList());
    }
}
