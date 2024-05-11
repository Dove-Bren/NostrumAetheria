package com.smanzana.nostrumaetheria.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.smanzana.nostrumaetheria.entity.EntityAetherBatteryMinecart;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;

public class RenderAetherBatteryMinecart extends MinecartRenderer<EntityAetherBatteryMinecart> {

	public RenderAetherBatteryMinecart(EntityRendererManager manager) {
		super(manager);
	}
	
	@Override
	protected void renderBlockState(EntityAetherBatteryMinecart cart, float partialTicks, BlockState stateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.renderBlockState(cart, partialTicks, stateIn, matrixStackIn, bufferIn, packedLightIn);
		if (cart.getAether() > 0) {
			final IVertexBuilder buffer = bufferIn.getBuffer(AetheriaRenderTypes.AETHER_FLAT);
			final double time = (double) cart.world.getGameTime() + partialTicks;
			
			//(cart.world, 0, 0, -1, partialTicks, 0, cart.getAether(), cart.getMaxAether(), true);
			matrixStackIn.push();
			matrixStackIn.translate(0, 0, 0);
			AetherBatteryRenderer.renderBatteryLiquid(matrixStackIn, buffer, packedLightIn, time, cart.getAether(), cart.getMaxAether(), true);
			matrixStackIn.pop();
		}
	}
}
