package com.smanzana.nostrumaetheria.tiles;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.client.gui.container.IAutoContainerInventoryWrapper;
import com.smanzana.nostrumaetheria.entity.AetheriaEntityTypes;
import com.smanzana.nostrumaetheria.entity.SentinelWispEntity;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.entity.WispEntity;
import com.smanzana.nostrummagica.item.ReagentItem;
import com.smanzana.nostrummagica.item.SpellScroll;
import com.smanzana.nostrummagica.util.Inventories;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WispBlockTileEntity extends AetherTickingTileEntity implements IAutoContainerInventoryWrapper {

	private static final String NBT_INVENTORY = "inventory";
	private static final String NBT_PARTIAL = "partial";
	
	// Synced+saved
	private @Nonnull ItemStack scroll;
	private @Nonnull ItemStack reagent;
	private float reagentPartial;
	private boolean activated;
	
	// Transient
	private List<WispEntity> wisps; // on server
	private int numWisps;
	
	// TODO add progression. Maybe insert essences or mani crystals?
	// TODO add some cool like mani crystal generation. That'd be neat :)
	// TODO part of progression: be able to spawn more wisps!
	private static final int MAX_WISPS = 3;
	private static final float REAGENT_PER_SECOND = (1f / 120f);  // 1 per 2 minutes
	
	private static final int MAX_AETHER = 5000;
	private static final int AETHER_PER_TICK = 2;
	
	private int ticksExisted;
	
	public WispBlockTileEntity(BlockPos pos, BlockState state) {
		super(AetheriaTileEntities.WispBlockEnt, pos, state, 0, MAX_AETHER);
		scroll = ItemStack.EMPTY;
		reagent = ItemStack.EMPTY;
		reagentPartial = 0f;
		wisps = new LinkedList<>();
		ticksExisted = 0;
		activated = false;
		this.setAutoSync(5);
		this.compWrapper.configureInOut(true, false);
	}
	
	public ItemStack getScroll() {
		return scroll;
	}
	
	public boolean setScroll(ItemStack item) {
		if (!item.isEmpty() && !this.scroll.isEmpty())
			return false;
		
		if (!canPlaceItem(0, item)) {
			return false;
		}
		
		this.setItem(0, item);
		return true;
	}
	
	public ItemStack getReagent() {
		return reagent;
	}
	
	public boolean setReagent(ItemStack item) {
		if (!item.isEmpty() && !this.reagent.isEmpty())
			return false;
		
		if (!canPlaceItem(1, item)) {
			return false;
		}
		
		this.setItem(1, item);
		return true;
	}
	
	public float getPartialReagent() {
		return reagentPartial;
	}
	
	public int getWispCount() {
		return this.level.isClientSide ? this.numWisps : this.wisps.size();
	}
	
	public int getMaxWisps() {
		return MAX_WISPS;
	}
	
	private void dirtyAndUpdate() {
		if (level != null) {
			level.sendBlockUpdated(worldPosition, this.level.getBlockState(worldPosition), this.level.getBlockState(worldPosition), 3);
			setChanged();
		}
	}
	
	// Cleans up any wisps as soon as we deactivate
	public void deactivate() {
		this.activated = false;
		
		for (WispEntity wisp : this.wisps) {
			wisp.discard();
		}
		wisps.clear();
		
		dirtyAndUpdate();
	}
	
	private void activate() {
		this.activated = true;
		dirtyAndUpdate();
	}
	
	private void spawnWisp() {
		
		BlockPos spawnPos = null;
		
		// Try to find a safe place to spawn the wisp
		int attempts = 20;
		do {
			spawnPos = this.worldPosition.offset(
					NostrumMagica.rand.nextInt(10) - 5,
					NostrumMagica.rand.nextInt(5),
					NostrumMagica.rand.nextInt(10) - 5);
		} while (!level.isEmptyBlock(spawnPos) && attempts-- >= 0);
		
		if (level.isEmptyBlock(spawnPos)) {
			WispEntity wisp = new SentinelWispEntity(AetheriaEntityTypes.sentinelWisp, this.level, this.worldPosition);
			wisp.setPos(spawnPos.getX() + .5, spawnPos.getY(), spawnPos.getZ() + .5);
			this.wisps.add(wisp);
			this.level.addFreshEntity(wisp);
			//this.dirtyAndUpdate();
		}
	}

	@Override
	public void tick() {
		super.tick();
		ticksExisted++;
		
		if (level.isClientSide) {
			return;
		}
		
		Iterator<WispEntity> it = wisps.iterator();
		while (it.hasNext()) {
			WispEntity wisp = it.next();
			if (!wisp.isAlive()) {
				it.remove();
				//this.dirtyAndUpdate();
			}
		}
		
		if (!activated) {
			if (!this.getScroll().isEmpty()
					&& (!this.getReagent().isEmpty() || this.reagentPartial >= REAGENT_PER_SECOND)
					/*&& (this.getOnlyMyAether(null) > AETHER_PER_TICK)*/) {
				activate();
			} else {
				return;
			}
		}
		
		// If no scroll is present, deactivate
		if (this.getScroll().isEmpty()) {
			deactivate();
			return;
		}
		
		// Passively burn reagents. If there are none, kill all wisps and deactivate
		if (ticksExisted % 20 == 0 && !wisps.isEmpty()) {
			float debt = REAGENT_PER_SECOND;
			if (reagentPartial < debt) {
				// Not enough partial. Try to consume from reagent stack.
				// If not there, next bit of logic will turn reagentPartial negative and
				// know to deactivate
				if (getReagent() != null && getReagent().getCount() > 0) {
					reagentPartial += 1f;
					if (getReagent().getCount() > 1) {
						getReagent().shrink(1);
					} else {
						setReagent(ItemStack.EMPTY);
					}
				}
			}
			
			// Regardless of if we have enough, subtract debt
			reagentPartial -= debt;
			
			// If negative, we didn't have enough to run for another tick! Deactivate!
			if (reagentPartial < 0) {
				deactivate();
			} else {
				// Update client
				//this.dirtyAndUpdate();
			}
		}
		
		// Every tick, consume aether
		if (!wisps.isEmpty()) {
			final int debt = AETHER_PER_TICK * getWispCount();
			if (this.compWrapper.getHandlerIfPresent().drawAether(null, debt) != debt) {
				// Didn't have enough. Deactivate!
				deactivate();
			} else {
				// Try to fill up what we just spent
				this.compWrapper.getHandlerIfPresent().fillAether(1000);
			}
		}
		
		if (!activated) {
			return;
		}
		
		// If not at max wisps, maybe spawn one every once in a while
		if (ticksExisted % (20 * 3) == 0 && wisps.size() < getMaxWisps()) {
			if (NostrumMagica.rand.nextInt(10) == 0) {
				spawnWisp();
			}
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		if (nbt == null)
			nbt = new CompoundTag();
		
		if (reagentPartial != 0f)
			nbt.putFloat(NBT_PARTIAL, reagentPartial);
		
		nbt.put(NBT_INVENTORY, Inventories.serializeInventory(this));
		
//		if (scroll != null)
//			nbt.put("scroll", scroll.serializeNBT());
//		
//		if (reagent != null)
//			nbt.put("reagent", reagent.serializeNBT());
//		
//		
//		
//		if (activated) {
//			nbt.putBoolean("active", activated);
//			nbt.putInt("wisps", wisps.size());
//		}
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		if (nbt == null)
			return;
		
		Inventories.deserializeInventory(this, nbt.get(NBT_INVENTORY));
		this.reagentPartial = nbt.getFloat(NBT_PARTIAL);
		
//		this.scroll = ItemStack.loadItemStackFromNBT(nbt.getCompound("scroll"));
//		this.reagent = ItemStack.loadItemStackFromNBT(nbt.getCompound("reagent"));
//		this.activated = nbt.getBoolean("active");
//		this.numWisps = nbt.getInt("wisps");
	}
	
	@Override
	public void setLevel(Level world) {
		super.setLevel(world);
		
		if (!world.isClientSide) {
			this.compWrapper.setAutoFill(true);
		}
	}
	
	@Override
	public int getContainerSize() {
		return 2;
	}
	
	@Override
	public ItemStack getItem(int index) {
		if (index < 0 || index >= getContainerSize())
			return ItemStack.EMPTY;
		
		if (index == 0) {
			return scroll;
		} else {
			return reagent;
		}
	}
	
	@Override
	public ItemStack removeItem(int index, int count) {
		if (index < 0 || index >= getContainerSize()) {
			return ItemStack.EMPTY;
		}
		
		ItemStack inSlot = getItem(index);
		if (inSlot.isEmpty()) {
			return ItemStack.EMPTY;
		}
		
		ItemStack stack;
		if (inSlot.getCount() <= count) {
			stack = inSlot;
			inSlot = ItemStack.EMPTY;
		} else {
			stack = inSlot.copy();
			stack.setCount(count);
			inSlot.shrink(count);
		}
		
		if (inSlot.isEmpty()) {
			setItem(index, inSlot);
		}
		
		this.dirtyAndUpdate();
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if (index < 0 || index >= getContainerSize())
			return ItemStack.EMPTY;
		
		ItemStack stack;
		if (index == 0) {
			stack = scroll;
			scroll = ItemStack.EMPTY;
		} else {
			stack = reagent;
			reagent = ItemStack.EMPTY;
		}
		
		this.dirtyAndUpdate();
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (!canPlaceItem(index, stack))
			return;
		
		if (index == 0) {
			scroll = stack;
		} else {
			reagent = stack;
		}
		
		this.dirtyAndUpdate();
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
		
		if (index == 0) {
			return (stack.isEmpty() || (stack.getItem() instanceof SpellScroll && SpellScroll.GetSpell(stack) != null));
		} else {
			return (stack.isEmpty() || stack.getItem() instanceof ReagentItem);
		}
		
	}
	
	private static final int partialToInt(float progress) {
		return Math.round(progress * 10000);
	}
	
	private static final float intToPartial(int value) {
		return (float) value / 10000f;
	}

	@Override
	public int getField(int id) {
		if (id == 0) {
			return partialToInt(this.reagentPartial);
		} else if (id == 1) {
			return this.activated ? 1 : 0;
		} else if (id == 2) {
			return level.isClientSide ? numWisps : wisps.size();
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0) {
			this.reagentPartial = intToPartial(value);
		} else if (id == 1) {
			this.activated = (value != 0);
		} else if (id == 2) {
			this.numWisps = value;
		}
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < getContainerSize(); i++) {
			removeItemNoUpdate(i);
		}
	}

	@Override
	public boolean isEmpty() {
		return !scroll.isEmpty() || !reagent.isEmpty();
	}
}
