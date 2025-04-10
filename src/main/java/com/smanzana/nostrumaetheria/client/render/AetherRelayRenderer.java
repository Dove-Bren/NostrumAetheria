package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;
import com.smanzana.nostrumaetheria.tiles.AetherRelayEntity;
import com.smanzana.nostrummagica.client.render.tile.BlockEntityRendererBase;
import com.smanzana.nostrummagica.util.Curves;
import com.smanzana.nostrummagica.util.RenderFuncs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AetherRelayRenderer extends BlockEntityRendererBase<AetherRelayEntity> {

	public AetherRelayRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	private void addVertex(PoseStack matrixStackIn, VertexConsumer buffer, int combinedLightIn, float red, float green, float blue, float alpha, Vec3 point, boolean repeat) {
		buffer.vertex(matrixStackIn.last().pose(), (float) point.x, (float) point.y, (float) point.z).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
		if (repeat) {
			this.addVertex(matrixStackIn, buffer, combinedLightIn, red, green, blue, alpha, point, false);
		}
	}
	
	protected void renderLine(PoseStack matrixStackIn, VertexConsumer buffer, int combinedLightIn, double totalTicks, float[] notColor, float[] dotDelta,
			Vec3 offset, float dotPos, int intervals, float dotLength) {
		// TODO have some capability system to turn this on or off
		
		// TODO use whether there's a TE there to change color or something
		
		// TODO offset based on the rotation of the te
		

		final float dotI = dotPos * (intervals + 1);
		final float perI = (1f / dotLength);
		
		final Vec3 dist = offset.scale(.25);
		final Vec3 control1 = dist.add(dist.yRot((float) (Math.PI * .5)));
		final Vec3 control2 = offset.subtract(dist).subtract(dist.yRot((float) (Math.PI * .5)));
		
		// Point debugging
//		for (Vector3d point : new Vector3d[]{origin, control1, control2, offset}) {
//			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
//			buffer.pos(point.xCoord, point.yCoord, point.zCoord).color(1f, 0f, 0f, 1f).endVertex();
//			buffer.pos(point.xCoord, point.yCoord + .5, point.zCoord).color(1f, 0f, 0f, 1f).endVertex();
//			tessellator.draw();
//		}
		
		for (int i = 0; i <= intervals; i++) {
			float prog = (float) i / (float) intervals;
			Vec3 point = Curves.bezier(prog, Vec3.ZERO, control1, control2, offset);
			
			float dotAmt = Math.max(0f, 1f - (perI * Math.abs(dotI - (float) i)));
			if (dotAmt == 0f) {
				float pretendI = (prog > .5f ? i - intervals : i + intervals);
				dotAmt = Math.max(0f, 1f - (perI * Math.abs(dotI - (float) pretendI)));
			}
			
			final float red = notColor[0] + dotDelta[0] * dotAmt;
			final float green = notColor[1] + dotDelta[1] * dotAmt;
			final float blue = notColor[2] + dotDelta[2] * dotAmt;
			final float alpha = notColor[3] + dotDelta[3] * dotAmt;
			
			// We aren't rendering a strip, so need to repeat every 'last' point.
			// We can do this simply by just adding each point twice except the first and last one.
			final boolean repeat = (i != 0 && i != intervals);
			
			addVertex(matrixStackIn, buffer, combinedLightIn, red, green, blue, alpha, point, repeat);
		}
	}
	
	@Override
	public void render(AetherRelayEntity te, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		// Link positions do not change often (especially at render-scale).
		// Additionally, all points on the curve are unchanging.
		// I could cache the HECK out of this... but is it worth it? Its just a bunch of math!
		
		IAetherHandler handler = te.getHandler();
		if (handler == null || !(handler instanceof AetherHandlerComponent)) {
			return;
		}
		
		final Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		boolean debug = player != null && (player.isCreative() || player.isSpectator());
		final boolean show = debug || APIProxy.hasAetherVision(player);
		
		if (!show) {
			return; // Now we have particles
		}
		
		if (debug) {
			AetherHandlerComponent relay = (AetherHandlerComponent) handler;
			final String str = relay.getAether(null) + "/" + relay.getMaxAether(null);
			matrixStackIn.pushPose();
//			matrixStackIn.translate(.5f, 0, .5f);
//			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180f));
//			matrixStackIn.scale(1f, -1f, 1f);
//			matrixStackIn.rotate(this.renderDispatcher.renderInfo.getRotation());
//			matrixStackIn.scale(.0625f, .0625f, .0625f);
//			matrixStackIn.translate(-mc.fontRenderer.getStringWidth(str)/2, AetherRelay.height + 8, 0);
//			
//			mc.fontRenderer.drawString(matrixStackIn, str, 0, 0, 0xFFFFFFFF);
			matrixStackIn.translate(.5f, (float)AetherRelay.height/8f, .5f);
			RenderFuncs.drawNameplate(matrixStackIn, bufferIn, str, mc.font, combinedLightIn, 0, false, this.context.getBlockEntityRenderDispatcher().camera);
			
			matrixStackIn.popPose();
		}

		final int intervals = 50;
		double period = (20 * 3);
		float dotLength = 10;
		float[] dotColor = {1f, 0f, 0f, 1f};
		float[] notColor = {0f, 0f, 0f, 0f};
		
		
		//if (debug) {
			period = (20 * 3);
			dotLength = 10;
			dotColor = new float[]{1f, 0f, 0f, 1f};
			notColor = new float[]{0f, 0f, 0f, 0f};
//		} else {
//			period = (20 * 10);
//			dotLength = 4;
//			dotColor = new float[]{1f, .3f, .3f, .2f};
//			notColor = new float[]{0f, 0f, 0f, 0f};
//		}
		
		final float[] dotDelta = {dotColor[0] - notColor[0], dotColor[1] - notColor[1], dotColor[2] - notColor[2], dotColor[3] - notColor[3]};
		
		matrixStackIn.pushPose();
		matrixStackIn.translate(.5f, (AetherRelay.height/16f), .5f);

		final double time = te.getLevel().getGameTime() + partialTicks;
		final float pos = (float) ((time % period) / period);
		final VertexConsumer buffer = bufferIn.getBuffer(AetheriaRenderTypes.RELAY_LINES);
		
		for (BlockPos linked : te.getLinkLocations()) {
			final Vec3 offset = Vec3.atLowerCornerOf(linked.immutable().subtract(te.getBlockPos()));
			renderLine(matrixStackIn, buffer, combinedLightIn, time, notColor, dotDelta, offset, pos, intervals, dotLength);
		}
		
		matrixStackIn.popPose();
		
	}
	
}
