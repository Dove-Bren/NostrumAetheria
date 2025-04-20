package com.smanzana.nostrumaetheria.tiles;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
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
	
	public AetherBoilerBlockEntity(BlockPos pos, BlockState state) {
		super(AetheriaTileEntities.Boiler, pos, state, 1, 0, 500);
		mode = BoilerBurnMode.FOCUS_AETHER;
	}
	
	protected @Nullable FurnaceBlockEntity getNearbyFurnace() {
		BlockEntity te = level.getBlockEntity(worldPosition.above());
		if (te != null && te instanceof FurnaceBlockEntity) {
			return (FurnaceBlockEntity) te;
		}
		return null;
	}
	
	protected void fuelNearbyFurnace() {
		FurnaceBlockEntity furnace = getNearbyFurnace();
		if (furnace != null) {
			try {
				ObfuscationReflectionHelper.setPrivateValue(AbstractFurnaceBlockEntity.class, furnace, 20, "litTime");// "burnTime");
			} catch (Exception e) {
				
			}
			this.level.setBlock(this.worldPosition.above(), this.level.getBlockState(this.worldPosition.above()).setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(true)), 3);
		}
	}
	
	/**
	 * Returns whether the provided furnace has work that can be done (item to consume, room in output)
	 * @param furnace
	 * @return
	 */
	protected static final boolean FurnaceHasWork(@Nullable FurnaceBlockEntity furnace) {
		if (furnace != null && furnace instanceof AbstractFurnaceBlockEntity) {
			if (Furnace_CanSmeltMethod == null) {
				try {
				Furnace_CanSmeltMethod = ObfuscationReflectionHelper.findMethod(AbstractFurnaceBlockEntity.class, "canBurn", Recipe.class); // "canSmelt"
				if (Furnace_CanSmeltMethod != null) {
					Furnace_CanSmeltMethod.setAccessible(true);
				}
				} catch (Exception e) {
					Furnace_CanSmeltMethod = null;
				}
			}
			
			if (Furnace_CanSmeltMethod != null) {
				try {
					Recipe<?> irecipe = furnace.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, furnace, furnace.getLevel()).orElse(null);
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
							FurnaceBlockEntity furnace = getNearbyFurnace();
							if (furnace != null) {
								return furnace.getItem(2);
							}
							return ItemStack.EMPTY;
						}

						@Override
						public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
							return stack;
						}

						@Override
						public ItemStack extractItem(int slot, int amount, boolean simulate) {
							FurnaceBlockEntity furnace = getNearbyFurnace();
							if (furnace != null) {
								if (simulate) {
									return furnace.getItem(2);
								} else {
									return furnace.removeItemNoUpdate(2);
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
		BlockState state = level.getBlockState(worldPosition);
		level.setBlockAndUpdate(worldPosition, AetheriaBlocks.boiler.defaultBlockState().setValue(AetherBoilerBlock.ON, newBurning).setValue(AetherBoilerBlock.FACING, state.getValue(AetherBoilerBlock.FACING)));
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
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		nbt.putString(NBT_BOILER_MODE, this.mode.name());
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		if (nbt.contains(NBT_BOILER_MODE, Tag.TAG_STRING)) {
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