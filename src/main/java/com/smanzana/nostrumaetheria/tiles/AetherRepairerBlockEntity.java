package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;
import com.smanzana.nostrumaetheria.blocks.AetherRepairerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.client.gui.container.IAutoContainerInventoryWrapper;
import com.smanzana.nostrumaetheria.recipes.RepairerRecipeManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AetherRepairerBlockEntity extends NativeAetherTickingTileEntity implements WorldlyContainer, IAutoContainerInventoryWrapper {
	
	private boolean on;
	private boolean aetherTick;
	
	private @Nonnull ItemStack stack = ItemStack.EMPTY;
	
	public AetherRepairerBlockEntity(BlockPos pos, BlockState state, int aether, int maxAether) {
		super(AetheriaTileEntities.Repairer, pos, state, aether, maxAether);
		
		this.setAutoSync(5);
		this.handler.configureInOut(true, false);
	}
	
	public AetherRepairerBlockEntity(BlockPos pos, BlockState state) {
		this(pos, state, 0, 500);
	}
	
	public ItemStack getItem() {
		return stack;
	}
	
	public void setItem(ItemStack stack) {
		this.stack = stack;
		forceUpdate();
	}
	
	private static final String NBT_ITEM = "item";
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		if (!stack.isEmpty()) {
			CompoundTag tag = new CompoundTag();
			tag = stack.save(tag);
			nbt.put(NBT_ITEM, tag);
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
		forceUpdate();
		return ret;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (index > 0)
			return;
		this.stack = stack;
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
		
		@Nullable IAetherRepairerRecipe recipe = RepairerRecipeManager.instance().findRecipe(stack);
		return recipe != null;
	}

	@Override
	public int getField(int id) {
		if (id == 0) {
			return this.handler.getAether(null);
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0) {
			this.handler.setAether(value);
		}
	}

	@Override
	public int getFieldCount() {
		return 1;
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		super.onAetherFlowTick(diff, added, taken);
		
		@Nullable IAetherRepairerRecipe recipe = RepairerRecipeManager.instance().findRecipe(stack);
		aetherTick = recipe != null;
	}
	
	@Override
	public void clearContent() {
		this.stack = ItemStack.EMPTY;
		forceUpdate();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		if (index != 0 || direction == Direction.DOWN || !this.canPlaceItem(0, itemStackIn))
			return false;
		
		return stack.isEmpty();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index == 0
				&& direction == Direction.DOWN
				&& !stack.isEmpty()
				&& null == RepairerRecipeManager.instance().findRecipe(stack);
	}
	
	@Override
	public void tick() {
		// If we have an item, try to repair it
		if (!level.isClientSide && this.ticksExisted % 20 == 0) {
			if (!stack.isEmpty()) {
				@Nullable IAetherRepairerRecipe recipe = RepairerRecipeManager.instance().findRecipe(stack);
				if (recipe != null) {
					final int aetherCost = recipe.getAetherCost(stack);
					if (handler.getAether(null) >= aetherCost) {
						recipe.repair(stack);
						handler.drawAether(null, aetherCost);
						aetherTick = true;
					}
				}
			}
			
			if (aetherTick != on) {
				level.setBlockAndUpdate(worldPosition, AetheriaBlocks.repairer.defaultBlockState().setValue(AetherRepairerBlock.ON, aetherTick));
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