package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.smanzana.nostrumaetheria.tiles.AetherBatteryEntity;
import com.smanzana.nostrummagica.utils.RenderFuncs;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class AetherBatteryRenderer extends TileEntityAetherDebugRenderer<AetherBatteryEntity> {

	public AetherBatteryRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	public static void renderBatteryLiquid(MatrixStack matrixStackIn, IVertexBuilder buffer, int combinedLightIn, double totalTicks, int aether, int maxAether, boolean opaque) {
		final float prog = ((float) aether / (float) maxAether);
		final float offset = 0.03f;
		final double glowPeriod = 20 * 5;
		final float glow = (float) Math.sin(Math.PI * 2 * (totalTicks % glowPeriod) / glowPeriod);
		final float alpha = opaque ? 1f : (.6f + (.2f * glow));
		
		final float red = .83f;
		final float green = .81f;
		final float blue = .5f;
		
		matrixStackIn.push();
		// Translate so block is centered horizontally, and centered vertically based on fill level
		matrixStackIn.translate(.5, 0 + (prog/2f), .5);
		matrixStackIn.scale(1-offset, prog*(1-offset*2), 1-offset);
		RenderFuncs.drawUnitCube(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
		matrixStackIn.pop();
	}
	
	@Override
	public void render(AetherBatteryEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		//super.render(te, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		
		final int aether = te.getHandler().getAether(null);
		final int maxAether = te.getHandler().getMaxAether(null);
		
		if (aether <= 0 || maxAether <= 0) {
			return;
		}
		
		final double ticks = (double) te.getWorld().getGameTime() + partialTicks;
		final IVertexBuilder buffer = bufferIn.getBuffer(AetheriaRenderTypes.AETHER_FLAT);
		renderBatteryLiquid(matrixStackIn, buffer, combinedLightIn, ticks, aether, maxAether, false);
	}
	
}
