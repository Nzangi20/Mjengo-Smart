package com.mjengo.service;

import com.mjengo.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TransactionService {
    private final List<Transaction> transactions = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Transaction> getAll() {
        return transactions;
    }

    public Optional<Transaction> getById(long id) {
        return transactions.stream().filter(t -> t.getId() == id).findFirst();
    }

    public Transaction add(Transaction t) {
        t.setId(idCounter.getAndIncrement());
        transactions.add(t);
        return t;
    }

    public void delete(long id) {
        transactions.removeIf(t -> t.getId() == id);
    }

    public double totalAmount() {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    public double totalByStatus(String status) {
        return transactions.stream().filter(t -> t.getStatus().equals(status)).mapToDouble(Transaction::getAmount)
                .sum();
    }
}
