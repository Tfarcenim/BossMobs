package tfar.bossmobs.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.world.entity.monster.Witch;

public class BossWitchRenderer extends WitchRenderer {
    public BossWitchRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected void scale(Witch witch, PoseStack poseStack, float f) {
        super.scale(witch, poseStack, f);
        poseStack.scale(2,2,2);
    }
}
