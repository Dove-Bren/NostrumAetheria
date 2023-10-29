package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;

public class NativeAetherTickingTileEntity extends AetherTickingTileEntity {

	protected AetherHandlerComponent handler;
	
	public NativeAetherTickingTileEntity(int defaultAether, int defaultMaxAether) {
		super(defaultAether, defaultMaxAether);
		
		this.handler = (AetherHandlerComponent) this.compWrapper.getHandlerIfPresent();
	}
	
	@Override
	public @Nullable IAetherHandler getHandler() {
		return handler; // importantly, lets subclasses swap out handler and then that be seen by other things
	}
	
}
