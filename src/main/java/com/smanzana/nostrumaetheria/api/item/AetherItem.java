package com.smanzana.nostrumaetheria.api.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler.AetherFlowConnection;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerItem;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.event.LivingAetherDrawEvent;
import com.smanzana.nostrumaetheria.api.event.LivingAetherDrawEvent.Phase;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

public abstract class AetherItem extends Item implements IAetherHandlerItem {

	private static final String NBT_AETHER_HANDLER = "aether_handler";
	private static final String NBT_CLIENT_DIRTY = "aether_client_dirty"; // always true on server, set to false on client
	private static final String NBT_PENDANT_ID = "id";
	
	public AetherItem(Item.Properties builder) {
		super(builder);
	}
	
	protected abstract int getDefaultMaxAether(ItemStack stack);
	
	protected abstract boolean shouldShowAether(@Nonnull ItemStack stack, Player playerIn, boolean advanced);
    
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (shouldShowAether(stack, APIProxy.getClientPlayer(), flagIn.isAdvanced())) {
			IAetherHandlerComponent comp = getAetherHandler(stack);
			int aether = comp.getAether(null);
			int maxAether = comp.getMaxAether(null);
			tooltip.add(new TranslatableComponent("item.info.aether", String.format("%.2f", (float) aether * .01f), String.format("%.2f", (float) maxAether * .01f))
					.withStyle(ChatFormatting.DARK_PURPLE));
		}
	}
	
	@Override
	public IAetherHandlerComponent getAetherHandler(@Nonnull ItemStack stack) {
		return GetOrCreateCachedHandlerItem(stack).component;
	}

	public int getAether(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		
		IAetherHandlerComponent comp = getAetherHandler(stack);
		int aether = comp.getAether(null);
		
		return aether;
	}
	
	public int deductAether(ItemStack stack, int amount) {
		if (stack.isEmpty()) {
			return 0;
		}
		
		IAetherHandlerComponent comp = getAetherHandler(stack);
		int taken = comp.drawAether(null, amount);
		commitHandler(stack);
		
		if (taken > 0) {
			this.onAetherChange(stack, comp.getAether(null));
		}
		
		return taken;
	}
	
	public int addAether(ItemStack stack, int amount) {
		if (stack.isEmpty()) {
			return amount;
		}
		
		IAetherHandlerComponent comp = getAetherHandler(stack);
		int leftover = comp.addAether(null, amount);
		commitHandler(stack);
		
		if (leftover != amount) {
			this.onAetherChange(stack, comp.getAether(null));
		}
		
		return leftover;
	}
	
	// Note: clears dirty flag, so update won't automatically pick up the change and call callbacks.
	// Anything that calls this should check and maybe call onAetherChange itself.
	private void commitHandler(ItemStack stack) {
		StackHandlerItem handlerItem = GetOrCreateCachedHandlerItem(stack);
		if (handlerItem.dirty) {
			CompoundTag nbt = stack.getTag();
			if (nbt == null) {
				nbt = new CompoundTag();
				stack.setTag(nbt);
			}
			
			nbt.put(NBT_AETHER_HANDLER, handlerItem.component.writeToNBT(new CompoundTag()));
			nbt.putBoolean(NBT_CLIENT_DIRTY, true);
			handlerItem.dirty = false;
		}
	}
	
	private static @Nullable StackHandlerItem GetCachedHandlerItem(UUID id) {
		return StackHandlerCache.get(id);
	}
	
	private static StackHandlerItem GetOrCreateCachedHandlerItem(ItemStack stack) {
		AetherItem type = (AetherItem) stack.getItem();
		UUID id = type.getItemID(stack);
		StackHandlerItem adapter = GetCachedHandlerItem(id);
		if (adapter == null) {
			adapter = new StackHandlerItem();
			final StackHandlerItem ref = adapter;
			adapter.component = APIProxy.createHandlerComponent(new IAetherComponentListener() {

				@Override
				public void dirty() {
					ref.dirty = true;
				}

				@Override
				public void addConnections(List<AetherFlowConnection> connections) {
					;
				}

				@Override
				public void onAetherFlowTick(int diff, boolean added, boolean taken) {
					ref.dirty = true;
				}
				
			}, 0, type.getDefaultMaxAether(stack));
			
			if (stack.hasTag() && stack.getTag().contains(NBT_AETHER_HANDLER)) {
				adapter.component.readFromNBT(stack.getTag().getCompound(NBT_AETHER_HANDLER));
			}
			
			StackHandlerCache.put(id, adapter);
		}
		return adapter;
	}
	
	public static @Nullable IAetherHandlerComponent LookupAetherItemHandler(@Nullable UUID id) {
		if (id != null) {
			@Nullable StackHandlerItem wrapper = GetCachedHandlerItem(id);
			if (wrapper != null) {
				return wrapper.component;
			}
		}
		return null;
	}
	
	public UUID getItemID(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}
		
		UUID id;
		CompoundTag nbt;
		if (!stack.hasTag()) {
			nbt = new CompoundTag();
		} else {
			nbt = stack.getTag();
		}
		
		if (!nbt.contains(NBT_PENDANT_ID)) {
			id = UUID.randomUUID();
			
			nbt.putString(NBT_PENDANT_ID, id.toString());
			stack.setTag(nbt);
		} else {
			id = UUID.fromString(nbt.getString(NBT_PENDANT_ID));
		}
		return id;
	}
	
	/**
	 * Return whether this item should automatically try to fill itself from other items in the same inventory.
	 * Ideally, the following is true:
	 *   <b>shouldAutoFill(stack) == !canBeDrawnFrom(stack)</b>
	 * Otherwise draw cycles that waste server and network resources will occur.
	 * @param stack
	 * @return
	 */
	protected abstract boolean shouldAutoFill(ItemStack stack, Level worldIn, Entity entityIn);
	
	/**
	 * Return whether this item can have aether taked from it as part of automatic inventory fillups.
	 * Ideally, the following is true:
	 *   <b>shouldAutoFill(stack) == !canBeDrawnFrom(stack)</b>
	 * Otherwise draw cycles that waste server and network resources will occur.
	 * @param stack
	 * @return
	 */
	public abstract boolean canBeDrawnFrom(@Nonnull ItemStack stack, @Nullable Level worldIn, Entity entityIn);
	
	protected void onFirstTick(ItemStack stack, Level worldIn, Entity entityIn) {
		getItemID(stack); // generates it if it's missing
	}
	
	protected void onAetherChange(ItemStack stack, int currentAether) {
		;
	}
	
	protected int getMaxAutoFill(ItemStack stack, Level worldIn, Entity entityIn) {
		return 2 * (20 * 1); // per second
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!stack.hasTag()) {
			// First time ticking!
			onFirstTick(stack, worldIn, entityIn);
		}
		
		if (!worldIn.isClientSide && entityIn.tickCount % 20 == 0 && shouldAutoFill(stack, worldIn, entityIn)) {
			final IAetherHandlerComponent comp = getAetherHandler(stack);
			final int aether = comp.getAether(null);
			final int maxAether = comp.getMaxAether(null);
			final int missing = Math.min(maxAether - aether, getMaxAutoFill(stack, worldIn, entityIn));
			int toDraw = missing;
			if (toDraw > 0) {
				
				LivingAetherDrawEvent event = new LivingAetherDrawEvent(Phase.BEFORE_EARLY, (LivingEntity) entityIn, stack, missing, toDraw);
				MinecraftForge.EVENT_BUS.post(event);
				toDraw = event.getAmtRemaining();
				
				if (toDraw > 0) {
					Container inv = null;
					if (entityIn instanceof Player) {
						inv = ((Player) entityIn).getInventory();
					}
					// else....
					
					if (inv != null) {
						toDraw -= APIProxy.drawFromInventory(worldIn, entityIn, inv, toDraw, stack);
					}
				}
				
				event = new LivingAetherDrawEvent(Phase.BEFORE_LATE, (LivingEntity) entityIn, stack,  missing, toDraw);
				MinecraftForge.EVENT_BUS.post(event);
				toDraw = event.getAmtRemaining();
				
				// if missing has changed, we drew aether
				int gained = missing - toDraw;
				if (gained > 0) {
					this.addAether(stack, gained);
				}
				
				event = new LivingAetherDrawEvent(Phase.AFTER, (LivingEntity) entityIn, stack,  missing, toDraw);
				MinecraftForge.EVENT_BUS.post(event);
				event.getAmtRemaining();
			}
		}
		
		// Check for un-forwarded aether handler changes
		StackHandlerItem handlerItem = GetOrCreateCachedHandlerItem(stack);
		if (!worldIn.isClientSide && handlerItem.dirty) {
			SaveItem(stack);
		}
		
		// On client side, check for dirty nbt
		if (worldIn.isClientSide && stack.hasTag() && stack.getTag().getBoolean(NBT_CLIENT_DIRTY)) {
			// Clear dirty flag (on client side. Will be overwritten with next update)
			CompoundTag tag = stack.getTag();
			tag.putBoolean(NBT_CLIENT_DIRTY, false);
			stack.setTag(tag);
			
			// Update local copy
			if (!Minecraft.getInstance().isLocalServer()) {
				StackHandlerCache.remove(getItemID(stack));
				GetOrCreateCachedHandlerItem(stack); // Creates it and loads from NBT :)
			}
		}
		
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	/**
	 * Items' aether status is commited to the stacks' NBT automatically when in a player's inventory.
	 * This is not true when on the ground or in a machine. To make that happen, the machine should call
	 * this function on the item to commit any aether changes to NBT.
	 * This function won't do any work if there are no changes.
	 * However, if you're changing aether each tick, this will re-write NBT every time it's called.
	 * @param stack
	 */
	public static void SaveItem(ItemStack stack) {
		// Check for un-forwarded aether handler changes
		StackHandlerItem handlerItem = GetOrCreateCachedHandlerItem(stack);
		AetherItem type = (AetherItem) stack.getItem();
		if (handlerItem.dirty) {
			type.onAetherChange(stack, handlerItem.component.getAether(null));
			type.commitHandler(stack);
		}
	}
	
	
	private static Map<UUID, StackHandlerItem> StackHandlerCache = new HashMap<>();
	
	private static final class StackHandlerItem {
		private IAetherHandlerComponent component;
		private boolean dirty;
	}
}
