package com.example.trueminds.repository;

import com.example.trueminds.model.FoodItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FoodRepository {

    private static final String FILE_PATH = "./data/foods.json";
    private final Map<String, FoodItem> storage = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public FoodRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        loadFromFile();
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
                FoodItem[] items = objectMapper.readValue(file, FoodItem[].class);
                for (FoodItem item : items) {
                    storage.put(item.getId(), item);
                }
            } catch (IOException e) {
                System.err.println("Corrupted foods.json – resetting.");
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

    public FoodItem save(FoodItem item) {
        storage.put(item.getId(), item);
        saveToFile();
        return item;
    }

    public Optional<FoodItem> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<FoodItem> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<FoodItem> findAvailable() {
        List<FoodItem> result = new ArrayList<>();
        for (FoodItem item : storage.values()) {
            if (item.isAvailable()) {
                result.add(item);
            }
        }
        return result;
    }

    public void deleteById(String id) {
        storage.remove(id);
        saveToFile();
    }
}