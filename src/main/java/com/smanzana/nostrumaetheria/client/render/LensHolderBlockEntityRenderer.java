package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.smanzana.nostrumaetheria.tiles.LensHolderBlockEntity;
import com.smanzana.nostrummagica.client.render.tile.BlockEntityRendererBase;
import com.smanzana.nostrummagica.util.RenderFuncs;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class LensHolderBlockEntityRenderer extends BlockEntityRendererBase<LensHolderBlockEntity> {

	public LensHolderBlockEntityRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	@Override
	public void render(LensHolderBlockEntity te, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

		ItemStack item = te.getItem();
		if (item.isEmpty())
			return;
		
		final double rotPeriod = 40 * 20;
		float rot = (float)((double)(te.getLevel().getGameTime() % rotPeriod) / rotPeriod) * 360f;
		float scale = .5f;
//		float yoffset = (float) (.1f * (-.5f + Math.sin(((double) System.currentTimeMillis()) / 1000.0))); // Copied from Altar
		
		matrixStackIn.pushPose();
		matrixStackIn.translate(.5f, .35f, .5f);
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rot));
		
		matrixStackIn.scale(scale, scale, scale);
		
		RenderFuncs.RenderWorldItem(item, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		matrixStackIn.popPose();
	}
	
}
