package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.smanzana.nostrumaetheria.tiles.AetherBathTileEntity;
import com.smanzana.nostrummagica.utils.RenderFuncs;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class AetherBathRenderer extends TileEntityRenderer<AetherBathTileEntity> {

	public AetherBathRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	private void renderPoolFilm(MatrixStack matrixStackIn, IVertexBuilder buffer, int packedLightIn, float red, float green, float blue, float alpha, float radius) {
		final int points = 8;
		
		RenderFuncs.drawEllipse(radius, radius, points, matrixStackIn, buffer, packedLightIn, red, green, blue, alpha);
		
//		GlStateManager.disableBlend();
//		GlStateManager.enableBlend();
//		GlStateManager.disableAlphaTest();
//		GlStateManager.enableAlphaTest();
//		GlStateManager.disableLighting();
//		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
//		GlStateManager.enableTexture();
//		GlStateManager.disableTexture();
//		GlStateManager.enableColorMaterial();
//		
//		Tessellator tessellator = Tessellator.getInstance();
//		BufferBuilder buffer = tessellator.getBuffer();
//		buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_NORMAL);
//		buffer.pos(0, 0, 0).normal(0, 1, 0).endVertex();
//		for (int i = points; i >= 0; i--) {
//			double angle = ((float) i / (float) points) * 2 * Math.PI;
//			double px = Math.cos(angle) * radius;
//			double py = Math.sin(angle) * radius;
//			buffer.pos(px, 0, py).normal(0, 1, 0).endVertex();
//		}
//		
//		tessellator.draw();
	}
	
	@Override
	public void render(AetherBathTileEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		int aether = te.getHandler().getAether(null);
		if (aether > 0) {
			float aetherLevel = (float) aether / (float) te.getHandler().getMaxAether(null);
			float radius;
			float levelOffset;
			
			if (aetherLevel > .8f) {
				radius = .535f;
				levelOffset = .95f;
			} else if (aetherLevel > .3f) {
				radius = .485f;
				levelOffset = .92f;
			} else {
				radius = .43f;
				levelOffset = .9f;
			}
			final double glowPeriod = 20 * 5;
			final float glow = (float) Math.sin(Math.PI * 2 * ((double) te.getWorld().getGameTime() % glowPeriod) / glowPeriod);
			final float alpha = .5f + (.025f * glow);
			
			final double wavePeriod = 20 * 8;
			final float wave = (float) Math.sin(Math.PI * 2 * ((double) te.getWorld().getGameTime() % wavePeriod) / wavePeriod);
			final float waveHeight = wave * .002f;
			
			final IVertexBuilder buffer = bufferIn.getBuffer(AetheriaRenderTypes.AETHER_FLAT_TRIS);
			
			//(te.getWorld().getTotalWorldTime() % 300) + partialTicks
			matrixStackIn.push();
			matrixStackIn.translate(.5f, levelOffset + waveHeight, .5);
			renderPoolFilm(matrixStackIn, buffer, combinedLightIn, .83f, .81f, .5f, alpha, radius);
			matrixStackIn.pop();
		}
		
		ItemStack item = te.getItem();
		if (item.isEmpty())
			return;
		
		final double rotPeriod = 90 * 20;
		float rot = (float)((double)(te.getWorld().getGameTime() % rotPeriod) / rotPeriod) * 360f;
		float scale = .75f;
		float yoffset = (float) (.1f * (-.5f + Math.sin(((double) System.currentTimeMillis()) / 1000.0))); // Copied from Altar
		
		matrixStackIn.push();
		matrixStackIn.translate(.5f, 1.25f + yoffset, .5f);
		matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rot));
		
		matrixStackIn.scale(scale, scale, scale);
		
		RenderFuncs.ItemRenderer(item, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		matrixStackIn.pop();
	}
	
}
