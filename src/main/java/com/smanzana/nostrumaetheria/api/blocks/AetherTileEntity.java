package com.smanzana.nostrumaetheria.api.blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics;
import com.smanzana.nostrumaetheria.api.aether.AetherFlowMechanics.AetherIterateContext;
import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AetherTileEntity extends TileEntity implements IAetherHandler, IAetherFlowHandler {

	private static final String NBT_AETHER = "aether";
	private static final String NBT_MAX_AETHER = "max_aether";
	private static final String NBT_SIDE_CONFIG = "side_config";
	
	private int maxAether;
	private int aether;
	
	private boolean sideConnections[];
	
	private Set<AetherFlowConnection> remoteConnections;
	
	public AetherTileEntity(int defaultAether, int defaultMaxAether) {
		maxAether = defaultMaxAether;
		aether = defaultAether;
		sideConnections = new boolean[EnumFacing.values().length + 1]; // +1 for NULL
		remoteConnections = new HashSet<>();
		fixAether();
		
		for (int i = 0; i < sideConnections.length; i++) {
			sideConnections[i] = true;
		}
	}
	
	public AetherTileEntity() {
		this(0, 0);
	}
	
	private void fixAether() {
		aether = Math.min(aether, maxAether);
	}
	
	private int getSideIndex(EnumFacing side) {
		return (side == null ? EnumFacing.values().length : side.ordinal());
	}
	
	/**
	 * Set whether the provided side is allowed to transfer aether.
	 * @param side The side. Note: NULL is allowed.
	 * @param enabled
	 */
	protected void enableSide(EnumFacing side, boolean enabled) {
		sideConnections[getSideIndex(side)] = enabled;
		markDirty();
	}
	
	/**
	 * Check whether the given side is enabled for aether transfer.
	 * Used on regular tick flow updates -- AKA when trying to PULL.
	 * By default, also used to regular whether things can push/pull to/from us.
	 * @param side
	 * @return
	 */
	protected boolean getSideEnabled(EnumFacing side) {
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
	
	protected void clearConnections() {
		remoteConnections.clear();
	}
	
	/**
	 * Returns whether or not this side can accept incoming aether.
	 * @param side
	 * @return
	 */
	protected boolean canAcceptOnSide(EnumFacing side) {
		return sideConnections[getSideIndex(side)];
	}
	
	/**
	 * Returns whether this side can have aether drawn from it.
	 * @param side
	 * @return
	 */
	protected boolean canDrawOnSide(EnumFacing side) {
		return sideConnections[getSideIndex(side)];
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
	public int addAether(EnumFacing side, int amount) {
		if (canAcceptOnSide(side)) {
			int start = aether;
			aether += amount;
			if (aether > maxAether) {
				amount = aether - maxAether;
				fixAether();
			} else {
				amount = 0;
			}
			
			if (aether != start) {
				markDirty();
			}
		}
		
		return amount;
	}
	
	@Override
	public boolean canAdd(EnumFacing side, int amount) {
		return (canAcceptOnSide(side) && maxAether - aether >= amount);
	}
	
	protected int drawAetherFromMyself(EnumFacing side, int amount) {
		if (amount != 0 && canDrawOnSide(side)) {
			amount = Math.min(amount, aether);
			aether -= amount;
			markDirty();
		} else {
			amount = 0;
		}
		
		return amount;
	}
	
	protected List<AetherFlowConnection> getConnections() {
		List<AetherFlowConnection> connections = new ArrayList<>(EnumFacing.values().length + 1 + remoteConnections.size());
		for (EnumFacing dir : EnumFacing.values()) {
			if (!getSideEnabled(dir)) {
				continue;
			}
			
			BlockPos cursor = pos.offset(dir);
			TileEntity te = worldObj.getTileEntity(cursor);
			if (te != null && te instanceof IAetherHandler) {
				connections.add(new AetherFlowConnection((IAetherHandler) te, dir.getOpposite()));
			}
		}
		
		connections.addAll(remoteConnections);
		return connections;
	}
	
	@Override
	public int drawAether(EnumFacing side, int amount, AetherIterateContext context) {
		context.addConnections(getConnections());
		return this.drawAetherFromMyself(side, amount);
	}

	@Override
	public int drawAether(EnumFacing side, int amount) {
		return AetherFlowMechanics.drawFromHandler(this, side, amount, false);
	}
	
	/**
	 * Attempts to recuperate any lost aether from configured connections.
	 * Presumably called in a tick function.
	 * @param maxDiff
	 */
	protected void fillAether(int maxDiff) {
		final int missing = maxAether - aether;
		if (missing > 0) {
			int amt = Math.min(maxDiff, missing);
			for (AetherFlowConnection connection : getConnections()) {
				int drawn = AetherFlowMechanics.drawFromHandler(this, null, connection.handler, connection.face, amt, false);
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
	protected void pushAether(int maxDiff) {
		if (aether > 0) {
			int start = Math.min(aether, maxDiff); 
			int amt = start;
			for (AetherFlowConnection connection : getConnections()) {
				amt = connection.handler.addAether(connection.face, amt);
				if (amt <= 0) {
					break;
				}
			}
			this.drawAetherFromMyself(null, (start - amt));
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
	public void validate() {
		super.validate();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		
		// Clean up connections
		cleanConnections();
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		cleanConnections();
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
	
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		compound.setInteger(NBT_AETHER, aether);
		compound.setInteger(NBT_MAX_AETHER, maxAether);
		compound.setByte(NBT_SIDE_CONFIG, configToByte(sideConnections));
		
		return compound;
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		this.aether = compound.getInteger(NBT_AETHER);
		this.maxAether = compound.getInteger(NBT_MAX_AETHER);
		configFromByte(sideConnections, compound.getByte(NBT_SIDE_CONFIG));
		fixAether();
	}

	@SideOnly(Side.CLIENT)
	public void syncAether(int aether) {
		this.aether = aether;
	}
}
