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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AetherTileEntity extends TileEntity implements IAetherHandlerProvider, IAetherComponentListener {

	private static final String NBT_HANDLER = "aether_handler";
	
	protected OptionalAetherHandlerComponent compWrapper;
	protected final IAetherStatTracker statTracker;
	
	public AetherTileEntity(TileEntityType<?> type, int defaultAether, int defaultMaxAether) {
		super(type);
		compWrapper = createComponent(defaultAether, defaultMaxAether);
		statTracker = createTracker();
	}
	
	public AetherTileEntity(TileEntityType<?> type) {
		this(type, 0, 0);
	}
	
	protected OptionalAetherHandlerComponent createComponent(int defaultAether, int defaultMaxAether) {
		return new OptionalAetherHandlerComponent(this, defaultAether, defaultMaxAether);
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
				
				BlockPos neighbor = pos.offset(dir);
				
				// First check for a TileEntity
				TileEntity te = world.getTileEntity(neighbor);
				if (te != null && te instanceof IAetherHandler) {
					connections.add(new AetherFlowConnection((IAetherHandler) te, dir.getOpposite()));
					continue;
				}
				if (te != null && te instanceof IAetherHandlerProvider) {
					connections.add(new AetherFlowConnection(((IAetherHandlerProvider) te).getHandler(), dir.getOpposite()));
					continue;
				}
				
				// See if block boasts being able to get us a handler
				BlockState attachedState = world.getBlockState(neighbor);
				Block attachedBlock = attachedState.getBlock();
				if (attachedBlock instanceof IAetherCapableBlock) {
					connections.add(new AetherFlowConnection(((IAetherCapableBlock) attachedBlock).getAetherHandler(world, attachedState, neighbor, dir), dir.getOpposite()));
					continue;
				}
			}
		}
	}
	
	@Override
	public void dirty() {
		this.markDirty();
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		if (this.world != null) {
			final int aether = (this.compWrapper.isPresent() ? this.compWrapper.getHandlerIfPresent().getAether(null) : 0);
			statTracker.reportTotal(world.getGameTime(), aether);
			
			if (added) {
				statTracker.reportInput(world.getGameTime(), diff);
			} else {
				statTracker.reportOutput(world.getGameTime(), diff);
			}
		}
	}
	
	@Override
	public void validate() {
		super.validate();
	}
	
	@Override
	public void remove() {
		super.remove();
		
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
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		
		compound.put(NBT_HANDLER, compWrapper.toNBT());
		
		return compound;
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		
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
}
