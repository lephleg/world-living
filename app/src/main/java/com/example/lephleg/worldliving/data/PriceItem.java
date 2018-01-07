package com.example.lephleg.worldliving.data;

public class PriceItem {

    public static final PriceItem[] ITEMS_LISTED = {
            new PriceItem(1, 1, "Meal, Inexpensive Restaurant"),

            new PriceItem(7, 2, "Water (0.33 liter bottle)"),
            new PriceItem(114, 2, "Cappuccino (regular)"),
            new PriceItem(6, 2, "Coke/Pepsi (0.33 liter bottle)"),
            new PriceItem(8, 2, "Milk (regular), (1 liter)"),
            new PriceItem(9, 2, "Loaf of Fresh White Bread (500g)"),
            new PriceItem(115, 2, "Rice (white), (1kg)"),
            new PriceItem(11, 2, "Eggs (regular) (12)"),
            new PriceItem(19, 2, "Chicken Breasts (Boneless, Skinless), (1kg)"),
            new PriceItem(121, 2, "Beef Round (1kg) (or Equivalent Back Leg Red Meat)"),
            new PriceItem(14, 2, "Bottle of Wine (Mid-Range)"),

            new PriceItem(18, 3, "One-way Ticket (Local Transport)"),
            new PriceItem(107, 3, "Taxi Start (Normal Tariff)"),
            new PriceItem(108, 3, "Taxi 1km (Normal Tariff)"),
            new PriceItem(24, 3, "Gasoline (1 liter)"),

            new PriceItem(30, 4, "Basic Utilities (Electricity, Heating, Cooling, Water, Garbage) (Monthly)"),
            new PriceItem(33, 4, "Internet (60 Mbps or More, Unlimited Data, Cable/ADSL) (Monthly"),

            new PriceItem(40, 5, "Fitness Club, Monthly Fee for 1 Adult"),
            new PriceItem(44, 5, "Cinema, International Release, 1 Seat"),

            new PriceItem(26, 6, "Apartment (1 bedroom) in City Centre (Monthly)"),
            new PriceItem(27, 6, "Apartment (1 bedroom) Outside of Centre (Monthly)"),
            new PriceItem(28, 6, "Apartment (3 bedrooms) in City Centre (Monthly)"),
            new PriceItem(29, 6, "Apartment (3 bedrooms) Outside of Centre (Monthly)"),

            new PriceItem(105, 7, "Average Monthly Net Salary (After Tax)"),
    };

    public int id;
    public int groupId;
    public String name;
    public Double avgPrice = null;

    private PriceItem(int id, int groupId, String name) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
    }

    public static PriceItem getPriceItemById(int priceItemId) {

        for (PriceItem i : ITEMS_LISTED) {
            if (priceItemId == i.id) {
                return i;
            }
        }
        return null;
    }

}
