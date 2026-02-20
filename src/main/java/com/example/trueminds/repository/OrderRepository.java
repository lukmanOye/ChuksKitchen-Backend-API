package com.example.trueminds.repository;

import com.example.trueminds.model.order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {

    private static final String FILE_PATH = "./data/orders.json";
    private static final String COUNTER_PATH = "./data/order-counter.txt";
    private final Map<String, order> storage = new HashMap<>();
    private final ObjectMapper objectMapper;
    private final AtomicLong idCounter;

    @Autowired
    public OrderRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.idCounter = new AtomicLong(loadCounter());
        loadFromFile();
    }

    private long loadCounter() {
        File file = new File(COUNTER_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) return Long.parseLong(line.trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 1000;
    }

    private void saveCounter() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COUNTER_PATH))) {
            writer.write(String.valueOf(idCounter.get()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            if (file.length() == 0) {
                storage.clear();
                saveToFile();
                return;
            }
            try {
                order[] orders = objectMapper.readValue(file, order[].class);
                for (order order : orders) {
                    storage.put(order.getId(), order);
                }
            } catch (IOException e) {
                System.err.println("Corrupted orders.json – resetting.");
                storage.clear();
                saveToFile();
            }
        } else {
            file.getParentFile().mkdirs();
            saveToFile();
        }
    }

    private void saveToFile() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), new ArrayList<>(storage.values()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateNextId() {
        long next = idCounter.incrementAndGet();
        saveCounter();
        return "ORD-" + next;
    }

    public order save(order order) {
        if (order.getId() == null) {
            order.setId(generateNextId());
        }
        storage.put(order.getId(), order);
        saveToFile();
        return order;
    }

    public Optional<order> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<order> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<order> findByUserId(String userId) {
        List<order> result = new ArrayList<>();
        for (order order : storage.values()) {
            if (userId.equals(order.getUserId())) {
                result.add(order);
            }
        }
        return result;
    }

    public void deleteById(String id) {
        storage.remove(id);
        saveToFile();
    }
}