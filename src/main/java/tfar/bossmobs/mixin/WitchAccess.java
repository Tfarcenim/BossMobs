package tfar.bossmobs.mixin;

import net.minecraft.world.entity.monster.Witch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Witch.class)
public interface WitchAccess {

    @Accessor int getUsingTime();

    @Accessor void setUsingTime(int time);


}
