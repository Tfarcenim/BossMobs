package tfar.bossmobs.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.bossmobs.entity.BossWitch;

@Mixin(Witch.class)
public abstract class WitchMixin extends Raider {

    protected WitchMixin(EntityType<? extends Raider> entityType, Level level) {
        super(entityType, level);
    }

    //too bad it's all hardcoded into one method
    @Inject(method = "aiStep",at = @At("RETURN"),cancellable = true)
    private void cancelWitchMechanics(CallbackInfo ci) {
        Witch witch = (Witch)(Object)this;
        if (witch instanceof BossWitch) {
           ((BossWitch) witch).modifiedAiStep();
            ci.cancel();
            //don't forget to call super
            super.aiStep();
        }
    }
}
