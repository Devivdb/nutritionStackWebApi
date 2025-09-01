package com.nutritionstack.nutritionstackwebapi.model.nutrition;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Unit {
    G("g"),
    KG("kg"),
    ML("ml"),
    L("l"),
    OZ("oz"),
    LB("lb"),
    CUP("cup"),
    TBSP("tbsp"),
    TSP("tsp");
    
    private final String value;
    
    Unit(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    @JsonCreator
    public static Unit fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }

        String cleanedText = text.trim().toLowerCase();
        
        return UnitParser.parseUnit(cleanedText);
    }
    
    public static boolean isValidUnit(String text) {
        try {
            fromString(text);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static class UnitParser {
        private static Unit parseUnit(String cleanedText) {
            switch (cleanedText) {
                case "g":
                case "gram":
                case "grams":
                    return G;
                case "kg":
                case "kilo":
                case "kilos":
                case "kilogram":
                case "kilograms":
                    return KG;
                case "ml":
                case "milliliter":
                case "milliliters":
                    return ML;
                case "l":
                case "liter":
                case "liters":
                case "litre":
                case "litres":
                    return L;
                case "oz":
                case "ounce":
                case "ounces":
                    return OZ;
                case "lb":
                case "pound":
                case "pounds":
                    return LB;
                case "cup":
                case "cups":
                    return CUP;
                case "tbsp":
                case "tablespoon":
                case "tablespoons":
                    return TBSP;
                case "tsp":
                case "teaspoon":
                case "teaspoons":
                    return TSP;
                default:
                    throw new IllegalArgumentException("Unknown unit: '" + cleanedText + "'. Allowed values: g, kg, ml, l, oz, lb, cup, tbsp, tsp");
            }
        }
    }
}
