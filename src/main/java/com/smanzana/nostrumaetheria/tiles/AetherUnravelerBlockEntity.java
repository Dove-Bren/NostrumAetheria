package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.recipes.IAetherUnravelerRecipe;
import com.smanzana.nostrumaetheria.blocks.AetherUnravelerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.client.gui.container.IAutoContainerInventoryWrapper;
import com.smanzana.nostrumaetheria.recipes.UnravelerRecipeManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AetherUnravelerBlockEntity extends NativeAetherTickingTileEntity implements WorldlyContainer, IAutoContainerInventoryWrapper {
	
	private boolean on;
	private boolean aetherTick;
	
	private @Nonnull ItemStack stack = ItemStack.EMPTY;
	private @Nullable IAetherUnravelerRecipe recipe;
	private int workTicks;
	
	public AetherUnravelerBlockEntity(BlockPos pos, BlockState state, int aether, int maxAether) {
		super(AetheriaTileEntities.Unraveler, pos, state, aether, maxAether);
		
		this.setAutoSync(5);
		this.handler.configureInOut(true, false);
	}
	
	public AetherUnravelerBlockEntity(BlockPos pos, BlockState state) {
		this(pos, state, 0, 500);
	}
	
	public @Nullable ItemStack getItem() {
		return stack;
	}
	
	public void setItem(@Nonnull ItemStack stack) {
		Validate.notNull(stack);
		this.stack = stack;
		refreshRecipe();
		forceUpdate();
	}
	
	public @Nullable IAetherUnravelerRecipe getCurrentRecipe() {
		return recipe;
	}
	
	protected void setRecipe(@Nullable IAetherUnravelerRecipe recipe) {
		this.recipe = recipe;
	}
	
	public void refreshRecipe() {
		// Figure out recipe based on current item
		setRecipe(UnravelerRecipeManager.instance().findRecipe(stack));
	}
	
	private static final String NBT_ITEM = "item";
	private static final String NBT_WORK_TICKS = "work_ticks";
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		if (!stack.isEmpty()) {
			CompoundTag tag = new CompoundTag();
			tag = stack.save(tag);
			nbt.put(NBT_ITEM, tag);
		}
		
		if (workTicks > 0) {
			nbt.putInt(NBT_WORK_TICKS, workTicks);
		}
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		if (nbt == null)
			return;
			
		if (!nbt.contains(NBT_ITEM, Tag.TAG_COMPOUND)) {
			stack = ItemStack.EMPTY;
		} else {
			CompoundTag tag = nbt.getCompound(NBT_ITEM);
			stack = ItemStack.of(tag);
		}
		refreshRecipe();
		
		workTicks = nbt.getInt(NBT_WORK_TICKS); // defaults 0 :)
	}
	
	private void forceUpdate() {
		level.sendBlockUpdated(worldPosition, this.level.getBlockState(worldPosition), this.level.getBlockState(worldPosition), 3);
		setChanged();
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public ItemStack getItem(int index) {
		if (index > 0)
			return ItemStack.EMPTY;
		return this.stack;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (index > 0)
			return ItemStack.EMPTY;
		ItemStack ret = this.stack.split(count);
		if (this.stack.isEmpty())
			this.stack = ItemStack.EMPTY;
		this.forceUpdate();
		return ret;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if (index > 0)
			return ItemStack.EMPTY;
		ItemStack ret = this.stack;
		this.stack = ItemStack.EMPTY;
		refreshRecipe();
		forceUpdate();
		return ret;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (index > 0)
			return;
		this.stack = stack;
		refreshRecipe();
		forceUpdate();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void startOpen(Player player) {
		;
	}

	@Override
	public void stopOpen(Player player) {
		;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		if (index != 0) {
			return false;
		}
		
		if (stack.isEmpty()) {
			return true;
		}
		
		return UnravelerRecipeManager.instance().findRecipe(stack) != null;
	}

	public int getMaxTicks() {
		if (this.recipe == null) {
			return 0;
		}
		
		return this.recipe.getDuration(stack);
	}
	
	public int getTotalAetherCost() {
		if (this.recipe == null) {
			return 0;
		}
		
		return this.recipe.getAetherCost(stack);
	}
	
	@Override
	public int getField(int id) {
		if (id == 0) {
			return this.handler.getAether(null);
		} else if (id == 1) {
			if (stack.isEmpty() || recipe == null) {
				return 0;
			}
			
			return (int) Math.round(((float) this.workTicks * 100f) / (float) getMaxTicks());
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0) {
			this.handler.setAether(value);
		} else if (id == 1) {
			if (value == 0 || stack.isEmpty() || recipe == null) {
				workTicks = 0;
			} else {
				this.workTicks = (int) Math.round(((float) value * (float) getMaxTicks()) / 100);
			}
		}
	}

	@Override
	public int getFieldCount() {
		return 2;
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		super.onAetherFlowTick(diff, added, taken);
		aetherTick = (stack != null);
	}
	
	@Override
	public void clearContent() {
		this.stack = ItemStack.EMPTY;
		refreshRecipe();
		forceUpdate();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		if (index != 0 || !this.canPlaceItem(0, itemStackIn))
			return false;
		
		return stack.isEmpty();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return false;
	}
	
	protected void processItem(ItemStack stack) {
		if (recipe == null) {
			return;
		}
		
		NonNullList<ItemStack> items = recipe.unravel(stack);
		if (items != null && items.size() > 0) {
			for (ItemStack item : items) {
				ItemEntity ent = new ItemEntity(level, worldPosition.getX() + .5, worldPosition.getY() + 1.2, worldPosition.getZ() + .5, item);
				level.addFreshEntity(ent);
			}
		}
		
		// Effects!
		double x = worldPosition.getX() + .5;
		double y = worldPosition.getY() + 1.2;
		double z = worldPosition.getZ() + .5;
		((ServerLevel) level).sendParticles(ParticleTypes.CRIT,
				x,
				y,
				z,
				15,
				.25,
				.6,
				.25,
				.1
				);
		//world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, null
		level.playSound(null, worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1f, 1f);
	}
	
	@Override
	public void tick() {
		// If we have an item, work and break it down
		if (!level.isClientSide) {
			if (!stack.isEmpty() && recipe != null) {
				boolean worked = true;
				final int aetherPerTick = getTotalAetherCost() / getMaxTicks();
				final int drawn = handler.drawAether(null, aetherPerTick);
				if (drawn == 0) {
					worked = false;
				} else if (drawn < aetherPerTick) {
					// Not enough for a full tick. Use probability instead!
					final float chance = (float) drawn / (float) aetherPerTick;
					worked = NostrumAetheria.random.nextFloat() < chance;
				} else {
					worked = true;
				}
				
				if (worked) {
					// Had and took aether. Advance
					workTicks++;
					if (workTicks > getMaxTicks()) {
						workTicks = 0;
						processItem(stack);
						stack.shrink(1);
						if (stack.isEmpty()) {
							this.setItem(ItemStack.EMPTY); // dirties and updates
						} else {
							this.forceUpdate();
						}
					}
				} else {
					// Wanted aether but didn't get it. Lose progress
					workTicks = Math.max(0, workTicks - 1);
				}
				
				aetherTick = (drawn > 0); // worked; // Always appear lit up even if not enough aether to really go?
			} else {
				// Reset progress, if any
				final boolean hadProgress = workTicks != 0;
				workTicks = 0;
				aetherTick = false;
				if (hadProgress) {
					this.setChanged();
				}
			}
			
			if (aetherTick != on) {
				level.setBlockAndUpdate(worldPosition, AetheriaBlocks.unraveler.defaultBlockState().setValue(AetherUnravelerBlock.ON, aetherTick));
			}
			
			on = aetherTick;
			aetherTick = false;
		}
			
		super.tick();
	}
	
	@Override
	public void setLevel(Level world) {
		super.setLevel(world);
		
		if (!world.isClientSide) {
			this.handler.setAutoFill(true);
		}
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}
}