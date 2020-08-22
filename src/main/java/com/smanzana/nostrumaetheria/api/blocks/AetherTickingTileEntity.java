package com.smanzana.nostrumaetheria.api.blocks;

import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.network.messages.AetherTileEntityMessage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public abstract class AetherTickingTileEntity extends AetherTileEntity implements ITickable {

	// Whether or not we automatically try to fill ourselves up from our connections
	protected boolean autoFill;
	// Automatically send block updates down to the client every n ticks (if there were changes)
	protected int autoSyncPeriod;
	
	// Transient tick variables
	protected int ticksExisted;
	protected boolean receivedAetherThisTick;
	protected boolean gaveAetherThisTick;
	protected int aetherLastTick;
	protected boolean aetherDirtyFlag; // for use with auto-sync
	
	public AetherTickingTileEntity(int defaultAether, int defaultMaxAether) {
		super(defaultAether, defaultMaxAether);
	}
	
	public AetherTickingTileEntity() {
		super();
	}
	
	public void setAutoFill(boolean fill) {
		autoFill = fill;
	}
	
	public void setAutoSync(int period) {
		autoSyncPeriod = period;
	}
	
//	/**
//	 * Enables or disables checking neighboring tiles every tick.
//	 * 'Connections' are still checked and flowed into.
//	 * @param enable
//	 */
//	protected void setCheckSides(boolean enable) {
//		checkSides = enable;
//		markDirty();
//	}
//	
//	/**
//	 * Return whether or not we're going to check neighboring tiles every tick
//	 * @return
//	 */
//	protected boolean getCheckSides() {
//		return checkSides;
//	}
	
	@Override
	public int addAether(EnumFacing side, int amount) {
		int start = amount;
		amount = super.addAether(side, amount);
		if (amount != start) {
			receivedAetherThisTick = true;
		}
		
		return amount;
	}
	
	@Override
	protected int drawAetherFromMyself(EnumFacing side, int amount) {
		amount = super.drawAetherFromMyself(side, amount);
		
		if (0 != amount) {
			gaveAetherThisTick = true;
		}
		
		return amount;
	}
	
	protected void syncAether() {
		NetworkHandler.getSyncChannel().sendToAllAround(new AetherTileEntityMessage(this),
				new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
	}
	
	protected int getMaxAetherFill() {
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Called every tick if we had input/output this tick.
	 */
	protected abstract void onAetherFlowTick(int diff, boolean added, boolean taken);
	
	@Override
	public void update() {
		ticksExisted++;
		
		this.cleanConnections();
		
		if (autoFill) {
			this.fillAether(getMaxAetherFill());
		}
		
		int aetherDiff = this.getAether(null) - aetherLastTick;
		if (aetherDiff != 0 || receivedAetherThisTick || gaveAetherThisTick) {
			onAetherFlowTick(aetherDiff, receivedAetherThisTick, gaveAetherThisTick);
			if (aetherDiff != 0) {
				aetherDirtyFlag = true;
			}
		}
		
		if (aetherDirtyFlag && autoSyncPeriod > 0 && (ticksExisted == 1 || ticksExisted % autoSyncPeriod == 0)) {
			//worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(pos), this.worldObj.getBlockState(pos), 2);
			syncAether();
			aetherDirtyFlag = false;
		}
		
		aetherLastTick = this.getAether(null);
		receivedAetherThisTick = false;
		gaveAetherThisTick = false;
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public void syncAether(int aether) {
		super.syncAether(aether);
		this.aetherLastTick = aether; // Prevent phony diff ticks
	}
}
