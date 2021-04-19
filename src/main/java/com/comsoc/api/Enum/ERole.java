package com.comsoc.api.Enum;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public enum ERole {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_MONITOR;

    private static final Set<String> _values = new HashSet<>();
    static{
        for (ERole choice : ERole.values()) {
            _values.add(choice.name().toUpperCase());
        }
    }
    public static boolean contains(String value){
        return _values.contains(value);
    }
}
