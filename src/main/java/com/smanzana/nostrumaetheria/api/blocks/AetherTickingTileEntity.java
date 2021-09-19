package com.smanzana.nostrumaetheria.api.blocks;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;

public abstract class AetherTickingTileEntity extends AetherTileEntity implements ITickable {

	// Automatically send block updates down to the client every n ticks (if there were changes)
	protected int autoSyncPeriod;
	
	// Transient tick variables
	protected int ticksExisted;
	protected boolean aetherDirtyFlag; // for use with auto-sync
	
	public AetherTickingTileEntity(int defaultAether, int defaultMaxAether) {
		super(defaultAether, defaultMaxAether);
	}
	
	public AetherTickingTileEntity() {
		super();
	}
	
	public void setAutoSync(int period) {
		autoSyncPeriod = period;
	}
	
	protected void syncServerAether() {
		APIProxy.syncTEAether(this);
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		if (diff != 0) {
			aetherDirtyFlag = true;
		}
	}
	
	@Override
	public void update() {
		ticksExisted++;
		
		compWrapper.tick();
		
		if (!world.isRemote && aetherDirtyFlag && autoSyncPeriod > 0 && (ticksExisted == 1 || ticksExisted % autoSyncPeriod == 0)) {
			//worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(pos), this.worldObj.getBlockState(pos), 2);
			syncServerAether();
			aetherDirtyFlag = false;
		}
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
}
