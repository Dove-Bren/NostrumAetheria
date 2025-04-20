package com.smanzana.nostrumaetheria.api.blocks;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AetherTickingTileEntity extends AetherTileEntity implements TickableBlockEntity {

	// Automatically send block updates down to the client every n ticks (if there were changes)
	protected int autoSyncPeriod;
	
	// Transient tick variables
	protected int ticksExisted;
	protected boolean aetherDirtyFlag; // for use with auto-sync
	
	public AetherTickingTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int defaultAether, int defaultMaxAether) {
		super(type, pos, state, defaultAether, defaultMaxAether);
	}
	
	public AetherTickingTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		
		if (!level.isClientSide && aetherDirtyFlag && autoSyncPeriod > 0 && (ticksExisted == 1 || ticksExisted % autoSyncPeriod == 0)) {
			//worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(pos), this.worldObj.getBlockState(pos), 2);
			syncServerAether();
			aetherDirtyFlag = false;
		}
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		//handleUpdateTag(pkt.getTag());
	}
}
