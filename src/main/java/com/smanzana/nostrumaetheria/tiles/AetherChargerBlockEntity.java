package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.blocks.AetherChargerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrummagica.utils.ContainerUtil.IAutoContainerInventory;

import net.minecraft.block.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherChargerBlockEntity extends AetherBathTileEntity implements IAutoContainerInventory {
	
	private boolean on;
	private boolean aetherTick;
	
	private int clientAetherDisplay; // Client-only.
	private int clientAetherMaxDisplay; // Client-only.
	
	public AetherChargerBlockEntity() {
		super(AetheriaTileEntities.Charger, 0, 500);
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
		
		if (!world.isRemote && this.ticksExisted % 5 == 0) {
			if (aetherTick != on) {
				BlockState state = world.getBlockState(pos);
				world.setBlockState(pos, AetheriaBlocks.charger.getDefaultState().with(AetherChargerBlock.ON, aetherTick).with(AetherChargerBlock.FACING, state.get(AetherChargerBlock.FACING)));
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