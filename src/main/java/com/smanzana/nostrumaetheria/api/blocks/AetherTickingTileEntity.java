package com.smanzana.nostrumaetheria.api.blocks;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class AetherTickingTileEntity extends AetherTileEntity implements ITickableTileEntity {

	// Automatically send block updates down to the client every n ticks (if there were changes)
	protected int autoSyncPeriod;
	
	// Transient tick variables
	protected int ticksExisted;
	protected boolean aetherDirtyFlag; // for use with auto-sync
	
	public AetherTickingTileEntity(TileEntityType<?> type, int defaultAether, int defaultMaxAether) {
		super(type, defaultAether, defaultMaxAether);
	}
	
	public AetherTickingTileEntity(TileEntityType<?> type) {
		super(type);
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
	public void tick() {
		ticksExisted++;
		
		compWrapper.tick();
		
		if (!world.isRemote && aetherDirtyFlag && autoSyncPeriod > 0 && (ticksExisted == 1 || ticksExisted % autoSyncPeriod == 0)) {
			//worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(pos), this.worldObj.getBlockState(pos), 2);
			syncServerAether();
			aetherDirtyFlag = false;
		}
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(this.getBlockState(), pkt.getNbtCompound());
	}
}
