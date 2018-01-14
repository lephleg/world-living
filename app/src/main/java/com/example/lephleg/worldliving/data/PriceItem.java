package com.example.lephleg.worldliving.data;

public class PriceItem {

    public static final PriceItem[] ITEMS_LISTED = {
            new PriceItem(1, 1, "Meal, Inexpensive Restaurant", PricesContract.PricesEntry.COLUMN_MEAL),

            new PriceItem(7, 2, "Water (0.33 liter bottle)", PricesContract.PricesEntry.COLUMN_WATER),
            new PriceItem(114, 2, "Cappuccino (regular)", PricesContract.PricesEntry.COLUMN_COFFEE),
            new PriceItem(6, 2, "Coke/Pepsi (0.33 liter bottle)", PricesContract.PricesEntry.COLUMN_COKE),
            new PriceItem(8, 2, "Milk (regular), (1 liter)", PricesContract.PricesEntry.COLUMN_MILK),
            new PriceItem(9, 2, "Loaf of Fresh White Bread (500g)", PricesContract.PricesEntry.COLUMN_BREAD),
            new PriceItem(115, 2, "Rice (white), (1kg)", PricesContract.PricesEntry.COLUMN_RICE),
            new PriceItem(11, 2, "Eggs (regular) (12)", PricesContract.PricesEntry.COLUMN_EGGS),
            new PriceItem(19, 2, "Chicken Breasts (Boneless, Skinless), (1kg)", PricesContract.PricesEntry.COLUMN_CHICKEN),
            new PriceItem(121, 2, "Beef Round (1kg) (or Equivalent Back Leg Red Meat)", PricesContract.PricesEntry.COLUMN_BEEF),
            new PriceItem(14, 2, "Bottle of Wine (Mid-Range)", PricesContract.PricesEntry.COLUMN_WINE),

            new PriceItem(18, 3, "One-way Ticket (Local Transport)", PricesContract.PricesEntry.COLUMN_TICKET),
            new PriceItem(107, 3, "Taxi Start (Normal Tariff)", PricesContract.PricesEntry.COLUMN_TAXI_START),
            new PriceItem(108, 3, "Taxi 1km (Normal Tariff)", PricesContract.PricesEntry.COLUMN_TAXI_1KM),
            new PriceItem(24, 3, "Gasoline (1 liter)", PricesContract.PricesEntry.COLUMN_GAS),

            new PriceItem(30, 4, "Basic Utilities (Electricity, Heating, Cooling, Water, Garbage) (Monthly)", PricesContract.PricesEntry.COLUMN_UTILITIES),
            new PriceItem(33, 4, "Internet (60 Mbps or More, Unlimited Data, Cable/ADSL) (Monthly", PricesContract.PricesEntry.COLUMN_INTERNET),

            new PriceItem(40, 5, "Fitness Club, Monthly Fee for 1 Adult", PricesContract.PricesEntry.COLUMN_GYM),
            new PriceItem(44, 5, "Cinema, International Release, 1 Seat", PricesContract.PricesEntry.COLUMN_CINEMA),

            new PriceItem(26, 6, "Apartment (1 bedroom) in City Centre (Monthly)", PricesContract.PricesEntry.COLUMN_RENT_SM_IN),
            new PriceItem(27, 6, "Apartment (1 bedroom) Outside of Centre (Monthly)", PricesContract.PricesEntry.COLUMN_RENT_SM_OUT),
            new PriceItem(28, 6, "Apartment (3 bedrooms) in City Centre (Monthly)", PricesContract.PricesEntry.COLUMN_RENT_MD_IN),
            new PriceItem(29, 6, "Apartment (3 bedrooms) Outside of Centre (Monthly)", PricesContract.PricesEntry.COLUMN_RENT_MD_OUT),

            new PriceItem(105, 7, "Average Monthly Net Salary (After Tax)", PricesContract.PricesEntry.COLUMN_SALARY),
    };

    public int id;
    public int groupId;
    public String name;
    public Double avgPrice = null;
    public String dbColumn;

    private PriceItem(int id, int groupId, String name, String dbColumn) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.dbColumn = dbColumn;
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
