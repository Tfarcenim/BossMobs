package tfar.bossmobs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import tfar.bossmobs.entity.*;

public class BossMobs implements ModInitializer {

	public static final String MODID = "bossmobs";

	@Override
	public void onInitialize() {
		ModItems.register();
		ModEntityTypes.register();
		FabricDefaultAttributeRegistry.register(ModEntityTypes.BOSS_BLAZE, BossBlaze.createBossAttributes());
		FabricDefaultAttributeRegistry.register(ModEntityTypes.BOSS_ENDERMAN, BossEnderman.createBossAttributes());
		FabricDefaultAttributeRegistry.register(ModEntityTypes.BOSS_PILLAGER, BossPillager.createBossAttributes());
		FabricDefaultAttributeRegistry.register(ModEntityTypes.BOSS_SHEEP, BossSheep.createBossAttributes());
		FabricDefaultAttributeRegistry.register(ModEntityTypes.BOSS_WITCH, BossWitch.createBossAttributes());
	}
}
