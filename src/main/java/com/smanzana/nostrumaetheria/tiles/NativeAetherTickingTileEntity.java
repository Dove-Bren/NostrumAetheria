package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;

import net.minecraft.tileentity.TileEntityType;

public class NativeAetherTickingTileEntity extends AetherTickingTileEntity {

	protected AetherHandlerComponent handler;
	
	public NativeAetherTickingTileEntity(TileEntityType<? extends NativeAetherTickingTileEntity> type, int defaultAether, int defaultMaxAether) {
		super(type, defaultAether, defaultMaxAether);
		
		this.handler = (AetherHandlerComponent) this.compWrapper.getHandlerIfPresent();
	}
	
	@Override
	public @Nullable IAetherHandler getHandler() {
		return handler; // importantly, lets subclasses swap out handler and then that be seen by other things
	}
	
}
