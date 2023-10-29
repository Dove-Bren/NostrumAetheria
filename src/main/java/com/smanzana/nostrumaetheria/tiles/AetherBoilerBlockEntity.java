package com.smanzana.nostrumaetheria.tiles;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class AetherBoilerBlockEntity extends AetherFurnaceGenericTileEntity {
	
	private static final String NBT_BOILER_MODE = "boiler_mode";
	
	public static enum BoilerBurnMode {
		FOCUS_AETHER("focus_aether"),
		FOCUS_FURNACE("focus_furnace"),
		FOCUS_BOTH("focus_both"),
		ALWAYS_ON("always_on"),
		;
		
		private final String unloc;
		
		private BoilerBurnMode(String unloc) {
			this.unloc = unloc;
		}
		
		public String getUnlocID() {
			return unloc;
		}
	}
	
	protected BoilerBurnMode mode;
	
	public AetherBoilerBlockEntity() {
		super(1, 0, 500);
		mode = BoilerBurnMode.FOCUS_AETHER;
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
			ObfuscationReflectionHelper.setPrivateValue(TileEntityFurnace.class, furnace, 20, "field_145956_a");// "furnaceBurnTime");
			BlockFurnace.setState(true, world, pos.up());
		}
	}
	
	/**
	 * Returns whether the provided furnace has work that can be done (item to consume, room in output)
	 * @param furnace
	 * @return
	 */
	protected static final boolean FurnaceHasWork(@Nullable TileEntityFurnace furnace) {
		if (Furnace_CanSmeltMethod == null) {
			Furnace_CanSmeltMethod = ObfuscationReflectionHelper.findMethod(furnace.getClass(), "func_145948_k", boolean.class); // "canSmelt"
			if (Furnace_CanSmeltMethod != null) {
				Furnace_CanSmeltMethod.setAccessible(true);
			}
		}
		
		if (furnace != null && Furnace_CanSmeltMethod != null) {
			try {
				return (boolean) Furnace_CanSmeltMethod.invoke(furnace);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return false;
	}
	private static Method Furnace_CanSmeltMethod = null;
	
	public BoilerBurnMode getBoilerMode() {
		return this.mode;
	}
	
	public void setBoilerMode(BoilerBurnMode mode) {
		this.mode = mode;
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
	
	@Override
	protected boolean shouldTryBurn() {
		boolean shouldBurn = false;
		
		final boolean hasAetherRoom = getHandler().getAether(null) < getHandler().getMaxAether(null);
		final boolean hasFurnaceWork = FurnaceHasWork(getNearbyFurnace());
		
		switch (this.mode) {
		case ALWAYS_ON:
			shouldBurn = true;
			break;
		case FOCUS_AETHER:
			// Only if there's room for more aether
			shouldBurn = hasAetherRoom;
			break;
		case FOCUS_FURNACE:
			// Only if furnace has work it can do
			shouldBurn = hasFurnaceWork;
			break;
		case FOCUS_BOTH:
			// If either furnace has work or we have aether room
			shouldBurn = hasAetherRoom || hasFurnaceWork;
			break;
		}
		
		return shouldBurn;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		
		nbt.setString(NBT_BOILER_MODE, this.mode.name());
		
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		if (nbt.hasKey(NBT_BOILER_MODE, NBT.TAG_STRING)) {
			BoilerBurnMode readMode = BoilerBurnMode.FOCUS_AETHER;
			try {
				readMode = BoilerBurnMode.valueOf(nbt.getString(NBT_BOILER_MODE));
			} catch (IllegalArgumentException e) {
				NostrumAetheria.logger.error("Failed to parse boiler mode: " + nbt.getString(NBT_BOILER_MODE));
				readMode = BoilerBurnMode.FOCUS_AETHER;
			}
			this.setBoilerMode(readMode);
		}
	}
}