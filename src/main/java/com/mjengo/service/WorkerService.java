package com.mjengo.service;

import com.mjengo.model.Worker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class WorkerService {
        private final List<Worker> workers = new ArrayList<>();
        private final AtomicLong idCounter = new AtomicLong(1);

        public List<Worker> getAll() {
                return workers;
        }

        public Optional<Worker> getById(long id) {
                return workers.stream().filter(w -> w.getId() == id).findFirst();
        }

        public Worker add(Worker w) {
                w.setId(idCounter.getAndIncrement());
                workers.add(w);
                return w;
        }

        public void delete(long id) {
                workers.removeIf(w -> w.getId() == id);
        }

        public long countByStatus(String status) {
                return workers.stream().filter(w -> w.getStatus().equals(status)).count();
        }
}
