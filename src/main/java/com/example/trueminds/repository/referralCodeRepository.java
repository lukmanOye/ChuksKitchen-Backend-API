package com.example.trueminds.repository;

import com.example.trueminds.model.referralCode;
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
public class referralCodeRepository {

    private static final String FILE_PATH = "./data/referralCodes.json";
    private final Map<String, referralCode> storage = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public referralCodeRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                referralCode[] codes = objectMapper.readValue(file, referralCode[].class);
                for (referralCode code : codes) {
                    storage.put(code.name(), code);
                }
            } catch (IOException e) {
                System.err.println("Corrupted referralCodes.json – resetting.");
                storage.clear();
                saveToFile();
            }
        } else {
            file.getParentFile().mkdirs();
            List<referralCode> defaults = List.of(
                    new referralCode(1L, "TRUEMINDS"),
                    new referralCode(2L, "WELCOME20")
            );
            try {
                objectMapper.writeValue(new File(FILE_PATH), defaults);
                for (referralCode code : defaults) {
                    storage.put(code.name(), code);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFile() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), new ArrayList<>(storage.values()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<referralCode> findByName(String name) {
        return Optional.ofNullable(storage.get(name));
    }

    public referralCode save(referralCode code) {
        storage.put(code.name(), code);
        saveToFile();
        return code;
    }

    public void deleteByName(String name) {
        storage.remove(name);
        saveToFile();
    }

    public List<referralCode> findAll() {
        return new ArrayList<>(storage.values());
    }
}