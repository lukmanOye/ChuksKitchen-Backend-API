package com.example.trueminds.repository;

import com.example.trueminds.model.user;
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
public class userRepository {

    private static final String FILE_PATH = "./data/users.json";
    private final Map<String, user> storage = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public userRepository(ObjectMapper objectMapper) {
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
                user[] users = objectMapper.readValue(file, user[].class);
                for (user u : users) {
                    storage.put(u.getId(), u);
                }
            } catch (IOException e) {
                System.err.println("Corrupted users.json file – resetting.");
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

    public user save(user user) {
        storage.put(user.getId(), user);
        saveToFile();
        return user;
    }

    public Optional<user> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<user> findByEmail(String email) {
        for (user u : storage.values()) {
            if (email.equals(u.getEmail())) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public Optional<user> findByPhone(String phone) {
        for (user u : storage.values()) {
            if (phone.equals(u.getPhone())) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public List<user> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(String id) {
        storage.remove(id);
        saveToFile();
    }
}