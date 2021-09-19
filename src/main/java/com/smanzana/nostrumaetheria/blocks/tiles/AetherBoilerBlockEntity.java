package com.smanzana.nostrumaetheria.blocks.tiles;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class AetherBoilerBlockEntity extends AetherFurnaceGenericTileEntity {
	
	public AetherBoilerBlockEntity() {
		super(1, 0, 500);
	}
	
	protected @Nullable TileEntityFurnace getNearbyFurnace() {
		TileEntity te = world.getTileEntity(pos.up());
		if (te != null && te instanceof TileEntityFurnace) {
			return (TileEntityFurnace) te;
		}
		return null;
	}
	
	protected void fuelNearbyFurnace() {
		TileEntityFurnace furnace = getNearbyFurnace();
		if (furnace != null) {
			ObfuscationReflectionHelper.setPrivateValue(TileEntityFurnace.class, furnace, 20, "furnaceBurnTime");
			BlockFurnace.setState(true, world, pos.up());
		}
	}

	@Override
	public String getName() {
		return "Aether Boiler Inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return !(oldState.getBlock().equals(newState.getBlock()));
	}

	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (facing == EnumFacing.DOWN && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
	}
	
	private IItemHandler handlerProxy = null;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == EnumFacing.DOWN) {
				
				// Proxy up to a furnace that's above us, if there is one
				if (handlerProxy == null) {
					handlerProxy = new IItemHandler() {

						@Override
						public int getSlots() {
							return 1;
						}

						@Override
						public ItemStack getStackInSlot(int slot) {
							TileEntityFurnace furnace = getNearbyFurnace();
							if (furnace != null) {
								return furnace.getStackInSlot(2);
							}
							return ItemStack.EMPTY;
						}

						@Override
						public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
							return stack;
						}

						@Override
						public ItemStack extractItem(int slot, int amount, boolean simulate) {
							TileEntityFurnace furnace = getNearbyFurnace();
							if (furnace != null) {
								if (simulate) {
									return furnace.getStackInSlot(2);
								} else {
									return furnace.removeStackFromSlot(2);
								}
							}
							return null;
						}

						@Override
						public int getSlotLimit(int slot) {
							return 64;
						}
						
					};
				}
				return (T) handlerProxy;
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	protected float getAetherMultiplier() {
		// Inverting duration multiplier because we don't want it to actually increase yield.
		// So this is 75% efficiency overall, BUT we power a furnace above us.
		return .75f * (1 / getDurationMultiplier());
	}

	@Override
	protected float getDurationMultiplier() {
		return 20f/5f; // default 1 reagent is 5 seconds, but we want 20 seconds of burn
	}

	@Override
	protected void onBurningChange(boolean newBurning) {
		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, AetherBoilerBlock.instance().getDefaultState().withProperty(AetherBoilerBlock.ON, newBurning).withProperty(AetherBoilerBlock.FACING, state.getValue(AetherBoilerBlock.FACING)));
	}
	
	@Override
	protected void generateAether() {
		super.generateAether();
		this.fuelNearbyFurnace();
	}
}