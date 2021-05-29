package tfar.bossmobs.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.phys.Vec3;
import tfar.bossmobs.entity.BossSheep;

public class BossSheepRenderer extends SheepRenderer {
    public BossSheepRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");

    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

    @Override
    public void render(Sheep mob, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        BossSheep boss = (BossSheep)mob;
        super.render(mob, f, g, poseStack, multiBufferSource, i);
        LivingEntity target =boss.getActiveAttackTarget();
        if (target != null) {
            float h = boss.getAttackAnimationScale(g);
            float j = (float)mob.level.getGameTime() + g;
            float k = j * 0.5F % 1.0F;
            float l = boss.getEyeHeight();
            poseStack.pushPose();
            poseStack.translate(0.0D, l, 0.0D);
            Vec3 vec3 = this.getPosition(target, target.getBbHeight() * 0.5D, g);
            Vec3 vec32 = this.getPosition(mob, l, g);
            Vec3 vec33 = vec3.subtract(vec32);
            float m = (float)(vec33.length() + 1.0D);
            vec33 = vec33.normalize();
            float n = (float)Math.acos(vec33.y);
            float o = (float)Math.atan2(vec33.z, vec33.x);
            poseStack.mulPose(Vector3f.YP.rotationDegrees((float) (90.0 - o * (180 / Math.PI))));
            poseStack.mulPose(Vector3f.XP.rotationDegrees((float) (n * (180 / Math.PI))));
            float q = j * 0.05F * -1.5F;
            float r = h * h;
            int s = 64 + (int)(r * 191.0F);
            int t = 32 + (int)(r * 191.0F);
            int u = 128 - (int)(r * 64.0F);
            float v = 0.2F;
            float w = 0.282F;
            float x = Mth.cos(q + 2.3561945F) * w;
            float y = Mth.sin(q + 2.3561945F) * w;
            float z = Mth.cos(q + 0.7853982F) * w;
            float aa = Mth.sin(q + 0.7853982F) * w;
            float ab = Mth.cos(q + 3.926991F) * w;
            float ac = Mth.sin(q + 3.926991F) * w;
            float ad = Mth.cos(q + 5.4977875F) * w;
            float ae = Mth.sin(q + 5.4977875F) * w;
            float af = Mth.cos(q + 3.1415927F) * v;
            float ag = Mth.sin(q + 3.1415927F) * v;
            float ah = Mth.cos(q + 0.0F) * v;
            float ai = Mth.sin(q + 0.0F) * v;
            float aj = Mth.cos(q + 1.5707964F) * v;
            float ak = Mth.sin(q + 1.5707964F) * v;
            float al = Mth.cos(q + 4.712389F) * v;
            float am = Mth.sin(q + 4.712389F) * v;
            float ao = 0.0F;
            float ap = 0.4999F;
            float aq = -1.0F + k;
            float ar = m * 2.5F + aq;
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            vertex(vertexConsumer, matrix4f, matrix3f, af, m, ag, s, t, u, 0.4999F, ar);
            vertex(vertexConsumer, matrix4f, matrix3f, af, 0.0F, ag, s, t, u, 0.4999F, aq);
            vertex(vertexConsumer, matrix4f, matrix3f, ah, 0.0F, ai, s, t, u, 0.0F, aq);
            vertex(vertexConsumer, matrix4f, matrix3f, ah, m, ai, s, t, u, 0.0F, ar);
            vertex(vertexConsumer, matrix4f, matrix3f, aj, m, ak, s, t, u, 0.4999F, ar);
            vertex(vertexConsumer, matrix4f, matrix3f, aj, 0.0F, ak, s, t, u, 0.4999F, aq);
            vertex(vertexConsumer, matrix4f, matrix3f, al, 0.0F, am, s, t, u, 0.0F, aq);
            vertex(vertexConsumer, matrix4f, matrix3f, al, m, am, s, t, u, 0.0F, ar);
            float as = 0.0F;
            if (mob.tickCount % 2 == 0) {
                as = 0.5F;
            }

            vertex(vertexConsumer, matrix4f, matrix3f, x, m, y, s, t, u, 0.5F, as + 0.5F);
            vertex(vertexConsumer, matrix4f, matrix3f, z, m, aa, s, t, u, 1.0F, as + 0.5F);
            vertex(vertexConsumer, matrix4f, matrix3f, ad, m, ae, s, t, u, 1.0F, as);
            vertex(vertexConsumer, matrix4f, matrix3f, ab, m, ac, s, t, u, 0.5F, as);
            poseStack.popPose();
        }
    }

    private Vec3 getPosition(LivingEntity livingEntity, double d, float f) {
        double e = Mth.lerp(f, livingEntity.xOld, livingEntity.getX());
        double g = Mth.lerp(f, livingEntity.yOld, livingEntity.getY()) + d;
        double h = Mth.lerp(f, livingEntity.zOld, livingEntity.getZ());
        return new Vec3(e, g, h);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float g, float h, int i, int j, int k, float l, float m) {
        vertexConsumer.vertex(matrix4f, f, g, h).color(i, j, k, 255).uv(l, m).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xf000f0).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    protected void scale(Sheep witch, PoseStack poseStack, float f) {
        super.scale(witch, poseStack, f);
        poseStack.scale(2,2,2);
    }

}
