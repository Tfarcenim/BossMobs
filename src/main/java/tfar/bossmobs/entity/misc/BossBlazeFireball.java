package tfar.bossmobs.entity.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import tfar.bossmobs.ModEntityTypes;

public class BossBlazeFireball extends Fireball {

    private static final float damage = 10;

    public BossBlazeFireball(EntityType<? extends Fireball> entityType, Level level) {
        super(entityType, level);
    }

    public BossBlazeFireball(double d, double e, double f, double g, double h, double i, Level level) {
        super(ModEntityTypes.BOSS_BLAZE_FIREBALL, d, e, f, g, h, i, level);
    }

    public BossBlazeFireball(LivingEntity livingEntity, double d, double e, double f, Level level) {
        super(ModEntityTypes.BOSS_BLAZE_FIREBALL, livingEntity, d, e, f, level);
    }


    protected void onHitEntity(EntityHitResult entityHitResult) {
       // super.onHitEntity(entityHitResult);
        if (!this.level.isClientSide) {
            Entity entity = entityHitResult.getEntity();
            if (!entity.fireImmune()) {
                Entity entity2 = this.getOwner();
                int i = entity.getRemainingFireTicks();
                entity.setSecondsOnFire(10);
                boolean bl = entity.hurt(DamageSource.fireball(this, entity2), damage);
                if (!bl) {
                    entity.setRemainingFireTicks(i);
                } else if (entity2 instanceof LivingEntity) {
                    this.doEnchantDamageEffects((LivingEntity)entity2, entity);
                }
            }
        }
        placeFires(blockPosition());
    }

    protected void onHitBlock(BlockHitResult blockHitResult) {
     //   super.onHitBlock(blockHitResult);
        if (!this.level.isClientSide) {
            Entity entity = this.getOwner();
            if (!(entity instanceof Mob) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                if (this.level.isEmptyBlock(blockPos)) {
                    this.level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level, blockPos));
                }
            }
        }
        placeFires(blockPosition());
    }

    protected void placeFires(BlockPos pos) {
        for (int x = -2; x < 2;x++) {
            for (int z = -2; z < 1; z++) {
                //todo ceilings?
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING,x + pos.getX(),z + pos.getZ());
                BlockPos firePos = new BlockPos(x,y,z);
                if (level.isEmptyBlock(firePos.above())) {
                    this.level.setBlockAndUpdate(firePos, BaseFireBlock.getState(this.level, firePos.above()));
                }
            }
        }
    }
}
