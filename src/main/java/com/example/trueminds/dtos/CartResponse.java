package com.example.trueminds.dtos;

import com.example.trueminds.model.CartItem;
import java.util.List;

public record CartResponse(String userId, List<CartItem> items,double total) {}