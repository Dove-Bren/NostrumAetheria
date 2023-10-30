package com.smanzana.nostrumaetheria.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.component.AetherRelayComponent;
import com.smanzana.nostrumaetheria.tiles.AetherRelayEntity;
import com.smanzana.nostrummagica.utils.Curves;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AetherRelayRenderer extends TileEntityRenderer<AetherRelayEntity> {

	public AetherRelayRenderer() {
		super();
	}
	
	@Override
	public void render(AetherRelayEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		
		// Link positions do not change often (especially at render-scale).
		// Additionally, all points on the curve are unchanging.
		// I could cache the HECK out of this... but is it worth it? Its just a bunch of math!
		
		IAetherHandler handler = te.getHandler();
		if (handler == null || !(handler instanceof AetherRelayComponent)) {
			return;
		}
		
		final Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		boolean debug = player != null && (player.isCreative() || player.isSpectator());
		
		if (!debug) {
			return; // Now we have particles
		}
		
		AetherRelayComponent relay = (AetherRelayComponent) handler;

		final Vec3d origin = Vec3d.ZERO;
		final int intervals = 50;
		double period = (20 * 3);
		float dotLength = 10;
		float[] dotColor = {1f, 0f, 0f, 1f};
		float[] notColor = {0f, 0f, 0f, 0f};
		
		
		if (debug) {
			period = (20 * 3);
			dotLength = 10;
			dotColor = new float[]{1f, 0f, 0f, 1f};
			notColor = new float[]{0f, 0f, 0f, 0f};
		} else {
			period = (20 * 10);
			dotLength = 4;
			dotColor = new float[]{1f, .3f, .3f, .2f};
			notColor = new float[]{0f, 0f, 0f, 0f};
		}
		
		final float[] dotDelta = {dotColor[0] - notColor[0], dotColor[1] - notColor[1], dotColor[2] - notColor[2], dotColor[3] - notColor[3]};
		final float perI = (1f / dotLength);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.pushMatrix();
		GlStateManager.translated(x + .5, y + AetherRelay.height, z + .5);
		GlStateManager.disableColorMaterial();
		//GlStateManager.enableTexture2D();
		//GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.enableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.lineWidth(3f);
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		
		for (BlockPos linked : relay.getLinkedPositions()) {
		
			// TODO have some capability system to turn this on or off
			
			// TODO use whether there's a TE there to change color or something
			
			// TODO offset based on the rotation of the te
			
			
			
			final Vec3d offset = new Vec3d(linked.toImmutable().subtract(te.getPos()));
			final Vec3d dist = offset.scale(.25);
			final Vec3d control1 = dist.add(dist.rotateYaw((float) (Math.PI * .5)));
			final Vec3d control2 = offset.subtract(dist).subtract(dist.rotateYaw((float) (Math.PI * .5)));
			
			// Point debugging
	//		for (Vec3d point : new Vec3d[]{origin, control1, control2, offset}) {
	//			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
	//			buffer.pos(point.xCoord, point.yCoord, point.zCoord).color(1f, 0f, 0f, 1f).endVertex();
	//			buffer.pos(point.xCoord, point.yCoord + .5, point.zCoord).color(1f, 0f, 0f, 1f).endVertex();
	//			tessellator.draw();
	//		}
			
			buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			
			final float pos = (float) ((((double) te.getWorld().getGameTime() + partialTicks) % period) / period);
			final float dotI = pos * (intervals + 1);
			
			for (int i = 0; i <= intervals; i++) {
				float prog = (float) i / (float) intervals;
				Vec3d point = Curves.bezier(prog, origin, control1, control2, offset);
				
				float dotAmt = Math.max(0f, 1f - (perI * Math.abs(dotI - (float) i)));
				if (dotAmt == 0f) {
					float pretendI = (prog > .5f ? i - intervals : i + intervals);
					dotAmt = Math.max(0f, 1f - (perI * Math.abs(dotI - (float) pretendI)));
				}
				
				buffer.pos(point.x, point.y, point.z)
						.color(notColor[0] + dotDelta[0] * dotAmt,
								notColor[1] + dotDelta[1] * dotAmt,
								notColor[2] + dotDelta[2] * dotAmt,
								notColor[3] + dotDelta[3] * dotAmt).endVertex();
			}
			
			tessellator.draw();
		}
		
		GlStateManager.enableColorMaterial();
		//GlStateManager.enableTexture2D();
		
		GlStateManager.popMatrix();
		
	}
	
}
