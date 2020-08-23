package com.smanzana.nostrumaetheria.client.render;

import org.lwjgl.opengl.GL11;

import com.smanzana.nostrumaetheria.blocks.AetherRelay.AetherRelayEntity;
import com.smanzana.nostrummagica.utils.Curves;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class AetherRelayRenderer extends TileEntitySpecialRenderer<AetherRelayEntity> {

	public static void init() {
		ClientRegistry.bindTileEntitySpecialRenderer(AetherRelayEntity.class,
				new AetherRelayRenderer());
	}
	
	public AetherRelayRenderer() {
		super();
	}
	
	@Override
	public void renderTileEntityAt(AetherRelayEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
//		AetherRelayEntity linked = te.getPairedRelay();
//		if (linked == null) {
//			return;
//		}
		
		BlockPos linked = te.getLinkedPosition();
		if (linked == null) {
			return;
		}
		
		// TODO have some capability system to turn this on or off
		
		// TODO use whether there's a TE there to change color or something
		
		// TODO offset based on the rotation of the te
		
		final int intervals = 50;
		
		Vec3d offset = new Vec3d(linked.toImmutable().subtract(te.getPos()));
		Vec3d origin = Vec3d.ZERO;
		Vec3d dist = offset.scale(.25);
		Vec3d control1 = dist.add(dist.rotateYaw((float) (Math.PI * .5)));
		Vec3d control2 = offset.subtract(dist).subtract(dist.rotateYaw((float) (Math.PI * .5)));
		
		// Move control orthoganal to the line
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + .5, y + .6, z + .5);
		GlStateManager.disableColorMaterial();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.glLineWidth(3f);
		GlStateManager.color(1f, 1f, 1f, 1f);
		
		// Point debugging
//		for (Vec3d point : new Vec3d[]{origin, control1, control2, offset}) {
//			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
//			buffer.pos(point.xCoord, point.yCoord, point.zCoord).color(1f, 0f, 0f, 1f).endVertex();
//			buffer.pos(point.xCoord, point.yCoord + .5, point.zCoord).color(1f, 0f, 0f, 1f).endVertex();
//			tessellator.draw();
//		}
		
		buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		double period = (20 * 3);
		float pos = (float) ((((double) te.getWorld().getTotalWorldTime() + partialTicks) % period) / period);
		// stretch pos so that it ranges from -.25 to 1.25
		//pos *= 1.5f;
		//pos -= .25f;
		
		
		float dotI = pos * (intervals + 1);
		float dotLength = 10;
		
		float[] dotColor = {1f, 0f, 0f, 1f};
		//float[] notColor = {0f, 0f, 0f, 1f};
		float[] notColor = {0f, 0f, 0f, 0f};
		
		float perI = (1f / dotLength);
		
		float[] dotDelta = {dotColor[0] - notColor[0], dotColor[1] - notColor[1], dotColor[2] - notColor[2], dotColor[3] - notColor[3]};
		for (int i = 0; i <= intervals; i++) {
			float prog = (float) i / (float) intervals;
			Vec3d point = Curves.bezier(prog, origin, control1, control2, offset);
			
			float dotAmt = Math.max(0f, 1f - (perI * Math.abs(dotI - (float) i)));
			if (dotAmt == 0f) {
				float pretendI = (prog > .5f ? i - intervals : i + intervals);
				dotAmt = Math.max(0f, 1f - (perI * Math.abs(dotI - (float) pretendI)));
			}
			
			buffer.pos(point.xCoord, point.yCoord, point.zCoord)
					.color(notColor[0] + dotDelta[0] * dotAmt,
							notColor[1] + dotDelta[1] * dotAmt,
							notColor[2] + dotDelta[2] * dotAmt,
							notColor[3] + dotDelta[3] * dotAmt).endVertex();
		}
		
		tessellator.draw();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableTexture2D();
		
		GlStateManager.popMatrix();
		
	}
	
}
