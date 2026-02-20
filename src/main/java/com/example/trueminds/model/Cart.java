package com.example.trueminds.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Cart {
    private String userId;
    private List<CartItem> items = new ArrayList<>();
}