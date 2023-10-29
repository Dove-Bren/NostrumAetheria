package com.smanzana.nostrumaetheria.client.render;

import com.smanzana.nostrumaetheria.entity.EntityAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.tiles.AetherBatteryEntity;

import net.minecraft.block.state.BlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderAetherBatteryMinecart extends RenderMinecart<EntityAetherBatteryMinecart> {

	public RenderAetherBatteryMinecart(RenderManager manager) {
		super(manager);
	}
	
	@Override
	protected void renderCartContents(EntityAetherBatteryMinecart cart, float partialTicks, BlockState state) {
		super.renderCartContents(cart, partialTicks, state);
		if (cart.getAether() > 0) {
			TileEntitySpecialRenderer<?> render = TileEntityRendererDispatcher.instance.getRenderer(AetherBatteryEntity.class); 
			((AetherBatteryRenderer) render).renderBatteryAt(cart.world, 0, 0, -1, partialTicks, 0, cart.getAether(), cart.getMaxAether(), true);
		}
	}
}
