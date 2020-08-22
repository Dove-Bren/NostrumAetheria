package com.smanzana.nostrumaetheria.client.render;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class TileEntityAetherDebugRenderer<T extends AetherTileEntity> extends TileEntitySpecialRenderer<T> {

	public static <T extends AetherTileEntity> void registerFor(Class<T> clazz) {
		ClientRegistry.bindTileEntitySpecialRenderer(clazz,
				new TileEntityAetherDebugRenderer<T>());
	}
	
	public TileEntityAetherDebugRenderer() {
		super();
	}
	
	@Override
	public void renderTileEntityAt(AetherTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {

		final int aether = te.getAether(null);
		final int maxAether = te.getMaxAether(null);
		final String str = aether + " / " + maxAether;
		final FontRenderer fonter = Minecraft.getMinecraft().fontRendererObj;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + .5, y + 1.5, z + .5);
		GlStateManager.scale(.05, .05, .05);
		GlStateManager.rotate(90f + (float) (360.0 * (Math.atan2(z, x) / (2 * Math.PI))), 0, -1, 0);
		GlStateManager.translate(0, 0, 0);
		GlStateManager.rotate(180, 1, 0, 0);
		// Make billboard
		GlStateManager.color(1f, 1f, 1f, .4f);
		GlStateManager.disableCull();
		fonter.drawString(str, -(fonter.getStringWidth(str) / 2), 0, 0xFF000000);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		
	}
	
}
