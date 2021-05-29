package tfar.bossmobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import tfar.bossmobs.ModEntityTypes;
import tfar.bossmobs.client.renderers.*;

public class BossMobsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(ModEntityTypes.BOSS_BLAZE, (EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) -> new BossBlazeRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(ModEntityTypes.BOSS_BLAZE_FIREBALL,(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) -> new ThrownItemRenderer<>(dispatcher, context.getItemRenderer(), 0.75F, true));
		EntityRendererRegistry.INSTANCE.register(ModEntityTypes.BOSS_ENDERMAN, (EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) -> new BossEndermanRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(ModEntityTypes.BOSS_PILLAGER, (EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) -> new BossPillagerRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(ModEntityTypes.BOSS_SHEEP, (EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) -> new BossSheepRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(ModEntityTypes.BOSS_WITCH, (EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) -> new BossWitchRenderer(dispatcher));

	}
}
