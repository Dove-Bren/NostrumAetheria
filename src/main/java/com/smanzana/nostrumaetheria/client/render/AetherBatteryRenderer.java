package com.smanzana.nostrumaetheria.client.render;

import org.lwjgl.opengl.GL11;

import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock.AetherBatteryEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class AetherBatteryRenderer extends TileEntityAetherDebugRenderer<AetherBatteryEntity> {

	public static void init() {
		ClientRegistry.bindTileEntitySpecialRenderer(AetherBatteryEntity.class,
				new AetherBatteryRenderer());
	}
	
	public AetherBatteryRenderer() {
		super();
	}
	
	@Override
	public void renderTileEntityAt(AetherBatteryEntity te, double x, double y, double z, float partialTicks, int destroyStage) {

		super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
		
		final int aether = te.getHandler().getAether(null);
		final int maxAether = te.getHandler().getMaxAether(null);
		
		if (aether <= 0 || maxAether <= 0) {
			return;
		}
		
		final double prog = ((double) aether / (double) maxAether);
		final double min = 0.03;
		final double max = (1.0 - .03);
		final double maxy = min + (prog * (max - min));
		final double glowPeriod = 20 * 5;
		final float glow = (float) Math.sin(Math.PI * 2 * ((double) te.getWorld().getTotalWorldTime() % glowPeriod) / glowPeriod);
		final float alpha = .4f + (.2f * glow);
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(x, y, z);
		GlStateManager.disableLighting();
		//GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
				SourceFactor.ONE, DestFactor.ZERO);
		//Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.disableColorMaterial();
		GlStateManager.disableTexture2D();
		
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
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
		
	}
	
}
