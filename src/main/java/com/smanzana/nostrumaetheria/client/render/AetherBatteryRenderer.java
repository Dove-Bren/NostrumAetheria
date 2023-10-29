package com.smanzana.nostrumaetheria.client.render;

import org.lwjgl.opengl.GL11;

import com.smanzana.nostrumaetheria.tiles.AetherBatteryEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;

public class AetherBatteryRenderer extends TileEntityAetherDebugRenderer<AetherBatteryEntity> {

	public AetherBatteryRenderer() {
		super();
	}
	
	public void renderBatteryAt(World world, double x, double y, double z, float partialTicks, int destroyStage, int aether, int maxAether, boolean opaque) {
		final double prog = ((double) aether / (double) maxAether);
		final double min = 0.03;
		final double max = (1.0 - .03);
		final double maxy = min + (prog * (max - min));
		final double glowPeriod = 20 * 5;
		final float glow = (float) Math.sin(Math.PI * 2 * ((double) world.getTotalWorldTime() % glowPeriod) / glowPeriod);
		final float alpha = opaque ? 1f : (.6f + (.2f * glow));
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(x, y, z);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
//		GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
//				SourceFactor.ONE, DestFactor.ZERO);
		//Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.disableBlend();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.disableAlpha();
		GlStateManager.enableColorMaterial();
		GlStateManager.disableColorMaterial();
		GlStateManager.enableTexture2D();
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.depthMask(true);
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
		// xy z=0
		buffer.pos(min, min, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(min, maxy, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, maxy, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, min, min).color(.83f, .81f, .5f, alpha).endVertex();
		
		// xy z=1
		buffer.pos(min, min, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, min, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, maxy, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(min, maxy, max).color(.83f, .81f, .5f, alpha).endVertex();
		
		// zy x=0
		buffer.pos(min, min, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(min, min, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(min, maxy, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(min, maxy, min).color(.83f, .81f, .5f, alpha).endVertex();

		// zy x=1
		buffer.pos(max, min, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, maxy, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, maxy, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, min, max).color(.83f, .81f, .5f, alpha).endVertex();
		
		// maxy
		buffer.pos(min, maxy, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(min, maxy, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, maxy, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, maxy, min).color(.83f, .81f, .5f, alpha).endVertex();
		
		// miny
		buffer.pos(min, min, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, min, min).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(max, min, max).color(.83f, .81f, .5f, alpha).endVertex();
		buffer.pos(min, min, max).color(.83f, .81f, .5f, alpha).endVertex();
		
		tessellator.draw();
		
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
	
	@Override
	public void render(AetherBatteryEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alphaIn) {

		super.render(te, x, y, z, partialTicks, destroyStage, alphaIn);
		
		final int aether = te.getHandler().getAether(null);
		final int maxAether = te.getHandler().getMaxAether(null);
		
		if (aether <= 0 || maxAether <= 0) {
			return;
		}
		
		renderBatteryAt(te.getWorld(), x, y, z, partialTicks, destroyStage, aether, maxAether, false);
	}
	
}
