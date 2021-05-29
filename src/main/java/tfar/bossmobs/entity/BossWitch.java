package tfar.bossmobs.entity;

import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import tfar.bossmobs.mixin.WitchAccess;

import java.util.List;

public class BossWitch extends Witch {

    private final ServerBossEvent bossEvent;


    public BossWitch(EntityType<? extends Witch> entityType, Level level) {
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

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.remove();
        } else {
            this.noActionTime = 0;
        }
    }

    public void modifiedAiStep() {
        if (!this.level.isClientSide && this.isAlive()) {
         /*   this.healRaidersGoal.decrementCooldown();
            if (this.healRaidersGoal.getCooldown() <= 0) {
                this.attackPlayersGoal.setCanAttack(true);
            } else {
                this.attackPlayersGoal.setCanAttack(false);
            }*/

            if (this.isDrinkingPotion()) {
                if (getAccess().getUsingTime() <= 0) {
                    this.setUsingItem(false);
                    ItemStack itemStack = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (itemStack.getItem() == Items.POTION) {
                        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemStack);

                        for (MobEffectInstance mobEffectInstance : list) {
                            this.addEffect(new MobEffectInstance(mobEffectInstance));
                        }
                    }

                    //this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
                }
                this.getAccess().setUsingTime(getAccess().getUsingTime() - 1);
            } else {
                Potion potion = null;
                if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    potion = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    potion = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5F && this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
                    potion = Potions.SWIFTNESS;
                }

                if (potion != null) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
                    getAccess().setUsingTime(this.getMainHandItem().getUseDuration());
                    this.setUsingItem(true);
                    if (!this.isSilent()) {
                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }

                   /* AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                    attributeInstance.removeModifier(SPEED_MODIFIER_DRINKING);
                    attributeInstance.addTransientModifier(SPEED_MODIFIER_DRINKING);*/
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.level.broadcastEntityEvent(this, (byte)15);
            }
        }
    }

    public WitchAccess getAccess() {
        return (WitchAccess)this;
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return Witch.createAttributes().add(Attributes.MAX_HEALTH, 150);
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float f) {
        if (Math.random() < .9) {
            if (!this.isDrinkingPotion()) {
                Vec3 vec3 = livingEntity.getDeltaMovement();
                double x = livingEntity.getX() + vec3.x - this.getX();
                double e = livingEntity.getEyeY() - 1.1 - this.getY();
                double g = livingEntity.getZ() + vec3.z - this.getZ();
                float h = Mth.sqrt(x * x + g * g);
                Potion potion = Potions.STRONG_HARMING;
                if (livingEntity instanceof Raider) {
                    if (livingEntity.getHealth() <= 4.0F) {
                        potion = Potions.HEALING;
                    } else {
                        potion = Potions.REGENERATION;
                    }

                    this.setTarget(null);
                } else if (h >= 8.0F && !livingEntity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    potion = Potions.STRONG_SLOWNESS;
                } else if (livingEntity.getHealth() >= 8.0F && !livingEntity.hasEffect(MobEffects.POISON)) {
                    potion = Potions.STRONG_POISON;
                } else if (h <= 3.0F && !livingEntity.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                    potion = Potions.WEAKNESS;
                }

                ThrownPotion thrownPotion = new ThrownPotion(this.level, this);
                thrownPotion.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
                thrownPotion.xRot -= -20.0F;
                thrownPotion.shoot(x, e + h * 0.2F, g, 0.75F, 8.0F);
                if (!this.isSilent()) {
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                }

                this.level.addFreshEntity(thrownPotion);
            }
        } else {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(livingEntity.level);
            lightningBolt.setPosRaw(livingEntity.getX(),livingEntity.level.getHeight(Heightmap.Types.MOTION_BLOCKING,(int)livingEntity.getX(),(int)livingEntity.getZ()),livingEntity.getZ());
            livingEntity.level.addFreshEntity(lightningBolt);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.isFire())
        return super.hurt(damageSource,3 * f);
        return super.hurt(damageSource, f);
    }

    //todo: potions are difficult to drop in loot tables, lets drop 1 of each
    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int i, boolean bl) {
        super.dropCustomDeathLoot(damageSource, i, bl);
        for (Potion potion : Registry.POTION) {
            List<MobEffectInstance> effects = potion.getEffects();
            for (MobEffectInstance instance : effects) {
                if (instance.getEffect().isBeneficial()) {
                    ItemStack stack = new ItemStack(Items.POTION);
                    PotionUtils.setPotion(stack,potion);
                    spawnAtLocation(stack);
                }
            }
        }
    }
}
