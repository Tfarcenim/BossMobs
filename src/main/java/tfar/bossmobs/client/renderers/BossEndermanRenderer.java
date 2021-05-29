package tfar.bossmobs.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import tfar.bossmobs.entity.BossBlaze;

public class BossEndermanRenderer extends EndermanRenderer {

    public BossEndermanRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected void scale(EnderMan livingEntity, PoseStack poseStack, float f) {
        poseStack.scale(2,2,2);
    }
}

