package tfar.bossmobs.entity;

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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tfar.bossmobs.Util;

public class BossEnderman extends EnderMan {

    private final ServerBossEvent bossEvent;

    public BossEnderman(EntityType<? extends EnderMan> entityType, Level level) {
        super(entityType, level);
        this.bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
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


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        //this.goalSelector.addGoal(1, new EnderMan.EndermanFreezeWhenLookedAt(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
       // this.goalSelector.addGoal(10, new EnderMan.EndermanLeaveBlockGoal(this));
       // this.goalSelector.addGoal(11, new EnderMan.EndermanTakeBlockGoal(this));
        //this.targetSelector.addGoal(1, new EnderMan.EndermanLookForPlayerGoal(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        //this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Endermite.class, 10, true, false, ENDERMITE_SELECTOR));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public float getScale() {
        return 2;
    }

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.remove();
        } else {
            this.noActionTime = 0;
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return EnderMan.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 12)//double
                .add(Attributes.MOVEMENT_SPEED, 0.25)//slightly faster
                .add(Attributes.MAX_HEALTH,200);//5x health
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        boolean hurt = super.hurt(damageSource, f);
        if (hurt) {
            //teleport the attacker
            Entity attacker = damageSource.getDirectEntity();
            if (attacker instanceof LivingEntity) {
                Util.teleport((LivingEntity) attacker);
            }
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean b = super.doHurtTarget(entity);
        if (b) {
            //teleport self after attack
            teleport();
        }
        return b;
    }


    //todo, spawn end city treasure
    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int i, boolean bl) {
        super.dropCustomDeathLoot(damageSource, i, bl);
    }
}
