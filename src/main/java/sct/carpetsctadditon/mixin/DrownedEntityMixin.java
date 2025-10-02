package sct.carpetsctadditon.mixin;

import net.minecraft.entity.mob.DrownedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sct.carpetsctadditon.SCTSettings;

@Mixin(DrownedEntity.class)
public class DrownedEntityMixin {

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void preventUpdateSwimming(CallbackInfo ci) {
        if (SCTSettings.disableDrownedSwimming) {
            ci.cancel();
        }
    }
}
