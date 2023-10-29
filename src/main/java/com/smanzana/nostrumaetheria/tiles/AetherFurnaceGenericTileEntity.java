package com.smanzana.nostrumaetheria.tiles;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.api.item.IAetherBurnable;
import com.smanzana.nostrummagica.utils.Inventories;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public abstract class AetherFurnaceGenericTileEntity extends NativeAetherTickingTileEntity implements IInventory {

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
	
	public AetherFurnaceGenericTileEntity(int slotCount, int defaultAether, int defaultMaxAether) {
		super(defaultAether, defaultMaxAether);
		
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
		Set<ReagentType> seen = new HashSet<>();
		
		
		if (reagent != null) {
			seen.add(ReagentType.getAndSet(reagent));
		}
		
		boolean success = true;
		for (int i = 0; i < getSizeInventory(); i++) {
			@Nonnull ItemStack stack = slots.get(i);
			if (stack.isEmpty()) {
				if (!allowEmpty) {
					success = false;
					break;
				}
				
				continue;
			}
			
			ReagentType reagentType = ReagentType.getAndSet(stack);
			if (!seen.add(reagentType)) {
				success = false;
				break;
			}
		}
		
		for (ReagentType type : seen) {
			ReagentType.release(type);
		}
		
		return success;
	}
	
	protected abstract float getAetherMultiplier();
	protected abstract float getDurationMultiplier();
	
	protected void consumeReagentStack() {
		aetherPerTick = 0;
		burnTicksMax = 0;
		float totalAether = 0;
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = this.decrStackSize(i, 1);
			if (stack.getItem() instanceof IAetherBurnable) {
				totalAether += ((IAetherBurnable) stack.getItem()).getAetherYield(stack);
				burnTicksMax += ((IAetherBurnable) stack.getItem()).getBurnTicks(stack);
			}
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
			this.markDirty();
			return true;
		} else if (allReagentsValid(null, false)) {
			consumeReagentStack();
			this.markDirty();
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
	public void update() {
		if (!world.isRemote) {
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
		super.update();
	}
	
	@Override
	public int getSizeInventory() {
		return slots.size();
	}
	
	private void initInventory(int count) {
		slots = NonNullList.withSize(count, ItemStack.EMPTY);
	}
	
	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt) {
		nbt = super.writeToNBT(nbt);
		
		nbt.putInt(NBT_INVENTORY_SLOTS, slots.size());
		nbt.setTag(NBT_INVENTORY, Inventories.serializeInventory(this));
		nbt.putFloat(NBT_AETHER_CARRY, this.aetherCarry);
		nbt.putFloat(NBT_AETHER_PER, this.aetherPerTick);
		nbt.putInt(NBT_TICKS_MAX, burnTicksMax);
		nbt.putInt(NBT_TICKS_LEFT, burnTicksRemaining);
		
		return nbt;
	}
	
	@Override
	public void readFromNBT(CompoundNBT nbt) {
		super.readFromNBT(nbt);
		
		int slotCount = nbt.getInt(NBT_INVENTORY_SLOTS);
		if (slotCount <= 0) {
			slotCount = 1;
		}
		initInventory(slotCount);
		
		Inventories.deserializeInventory(this, nbt.getTag(NBT_INVENTORY));
		this.aetherCarry = nbt.getFloat(NBT_AETHER_CARRY);
		this.aetherPerTick = nbt.getFloat(NBT_AETHER_PER);
		this.burnTicksMax = nbt.getInt(NBT_TICKS_MAX);
		this.burnTicksRemaining = nbt.getInt(NBT_TICKS_LEFT);
	}
	
	@Override
	public ItemStack getStackInSlot(int index) {
		if (index < 0 || index >= getSizeInventory())
			return ItemStack.EMPTY;
		
		return slots.get(index);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (index < 0 || index >= getSizeInventory() || slots.get(index).isEmpty())
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
		
		this.markDirty();
		
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (index < 0 || index >= getSizeInventory())
			return ItemStack.EMPTY;
		
		ItemStack stack = slots.get(index);
		slots.set(index, ItemStack.EMPTY);
		
		this.markDirty();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (!isItemValidForSlot(index, stack))
			return;
		
		slots.set(index, stack);
		this.markDirty();
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {
	}

	@Override
	public void closeInventory(PlayerEntity player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (index < 0 || index >= getSizeInventory())
			return false;
		
		if (!stack.isEmpty() && (
				!(stack.getItem() instanceof IAetherBurnable) || ((IAetherBurnable) stack.getItem()).getBurnTicks(stack) <= 0
				)) {
			return false;
		}
		
		ItemStack inSlot = this.getStackInSlot(index);
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
	public void clear() {
		for (int i = 0; i < getSizeInventory(); i++) {
			removeStackFromSlot(i);
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

	private static final class ReagentType {
		private Item item;
		private int meta;
		
		private ReagentType() {
			;
		}
		
		public void set(Item item, int meta) {
			this.item = item;
			this.meta = meta;
		}
		
		@Override
		public boolean equals(Object o) {
			return o instanceof ReagentType && ((ReagentType) o).item == item && ((ReagentType) o).meta == meta;
		}
		
		@Override
		public int hashCode() {
			return item.hashCode() * 503 + meta * 71; 
		}
		
		private static List<ReagentType> Pool = new LinkedList<>();
		
		public static ReagentType get() {
			if (Pool.isEmpty()) {
				return new ReagentType();
			}
			
			return Pool.remove(0);
		}
		
		public static ReagentType getAndSet(Item item, int meta) {
			ReagentType type = get();
			type.set(item, meta);
			return type;
		}
		
		public static ReagentType getAndSet(ItemStack stack) {
			return getAndSet(stack.getItem(), stack.getMetadata());
		}
		
		public static void release(ReagentType type) {
			type.item = null;
			type.meta = 0;
			Pool.add(type);
		}
	}
}
