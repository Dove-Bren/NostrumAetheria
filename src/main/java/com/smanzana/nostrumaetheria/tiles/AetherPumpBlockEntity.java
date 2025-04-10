package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.blocks.AetherPumpBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class AetherPumpBlockEntity extends AetherTickingTileEntity {
	
	public AetherPumpBlockEntity(BlockPos pos, BlockState state) {
		super(AetheriaTileEntities.Pump, pos, state, 0, 500);
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		super.onAetherFlowTick(diff, added, taken);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (!level.isClientSide) {
			BlockState state = level.getBlockState(worldPosition);
			if (!(state.getBlock() instanceof AetherPumpBlock)) {
				level.removeBlockEntity(worldPosition);
				return;
			}
			
			final Direction direction = ((AetherPumpBlock) state.getBlock()).getFacing(state);
			
			// Pull
			// Note: carts handled by the cart
			{
				final int maxAether = this.getHandler().getMaxAether(null);
				final int room = maxAether - this.getHandler().getAether(null);
				// Attempt to draw from a handler we're pointed at
				@Nullable IAetherHandler handler = IAetherHandler.GetHandlerAt(level, worldPosition.relative(direction), direction.getOpposite());
				if (handler != null) {
					final int drawn = handler.drawAether(direction.getOpposite(), room);
					this.getHandler().addAether(null, drawn);
					this.setChanged();
				}
			}
			
			// Push
			if (this.getHandler().getAether(null) > 0) {
				// Look up handler at pointed position
				@Nullable IAetherHandler handler = IAetherHandler.GetHandlerAt(level, worldPosition.relative(direction.getOpposite()), direction);
				if (handler != null && handler.canAdd(direction, 1)) {
					final int orig = this.getHandler().getAether(null);
					final int leftover = handler.addAether(direction, orig);
					if (leftover != orig) {
						// Pushed some out
						this.getHandler().drawAether(null, orig - leftover);
						this.setChanged();
					}
				}
			}
		}
	}

}