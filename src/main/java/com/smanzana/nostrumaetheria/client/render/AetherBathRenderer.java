package com.smanzana.nostrumaetheria.client.render;

import org.lwjgl.opengl.GL11;

import com.smanzana.nostrumaetheria.tiles.AetherBathTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

public class AetherBathRenderer extends TileEntitySpecialRenderer<AetherBathTileEntity> {

	public AetherBathRenderer() {
		
	}
	
	private void renderPoolFilm(double radius) {
		final int points = 8;
		
		GlStateManager.disableBlend();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.enableAlpha();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
		GlStateManager.disableTexture2D();
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
	public void render(AetherBathTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alphaIn) {

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
			final float glow = (float) Math.sin(Math.PI * 2 * ((double) te.getWorld().getTotalWorldTime() % glowPeriod) / glowPeriod);
			final float alpha = .5f + (.025f * glow);
			
			final double wavePeriod = 20 * 8;
			final float wave = (float) Math.sin(Math.PI * 2 * ((double) te.getWorld().getTotalWorldTime() % wavePeriod) / wavePeriod);
			final float waveHeight = wave * .002f;
			
			//(te.getWorld().getTotalWorldTime() % 300) + partialTicks
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + .5, y + levelOffset + waveHeight, z + .5);
			GlStateManager.color(.83f, .81f, .5f, alpha);
			renderPoolFilm(radius);
			GlStateManager.popMatrix();
			GlStateManager.enableTexture2D();
		}
		
		ItemStack item = te.getItem();
		if (item.isEmpty())
			return;
		
		float rot = 2.0f * (Minecraft.getSystemTime() / 50);
		float scale = .75f;
		float yoffset = (float) (.1f * (-.5f + Math.sin(((double) Minecraft.getSystemTime()) / 1000.0)));
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + .5, y + 1.25 + yoffset, z + .5);
		GlStateManager.rotate(rot, 0, 1f, 0);
		
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableTexture2D();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		Minecraft.getMinecraft().getRenderItem()
			.renderItem(item, TransformType.GROUND);
		GlStateManager.popMatrix();
		GlStateManager.enableTexture2D();
	}
	
}
