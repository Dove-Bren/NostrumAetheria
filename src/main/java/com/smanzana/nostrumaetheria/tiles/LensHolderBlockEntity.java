package com.smanzana.nostrumaetheria.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.smanzana.nostrumaetheria.api.blocks.IAetherInfusableTileEntity;
import com.smanzana.nostrumaetheria.api.blocks.IAetherInfuserTileEntity;
import com.smanzana.nostrumaetheria.api.item.IAetherInfuserLens;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LensHolderBlockEntity extends BlockEntity implements WorldlyContainer, IAetherInfusableTileEntity {
	
	private ItemStack stack = ItemStack.EMPTY;
	
	public LensHolderBlockEntity(BlockEntityType<? extends LensHolderBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public LensHolderBlockEntity(BlockPos pos, BlockState state) {
		this(AetheriaTileEntities.LensHolder, pos, state);
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
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithId();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		//handleUpdateTag(pkt.getTag());
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
		return (stack.getItem() instanceof IAetherInfuserLens);
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
		return index == 0 && direction == Direction.DOWN && !stack.isEmpty();
	}
	
	@Override
	public boolean isEmpty() {
		return this.stack.isEmpty();
	}
	
	protected @Nullable IAetherInfuserLens getLens() {
		if (!this.stack.isEmpty() && this.stack.getItem() instanceof IAetherInfuserLens lens) {
			return lens;
		}
		return null;
	}

	@Override
	public boolean canAcceptAether(IAetherInfuserTileEntity source, BlockPos pos, int maxAether) {
		return !isEmpty() && getLens().canAcceptAetherInfuse(stack, pos, source, maxAether);
	}

	@Override
	public int acceptAether(IAetherInfuserTileEntity source, BlockPos pos, int maxAether) {
		return getLens().acceptAetherInfuse(stack, pos, source, maxAether);
	}
}