package tfar.bossmobs.entity;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import tfar.bossmobs.goals.BossBlazeAttackGoal;
import tfar.bossmobs.mixin.BlazeAccess;

import java.util.ArrayList;
import java.util.List;

public class BossBlaze extends Blaze {

    private final ServerBossEvent bossEvent;
    public BossBlaze(EntityType<? extends Blaze> entityType, Level level) {
        super(entityType, level);
        this.bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
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

    public void charge() {
        ((BlazeAccess)this).$setCharged(true);
    }

    public void discharge() {
        ((BlazeAccess)this).$setCharged(false);
    }

    @Override
    protected void registerGoals() {
        //this.goalSelector.addGoal(4, new Blaze.BlazeAttackGoal(this));
        this.goalSelector.addGoal(4,new BossBlazeAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return Blaze.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 14)//double
                .add(Attributes.MOVEMENT_SPEED, 0.50)//slightly faster
                .add(Attributes.MAX_HEALTH,100);//5x health
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource instanceof IndirectEntityDamageSource && damageSource.getEntity() instanceof Snowball) {
            //double damage for snowballs
            return super.hurt(damageSource, 2 * f);
        }
        return super.hurt(damageSource, f);
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


    //todo, move to loot tables
    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int i, boolean bl) {
        super.dropCustomDeathLoot(damageSource, i, bl);
        List<ItemStack> extras = new ArrayList<>();
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(Enchantments.FIRE_ASPECT,1);
        extras.add(sword);
    }
}
