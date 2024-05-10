package com.smanzana.nostrumaetheria.tiles;

import com.smanzana.nostrumaetheria.blocks.AetherRelay.RelayMode;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class EnhancedAetherRelayEntity extends AetherRelayEntity {

		private static final int AETHER_BUFFER_AMT = 0;
		private static final int MAX_LINK_RANGE = 25;
		
		public EnhancedAetherRelayEntity() {
			this(Direction.UP);
		}
		
		public EnhancedAetherRelayEntity(Direction facing) {
			super(AetheriaTileEntities.EnhancedRelay, facing, RelayMode.INOUT);
			this.handler.setMaxAether(AETHER_BUFFER_AMT);
			this.handler.setShouldPropagate(true);
			handler.enableSide(getSide().getOpposite(), false);
		}
		
		@Override
		public void setMode(RelayMode mode, boolean flush) {
			this.mode = mode;
			return; // Ignore mode
		}
		
		@Override
		protected void setBlockStateFromMode(RelayMode mode) {
			; // Should never be called, but block doesn't have different states for mode
		}
		
		@Override
		protected boolean canLinkTo(BlockPos pos) {
			return this.pos.manhattanDistance(pos) <= MAX_LINK_RANGE; // Override to use our range
		}
		
		@Override
		public CompoundNBT write(CompoundNBT compound) {
			return super.write(compound);
		}
		
		@Override
		public void read(BlockState state, CompoundNBT compound) {
			super.read(state, compound);
		}
		
		@Override
		public void tick() {
			super.tick();
		}
		
		@Override
		protected void inOutTick() {
			; // do nothing
		}
	}