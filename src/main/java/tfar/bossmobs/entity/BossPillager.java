package tfar.bossmobs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;

public class BossPillager extends Pillager {

    private final ServerBossEvent bossEvent;


    public BossPillager(EntityType<? extends Pillager> entityType, Level level) {
        super(entityType, level);
        this.bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
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

    public static AttributeSupplier.Builder createBossAttributes() {
        return Pillager.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, .30);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        boolean hurt = super.hurt(damageSource, damage);
        if (hurt) {
            if (damageSource.getEntity() instanceof Player) {
                summonPillagers((Player) damageSource.getEntity());
            }
        }
        return hurt;
    }

    public void summonPillagers(Player player) {
        if (player.getMainHandItem().getItem() == Items.ROTTEN_FLESH) {
            addEffect(new MobEffectInstance(MobEffects.POISON, 400, 0));
        }

        if (this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE) && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            int xPos = Mth.floor(this.getX());
            int yPos = Mth.floor(this.getY());
            int zPos = Mth.floor(this.getZ());
            Pillager pillager = EntityType.PILLAGER.create(level);

            int near = 7;
            int far = 16;

            for (int l = 0; l < 50; ++l) {
                int x1 = xPos + Mth.nextInt(this.random, near, far) * Mth.nextInt(this.random, -1, 1);
                int y1 = yPos + Mth.nextInt(this.random, near, far) * Mth.nextInt(this.random, -1, 1);
                int z1 = zPos + Mth.nextInt(this.random, near, far) * Mth.nextInt(this.random, -1, 1);
                BlockPos blockPos = new BlockPos(x1, y1, z1);
                EntityType<?> entityType = pillager.getType();
                SpawnPlacements.Type type = SpawnPlacements.getPlacementType(entityType);
                if (NaturalSpawner.isSpawnPositionOk(type, this.level, blockPos, entityType) && SpawnPlacements.checkSpawnRules(entityType, (ServerLevelAccessor) level, MobSpawnType.REINFORCEMENT, blockPos, this.level.random)) {
                    pillager.setPos(x1, y1, z1);
                    if (!this.level.hasNearbyAlivePlayer(x1, y1, z1, 7.0D) && this.level.isUnobstructed(pillager) && this.level.noCollision(pillager) && !this.level.containsAnyLiquid(pillager.getBoundingBox())) {
                        pillager.setTarget(player);
                        pillager.finalizeSpawn((ServerLevelAccessor) level, this.level.getCurrentDifficultyAt(pillager.blockPosition()), MobSpawnType.REINFORCEMENT, null, null);
                        ((ServerLevelAccessor) level).addFreshEntityWithPassengers(pillager);
                        // this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                        break;
                    }
                }
            }
        }
    }

    private static final Item[] items = new Item[]{
            Items.IRON_HELMET,
            Items.IRON_CHESTPLATE,
            Items.IRON_LEGGINGS,
            Items.IRON_BOOTS,

            Items.IRON_PICKAXE,
            Items.IRON_SHOVEL,
            Items.IRON_HOE,
            Items.IRON_AXE
    };

    //todo: loot tables with enchantments are hard to do
    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int i, boolean bl) {
        super.dropCustomDeathLoot(damageSource, i, bl);

        for (Item item : items) {
            ItemStack stack = new ItemStack(item);
            if (item instanceof ArmorItem) {
                boolean enchant = Math.random() < .5;
                if (enchant) {
                    EnchantmentHelper.enchantItem(random, stack, 50, true);
                }
            }
            this.spawnAtLocation(stack);
        }
    }
}
