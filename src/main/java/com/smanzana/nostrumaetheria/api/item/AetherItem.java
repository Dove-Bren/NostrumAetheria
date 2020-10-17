package com.smanzana.nostrumaetheria.api.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler.AetherFlowConnection;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerItem;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AetherItem extends Item implements IAetherHandlerItem {

	private static final String NBT_AETHER_HANDLER = "aether_handler";
	private static final String NBT_PENDANT_ID = "id";
	
	public AetherItem() {
		super();
	}
	
	protected abstract int getDefaultMaxAether(ItemStack stack);
	
	protected abstract boolean shouldShowAether(ItemStack stack, EntityPlayer playerIn, boolean advanced);
    
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (stack == null)
			return;
		
		if (shouldShowAether(stack, playerIn, advanced)) {
			IAetherHandlerComponent comp = getAetherHandler(stack);
			int aether = comp.getAether(null);
			int maxAether = comp.getMaxAether(null);
			tooltip.add(ChatFormatting.DARK_PURPLE + I18n.format("item.info.aether", new Object[] {String.format("%.2f", (float) aether * .01f), String.format("%.2f", (float) maxAether * .01f)}));
		}
	}
	
	public IAetherHandlerComponent getAetherHandler(ItemStack stack) {
		return getCachedHandlerItem(stack).component;
	}

	public int getAether(ItemStack stack) {
		if (stack == null) {
			return 0;
		}
		
		IAetherHandlerComponent comp = getAetherHandler(stack);
		int aether = comp.getAether(null);
		
		return aether;
	}
	
	public int deductAether(ItemStack stack, int amount) {
		if (stack == null) {
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
		if (stack == null) {
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
		StackHandlerItem handlerItem = getCachedHandlerItem(stack);
		if (handlerItem.dirty) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt == null) {
				nbt = new NBTTagCompound();
				stack.setTagCompound(nbt);
			}
			
			nbt.setTag(NBT_AETHER_HANDLER, handlerItem.component.writeToNBT(new NBTTagCompound()));
			handlerItem.dirty = false;
		}
	}
	
	private StackHandlerItem getCachedHandlerItem(ItemStack stack) {
		UUID id = getItemID(stack);
		StackHandlerItem adapter = StackHandlerCache.get(id);
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
				
			}, 0, getDefaultMaxAether(stack));
			
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_AETHER_HANDLER)) {
				adapter.component.readFromNBT(stack.getTagCompound().getCompoundTag(NBT_AETHER_HANDLER));
			}
			
			StackHandlerCache.put(id, adapter);
		}
		
		return adapter;
	}
	
	public UUID getItemID(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		
		UUID id;
		NBTTagCompound nbt;
		if (!stack.hasTagCompound()) {
			nbt = new NBTTagCompound();
		} else {
			nbt = stack.getTagCompound();
		}
		
		if (!nbt.hasKey(NBT_PENDANT_ID)) {
			id = UUID.randomUUID();
			
			nbt.setString(NBT_PENDANT_ID, id.toString());
			stack.setTagCompound(nbt);
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
	protected abstract boolean shouldAutoFill(ItemStack stack, World worldIn, Entity entityIn);
	
	/**
	 * Return whether this item can have aether taked from it as part of automatic inventory fillups.
	 * Ideally, the following is true:
	 *   <b>shouldAutoFill(stack) == !canBeDrawnFrom(stack)</b>
	 * Otherwise draw cycles that waste server and network resources will occur.
	 * @param stack
	 * @return
	 */
	public abstract boolean canBeDrawnFrom(@Nullable ItemStack stack, @Nullable World worldIn, Entity entityIn);
	
	protected void onFirstTick(ItemStack stack, World worldIn, Entity entityIn) {
		getItemID(stack); // generates it if it's missing
	}
	
	protected void onAetherChange(ItemStack stack, int currentAether) {
		;
	}
	
	protected int getMaxAutoFill(ItemStack stack, World worldIn, Entity entityIn) {
		return 2 * (20 * 1); // per second
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!stack.hasTagCompound()) {
			// First time ticking!
			onFirstTick(stack, worldIn, entityIn);
		}
		
		if (!worldIn.isRemote && entityIn.ticksExisted % 20 == 0 && shouldAutoFill(stack, worldIn, entityIn)) {
			final IAetherHandlerComponent comp = getAetherHandler(stack);
			final int aether = comp.getAether(null);
			final int maxAether = comp.getMaxAether(null);
			final int missing = Math.min(maxAether - aether, getMaxAutoFill(stack, worldIn, entityIn));
			int toDraw = missing;
			if (toDraw > 0) {
				IInventory inv = null;
				if (entityIn instanceof EntityPlayer) {
					inv = ((EntityPlayer) entityIn).inventory;
				}
				// else....
				
				if (inv != null) {
					toDraw -= APIProxy.drawFromInventory(worldIn, entityIn, inv, toDraw, stack);
				}
				
				// if missing has changed, we drew aether
				int gained = missing - toDraw;
				if (gained > 0) {
					this.addAether(stack, gained);
				}
			}
		}
		
		// Check for un-forwarded aether handler changes
		StackHandlerItem handlerItem = getCachedHandlerItem(stack);
		if (!worldIn.isRemote && handlerItem.dirty) {
			this.onAetherChange(stack, handlerItem.component.getAether(null));
			commitHandler(stack);
		}
		
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	
	
	private Map<UUID, StackHandlerItem> StackHandlerCache = new HashMap<>();
	
	private static final class StackHandlerItem {
		private IAetherHandlerComponent component;
		private boolean dirty;
	}
}
