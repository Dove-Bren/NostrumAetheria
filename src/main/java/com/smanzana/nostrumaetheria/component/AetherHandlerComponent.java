package com.smanzana.nostrumaetheria.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics;
import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics.AetherIterateContext;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class AetherHandlerComponent implements IAetherHandlerComponent {
	
	private static final String NBT_AETHER = "aether";
	private static final String NBT_MAX_AETHER = "max_aether";
	private static final String NBT_SIDE_CONFIG = "side_config";
	private static final String NBT_INOUTBOUND_CONFIG = "inout_config";
	
	private int maxAether;
	private int aether;
	// Whether or not we automatically try to fill ourselves up from our connections
	protected boolean autoFill;
	// side configuration
	protected boolean sideConnections[];
	// Global disable switch on in-flowing aether
	protected boolean allowInboundAether;
	// "" but for outbound aether
	protected boolean allowOutboundAether;
	// Connections set via code instead of from parent (usually distant blocks, like relays)
	private Set<AetherFlowConnection> remoteConnections;
	private int maxAetherFill;
	
	protected final IAetherComponentListener listener;
	
	// Transient tick variables
	protected int ticksExisted;
	protected boolean receivedAetherThisTick;
	protected boolean gaveAetherThisTick;
	protected int aetherLastTick;
	
	public AetherHandlerComponent(IAetherComponentListener listener, int defaultAether, int defaultMaxAether) {
		maxAether = defaultMaxAether;
		aether = defaultAether;
		sideConnections = new boolean[EnumFacing.values().length + 1]; // +1 for NULL
		remoteConnections = new HashSet<>();
		fixAether();
		
		for (int i = 0; i < sideConnections.length; i++) {
			sideConnections[i] = true;
		}
		
		this.allowInboundAether = this.allowOutboundAether = true;
		
		if (listener == null) {
			throw new IllegalArgumentException("listener cannot be null");
		}
		this.listener = listener;
	}
	
	public AetherHandlerComponent(IAetherComponentListener listener) {
		this(listener, 0, 0);
	}
	
	public void setAutoFill(boolean fill, int maxPerTick) {
		autoFill = fill;
		maxAetherFill = maxPerTick;
	}
	
	public void setAutoFill(boolean fill) {
		this.setAutoFill(fill, Integer.MAX_VALUE);
	}
	
	public void setInboundEnabled(boolean enabled) {
		allowInboundAether = enabled;
		this.dirty();
	}
	
	protected boolean getInboundAllowed() {
		return allowInboundAether;
	}
	
	public void setOutboundEnabled(boolean enabled) {
		allowOutboundAether = enabled;
		this.dirty();
	}
	
	protected boolean getOutboundAllowed() {
		return allowOutboundAether;
	}
	
	public void configureInOut(boolean inputAllowed, boolean outputAllowed) {
		setInboundEnabled(inputAllowed);
		setOutboundEnabled(outputAllowed);
	}
	
	private void fixAether() {
		aether = Math.min(aether, maxAether);
	}
	
	private int getSideIndex(EnumFacing side) {
		return (side == null ? EnumFacing.values().length : side.ordinal());
	}
	
	protected void dirty() {
		listener.dirty();
	}
	
	/**
	 * Set whether the provided side is allowed to transfer aether.
	 * @param side The side. Note: NULL is allowed.
	 * @param enabled
	 */
	public void enableSide(EnumFacing side, boolean enabled, boolean dirty) {
		sideConnections[getSideIndex(side)] = enabled;
		if (dirty) {
			dirty();
		}
	}
	
	public void enableSide(EnumFacing side, boolean enabled) {
		enableSide(side, enabled, true);
	}
	
	/**
	 * Check whether the given side is enabled for aether transfer.
	 * Used on regular tick flow updates -- AKA when trying to PULL.
	 * By default, also used to regular whether things can push/pull to/from us.
	 * @param side
	 * @return
	 */
	public boolean getSideEnabled(EnumFacing side) {
		return sideConnections[getSideIndex(side)];
	}
	
	/**
	 * Adds a (usually distant) aether connection that may be used to draw aether from.
	 * Dir is the direction we will 'draw' from remotely. Null is acceptable.
	 * Note: Two-way links can be achieved by making links in both directions. Draw mechanics should be able
	 * to properly handle two-way links and loops.
	 * @param handler
	 * @param dir
	 */
	public void addAetherConnection(IAetherHandler handler, EnumFacing dir) {
		remoteConnections.add(new AetherFlowConnection(handler, dir));
	}
	
	public void removeAetherConnection(IAetherHandler handler, EnumFacing dir) {
		Iterator<AetherFlowConnection> it = remoteConnections.iterator();
		while (it.hasNext()) {
			AetherFlowConnection conn = it.next();
			if (conn.handler == handler && conn.face == dir) {
				it.remove();
			}
		}
	}
	
	public void clearConnections() {
		remoteConnections.clear();
	}
	
	/**
	 * Returns whether or not this side can accept incoming aether.
	 * @param side
	 * @return
	 */
	protected boolean canAcceptOnSide(EnumFacing side) {
		return allowInboundAether && sideConnections[getSideIndex(side)];
	}
	
	/**
	 * Returns whether this side can have aether drawn from it.
	 * @param side
	 * @return
	 */
	protected boolean canDrawOnSide(EnumFacing side) {
		return allowOutboundAether && sideConnections[getSideIndex(side)];
	}
	
	@Override
	public int getAether(EnumFacing side) {
		return aether;
	}
	
	@Override
	public int getMaxAether(EnumFacing side) {
		return maxAether;
	}
	
	@Override
	public void setMaxAether(int maxAether) {
		this.maxAether = maxAether;
	}

	public int addAether(EnumFacing side, int amount, boolean force) {
		if (force || canAcceptOnSide(side)) {
			int start = aether;
			aether += amount;
			if (aether > maxAether) {
				amount = aether - maxAether;
				fixAether();
			} else {
				amount = 0;
			}
			
			if (aether != start) {
				receivedAetherThisTick = true;
				dirty();
			}
		}
		
		return amount;
	}
	
	@Override
	public int addAether(EnumFacing side, int amount) {
		return this.addAether(side, amount, false);
	}
	
	@Override
	public boolean canAdd(EnumFacing side, int amount) {
		return (canAcceptOnSide(side) && maxAether - aether >= amount);
	}
	
	protected int drawAetherFromMyself(EnumFacing side, int amount, boolean internal) {
		if (amount != 0 && (internal || canDrawOnSide(side)) && aether != 0) {
			amount = Math.min(amount, aether);
			aether -= amount;
			dirty();
			gaveAetherThisTick = true;
		} else {
			amount = 0;
		}
		
		return amount;
	}
	
	protected List<AetherFlowConnection> getConnections() {
		List<AetherFlowConnection> connections = new ArrayList<>(EnumFacing.values().length + 1 + remoteConnections.size());
		connections.addAll(remoteConnections);
		listener.addConnections(connections);
		return connections;
	}
	
	@Override
	public int drawAether(EnumFacing side, int amount, AetherIterateContext context) {
		context.addConnections(getConnections());
		return this.drawAetherFromMyself(side, amount, false);
	}

	@Override
	public int drawAether(EnumFacing side, int amount) {
		final int start = amount;
		// Check if have enough ourselves before using the iterative approach
		amount -= drawAetherFromMyself(side, amount, true);
		if (amount <= 0) {
			return start;
		}
		
		amount -= AetherFlowMechanics.drawFromHandler(this, side, amount, false);
		return start - amount;
	}
	
	/**
	 * Attempts to recuperate any lost aether from configured connections.
	 * Called automatically if you set via {@link #setAutoFill(boolean)} and are calling the tick func. 
	 * @param maxDiff
	 */
	public void fillAether(int maxDiff) {
		final int missing = maxAether - aether;
		if (missing > 0) {
			int amt = Math.min(maxDiff, missing);
			for (AetherFlowConnection connection : getConnections()) {
				int drawn = AetherFlowMechanics.drawFromHandler(this, connection.handler, connection.face, amt, false);
				addAether(null, drawn);
				amt -= drawn;
				if (amt <= 0) {
					break;
				}
			}
			
		}
	}
	
	/**
	 * Attempts to push out any stored aether into nearby connections.
	 * @param maxDiff
	 */
	public void pushAether(int maxDiff) {
		if (aether > 0) {
			int start = Math.min(aether, maxDiff); 
			int amt = start;
			for (AetherFlowConnection connection : getConnections()) {
				amt = connection.handler.addAether(connection.face, amt);
				if (amt <= 0) {
					break;
				}
			}
			this.drawAetherFromMyself(null, (start - amt), true);
		}
	}
	
	/**
	 * Scans through remote connections and attempts to clean up ones we know are bad.
	 * Some connections have no way of signaling they're invalid. Those ones should be certain to remove
	 * themselves from connection lists if they are ever added.
	 * Tile entities that are ticked should call this in a tick func. It's not called by default.
	 */
	protected void cleanConnections() {
		Iterator<AetherFlowConnection> it = remoteConnections.iterator();
		while (it.hasNext()) {
			AetherFlowConnection conn = it.next();
			if (conn.handler instanceof TileEntity) {
				if (((TileEntity) conn.handler).isInvalid()) {
					it.remove();
					continue;
				}
			}
		}
	}
	
	@Override
	public int getAetherTotal(EnumFacing side, AetherIterateContext context) {
		context.addConnections(getConnections());
		return getAether(side);
	}
	
	@Override
	public boolean canDraw(EnumFacing side, int amount) {
		if (canDrawOnSide(side)) {
			return aether >= amount;
		}
		return false;
	}
	
	public void tick() {
		ticksExisted++;
		
		this.cleanConnections();
		if (autoFill) {
			this.fillAether(maxAetherFill);
		}
		
		int aetherDiff = this.getAether(null) - aetherLastTick;
		if (aetherDiff != 0 || receivedAetherThisTick || gaveAetherThisTick) {
			listener.onAetherFlowTick(aetherDiff, receivedAetherThisTick, gaveAetherThisTick);
		}
		
		aetherLastTick = this.getAether(null);
		receivedAetherThisTick = false;
		gaveAetherThisTick = false;
	}
	
	private static final void configFromByte(boolean[] config, byte b) {
		for (EnumFacing facing : EnumFacing.values()) {
			config[facing.ordinal()] = ((b & 1) == 1);
			b >>= 1;
		}
		config[EnumFacing.values().length] = ((b & 1) == 1);
	}
	
	private static final byte configToByte(boolean[] config) {
		byte b = 0;
		b |= (config[EnumFacing.values().length] ? 1 : 0);
		EnumFacing values[] = EnumFacing.values();
		for (int i = values.length - 1; i >= 0; i--) { // backwards to counter how we write
			b <<= 1;
			b |= (config[i] ? 1 : 0);
		}
		return b;
	}
	
	private static final void inoutConfigFromByte(AetherHandlerComponent comp, byte b) {
		// ugh I'll just set them from here
		comp.configureInOut(((b >> 1) & 1) == 1, ((b >> 0) & 1) == 1);
	}
	
	private static final byte inoutConfigToByte(boolean input, boolean output) {
		return (byte) ((input ? 1 : 0) << 1
				| (output ? 1 : 0) << 0);
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(NBT_AETHER, aether);
		compound.setInteger(NBT_MAX_AETHER, maxAether);
		compound.setByte(NBT_SIDE_CONFIG, configToByte(sideConnections));
		compound.setByte(NBT_INOUTBOUND_CONFIG, inoutConfigToByte(allowInboundAether, allowOutboundAether));
		
		return compound;
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		this.aether = compound.getInteger(NBT_AETHER);
		this.maxAether = compound.getInteger(NBT_MAX_AETHER);
		configFromByte(sideConnections, compound.getByte(NBT_SIDE_CONFIG));
		inoutConfigFromByte(this, compound.getByte(NBT_INOUTBOUND_CONFIG));
		fixAether();
	}
	
	public void setAether(int aether) {
		this.aether = aether;
		//this.aetherLastTick = aether; // let this generate diffs so that the client is interesting to look at
	}
}
