package com.smanzana.nostrumaetheria.client.render;

import com.smanzana.nostrumaetheria.blocks.tiles.AetherBatteryEntity;
import com.smanzana.nostrumaetheria.entities.EntityAetherBatteryMinecart;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderAetherBatteryMinecart extends RenderMinecart<EntityAetherBatteryMinecart> {

	public RenderAetherBatteryMinecart(RenderManager manager) {
		super(manager);
	}
	
	@Override
	protected void renderCartContents(EntityAetherBatteryMinecart cart, float partialTicks, IBlockState state) {
		super.renderCartContents(cart, partialTicks, state);
		if (cart.getAether() > 0) {
			TileEntitySpecialRenderer<?> render = TileEntityRendererDispatcher.instance.getRenderer(AetherBatteryEntity.class); 
			((AetherBatteryRenderer) render).renderBatteryAt(cart.world, 0, 0, -1, partialTicks, 0, cart.getAether(), cart.getMaxAether(), true);
		}
	}
}
