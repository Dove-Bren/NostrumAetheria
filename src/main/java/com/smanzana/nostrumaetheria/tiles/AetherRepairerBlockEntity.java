package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;
import com.smanzana.nostrumaetheria.blocks.AetherRepairerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.client.gui.container.IAutoContainerInventoryWrapper;
import com.smanzana.nostrumaetheria.recipes.RepairerRecipeManager;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class AetherRepairerBlockEntity extends NativeAetherTickingTileEntity implements ISidedInventory, IAutoContainerInventoryWrapper {
	
	private boolean on;
	private boolean aetherTick;
	
	private @Nonnull ItemStack stack = ItemStack.EMPTY;
	
	public AetherRepairerBlockEntity(int aether, int maxAether) {
		super(AetheriaTileEntities.Repairer, aether, maxAether);
		
		this.setAutoSync(5);
		this.handler.configureInOut(true, false);
	}
	
	public AetherRepairerBlockEntity() {
		this(0, 500);
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
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		
		if (!stack.isEmpty()) {
			CompoundNBT tag = new CompoundNBT();
			tag = stack.write(tag);
			nbt.put(NBT_ITEM, tag);
		}
		
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
		if (nbt == null)
			return;
			
		if (!nbt.contains(NBT_ITEM, NBT.TAG_COMPOUND)) {
			stack = ItemStack.EMPTY;
		} else {
			CompoundNBT tag = nbt.getCompound(NBT_ITEM);
			stack = ItemStack.read(tag);
		}
	}
	
	private void forceUpdate() {
		world.notifyBlockUpdate(pos, this.world.getBlockState(pos), this.world.getBlockState(pos), 3);
		markDirty();
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index > 0)
			return ItemStack.EMPTY;
		return this.stack;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (index > 0)
			return ItemStack.EMPTY;
		ItemStack ret = this.stack.split(count);
		if (this.stack.isEmpty())
			this.stack = ItemStack.EMPTY;
		this.forceUpdate();
		return ret;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (index > 0)
			return ItemStack.EMPTY;
		ItemStack ret = this.stack;
		this.stack = ItemStack.EMPTY;
		forceUpdate();
		return ret;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index > 0)
			return;
		this.stack = stack;
		forceUpdate();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		;
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
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
	public void clear() {
		this.stack = ItemStack.EMPTY;
		forceUpdate();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		if (index != 0 || direction == Direction.DOWN || !this.isItemValidForSlot(0, itemStackIn))
			return false;
		
		return stack.isEmpty();
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 0
				&& direction == Direction.DOWN
				&& !stack.isEmpty()
				&& null == RepairerRecipeManager.instance().findRecipe(stack);
	}
	
	@Override
	public void tick() {
		// If we have an item, try to repair it
		if (!world.isRemote && this.ticksExisted % 20 == 0) {
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
				world.setBlockState(pos, AetheriaBlocks.repairer.getDefaultState().with(AetherRepairerBlock.ON, aetherTick));
			}
			
			on = aetherTick;
			aetherTick = false;
		}
			
		super.tick();
	}
	
	@Override
	public void setWorldAndPos(World world, BlockPos pos) {
		super.setWorldAndPos(world, pos);
		
		if (!world.isRemote) {
			this.handler.setAutoFill(true);
		}
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}
}