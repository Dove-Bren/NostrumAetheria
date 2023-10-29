package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerItem;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerProvider;
import com.smanzana.nostrumaetheria.api.item.AetherItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class AetherBathTileEntity extends NativeAetherTickingTileEntity implements ISidedInventory {
	
	private ItemStack stack = ItemStack.EMPTY;
	
	public AetherBathTileEntity(TileEntityType<? extends AetherBathTileEntity> type, int aether, int maxAether) {
		super(type, aether, maxAether);
		
		this.setAutoSync(5);
		this.handler.configureInOut(true, false);
	}
	
	public AetherBathTileEntity() {
		this(AetheriaTileEntities.Bath, 0, 250);
	}
	
	public @Nonnull ItemStack getItem() {
		return stack;
	}
	
	public void setItem(ItemStack stack) {
		Validate.notNull(stack);
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
	public void read(CompoundNBT nbt) {
		super.read(nbt);
		
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
		
		// We specifically want aether handlers
		return (stack.getItem() instanceof IAetherHandler
				|| stack.getItem() instanceof IAetherHandlerProvider
				|| stack.getItem() instanceof IAetherHandlerItem);
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
		return index == 0 && direction == Direction.DOWN && !stack.isEmpty() && heldItemFull();
	}
	
	public @Nullable IAetherHandler getHeldHandler() {
		if (stack.isEmpty()) {
			return null;
		}
		
		if (stack.getItem() instanceof IAetherHandler) {
			return (IAetherHandler) stack.getItem();
		}
		
		if (stack.getItem() instanceof IAetherHandlerProvider) {
			return ((IAetherHandlerProvider) stack.getItem()).getHandler();
		}
		
		if (stack.getItem() instanceof IAetherHandlerItem) {
			return ((IAetherHandlerItem) stack.getItem()).getAetherHandler(stack);
		}
		
		// How??
		return null;
	}
	
	public boolean heldItemFull() {
		IAetherHandler handler = getHeldHandler();
		return handler == null || handler.getAether(null) >= handler.getMaxAether(null);
	}
	
	protected int maxAetherPerTick() {
		return 1;
	}
	
	@Override
	public void tick() {
		// If we have an item, try to add aether to it
		if (!world.isRemote) {
			
			if (!stack.isEmpty() && stack.getItem() instanceof AetherItem) {
				// Pretty dumb thing I'm doing here for these special items.
				AetherItem aetherItem = (AetherItem) stack.getItem();
				int start = Math.min(maxAetherPerTick(), handler.getAether(null));
				int leftover = aetherItem.addAether(stack, start);
				if (start != leftover) {
					handler.drawAether(null, start - leftover);
					AetherItem.SaveItem(stack); // Maybe should call every couple of ticks?
				}
			} else {
				IAetherHandler handler = getHeldHandler();
				if (handler != null) {
					int start = Math.min(maxAetherPerTick(), this.handler.getAether(null));
					int leftover = handler.addAether(null, start);
					if (start != leftover) {
						this.handler.drawAether(null, start - leftover);
						AetherItem.SaveItem(stack); // Maybe should call every couple of ticks?
					}
				}
			}
		}
		
		super.tick();
	}
	
	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		
		if (!world.isRemote) {
			this.handler.setAutoFill(true);
		}
	}

	@Override
	public boolean isEmpty() {
		return this.stack.isEmpty();
	}
}