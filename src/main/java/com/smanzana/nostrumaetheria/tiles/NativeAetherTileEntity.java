package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;

import net.minecraft.tileentity.TileEntityType;

public class NativeAetherTileEntity extends AetherTileEntity {

	protected AetherHandlerComponent handler;
	
	public NativeAetherTileEntity(TileEntityType<? extends NativeAetherTileEntity> type, int defaultAether, int defaultMaxAether) {
		super(type, defaultAether, defaultMaxAether);
		
		this.handler = (AetherHandlerComponent) this.compWrapper.getHandlerIfPresent();
	}
	
	@Override
	public @Nullable IAetherHandler getHandler() {
		return handler; // importantly, lets subclasses swap out handler and then that be seen by other things
	}
}
