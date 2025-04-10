package com.smanzana.nostrumaetheria.tiles;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.api.capability.IAetherBurnable;
import com.smanzana.nostrumaetheria.client.gui.container.IAutoContainerInventoryWrapper;
import com.smanzana.nostrummagica.util.Inventories;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public abstract class AetherFurnaceGenericTileEntity extends NativeAetherTickingTileEntity implements Container, IAutoContainerInventoryWrapper {

	private static final String NBT_INVENTORY_SLOTS = "slots";
	private static final String NBT_INVENTORY = "inventory";
	private static final String NBT_TICKS_MAX = "cur_duration_max";
	private static final String NBT_TICKS_LEFT = "cur_duration";
	private static final String NBT_AETHER_PER = "cur_aether_per";
	private static final String NBT_AETHER_CARRY = "cur_aether_carry";
	
	private float aetherPerTick;
	private int burnTicksRemaining;
	private int burnTicksMax;
	
	private NonNullList<ItemStack> slots;
	private boolean burning;
	private float aetherCarry; // for handling floating point in an int world
	
	public AetherFurnaceGenericTileEntity(BlockEntityType<? extends AetherFurnaceGenericTileEntity> type, BlockPos pos, BlockState state, int slotCount, int defaultAether, int defaultMaxAether) {
		super(type, pos, state, defaultAether, defaultMaxAether);
		
		this.setAutoSync(5);
		this.handler.configureInOut(false, true);
		
		this.initInventory(slotCount);
	}
	
	public float getBurnProgress() {
		return (burnTicksMax > 0 ? (float) burnTicksRemaining / (float) burnTicksMax : 0);
	}
	
	/**
	 * Checks whether this furnace's slots are filled with unique reagents that can be consumed.
	 * Takes an optional reagent that is being tested being added. If present, will check if it _would_
	 * be valid given that type.
	 * allowEmpty allows there to be empty slots as long as all are unique.
	 */
	protected boolean allReagentsValid(ItemStack reagent, boolean allowEmpty) {
		Set<Item> seen = new HashSet<>();
		
		
		if (reagent != null && !reagent.isEmpty()) {
			seen.add(reagent.getItem());
		}
		
		boolean success = true;
		for (int i = 0; i < getContainerSize(); i++) {
			@Nonnull ItemStack stack = slots.get(i);
			if (stack.isEmpty()) {
				if (!allowEmpty) {
					success = false;
					break;
				}
				
				continue;
			}
			
			if (!seen.add(stack.getItem())) {
				success = false;
				break;
			}
		}
		
		return success;
	}
	
	protected abstract float getAetherMultiplier();
	protected abstract float getDurationMultiplier();
	
	protected void consumeReagentStack() {
		aetherPerTick = 0;
		burnTicksMax = 0;
		float totalAether = 0;
		for (int i = 0; i < getContainerSize(); i++) {
			ItemStack stack = this.removeItem(i, 1);
			LazyOptional<IAetherBurnable> cap = stack.getCapability(IAetherBurnable.CAPABILITY);
			
			totalAether += cap.map(burn -> burn.getAetherYield()).orElse(0f);
			burnTicksMax += cap.map(burn -> burn.getBurnTicks()).orElse(0);
		}
		
		// Add multipliers
		totalAether *= getAetherMultiplier() * getDurationMultiplier(); // add duration here to actually increase aether generated
		burnTicksMax *= getDurationMultiplier();
		
		this.burnTicksRemaining = burnTicksMax;
		this.aetherPerTick = totalAether / (float) burnTicksMax;
	}
	
	/**
	 * Attempts to consume reagents from the inventory (or eat up burn ticks). Returns true if
	 * there is fuel that was consumed and the furnace is still powered.
	 * @return
	 */
	protected boolean consumeTick() {
		if (burnTicksRemaining > 0) {
			burnTicksRemaining--;
			this.setChanged();
			return true;
		} else if (allReagentsValid(null, false)) {
			consumeReagentStack();
			this.setChanged();
			return true;
		}
		
		return false;
	}
	
	protected void generateAether() {
		this.aetherCarry += aetherPerTick;
		int whole = (int) aetherCarry;
		
		this.handler.addAether(null, whole, true); // 'force' to disable having aether added by others but force ourselves.
		this.aetherCarry -= whole;
	}
	
	protected boolean shouldTryBurn() {
		return handler.getAether(null) < handler.getMaxAether(null);
	}
	
	protected abstract void onBurningChange(boolean newBurning);
	
	@Override
	public void tick() {
		if (!level.isClientSide) {
			this.handler.pushAether(500);
			if (shouldTryBurn() && consumeTick()) {
				generateAether();
				
				if (!burning) {
					onBurningChange(true);
					burning = true;
				}
			} else {
				if (burning) {
					onBurningChange(false);
					burning = false;
				}
			}
		}
		super.tick();
	}
	
	@Override
	public int getContainerSize() {
		return slots.size();
	}
	
	private void initInventory(int count) {
		slots = NonNullList.withSize(count, ItemStack.EMPTY);
	}
	
	@Override
	public CompoundTag save(CompoundTag nbt) {
		nbt = super.save(nbt);
		
		nbt.putInt(NBT_INVENTORY_SLOTS, slots.size());
		nbt.put(NBT_INVENTORY, Inventories.serializeInventory(this));
		nbt.putFloat(NBT_AETHER_CARRY, this.aetherCarry);
		nbt.putFloat(NBT_AETHER_PER, this.aetherPerTick);
		nbt.putInt(NBT_TICKS_MAX, burnTicksMax);
		nbt.putInt(NBT_TICKS_LEFT, burnTicksRemaining);
		
		return nbt;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		int slotCount = nbt.getInt(NBT_INVENTORY_SLOTS);
		if (slotCount <= 0) {
			slotCount = 1;
		}
		initInventory(slotCount);
		
		Inventories.deserializeInventory(this, nbt.getList(NBT_INVENTORY, Tag.TAG_COMPOUND));
		this.aetherCarry = nbt.getFloat(NBT_AETHER_CARRY);
		this.aetherPerTick = nbt.getFloat(NBT_AETHER_PER);
		this.burnTicksMax = nbt.getInt(NBT_TICKS_MAX);
		this.burnTicksRemaining = nbt.getInt(NBT_TICKS_LEFT);
	}
	
	@Override
	public ItemStack getItem(int index) {
		if (index < 0 || index >= getContainerSize())
			return ItemStack.EMPTY;
		
		return slots.get(index);
	}
	
	@Override
	public ItemStack removeItem(int index, int count) {
		if (index < 0 || index >= getContainerSize() || slots.get(index).isEmpty())
			return ItemStack.EMPTY;
		
		ItemStack stack;
		if (slots.get(index).getCount() <= count) {
			stack = slots.get(index);
			slots.set(index, ItemStack.EMPTY);
		} else {
			stack = slots.get(index).copy();
			stack.setCount(count);
			slots.get(index).shrink(count);
		}
		
		this.setChanged();
		
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if (index < 0 || index >= getContainerSize())
			return ItemStack.EMPTY;
		
		ItemStack stack = slots.get(index);
		slots.set(index, ItemStack.EMPTY);
		
		this.setChanged();
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (!canPlaceItem(index, stack))
			return;
		
		slots.set(index, stack);
		this.setChanged();
	}
	
	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void startOpen(Player player) {
	}

	@Override
	public void stopOpen(Player player) {
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		if (index < 0 || index >= getContainerSize())
			return false;
		
		if (!stack.isEmpty() && !stack.getCapability(IAetherBurnable.CAPABILITY).isPresent()) {
			return false;
		}
		
		ItemStack inSlot = this.getItem(index);
		if (inSlot.isEmpty()) {
			if (!allReagentsValid(stack, true)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int getField(int id) {
		if (id == 0) {
			return burnTicksRemaining;
		}
		if (id == 1) {
			return burnTicksMax;
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0) {
			burnTicksRemaining = value;
		} else if (id == 1) {
			burnTicksMax = value;
		}
		
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < getContainerSize(); i++) {
			removeItemNoUpdate(i);
		}
	}
	
	@Override
	public boolean isEmpty() {
		for (ItemStack stack : slots) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
}
