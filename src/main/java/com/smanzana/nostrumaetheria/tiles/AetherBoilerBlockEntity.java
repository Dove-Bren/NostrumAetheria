package com.smanzana.nostrumaetheria.tiles;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
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
		super(AetheriaTileEntities.Boiler, 1, 0, 500);
		mode = BoilerBurnMode.FOCUS_AETHER;
	}
	
	protected @Nullable FurnaceTileEntity getNearbyFurnace() {
		TileEntity te = world.getTileEntity(pos.up());
		if (te != null && te instanceof FurnaceTileEntity) {
			return (FurnaceTileEntity) te;
		}
		return null;
	}
	
	protected void fuelNearbyFurnace() {
		FurnaceTileEntity furnace = getNearbyFurnace();
		if (furnace != null) {
			try {
				ObfuscationReflectionHelper.setPrivateValue(AbstractFurnaceTileEntity.class, furnace, 20, "field_214018_j");// "burnTime");
			} catch (Exception e) {
				
			}
			this.world.setBlockState(this.pos.up(), this.world.getBlockState(this.pos.up()).with(AbstractFurnaceBlock.LIT, Boolean.valueOf(true)), 3);
		}
	}
	
	/**
	 * Returns whether the provided furnace has work that can be done (item to consume, room in output)
	 * @param furnace
	 * @return
	 */
	protected static final boolean FurnaceHasWork(@Nullable FurnaceTileEntity furnace) {
		if (furnace != null && furnace instanceof AbstractFurnaceTileEntity) {
			if (Furnace_CanSmeltMethod == null) {
				try {
				Furnace_CanSmeltMethod = ObfuscationReflectionHelper.findMethod(AbstractFurnaceTileEntity.class, "func_214008_b", IRecipe.class); // "canSmelt"
				if (Furnace_CanSmeltMethod != null) {
					Furnace_CanSmeltMethod.setAccessible(true);
				}
				} catch (Exception e) {
					Furnace_CanSmeltMethod = null;
				}
			}
			
			if (Furnace_CanSmeltMethod != null) {
				try {
					IRecipe<?> irecipe = furnace.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, furnace, furnace.getWorld()).orElse(null);
					return (boolean) Furnace_CanSmeltMethod.invoke(furnace, irecipe);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
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

	public boolean hasCapability(Capability<?> capability, Direction facing) {
		return (facing == Direction.DOWN && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
	}
	
	private LazyOptional<IItemHandler> handlerProxy = null;

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == Direction.DOWN) {
				
				// Proxy up to a furnace that's above us, if there is one
				if (handlerProxy == null) {
					handlerProxy = LazyOptional.of(() -> new IItemHandler() {

						@Override
						public int getSlots() {
							return 1;
						}

						@Override
						public ItemStack getStackInSlot(int slot) {
							FurnaceTileEntity furnace = getNearbyFurnace();
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
							FurnaceTileEntity furnace = getNearbyFurnace();
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

						@Override
						public boolean isItemValid(int slot, ItemStack stack) {
							return false;
						}
						
					});
				}
				return handlerProxy.cast();
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
		BlockState state = world.getBlockState(pos);
		world.setBlockState(pos, AetheriaBlocks.boiler.getDefaultState().with(AetherBoilerBlock.ON, newBurning).with(AetherBoilerBlock.FACING, state.get(AetherBoilerBlock.FACING)));
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
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		
		nbt.putString(NBT_BOILER_MODE, this.mode.name());
		
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
		if (nbt.contains(NBT_BOILER_MODE, NBT.TAG_STRING)) {
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