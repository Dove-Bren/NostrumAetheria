package com.smanzana.nostrumaetheria.api.component;

import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics.AetherIterateContext;
import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public interface IAetherHandlerComponent extends IAetherHandler, IAetherFlowHandler {
	
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
	public void enableSide(EnumFacing side, boolean enabled, boolean dirty);
	
	public void enableSide(EnumFacing side, boolean enabled);
	
	/**
	 * Check whether the given side is enabled for aether transfer.
	 * Used on regular tick flow updates -- AKA when trying to PULL.
	 * By default, also used to regular whether things can push/pull to/from us.
	 * @param side
	 * @return
	 */
	public boolean getSideEnabled(EnumFacing side);
	
	/**
	 * Adds a (usually distant) aether connection that may be used to draw aether from.
	 * Dir is the direction we will 'draw' from remotely. Null is acceptable.
	 * Note: Two-way links can be achieved by making links in both directions. Draw mechanics should be able
	 * to properly handle two-way links and loops.
	 * @param handler
	 * @param dir
	 */
	public void addAetherConnection(IAetherHandler handler, EnumFacing dir);
	
	public void removeAetherConnection(IAetherHandler handler, EnumFacing dir);
	
	public void clearConnections();
	
	@Override
	public int getAether(EnumFacing side);
	
	@Override
	public int getMaxAether(EnumFacing side);
	
	public void setMaxAether(int maxAether);

	public int addAether(EnumFacing side, int amount, boolean force);
	
	@Override
	public int addAether(EnumFacing side, int amount);
	
	@Override
	public boolean canAdd(EnumFacing side, int amount);
	
	@Override
	public int drawAether(EnumFacing side, int amount, AetherIterateContext context);

	@Override
	public int drawAether(EnumFacing side, int amount);
	
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
	public int getAetherTotal(EnumFacing side, AetherIterateContext context);
	
	@Override
	public boolean canDraw(EnumFacing side, int amount);
	
	public void tick();
	
	public NBTTagCompound writeToNBT(NBTTagCompound compound);
	
	public void readFromNBT(NBTTagCompound compound);
	
	public void setAether(int aether);
}
