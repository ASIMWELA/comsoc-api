package com.comsoc.api.Enum;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public enum EPosition {
    PRESIDENT,
    V_PRESIDENT,
    GENERAL_SECRETARY,
    V_GENERAL_SECRETARY,
    PUBLICITY_SECRETARY,
    V_PUBLICITY_SECRETARY,
    PROJECT_COORDINATOR,
    V_PROJECT_COORDINATOR,
    FIRST_YEAR_1_REPRESENTATIVE,
    FIRST_YEAR_2_REPRESENTATIVE,
    SECOND_YEAR_REPRESENTATIVE,
    THIRD_YEAR_REPRESENTATIVE,
    TREASURE;
    private static final Set<String> _values = new HashSet<>();
    static{
        for (EPosition choice : EPosition.values()) {
            _values.add(choice.name().toUpperCase());
        }
    }
    public static boolean contains(String value){
        return _values.contains(value);
    }
}
