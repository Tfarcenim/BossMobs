package tfar.bossmobs.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.world.entity.monster.Pillager;

public class BossPillagerRenderer extends PillagerRenderer {
    public BossPillagerRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected void scale(Pillager abstractIllager, PoseStack poseStack, float f) {
        super.scale(abstractIllager, poseStack, f);
        poseStack.scale(2,2,2);
    }
}
