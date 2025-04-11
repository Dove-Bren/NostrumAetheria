package com.smanzana.nostrumaetheria.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics;
import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics.AetherIterateContext;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IWorldAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.stats.AetherTickIOEntry;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AetherHandlerComponent implements IAetherHandlerComponent {
	
	private static final String NBT_AETHER = "aether";
	private static final String NBT_MAX_AETHER = "max_aether";
	private static final String NBT_SIDE_CONFIG = "side_config";
	private static final String NBT_INOUTBOUND_CONFIG = "inout_config";
	private static final String NBT_DIM = "dimension";
	private static final String NBT_POS = "position";
	
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
	protected @Nullable ResourceKey<Level> dimension;
	protected @Nullable BlockPos pos;
	// Connections set via code instead of from parent (usually distant blocks, like relays)
	private Set<AetherFlowConnection> remoteConnections;
	private int maxAetherFill;
	// Whether this should add its connections to a draw flow
	protected boolean propagateFlow;
	
	protected final IAetherComponentListener listener;
	
	// Transient tick variables
	protected int ticksExisted;
	protected boolean receivedAetherThisTick; // internal
	protected boolean gaveAetherThisTick; // internal
	protected final Map<BlockPos, AetherTickIOEntry> aetherTickIOData; // Note: values must be released
	protected boolean aetherActiveTick; // useful for checking if active after super.tick()
	protected int aetherLastTick;
	
	public AetherHandlerComponent(@Nullable ResourceKey<Level> dimension, @Nullable BlockPos pos, IAetherComponentListener listener, int defaultAether, int defaultMaxAether) {
		maxAether = defaultMaxAether;
		aether = defaultAether;
		this.dimension = dimension;
		this.pos = pos;
		sideConnections = new boolean[Direction.values().length + 1]; // +1 for NULL
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
		this.aetherTickIOData = new HashMap<>();
	}
	
	public AetherHandlerComponent(IAetherComponentListener listener) {
		this(null, null, listener, 0, 0);
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
	
	public void setShouldPropagate(boolean propagate) {
		this.propagateFlow = propagate;
	}
	
	private void fixAether() {
		aether = Math.min(aether, maxAether);
	}
	
	private int getSideIndex(Direction side) {
		return (side == null ? Direction.values().length : side.ordinal());
	}
	
	protected void dirty() {
		listener.dirty();
	}
	
	/**
	 * Set whether the provided side is allowed to transfer aether.
	 * @param side The side. Note: NULL is allowed.
	 * @param enabled
	 */
	public void enableSide(Direction side, boolean enabled, boolean dirty) {
		sideConnections[getSideIndex(side)] = enabled;
		if (dirty) {
			dirty();
		}
	}
	
	public void enableSide(Direction side, boolean enabled) {
		enableSide(side, enabled, true);
	}
	
	/**
	 * Check whether the given side is enabled for aether transfer.
	 * Used on regular tick flow updates -- AKA when trying to PULL.
	 * By default, also used to regular whether things can push/pull to/from us.
	 * @param side
	 * @return
	 */
	public boolean getSideEnabled(Direction side) {
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
	public void addAetherConnection(IAetherHandler handler, Direction dir) {
		remoteConnections.add(new AetherFlowConnection(handler, dir));
	}
	
	public void removeAetherConnection(IAetherHandler handler, Direction dir) {
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
	protected boolean canAcceptOnSide(Direction side) {
		return allowInboundAether && sideConnections[getSideIndex(side)];
	}
	
	/**
	 * Returns whether this side can have aether drawn from it.
	 * @param side
	 * @return
	 */
	protected boolean canDrawOnSide(Direction side) {
		return allowOutboundAether && sideConnections[getSideIndex(side)];
	}
	
	@Override
	public int getAether(Direction side) {
		return aether;
	}
	
	@Override
	public int getMaxAether(Direction side) {
		return maxAether;
	}
	
	@Override
	public void setMaxAether(int maxAether) {
		this.maxAether = maxAether;
	}

	public int addAether(Direction side, int amount, boolean force) {
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
	public int addAether(Direction side, int amount) {
		return this.addAether(side, amount, false);
	}
	
	@Override
	public boolean canAdd(Direction side, int amount) {
		return (canAcceptOnSide(side) && maxAether - aether >= amount);
	}
	
	protected int drawAetherFromMyself(Direction side, int amount, boolean internal) {
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
		List<AetherFlowConnection> connections = new ArrayList<>(Direction.values().length + 1 + remoteConnections.size());
		connections.addAll(remoteConnections);
		listener.addConnections(connections);
		return connections;
	}
	
	@Override
	public void addFlowPropagationConnections(AetherIterateContext context) {
		if (this.propagateFlow) {
			context.addConnections(getConnections());
		}
	}
	
	@Override
	public int drawAether(Direction side, int amount, AetherIterateContext context) {
		return this.drawAetherFromMyself(side, amount, false);
	}

	@Override
	public int drawAether(Direction side, int amount) {
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
				
				if (drawn != 0) {
					addAether(null, drawn, true);
					amt -= drawn;
					
					if (connection.handler instanceof IWorldAetherHandler) {
						final BlockPos otherPos = ((IWorldAetherHandler) connection.handler).getPosition();
						if (otherPos != null) {
							addTickIO(otherPos, drawn, 0);
						}
					}
					
					if (amt <= 0) {
						break;
					}
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
				final int connStart = amt;
				amt = connection.handler.addAether(connection.face, amt);
				
				if (amt != connStart) {
					if (connection.handler instanceof IWorldAetherHandler) {
						final BlockPos otherPos = ((IWorldAetherHandler) connection.handler).getPosition();
						if (otherPos != null) {
							addTickIO(otherPos, 0, connStart - amt);
						}
					}
				}
				
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
			if (conn.handler instanceof BlockEntity) {
				if (((BlockEntity) conn.handler).isRemoved()) {
					it.remove();
					continue;
				}
			}
		}
	}
	
	@Override
	public int getAetherTotal(Direction side, AetherIterateContext context) {
		context.addConnections(getConnections());
		return getAether(side);
	}
	
	@Override
	public boolean canDraw(Direction side, int amount) {
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
			aetherActiveTick = true;
			listener.onAetherFlowTick(aetherDiff, receivedAetherThisTick, gaveAetherThisTick);
		} else {
			aetherActiveTick = false;
		}
		
		aetherLastTick = this.getAether(null);
		receivedAetherThisTick = false;
		gaveAetherThisTick = false;
		clearTickIOMap();
	}
	
	/**
	 * Most accurate right after a tick (or super.tick()) call.
	 * @return
	 */
	public boolean isAetherActive() {
		return aetherActiveTick;
	}
	
	private static final void configFromByte(boolean[] config, byte b) {
		for (Direction facing : Direction.values()) {
			config[facing.ordinal()] = ((b & 1) == 1);
			b >>= 1;
		}
		config[Direction.values().length] = ((b & 1) == 1);
	}
	
	private static final byte configToByte(boolean[] config) {
		byte b = 0;
		b |= (config[Direction.values().length] ? 1 : 0);
		Direction values[] = Direction.values();
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
	
	public CompoundTag writeToNBT(CompoundTag compound) {
		compound.putInt(NBT_AETHER, aether);
		compound.putInt(NBT_MAX_AETHER, maxAether);
		compound.putByte(NBT_SIDE_CONFIG, configToByte(sideConnections));
		compound.putByte(NBT_INOUTBOUND_CONFIG, inoutConfigToByte(allowInboundAether, allowOutboundAether));
		if (dimension != null && pos != null) {
			compound.putString(NBT_DIM, dimension.location().toString());
			compound.put(NBT_POS, NbtUtils.writeBlockPos(pos));
		}
		
		return compound;
	}
	
	public void readFromNBT(CompoundTag compound) {
		this.aether = compound.getInt(NBT_AETHER);
		this.maxAether = compound.getInt(NBT_MAX_AETHER);
		configFromByte(sideConnections, compound.getByte(NBT_SIDE_CONFIG));
		inoutConfigFromByte(this, compound.getByte(NBT_INOUTBOUND_CONFIG));
		if (compound.contains(NBT_DIM) && compound.contains(NBT_POS)) {
			this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString(NBT_DIM)));
		}
		
		fixAether();
	}
	
	public void setAether(int aether) {
		this.aether = aether;
		//this.aetherLastTick = aether; // let this generate diffs so that the client is interesting to look at
	}

	@Override
	public ResourceKey<Level> getDimension() {
		return this.dimension;
	}

	@Override
	public BlockPos getPosition() {
		return this.pos;
	}
	
	protected void clearTickIOMap() {
		for (AetherTickIOEntry entry : aetherTickIOData.values()) {
			entry.release();
		}
		
		aetherTickIOData.clear();
	}
	
	public void addTickIO(BlockPos pos, int input, int output) {
		if (aetherTickIOData.containsKey(pos)) {
			AetherTickIOEntry entry = aetherTickIOData.get(pos);
			input += entry.getInput();
			output += entry.getOutput();
			entry.release();
		}
		
		aetherTickIOData.put(pos, AetherTickIOEntry.reserve(input, output));
	}
	
	/**
	 * Warning: expires at the end of the tick, so don't save the entry
	 * @param pos
	 * @return
	 */
	public AetherTickIOEntry getIOStatsFor(BlockPos pos) {
		return aetherTickIOData.get(pos);
	}
	
	@Override
	public void setPosition(Level world, BlockPos pos) {
		this.dimension = world == null ? null : world.dimension();
		this.pos = pos;
		//this.dirty();
	}
}
