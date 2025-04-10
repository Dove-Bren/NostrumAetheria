package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.blocks.AetherChargerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.client.gui.container.IAutoContainerInventoryWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherChargerBlockEntity extends AetherBathTileEntity implements IAutoContainerInventoryWrapper {
	
	private boolean on;
	private boolean aetherTick;
	
	private int clientAetherDisplay; // Client-only.
	private int clientAetherMaxDisplay; // Client-only.
	
	public AetherChargerBlockEntity(BlockPos pos, BlockState state) {
		super(AetheriaTileEntities.Charger, pos, state, 0, 500);
	}
	
	@Override
	public int getField(int id) {
		if (id == 0) {
			return this.handler.getAether(null);
		} else if (id == 1) {
			@Nullable IAetherHandler handler = this.getHeldHandler();
			if (handler != null) {
				return handler.getAether(null);
			}
			return 0;
		} else if (id == 2) {
			@Nullable IAetherHandler handler = this.getHeldHandler();
			if (handler != null) {
				return handler.getMaxAether(null);
			}
			return 0;
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0) {
			this.handler.setAether(value);
		} else if (id == 1) {
			clientAetherDisplay = value;
		} else if (id == 2) {
			clientAetherMaxDisplay = value;
		}
	}

	@Override
	public int getFieldCount() {
		return 3;
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		super.onAetherFlowTick(diff, added, taken);
		aetherTick = !this.heldItemFull();
	}
	
	@Override
	protected int maxAetherPerTick() {
		return 5;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (!level.isClientSide && this.ticksExisted % 5 == 0) {
			if (aetherTick != on) {
				BlockState state = level.getBlockState(worldPosition);
				level.setBlockAndUpdate(worldPosition, AetheriaBlocks.charger.defaultBlockState().setValue(AetherChargerBlock.ON, aetherTick).setValue(AetherChargerBlock.FACING, state.getValue(AetherChargerBlock.FACING)));
			}
			
			on = aetherTick;
			aetherTick = false;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getAetherDisplay() {
		return clientAetherDisplay;
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getMaxAetherDisplay() {
		return clientAetherMaxDisplay;
	}

}