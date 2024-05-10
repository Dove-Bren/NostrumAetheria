package com.smanzana.nostrumaetheria.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class InfiniteAetherBlockEntity extends NativeAetherTickingTileEntity {

	public InfiniteAetherBlockEntity() {
		super(AetheriaTileEntities.InfiniteBlock, 0, 10000);
		this.setAutoSync(5);
		this.handler.configureInOut(false, true);
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			int leftoverGen = this.handler.addAether(null, 10000, true); // 'force' to disable having aether added by others but force ourselves.
			
			this.handler.pushAether(10000);
			// Fix issue where a deficit at the start will never be recouped:
			if (leftoverGen > 0) {
				this.handler.addAether(null, leftoverGen, true);
			}
		}
		super.tick();
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		return super.write(nbt);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
	}
}