package tfar.bossmobs.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import tfar.bossmobs.entity.BossBlaze;
import tfar.bossmobs.entity.misc.BossBlazeFireball;

import java.util.EnumSet;

public class BossBlazeAttackGoal extends Goal {
    private final BossBlaze blaze;
    private int attackStep;
    private int attackTime;
    private int lastSeen;

    public BossBlazeAttackGoal(BossBlaze blaze) {
        this.blaze = blaze;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity livingEntity = this.blaze.getTarget();
        return livingEntity != null && livingEntity.isAlive() && this.blaze.canAttack(livingEntity);
    }

    public void start() {
        this.attackStep = 0;
    }

    public void stop() {
        this.blaze.discharge();
        this.lastSeen = 0;
    }

    private static final int COUNT = 64;

    public void tick() {
        --this.attackTime;
        LivingEntity livingEntity = this.blaze.getTarget();
        if (livingEntity != null) {
            boolean canSee = this.blaze.getSensing().canSee(livingEntity);
            if (canSee) {
                this.lastSeen = 0;
            } else {
                ++this.lastSeen;
            }

            double distanceSquared = this.blaze.distanceToSqr(livingEntity);
            if (distanceSquared < 4.0D) {
                if (!canSee) {
                    return;
                }

                if (this.attackTime <= 0) {
                    this.attackTime = 20;
                    this.blaze.doHurtTarget(livingEntity);
                }

                this.blaze.getMoveControl().setWantedPosition(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0D);
            } else if (distanceSquared < this.getFollowDistance() * this.getFollowDistance() && canSee) {
                double x1 = livingEntity.getX() - this.blaze.getX();
                double y1 = livingEntity.getY(0.5D) - this.blaze.getY(0.5D);
                double z1 = livingEntity.getZ() - this.blaze.getZ();
                if (this.attackTime <= 0) {
                    ++this.attackStep;
                    if (this.attackStep == 1) {
                        this.attackTime = 60;
                        this.blaze.charge();
                    } else if (this.attackStep <= COUNT) {
                        this.attackTime = 5;
                    } else {
                        this.attackTime = 100;
                        this.attackStep = 0;
                        this.blaze.discharge();
                    }

                    if (this.attackStep > 1) {
                        float h = Mth.sqrt(Mth.sqrt(distanceSquared)) * 0.75F;
                        if (!this.blaze.isSilent()) {
                            this.blaze.level.levelEvent(null, 1018, this.blaze.blockPosition(), 0);
                        }

                        for(int i = 0; i < 1; ++i) {
                            BossBlazeFireball smallFireball = new BossBlazeFireball(this.blaze, x1 + this.blaze.getRandom().nextGaussian() * (double)h, y1, z1 + this.blaze.getRandom().nextGaussian() * (double)h, this.blaze.level);
                            smallFireball.setPos(smallFireball.getX(), this.blaze.getY(0.5D) + 0.5D, smallFireball.getZ());
                            this.blaze.level.addFreshEntity(smallFireball);
                        }
                    }
                }

                this.blaze.getLookControl().setLookAt(livingEntity, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
                this.blaze.getMoveControl().setWantedPosition(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0D);
            }

            super.tick();
        }
    }

    private double getFollowDistance() {
        return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}
