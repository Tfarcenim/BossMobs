package tfar.bossmobs;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tfar.bossmobs.entity.*;
import tfar.bossmobs.entity.misc.BossBlazeFireball;

public class ModEntityTypes {

    private static final float scale = 2;

    public static final EntityType<BossBlaze> BOSS_BLAZE = EntityType.Builder.of(BossBlaze::new, MobCategory.MONSTER).fireImmune().sized(0.6F * scale, 1.8F * scale)
            .clientTrackingRange(8).build("boss_blaze");

    public static final EntityType<BossBlazeFireball> BOSS_BLAZE_FIREBALL = EntityType.Builder.<BossBlazeFireball>of(BossBlazeFireball::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10)
            .build("boss_blaze_fireball");

    public static final EntityType<BossEnderman> BOSS_ENDERMAN = EntityType.Builder.of(BossEnderman::new, MobCategory.MONSTER).sized(0.6F * scale, 2.9F * scale)
            .clientTrackingRange(8).build("boss_enderman");

    public static final EntityType<BossPillager> BOSS_PILLAGER = EntityType.Builder.of(BossPillager::new, MobCategory.MONSTER).sized(0.6F * scale, 1.9F * scale)
            .clientTrackingRange(8).build("boss_pillager");

    public static final EntityType<BossSheep> BOSS_SHEEP = EntityType.Builder.of(BossSheep::new, MobCategory.MONSTER).sized(0.9F * scale, 1.3F * scale)
            .clientTrackingRange(10).build("boss_sheep");

    public static final EntityType<BossWitch> BOSS_WITCH = EntityType.Builder.of(BossWitch::new, MobCategory.MONSTER).sized(0.6F * scale, 1.9F * scale)
            .clientTrackingRange(8).build("boss_witch");

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE,new ResourceLocation(BossMobs.MODID,"boss_blaze"),BOSS_BLAZE);
        Registry.register(Registry.ENTITY_TYPE,new ResourceLocation(BossMobs.MODID,"boss_blaze_fireball"),BOSS_BLAZE_FIREBALL);
        Registry.register(Registry.ENTITY_TYPE,new ResourceLocation(BossMobs.MODID,"boss_enderman"),BOSS_ENDERMAN);
        Registry.register(Registry.ENTITY_TYPE,new ResourceLocation(BossMobs.MODID,"boss_pillager"),BOSS_PILLAGER);
        Registry.register(Registry.ENTITY_TYPE,new ResourceLocation(BossMobs.MODID,"boss_sheep"),BOSS_SHEEP);
        Registry.register(Registry.ENTITY_TYPE,new ResourceLocation(BossMobs.MODID,"boss_witch"),BOSS_WITCH);
    }
}
