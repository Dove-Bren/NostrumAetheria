package com.smanzana.nostrumaetheria.api.aether;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.blocks.IAetherCapableBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Indicates that this class has the ability to store aether
 * @author Skyler
 *
 */
public interface IAetherHandler {

	/**
	 * Return the current amount of aether stored
	 * @param side
	 * @return
	 */
	public int getAether(Direction side);
	
	/**
	 * Return the maximum amount of aether than can be stored
	 * @param side
	 * @return
	 */
	public int getMaxAether(Direction side);
	
	/**
	 * Add the specified amount of aether. If not all of it fits, return the leftover.
	 * @param side
	 * @param amount
	 * @return
	 */
	public int addAether(Direction side, int amount);
	
	/**
	 * Check whether this handler could accept all of amount on this side.
	 * @param side
	 * @param amount
	 * @return
	 */
	public boolean canAdd(Direction side, int amount);
	
	/**
	 * Attempt to draw aether. If not all is present, draw as much as possible.
	 * @param side
	 * @param amount
	 * @return The amount actually drawn
	 */
	public int drawAether(Direction side, int amount);
	
	/**
	 * Check whether all of the aether requested is available to be drawn from this side
	 * @param side
	 * @param amount
	 * @return
	 */
	public boolean canDraw(Direction side, int amount);
	

	
	/**
	 * Look up a handler at the given position in the world.
	 * Naturally, this only includes blocks and tile entities.
	 * @param world
	 * @param pos
	 * @param side
	 * @return
	 */
	public static @Nullable IAetherHandler GetHandlerAt(Level world, BlockPos pos, @Nullable Direction side) {
		// First check for a TileEntity
		BlockEntity te = world.getBlockEntity(pos);
		if (te != null && te instanceof IAetherHandler) {
			return (IAetherHandler) te;
		}
		if (te != null && te instanceof IAetherHandlerProvider) {
			return ((IAetherHandlerProvider) te).getHandler();
		}
		
		// See if block boasts being able to get us a handler
		BlockState attachedState = world.getBlockState(pos);
		Block attachedBlock = attachedState.getBlock();
		if (attachedBlock instanceof IAetherCapableBlock) {
			return ((IAetherCapableBlock) attachedBlock).getAetherHandler(world, attachedState, pos, side);
		}
		
		return null;
	}
	
}
