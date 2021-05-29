package tfar.bossmobs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Util {

    public static boolean teleport(LivingEntity entity) {
        if (!entity.level.isClientSide() && entity.isAlive()) {
            double d = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 64.0D;
            double e = entity.getY() + (double)(entity.getRandom().nextInt(64) - 32);
            double f = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 64.0D;
            if (entity instanceof ServerPlayer) {
                return teleportPlayer((ServerPlayer) entity, d, e, f);
            } else {
            return teleport(entity, d, e, f);
        }
        } else {
            return false;
        }
    }

    private static boolean teleport(LivingEntity entity, double d, double e, double f) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(d, e, f);

        while(mutableBlockPos.getY() > 0 && !entity.level.getBlockState(mutableBlockPos).getMaterial().blocksMotion()) {
            mutableBlockPos.move(Direction.DOWN);
        }

        BlockState blockState = entity.level.getBlockState(mutableBlockPos);
        boolean bl = blockState.getMaterial().blocksMotion();
        if (bl) {
            boolean bl3 = entity.randomTeleport(d, e, f, true);
            if (bl3 && !entity.isSilent()) {
                entity.level.playSound(null, entity.xo, entity.yo, entity.zo, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
                entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F); }

            return bl3;
        } else {
            return false;
        }
    }


    private static boolean teleportPlayer(ServerPlayer entity, double d, double e, double f) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(d, e, f);
        e = e + 2;
        while(mutableBlockPos.getY() > 0 && !entity.level.getBlockState(mutableBlockPos).getMaterial().blocksMotion()) {
            mutableBlockPos.move(Direction.DOWN);
        }

        BlockState blockState = entity.level.getBlockState(mutableBlockPos);
        boolean blocksMotion = blockState.getMaterial().blocksMotion();
        if (blocksMotion) {
            entity.teleportTo(d,e,f);
            boolean bl3 = entity.randomTeleport(d, e, f, true);
            if (bl3 && !entity.isSilent()) {
                entity.level.playSound(null, entity.xo, entity.yo, entity.zo, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
                entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F); }

            return bl3;
        } else {
            return false;
        }
    }
}
