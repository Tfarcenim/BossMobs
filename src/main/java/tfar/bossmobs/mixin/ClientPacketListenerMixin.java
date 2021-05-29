package tfar.bossmobs.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.bossmobs.ModEntityTypes;
import tfar.bossmobs.entity.misc.BossBlazeFireball;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow private ClientLevel level;

    @Inject(method = "handleAddEntity",at = @At("RETURN"))
    private void handleEntitySpawn(ClientboundAddEntityPacket clientboundAddEntityPacket, CallbackInfo ci) {
        EntityType<?> type = clientboundAddEntityPacket.getType();
        double d = clientboundAddEntityPacket.getX();
        double e = clientboundAddEntityPacket.getY();
        double f = clientboundAddEntityPacket.getZ();
        if (type == ModEntityTypes.BOSS_BLAZE_FIREBALL) {
            Entity fireball = new BossBlazeFireball(d,e,f, clientboundAddEntityPacket.getXa(), clientboundAddEntityPacket.getYa(), clientboundAddEntityPacket.getZa(),this.level);
            int i = clientboundAddEntityPacket.getId();
            fireball.setPacketCoordinates(d, e, f);
            fireball.moveTo(d, e, f);
            fireball.xRot = (float)(clientboundAddEntityPacket.getxRot() * 360) / 256.0F;
            fireball.yRot = (float)(clientboundAddEntityPacket.getyRot() * 360) / 256.0F;
            fireball.setId(i);
            fireball.setUUID(clientboundAddEntityPacket.getUUID());
            this.level.putNonPlayerEntity(i, fireball);
        }
    }
}
