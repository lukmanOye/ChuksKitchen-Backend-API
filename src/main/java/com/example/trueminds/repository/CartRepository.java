package com.example.trueminds.repository;

import com.example.trueminds.model.Cart;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class CartRepository {

    private static final String FILE_PATH = "./data/carts.json";
    private final Map<String, Cart> storage = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public CartRepository(ObjectMapper objectMapper) {
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
                Cart[] carts = objectMapper.readValue(file, Cart[].class);
                for (Cart cart : carts) {
                    storage.put(cart.getUserId(), cart);
                }
            } catch (IOException e) {
                System.err.println("Corrupted carts.json – resetting.");
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

    public Cart save(Cart cart) {
        storage.put(cart.getUserId(), cart);
        saveToFile();
        return cart;
    }

    public Optional<Cart> findByUserId(String userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    public void deleteByUserId(String userId) {
        storage.remove(userId);
        saveToFile();
    }
}