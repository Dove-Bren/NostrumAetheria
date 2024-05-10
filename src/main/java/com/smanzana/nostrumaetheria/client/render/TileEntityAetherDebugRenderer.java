package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class TileEntityAetherDebugRenderer<T extends AetherTileEntity> extends TileEntityRenderer<T> {

	public static <T extends AetherTileEntity> void registerFor(TileEntityType<T> type) {
		ClientRegistry.bindTileEntityRenderer(type,	(inst) -> new TileEntityAetherDebugRenderer<T>(inst));
	}
	
	public TileEntityAetherDebugRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	@Override
	public void render(AetherTileEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

//		final int aether = te.getHandler().getAether(null);
//		final int maxAether = te.getHandler().getMaxAether(null);
//		final String str = aether + " / " + maxAether;
//		final FontRenderer fonter = Minecraft.getInstance().fontRendererObj;
//		
//		matrixStackIn.push();
//		GlStateManager.translate(x + .5, y + 1.5, z + .5);
//		GlStateManager.scale(.05, .05, .05);
//		GlStateManager.rotate(90f + (float) (360.0 * (Math.atan2(z + .5, x + .5) / (2 * Math.PI))), 0, -1, 0);
//		GlStateManager.rotate(180, 1, 0, 0);
//		// Make billboard
//		GlStateManager.color(1f, 1f, 1f, .4f);
//		GlStateManager.disableCull();
//		GlStateManager.enableColorMaterial();
//		GlStateManager.enableTexture2D();
//		GlStateManager.disableLighting();
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
//		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
//		
//		
////		GlStateManager.disableColorMaterial();
////		GlStateManager.disableTexture2D();
////		GlStateManager.disableLighting();
////		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
////		GlStateManager.enableBlend();
////		GlStateManager.enableAlpha();
//		
//		fonter.drawString(str, -(fonter.getStringWidth(str) / 2), 0, 0xFF000000);
//		GlStateManager.enableCull();
//		matrixStackIn.pop();
		
	}
	
}
