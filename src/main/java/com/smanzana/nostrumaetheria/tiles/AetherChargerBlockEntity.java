package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.blocks.AetherChargerBlock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherChargerBlockEntity extends AetherBathTileEntity {
	
	private boolean on;
	private boolean aetherTick;
	
	private int clientAetherDisplay; // Client-only.
	private int clientAetherMaxDisplay; // Client-only.
	
	public AetherChargerBlockEntity() {
		super(0, 500);
	}
	
	@Override
	public String getName() {
		return "Aether Charger Inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return !(oldState.getBlock().equals(newState.getBlock()));
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
	public void update() {
		super.update();
		
		if (!world.isRemote && this.ticksExisted % 5 == 0) {
			if (aetherTick != on) {
				IBlockState state = world.getBlockState(pos);
				world.setBlockState(pos, AetherChargerBlock.instance().getDefaultState().withProperty(AetherChargerBlock.ON, aetherTick).withProperty(AetherChargerBlock.FACING, state.getValue(AetherChargerBlock.FACING)));
			}
			
			on = aetherTick;
			aetherTick = false;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public int getAetherDisplay() {
		return clientAetherDisplay;
	}
	
	@SideOnly(Side.CLIENT)
	public int getMaxAetherDisplay() {
		return clientAetherMaxDisplay;
	}

}