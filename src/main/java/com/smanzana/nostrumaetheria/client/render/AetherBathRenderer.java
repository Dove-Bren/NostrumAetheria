package com.smanzana.nostrumaetheria.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.smanzana.nostrumaetheria.tiles.AetherBathTileEntity;
import com.smanzana.nostrummagica.utils.RenderFuncs;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

public class AetherBathRenderer extends TileEntityRenderer<AetherBathTileEntity> {

	public AetherBathRenderer() {
		
	}
	
	private void renderPoolFilm(double radius) {
		final int points = 8;
		
		GlStateManager.disableBlend();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		//GlStateManager.enableTexture();
		//GlStateManager.disableTexture();
		GlStateManager.enableColorMaterial();
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_NORMAL);
		buffer.pos(0, 0, 0).normal(0, 1, 0).endVertex();
		for (int i = points; i >= 0; i--) {
			double angle = ((float) i / (float) points) * 2 * Math.PI;
			double px = Math.cos(angle) * radius;
			double py = Math.sin(angle) * radius;
			buffer.pos(px, 0, py).normal(0, 1, 0).endVertex();
		}
		
		tessellator.draw();
	}
	
	@Override
	public void render(AetherBathTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {

		int aether = te.getHandler().getAether(null);
		if (aether > 0) {
			float aetherLevel = (float) aether / (float) te.getHandler().getMaxAether(null);
			double radius;
			double levelOffset;
			
			if (aetherLevel > .8f) {
				radius = .535;
				levelOffset = .95;
			} else if (aetherLevel > .3f) {
				radius = .485;
				levelOffset = .92;
			} else {
				radius = .43;
				levelOffset = .9;
			}
			final double glowPeriod = 20 * 5;
			final float glow = (float) Math.sin(Math.PI * 2 * ((double) te.getWorld().getGameTime() % glowPeriod) / glowPeriod);
			final float alpha = .5f + (.025f * glow);
			
			final double wavePeriod = 20 * 8;
			final float wave = (float) Math.sin(Math.PI * 2 * ((double) te.getWorld().getGameTime() % wavePeriod) / wavePeriod);
			final float waveHeight = wave * .002f;
			
			//(te.getWorld().getTotalWorldTime() % 300) + partialTicks
			GlStateManager.pushMatrix();
			GlStateManager.translated(x + .5, y + levelOffset + waveHeight, z + .5);
			GlStateManager.color4f(.83f, .81f, .5f, alpha);
			renderPoolFilm(radius);
			GlStateManager.popMatrix();
			GlStateManager.enableTexture();
		}
		
		ItemStack item = te.getItem();
		if (item.isEmpty())
			return;
		
		final double rotPeriod = 90 * 20;
		float rot = (float)((double)(te.getWorld().getGameTime() % rotPeriod) / rotPeriod) * 360f;
		float scale = .75f;
		float yoffset = (float) (.1f * (-.5f + Math.sin(((double) System.currentTimeMillis()) / 1000.0))); // Copied from Altar
		
		GlStateManager.pushMatrix();
		GlStateManager.translated(x + .5, y + 1.25 + yoffset, z + .5);
		GlStateManager.rotatef(rot, 0, 1f, 0);
		
		GlStateManager.scalef(scale, scale, scale);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();
		GlStateManager.enableAlphaTest();
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableTexture();
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		RenderFuncs.renderItemStandard(item);
//		Minecraft.getInstance().getRenderItem()
//			.renderItem(item, TransformType.GROUND);
		GlStateManager.popMatrix();
		GlStateManager.enableTexture();
	}
	
}
