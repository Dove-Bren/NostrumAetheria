package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;
import com.smanzana.nostrumaetheria.tiles.AetherRelayEntity;
import com.smanzana.nostrummagica.utils.Curves;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class AetherRelayRenderer extends TileEntityRenderer<AetherRelayEntity> {

	public AetherRelayRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	private void addVertex(MatrixStack matrixStackIn, IVertexBuilder buffer, int combinedLightIn, float red, float green, float blue, float alpha, Vector3d point, boolean repeat) {
		buffer.pos(matrixStackIn.getLast().getMatrix(), (float) point.x, (float) point.y, (float) point.z).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
		if (repeat) {
			this.addVertex(matrixStackIn, buffer, combinedLightIn, red, green, blue, alpha, point, false);
		}
	}
	
	protected void renderLine(MatrixStack matrixStackIn, IVertexBuilder buffer, int combinedLightIn, double totalTicks, float[] notColor, float[] dotDelta,
			Vector3d offset, float dotPos, int intervals, float dotLength) {
		// TODO have some capability system to turn this on or off
		
		// TODO use whether there's a TE there to change color or something
		
		// TODO offset based on the rotation of the te
		

		final float dotI = dotPos * (intervals + 1);
		final float perI = (1f / dotLength);
		
		final Vector3d dist = offset.scale(.25);
		final Vector3d control1 = dist.add(dist.rotateYaw((float) (Math.PI * .5)));
		final Vector3d control2 = offset.subtract(dist).subtract(dist.rotateYaw((float) (Math.PI * .5)));
		
		// Point debugging
//		for (Vector3d point : new Vector3d[]{origin, control1, control2, offset}) {
//			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
//			buffer.pos(point.xCoord, point.yCoord, point.zCoord).color(1f, 0f, 0f, 1f).endVertex();
//			buffer.pos(point.xCoord, point.yCoord + .5, point.zCoord).color(1f, 0f, 0f, 1f).endVertex();
//			tessellator.draw();
//		}
		
		for (int i = 0; i <= intervals; i++) {
			float prog = (float) i / (float) intervals;
			Vector3d point = Curves.bezier(prog, Vector3d.ZERO, control1, control2, offset);
			
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
	public void render(AetherRelayEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		// Link positions do not change often (especially at render-scale).
		// Additionally, all points on the curve are unchanging.
		// I could cache the HECK out of this... but is it worth it? Its just a bunch of math!
		
		IAetherHandler handler = te.getHandler();
		if (handler == null || !(handler instanceof AetherHandlerComponent)) {
			return;
		}
		
		final Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		boolean debug = player != null && (player.isCreative() || player.isSpectator());
		final boolean show = debug || APIProxy.hasAetherVision(player);
		
		if (!show) {
			return; // Now we have particles
		}
		
		if (debug) {
			AetherHandlerComponent relay = (AetherHandlerComponent) handler;
			final String str = relay.getAether(null) + "/" + relay.getMaxAether(null);
			matrixStackIn.push();
			matrixStackIn.translate(.5f, 0, .5f);
			matrixStackIn.scale(.0625f, .0625f, .0625f);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90f));
			matrixStackIn.translate(-mc.fontRenderer.getStringWidth(str)/2, AetherRelay.height + 8, 0);
			matrixStackIn.scale(1f, -1f, 1f);
			
			mc.fontRenderer.drawString(matrixStackIn, str, 0, 0, 0xFFFFFFFF);
			
			matrixStackIn.pop();
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
		
		matrixStackIn.push();
		matrixStackIn.translate(.5f, (AetherRelay.height/16f), .5f);

		final double time = te.getWorld().getGameTime() + partialTicks;
		final float pos = (float) ((time % period) / period);
		final IVertexBuilder buffer = bufferIn.getBuffer(AetheriaRenderTypes.RELAY_LINES);
		
		for (BlockPos linked : te.getLinkLocations()) {
			final Vector3d offset = Vector3d.copy(linked.toImmutable().subtract(te.getPos()));
			renderLine(matrixStackIn, buffer, combinedLightIn, time, notColor, dotDelta, offset, pos, intervals, dotLength);
		}
		
		matrixStackIn.pop();
		
	}
	
}
