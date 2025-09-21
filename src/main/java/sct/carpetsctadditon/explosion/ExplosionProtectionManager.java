package sct.carpetsctadditon.explosion;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class ExplosionProtectionManager {
    private static final List<BlockBox> protectionAreas = new ArrayList<>();

    public static boolean isExplosionProtected(BlockPos pos) {
        for (BlockBox area : protectionAreas) {
            if (area.contains(pos)) {
                return true;
            }
        }
        return false;
    }

    public static void addProtectionArea(BlockBox area) {
        protectionAreas.add(area);
    }

    public static List<BlockBox> getProtectionAreas() {
        return new ArrayList<>(protectionAreas);
    }

    public static void removeProtectionArea(int index) {
        if (index >= 0 && index < protectionAreas.size()) {
            protectionAreas.remove(index);
        }
    }

    public static void clearProtectionAreas() {
        protectionAreas.clear();
    }
}