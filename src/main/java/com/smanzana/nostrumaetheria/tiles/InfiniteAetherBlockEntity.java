package com.smanzana.nostrumaetheria.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class InfiniteAetherBlockEntity extends NativeAetherTickingTileEntity {

	public InfiniteAetherBlockEntity(BlockPos pos, BlockState state) {
		super(AetheriaTileEntities.InfiniteBlock, pos, state, 0, 10000);
		this.setAutoSync(5);
		this.handler.configureInOut(false, true);
	}

	@Override
	public void tick() {
		if (!level.isClientSide) {
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
	public CompoundTag save(CompoundTag nbt) {
		return super.save(nbt);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
	}
}