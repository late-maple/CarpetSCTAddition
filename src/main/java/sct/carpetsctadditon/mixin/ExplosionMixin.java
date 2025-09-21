package sct.carpetsctadditon.mixin;

import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sct.carpetsctadditon.SCTSettings;
import sct.carpetsctadditon.explosion.ExplosionProtectionManager;
import net.minecraft.util.math.BlockPos;
import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void onExplosionAffectWorld(boolean particles, CallbackInfo ci) {
        // 检查是否启用了防爆区域功能
        if (SCTSettings.explosionProtectionAreas) {
            Explosion explosion = (Explosion) (Object) this;
            List<BlockPos> affectedBlocks = explosion.getAffectedBlocks();

            // 移除在保护区域内的方块
            affectedBlocks.removeIf(ExplosionProtectionManager::isExplosionProtected);
        }
    }
}
