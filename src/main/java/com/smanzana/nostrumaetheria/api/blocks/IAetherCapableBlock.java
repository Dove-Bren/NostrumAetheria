package com.smanzana.nostrumaetheria.api.blocks;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface IAetherCapableBlock {
	
	public IAetherHandler getAetherHandler(BlockGetter world, BlockState state, BlockPos pos, Direction side);
	
}
