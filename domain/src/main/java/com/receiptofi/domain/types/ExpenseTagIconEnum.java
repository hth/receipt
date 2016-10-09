package com.receiptofi.domain.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: hitender
 * Date: 10/4/16 10:49 AM
 */
public enum ExpenseTagIconEnum {
    V100("V100", "Blank", "/static/images/expense"),
    V101("V101", "Home", "/static/images/expense"),
    V102("V102", "Business", "/static/images/expense"),
    V103("V103", "Auto/Transport", "/static/images/expense"),
    V104("V104", "Bank", "/static/images/expense"),
    V105("V105", "Studies/School/Books/University", "/static/images/expense"),
    V106("V106", "Food/Lunch/Dinner/Restaurant", "/static/images/expense"),
    V107("V107", "Gas/Petrol", "/static/images/expense"),
    V108("V108", "Clothes/Shopping", "/static/images/expense"),
    V109("V109", "Grocery ", "/static/images/expense"),
    V110("V110", "Travel", "/static/images/expense"),
    V111("V111", "Pets", "/static/images/expense"),
    V112("V112", "Pharmacy/Medicine", "/static/images/expense"),
    V113("V113", "Doctor", "/static/images/expense"),
    V114("V114", "Tools & Hardware", "/static/images/expense"),
    V115("V115", "Phone", "/static/images/expense"),
    V116("V116", "Fitness", "/static/images/expense"),
    V117("V117", "Fruits & Vegetables", "/static/images/expense"),
    V118("V118", "Insurance", "/static/images/expense"),
    V119("V119", "Party", "/static/images/expense"),
    V120("V120", "Coupons", "/static/images/expense"),
    V121("V121", "Horses", "/static/images/expense"),
    V122("V122", "Utilities", "/static/images/expense"),
    V123("V123", "Coffee & Tea", "/static/images/expense"),
    V124("V124", "Decorative/Christmas", "/static/images/expense"),
    V125("V125", "Hotels/Lodging and Boarding", "/static/images/expense"),
    V126("V126", "Miscellaneous", "/static/images/expense"),
    V127("V127", "Home Improvement", "/static/images/expense"),
    V128("V128", "Rent", "/static/images/expense"),
    V129("V129", "Movies", "/static/images/expense");

    private final String name;
    private final String description;
    private final String webLocation;

    ExpenseTagIconEnum(String name, String description, String webLocation) {
        this.name = name;
        this.description = description;
        this.webLocation = webLocation;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWebLocation() {
        return webLocation;
    }

    public String getWebLocationWithFilename() {
        return webLocation + "/" + name + ".png";
    }

    public static List<ExpenseTagIconEnum> asList() {
        ExpenseTagIconEnum[] all = ExpenseTagIconEnum.values();
        return Arrays.asList(all);
    }

    public static List<String> asListLocation() {
        List<String> locations = new ArrayList<>();
        for (ExpenseTagIconEnum expenseTagIcon : asList()) {
            locations.add(expenseTagIcon.getWebLocationWithFilename());
        }
        /* Ignore the blank icon. */
        return locations.subList(1, locations.size());
    }

    public static ExpenseTagIconEnum getExpenseTagIcon(int index) {
        return asList().get(index);
    }

    @Override
    public String toString() {
        return description;
    }
}
