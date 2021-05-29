package tfar.bossmobs.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import tfar.bossmobs.entity.BossBlaze;

public class BossBlazeRenderer extends MobRenderer<BossBlaze, BlazeModel<BossBlaze>> {
    private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");

    public BossBlazeRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BlazeModel<>(), 1F);
    }

    protected int getBlockLightLevel(BossBlaze blaze, BlockPos blockPos) {
        return 15;
    }

    public ResourceLocation getTextureLocation(BossBlaze blaze) {
        return BLAZE_LOCATION;
    }

    @Override
    protected void scale(BossBlaze livingEntity, PoseStack poseStack, float f) {
        poseStack.scale(2,2,2);
    }
}

