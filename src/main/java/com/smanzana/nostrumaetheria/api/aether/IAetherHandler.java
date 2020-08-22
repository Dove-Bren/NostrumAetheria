package com.smanzana.nostrumaetheria.api.aether;

import net.minecraft.util.EnumFacing;

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
	public int getAether(EnumFacing side);
	
	/**
	 * Return the maximum amount of aether than can be stored
	 * @param side
	 * @return
	 */
	public int getMaxAether(EnumFacing side);
	
	/**
	 * Add the specified amount of aether. If not all of it fits, return the leftover.
	 * @param side
	 * @param amount
	 * @return
	 */
	public int addAether(EnumFacing side, int amount);
	
	/**
	 * Check whether this handler could accept all of amount on this side.
	 * @param side
	 * @param amount
	 * @return
	 */
	public boolean canAdd(EnumFacing side, int amount);
	
	/**
	 * Attempt to draw aether. If not all is present, draw as much as possible.
	 * @param side
	 * @param amount
	 * @return The amount actually drawn
	 */
	public int drawAether(EnumFacing side, int amount);
	
	/**
	 * Check whether all of the aether requested is available to be drawn from this side
	 * @param side
	 * @param amount
	 * @return
	 */
	public boolean canDraw(EnumFacing side, int amount);
	
}
