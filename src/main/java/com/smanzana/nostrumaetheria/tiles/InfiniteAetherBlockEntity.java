package com.smanzana.nostrumaetheria.tiles;

import net.minecraft.nbt.NBTTagCompound;

public class InfiniteAetherBlockEntity extends NativeAetherTickingTileEntity {

	public InfiniteAetherBlockEntity() {
		super(0, 10000);
		this.setAutoSync(5);
		this.handler.configureInOut(false, true);
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			int leftoverGen = this.handler.addAether(null, 10000, true); // 'force' to disable having aether added by others but force ourselves.
			
			this.handler.pushAether(10000);
			// Fix issue where a deficit at the start will never be recouped:
			if (leftoverGen > 0) {
				this.handler.addAether(null, leftoverGen, true);
			}
		}
		super.update();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return super.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}
}