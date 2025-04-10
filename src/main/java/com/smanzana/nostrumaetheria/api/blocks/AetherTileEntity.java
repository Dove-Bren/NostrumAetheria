package com.smanzana.nostrumaetheria.api.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler.AetherFlowConnection;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerProvider;
import com.smanzana.nostrumaetheria.api.aether.stats.IAetherStatTracker;
import com.smanzana.nostrumaetheria.api.component.AetherStatTrackerComponent;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.component.OptionalAetherHandlerComponent;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AetherTileEntity extends BlockEntity implements IAetherHandlerProvider, IAetherComponentListener {

	private static final String NBT_HANDLER = "aether_handler";
	
	protected OptionalAetherHandlerComponent compWrapper;
	protected final IAetherStatTracker statTracker;
	
	public AetherTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int defaultAether, int defaultMaxAether) {
		super(type, pos, state);
		compWrapper = createComponent(defaultAether, defaultMaxAether);
		statTracker = createTracker();
	}
	
	public AetherTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		this(type, pos, state, 0, 0);
	}
	
	protected OptionalAetherHandlerComponent createComponent(int defaultAether, int defaultMaxAether) {
		return new OptionalAetherHandlerComponent(level, worldPosition, this, defaultAether, defaultMaxAether);
	}
	
	protected IAetherStatTracker createTracker() {
		return new AetherStatTrackerComponent();
	}
	
	@Override
	public void addConnections(List<AetherFlowConnection> connections) {
		IAetherHandlerComponent comp = compWrapper.getHandlerIfPresent();
		if (comp != null) {
			for (Direction dir : Direction.values()) {
				if (!comp.getSideEnabled(dir)) {
					continue;
				}
				
				BlockPos neighbor = worldPosition.relative(dir);
				
				// First check for a TileEntity
				BlockEntity te = level.getBlockEntity(neighbor);
				if (te != null && te instanceof IAetherHandler) {
					connections.add(new AetherFlowConnection((IAetherHandler) te, dir.getOpposite()));
					continue;
				}
				if (te != null && te instanceof IAetherHandlerProvider) {
					connections.add(new AetherFlowConnection(((IAetherHandlerProvider) te).getHandler(), dir.getOpposite()));
					continue;
				}
				
				// See if block boasts being able to get us a handler
				BlockState attachedState = level.getBlockState(neighbor);
				Block attachedBlock = attachedState.getBlock();
				if (attachedBlock instanceof IAetherCapableBlock) {
					connections.add(new AetherFlowConnection(((IAetherCapableBlock) attachedBlock).getAetherHandler(level, attachedState, neighbor, dir), dir.getOpposite()));
					continue;
				}
			}
		}
	}
	
	@Override
	public void dirty() {
		this.setChanged();
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		if (this.level != null) {
			final int aether = (this.compWrapper.isPresent() ? this.compWrapper.getHandlerIfPresent().getAether(null) : 0);
			statTracker.reportTotal(level.getGameTime(), aether);
			
			if (added) {
				statTracker.reportInput(level.getGameTime(), diff);
			} else {
				statTracker.reportOutput(level.getGameTime(), diff);
			}
		}
	}
	
	@Override
	public void clearRemoved() {
		super.clearRemoved();
	}
	
	@Override
	public void setRemoved() {
		super.setRemoved();
		
		// Clean up connections
		IAetherHandlerComponent comp = compWrapper.getHandlerIfPresent();
		if (comp != null) {
			comp.clearConnections();
		}
	}
	
//	@Override
//	public void onChunkUnload() {
//		super.onChunkUnload();
//		IAetherHandlerComponent comp = compWrapper.getHandlerIfPresent();
//		if (comp != null) {
//			comp.clearConnections();
//		}
//	}
	
	@Override
	public CompoundTag save(CompoundTag compound) {
		super.save(compound);
		
		compound.put(NBT_HANDLER, compWrapper.toNBT());
		
		return compound;
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		compWrapper.loadNBT(compound.get(NBT_HANDLER));
	}

	@OnlyIn(Dist.CLIENT)
	public void syncAether(int aether) {
		IAetherHandlerComponent comp = compWrapper.getHandlerIfPresent();
		if (comp != null) {
			comp.setAether(aether);
		}
	}
	
	@Override
	public @Nullable IAetherHandler getHandler() {
		return compWrapper.getHandlerIfPresent();
	}
	
	public IAetherStatTracker getStats() {
		return this.statTracker;
	}
	
	@Override
	public void setLevel(Level world) {
		super.setLevel(world);
		
		this.compWrapper.getHandlerIfPresent().setPosition(this.level, this.worldPosition);
	}
	
//	@Override
//	public void setPosition(BlockPos pos) {
//		super.setPosition(pos);
//		
//		if (this.compWrapper.isPresent() && this.level != null) {
//			this.compWrapper.getHandlerIfPresent().setPosition(this.level, this.worldPosition);
//		}
//	}
}
