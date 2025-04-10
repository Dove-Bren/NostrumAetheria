package com.smanzana.nostrumaetheria.tiles;

import com.smanzana.nostrumaetheria.blocks.AetherRelay.RelayMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class EnhancedAetherRelayEntity extends AetherRelayEntity {

		private static final int AETHER_BUFFER_AMT = 0;
		private static final int MAX_LINK_RANGE = 25;
		
		public EnhancedAetherRelayEntity(BlockPos pos, BlockState state) {
			this(pos, state, Direction.UP);
		}
		
		public EnhancedAetherRelayEntity(BlockPos pos, BlockState state, Direction facing) {
			super(AetheriaTileEntities.EnhancedRelay, pos, state, facing, RelayMode.INOUT);
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
			return this.worldPosition.distManhattan(pos) <= MAX_LINK_RANGE; // Override to use our range
		}
		
		@Override
		public CompoundTag save(CompoundTag compound) {
			return super.save(compound);
		}
		
		@Override
		public void load(CompoundTag compound) {
			super.load(compound);
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