package com.example.trueminds.repository;

import com.example.trueminds.model.Rating;
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
public class RatingRepository {

    private static final String FILE_PATH = "./data/ratings.json";
    private final Map<String, Rating> storage = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public RatingRepository(ObjectMapper objectMapper) {
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
                Rating[] ratings = objectMapper.readValue(file, Rating[].class);
                for (Rating r : ratings) {
                    storage.put(r.getId(), r);
                }
            } catch (IOException e) {
                System.err.println("Corrupted ratings.json – resetting.");
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

    public Rating save(Rating rating) {
        storage.put(rating.getId(), rating);
        saveToFile();
        return rating;
    }

    public Optional<Rating> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Rating> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(String id) {
        storage.remove(id);
        saveToFile();
    }
}