package sct.carpetsctadditon;


import carpet.api.settings.Rule;


public class SCTSettings {
    public static final String SCT = "SCT";

    public enum DropsIntoShulkerMode {
        FALSE, OFFHAND, HOTBAR, INVENTORY, ALL
    }

    @Rule(categories = {SCT})
    public static DropsIntoShulkerMode dropsIntoShulker = DropsIntoShulkerMode.FALSE;

    @Rule(categories = {SCT} )
    public static boolean explosionProtectionAreas = false;

    @Rule(categories = {SCT})
    public static boolean disableDrownedSwimming = false;

    @Rule(categories = {SCT}, options = {"-1", "6000", "12000", "24000", "32000"}, strict = false )
    public static int CustomItemDiscardAge = 6000;
}