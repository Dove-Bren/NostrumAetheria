package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.smanzana.nostrumaetheria.tiles.AetherBathTileEntity;
import com.smanzana.nostrummagica.client.render.tile.BlockEntityRendererBase;
import com.smanzana.nostrummagica.util.RenderFuncs;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class AetherBathRenderer extends BlockEntityRendererBase<AetherBathTileEntity> {

	public AetherBathRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	private void renderPoolFilm(PoseStack matrixStackIn, VertexConsumer buffer, int packedLightIn, float red, float green, float blue, float alpha, float radius) {
		final int points = 8;
		
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90f));
		RenderFuncs.drawEllipse(radius, radius, points, matrixStackIn, buffer, packedLightIn, red, green, blue, alpha);
		matrixStackIn.popPose();
		
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
	public void render(AetherBathTileEntity te, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

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
			final float glow = (float) Math.sin(Math.PI * 2 * ((double) te.getLevel().getGameTime() % glowPeriod) / glowPeriod);
			final float alpha = .5f + (.025f * glow);
			
			final double wavePeriod = 20 * 8;
			final float wave = (float) Math.sin(Math.PI * 2 * ((double) te.getLevel().getGameTime() % wavePeriod) / wavePeriod);
			final float waveHeight = wave * .002f;
			
			final VertexConsumer buffer = bufferIn.getBuffer(AetheriaRenderTypes.AETHER_FLAT_TRIS);
			
			//(te.getWorld().getTotalWorldTime() % 300) + partialTicks
			matrixStackIn.pushPose();
			matrixStackIn.translate(.5f, levelOffset + waveHeight, .5);
			renderPoolFilm(matrixStackIn, buffer, combinedLightIn, .83f, .81f, .5f, alpha, radius);
			matrixStackIn.popPose();
		}
		
		ItemStack item = te.getItem();
		if (item.isEmpty())
			return;
		
		final double rotPeriod = 90 * 20;
		float rot = (float)((double)(te.getLevel().getGameTime() % rotPeriod) / rotPeriod) * 360f;
		float scale = .75f;
		float yoffset = (float) (.1f * (-.5f + Math.sin(((double) System.currentTimeMillis()) / 1000.0))); // Copied from Altar
		
		matrixStackIn.pushPose();
		matrixStackIn.translate(.5f, 1.25f + yoffset, .5f);
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rot));
		
		matrixStackIn.scale(scale, scale, scale);
		
		RenderFuncs.RenderWorldItem(item, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		matrixStackIn.popPose();
	}
	
}
