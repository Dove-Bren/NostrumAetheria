package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerItem;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerProvider;
import com.smanzana.nostrumaetheria.api.item.AetherItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AetherBathTileEntity extends NativeAetherTickingTileEntity implements WorldlyContainer {
	
	private ItemStack stack = ItemStack.EMPTY;
	
	public AetherBathTileEntity(BlockEntityType<? extends AetherBathTileEntity> type, BlockPos pos, BlockState state, int aether, int maxAether) {
		super(type, pos, state, aether, maxAether);
		
		this.setAutoSync(5);
		this.handler.configureInOut(true, false);
	}
	
	public AetherBathTileEntity(BlockPos pos, BlockState state) {
		this(AetheriaTileEntities.Bath, pos, state, 0, 250);
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
		
		// We specifically want aether handlers
		return (stack.getItem() instanceof IAetherHandler
				|| stack.getItem() instanceof IAetherHandlerProvider
				|| stack.getItem() instanceof IAetherHandlerItem);
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
		if (!level.isClientSide) {
			
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
	public void setLevel(Level world) {
		super.setLevel(world);
		
		if (!world.isClientSide) {
			this.handler.setAutoFill(true);
		}
	}

	@Override
	public boolean isEmpty() {
		return this.stack.isEmpty();
	}
}