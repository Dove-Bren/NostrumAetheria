package com.smanzana.nostrumaetheria.api.blocks;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IAetherCapableBlock {
	
	public IAetherHandler getAetherHandler(IBlockReader world, BlockState state, BlockPos pos, Direction side);
	
}
