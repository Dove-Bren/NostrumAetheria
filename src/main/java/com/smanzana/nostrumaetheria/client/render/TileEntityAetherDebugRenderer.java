package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrummagica.client.render.tile.BlockEntityRendererBase;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class TileEntityAetherDebugRenderer<T extends AetherTileEntity> extends BlockEntityRendererBase<T> {

	public static <T extends AetherTileEntity> void registerFor(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<T> type) {
		event.registerBlockEntityRenderer(type,	TileEntityAetherDebugRenderer<T>::new);
	}
	
	public TileEntityAetherDebugRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	@Override
	public void render(AetherTileEntity te, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

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
