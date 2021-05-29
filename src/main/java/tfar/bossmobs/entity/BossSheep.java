package tfar.bossmobs.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BossSheep extends Sheep {

    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET= SynchedEntityData.defineId(Guardian.class, EntityDataSerializers.INT);
    private LivingEntity clientSideCachedAttackTarget;
    private int clientSideAttackTime;

    private final ServerBossEvent bossEvent;

    public BossSheep(EntityType<? extends Sheep> entityType, Level level) {
        super(entityType, level);
        this.bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_ATTACK_TARGET, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        //some attacks
        this.targetSelector.addGoal(1, new MeleeAttackGoal(this,2,true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(4, new LaserAttackGoal(this));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
    }

    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.bossEvent.addPlayer(serverPlayer);
    }

    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
    }

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.remove();
        } else {
            this.noActionTime = 0;
        }
    }

    public int getAttackDuration() {
        return 60;
    }

    private void setActiveAttackTarget(int i) {
        this.entityData.set(DATA_ID_ATTACK_TARGET, i);
    }

    public boolean hasActiveAttackTarget() {
        return this.entityData.get(DATA_ID_ATTACK_TARGET) != 0;
    }

    @Nullable
    public LivingEntity getActiveAttackTarget() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientSideCachedAttackTarget != null) {
                return this.clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET));
                if (entity instanceof LivingEntity) {
                    this.clientSideCachedAttackTarget = (LivingEntity)entity;
                    return this.clientSideCachedAttackTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (DATA_ID_ATTACK_TARGET.equals(entityDataAccessor)) {
            this.clientSideAttackTime = 0;
            this.clientSideCachedAttackTarget = null;
        }

    }

    public float getAttackAnimationScale(float f) {
        return (this.clientSideAttackTime + f) / this.getAttackDuration();
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return Sheep.createAttributes().add(Attributes.MAX_HEALTH, 60).add(Attributes.ATTACK_DAMAGE,10);
    }


    static class LaserAttackGoal extends Goal {
        private final BossSheep sheep;
        private int attackTime;

        public LaserAttackGoal(BossSheep sheep) {
            this.sheep = sheep;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingEntity = this.sheep.getTarget();
            return livingEntity != null && livingEntity.isAlive();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && (this.sheep.distanceToSqr(this.sheep.getTarget()) > 9.0D);
        }

        public void start() {
            this.attackTime = -10;
            this.sheep.getNavigation().stop();
            this.sheep.getLookControl().setLookAt(this.sheep.getTarget(), 90.0F, 90.0F);
            this.sheep.hasImpulse = true;
        }

        public void stop() {
            this.sheep.setActiveAttackTarget(0);
            this.sheep.setTarget(null);
           // this.guardian.randomStrollGoal.trigger();
        }

        public void tick() {
            LivingEntity livingEntity = this.sheep.getTarget();
            this.sheep.getNavigation().stop();
            this.sheep.getLookControl().setLookAt(livingEntity, 90.0F, 90.0F);
            if (!this.sheep.canSee(livingEntity)) {
                this.sheep.setTarget(null);
            } else {
                ++this.attackTime;
                if (this.attackTime == 0) {
                    this.sheep.setActiveAttackTarget(this.sheep.getTarget().getId());
                    if (!this.sheep.isSilent()) {
                        //todo, crashes because of a cast in clientpacketlistener
                        //this.guardian.level.broadcastEntityEvent(this.guardian, (byte)21);
                    }
                } else if (this.attackTime >= this.sheep.getAttackDuration()) {
                    float f = 1.0F;
                    if (this.sheep.level.getDifficulty() == Difficulty.HARD) {
                        f += 2.0F;
                    }

                    livingEntity.hurt(DamageSource.indirectMagic(this.sheep, this.sheep), f);
                    livingEntity.hurt(DamageSource.mobAttack(this.sheep), (float)this.sheep.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.sheep.setTarget(null);
                }

                super.tick();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isSheared()) {
            //50% more damage when sheared
            return super.hurt(damageSource,1.5f *  f);
        }
        return super.hurt(damageSource, f);
    }
}
