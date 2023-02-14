package ru.netology.diplom.data;

import com.github.javafaker.Faker;
import lombok.*;

import java.util.Locale;

@NoArgsConstructor
@Data

public class DataGenerator {
    @Value
    public static class DataCard {
        private String cardNumber;
        private String cardOwner;
        private String cvcCode;
    }

    private static String specSymbols = "/.,|?$%^':!@#*()_+-dй №ё";
    private static String specSymbolsInOwnerField = "/3,|?$%^:!@#*()+№";

    public static String generateOwner(String locale) {
        var faker = new Faker(new Locale(locale));
        var name = faker.name().fullName().toUpperCase();
        return name;
    }

    public static String generateCodeCVC() {
        var count = 1000 + (int) (Math.random() * 1000);
        var cvcCode = String.valueOf(count).substring(1);
        return cvcCode;
    }

    public static String getSpecSymbols() {
        return specSymbols;
    }

    public static String getSpecSymbolsInOwnerField() {
        return specSymbolsInOwnerField;
    }

    public static DataCard getFirstCardData(String locale) {
        return new DataCard("4444 4444 4444 4441", generateOwner(locale), generateCodeCVC());
    }

    public static DataCard getSecondCardData(String locale) {
        return new DataCard("4444 4444 4444 4442", generateOwner(locale), generateCodeCVC());
    }

    public static String generateFifteenValuesNumber() {
        var count = 1_000_000_000_000_000L + (long) (Math.random() * 1_000_000_000_000_000L);
        var cardNumberFifteenValues = String.valueOf(count).substring(1);
        return cardNumberFifteenValues;
    }

    public static String getRightNumberWithSpecSymbols() {
        var firstHalfANumber = "1111 2222";
        var secondHalfANumber = "3333 4444";
        var additionalValues = "55 888";
        var result = firstHalfANumber + specSymbols + secondHalfANumber + additionalValues;
        return result;
    }

    public static String generateWrongCvcCode() {
        var wrongCVCCode = (int) (Math.random() * 99);
        return String.valueOf(wrongCVCCode);
    }

    public static DataCard getWrongDataCard(String locale) {
        return new DataCard(generateFifteenValuesNumber(), generateOwner(locale), generateWrongCvcCode());
    }

    public static DataCard getRightDataCard(String locale) {
        return new DataCard(getRightNumberWithSpecSymbols(), generateOwner(locale), generateCodeCVC());
    }
}
