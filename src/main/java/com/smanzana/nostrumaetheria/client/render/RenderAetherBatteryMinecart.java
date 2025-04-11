package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.smanzana.nostrumaetheria.entity.EntityAetherBatteryMinecart;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.world.level.block.state.BlockState;

public class RenderAetherBatteryMinecart extends MinecartRenderer<EntityAetherBatteryMinecart> {

	public RenderAetherBatteryMinecart(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, ModelLayers.FURNACE_MINECART);
	}
	
	@Override
	protected void renderMinecartContents(EntityAetherBatteryMinecart cart, float partialTicks, BlockState stateIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		int unused; // this doesn't work
		if (cart.getAether() > 0) {
			final VertexConsumer buffer = bufferIn.getBuffer(AetheriaRenderTypes.AETHER_FLAT);
			final double time = (double) cart.level.getGameTime() + partialTicks;
			
			//(cart.world, 0, 0, -1, partialTicks, 0, cart.getAether(), cart.getMaxAether(), true);
			matrixStackIn.pushPose();
			matrixStackIn.translate(0, 0, 0);
			AetherBatteryRenderer.renderBatteryLiquid(matrixStackIn, buffer, packedLightIn, time, cart.getAether(), cart.getMaxAether(), true);
			matrixStackIn.popPose();
		}
		super.renderMinecartContents(cart, partialTicks, stateIn, matrixStackIn, bufferIn, packedLightIn);
	}
}
