package sct.carpetsctadditon;

import carpet.CarpetExtension;
import carpet.api.settings.Rule;
import carpet.api.settings.Validators;

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
}