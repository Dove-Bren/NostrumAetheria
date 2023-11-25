package com.smanzana.nostrumaetheria.api.component;

import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics.AetherIterateContext;
import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IWorldAetherHandler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAetherHandlerComponent extends IWorldAetherHandler, IAetherFlowHandler {
	
	public void setAutoFill(boolean fill, int maxPerTick);
	
	public void setAutoFill(boolean fill);
	
	public void setInboundEnabled(boolean enabled);
	
	public void setOutboundEnabled(boolean enabled);
	
	public void configureInOut(boolean inputAllowed, boolean outputAllowed);
	
	/**
	 * Set whether the provided side is allowed to transfer aether.
	 * @param side The side. Note: NULL is allowed.
	 * @param enabled
	 */
	public void enableSide(Direction side, boolean enabled, boolean dirty);
	
	public void enableSide(Direction side, boolean enabled);
	
	/**
	 * Check whether the given side is enabled for aether transfer.
	 * Used on regular tick flow updates -- AKA when trying to PULL.
	 * By default, also used to regular whether things can push/pull to/from us.
	 * @param side
	 * @return
	 */
	public boolean getSideEnabled(Direction side);
	
	/**
	 * Adds a (usually distant) aether connection that may be used to draw aether from.
	 * Dir is the direction we will 'draw' from remotely. Null is acceptable.
	 * Note: Two-way links can be achieved by making links in both directions. Draw mechanics should be able
	 * to properly handle two-way links and loops.
	 * @param handler
	 * @param dir
	 */
	public void addAetherConnection(IAetherHandler handler, Direction dir);
	
	public void removeAetherConnection(IAetherHandler handler, Direction dir);
	
	public void clearConnections();
	
	@Override
	public int getAether(Direction side);
	
	@Override
	public int getMaxAether(Direction side);
	
	public void setMaxAether(int maxAether);

	public int addAether(Direction side, int amount, boolean force);
	
	@Override
	public int addAether(Direction side, int amount);
	
	@Override
	public boolean canAdd(Direction side, int amount);
	
	@Override
	public int drawAether(Direction side, int amount, AetherIterateContext context);

	@Override
	public int drawAether(Direction side, int amount);
	
	/**
	 * Attempts to recuperate any lost aether from configured connections.
	 * Called automatically if you set via {@link #setAutoFill(boolean)} and are calling the tick func. 
	 * @param maxDiff
	 */
	public void fillAether(int maxDiff);
	
	/**
	 * Attempts to push out any stored aether into nearby connections.
	 * @param maxDiff
	 */
	public void pushAether(int maxDiff);
	
	@Override
	public int getAetherTotal(Direction side, AetherIterateContext context);
	
	@Override
	public boolean canDraw(Direction side, int amount);
	
	public void tick();
	
	public CompoundNBT writeToNBT(CompoundNBT compound);
	
	public void readFromNBT(CompoundNBT compound);
	
	public void setAether(int aether);

	public void setPosition(World world, BlockPos pos);
}
