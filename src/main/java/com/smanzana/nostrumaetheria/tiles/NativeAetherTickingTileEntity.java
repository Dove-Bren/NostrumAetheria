package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class NativeAetherTickingTileEntity extends AetherTickingTileEntity {

	protected AetherHandlerComponent handler;
	
	public NativeAetherTickingTileEntity(BlockEntityType<? extends NativeAetherTickingTileEntity> type, BlockPos pos, BlockState state, int defaultAether, int defaultMaxAether) {
		super(type, pos, state, defaultAether, defaultMaxAether);
		
		this.handler = (AetherHandlerComponent) this.compWrapper.getHandlerIfPresent();
	}
	
	@Override
	public @Nullable IAetherHandler getHandler() {
		return handler; // importantly, lets subclasses swap out handler and then that be seen by other things
	}
	
}
