package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.blocks.AetherPumpBlock;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class AetherPumpBlockEntity extends AetherTickingTileEntity {
	
	public AetherPumpBlockEntity() {
		super(AetheriaTileEntities.Pump, 0, 500);
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		super.onAetherFlowTick(diff, added, taken);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (!world.isRemote) {
			BlockState state = world.getBlockState(pos);
			if (!(state.getBlock() instanceof AetherPumpBlock)) {
				world.removeTileEntity(pos);
				return;
			}
			
			final Direction direction = ((AetherPumpBlock) state.getBlock()).getFacing(state);
			
			// Pull
			// Note: carts handled by the cart
			{
				final int maxAether = this.getHandler().getMaxAether(null);
				final int room = maxAether - this.getHandler().getAether(null);
				// Attempt to draw from a handler we're pointed at
				@Nullable IAetherHandler handler = IAetherHandler.GetHandlerAt(world, pos.offset(direction), direction.getOpposite());
				if (handler != null) {
					final int drawn = handler.drawAether(direction.getOpposite(), room);
					this.getHandler().addAether(null, drawn);
					this.markDirty();
				}
			}
			
			// Push
			if (this.getHandler().getAether(null) > 0) {
				// Look up handler at pointed position
				@Nullable IAetherHandler handler = IAetherHandler.GetHandlerAt(world, pos.offset(direction.getOpposite()), direction);
				if (handler != null && handler.canAdd(direction, 1)) {
					final int orig = this.getHandler().getAether(null);
					final int leftover = handler.addAether(direction, orig);
					if (leftover != orig) {
						// Pushed some out
						this.getHandler().drawAether(null, orig - leftover);
						this.markDirty();
					}
				}
			}
		}
	}

}