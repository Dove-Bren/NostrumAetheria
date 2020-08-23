package com.smanzana.nostrumaetheria.api.blocks;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IAetherCapableBlock {
	
	public IAetherHandler getAetherHandler(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side);
	
}
