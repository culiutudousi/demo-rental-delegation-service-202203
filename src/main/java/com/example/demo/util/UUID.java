package com.example.demo.util;

import java.util.Locale;

public class UUID {
    public static String generateUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
