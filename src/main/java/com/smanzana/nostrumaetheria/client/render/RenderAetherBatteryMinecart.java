package com.smanzana.nostrumaetheria.client.render;

import com.smanzana.nostrumaetheria.entity.EntityAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.tiles.AetherBatteryEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RenderAetherBatteryMinecart extends MinecartRenderer<EntityAetherBatteryMinecart> {

	public RenderAetherBatteryMinecart(EntityRendererManager manager) {
		super(manager);
	}
	
	@Override
	protected void renderCartContents(EntityAetherBatteryMinecart cart, float partialTicks, BlockState state) {
		super.renderCartContents(cart, partialTicks, state);
		if (cart.getAether() > 0) {
			TileEntityRenderer<?> render = TileEntityRendererDispatcher.instance.getRenderer(AetherBatteryEntity.class); 
			((AetherBatteryRenderer) render).renderBatteryAt(cart.world, 0, 0, -1, partialTicks, 0, cart.getAether(), cart.getMaxAether(), true);
		}
	}
}
