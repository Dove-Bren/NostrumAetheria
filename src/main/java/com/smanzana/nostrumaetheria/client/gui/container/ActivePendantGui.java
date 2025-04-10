package com.smanzana.nostrumaetheria.client.gui.container;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.items.ActivePendant;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.item.ReagentItem;
import com.smanzana.nostrummagica.util.ContainerUtil;
import com.smanzana.nostrummagica.util.ContainerUtil.IPackedContainerProvider;
import com.smanzana.nostrummagica.util.Inventories;
import com.smanzana.nostrummagica.util.RenderFuncs;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ActivePendantGui {
	
	private static final ResourceLocation TEXT= new ResourceLocation(NostrumAetheria.MODID + ":textures/gui/container/active_pendant_gui.png");
	private static final int GUI_TEXT_WIDTH = 176;
	private static final int GUI_TEXT_HEIGHT = 175;
	private static final int GUI_PENDANT_INV_HOFFSET = 81;
	private static final int GUI_PENDANT_INV_VOFFSET = 27;
	private static final int GUI_PLAYER_INV_HOFFSET = 8;
	private static final int GUI_PLAYER_INV_VOFFSET = 93;
	private static final int GUI_HOTBAR_INV_HOFFSET = 8;
	private static final int GUI_HOTBAR_INV_VOFFSET = 151;

	public static class ActivePendantContainer extends AbstractContainerMenu {
		
		public static final String ID = "active_pendant";
		
		protected final ItemStack pendant;
		protected final Container pendantInvWrapper;
		protected boolean valid = true;
		
		public ActivePendantContainer(int windowId, Inventory playerInv, ItemStack pendant) {
			super(AetheriaContainers.ActivePendant, windowId);
			this.pendant = pendant;
			// Preemptively make sure an ID has been generated
			ActivePendant.lyonGetID(pendant);
			ItemStack inv[] = new ItemStack[] {ActivePendant.lyonGetReagents(pendant)};
			pendantInvWrapper = new Inventories.ItemStackArrayWrapper(inv) {
				@Override
				public boolean canPlaceItem(int index, ItemStack stack) {
					return index == 0 && (stack.isEmpty() || stack.getItem() instanceof ReagentItem);
				}
				
				@Override
				public void setChanged() {
					// Update backing pendant item
					ActivePendant.lyonSetReagents(pendant, inv[0]);
				}
			};
						
			// Construct player inventory
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; x++) {
					this.addSlot(new Slot(playerInv, x + y * 9 + 9, GUI_PLAYER_INV_HOFFSET + (x * 18), GUI_PLAYER_INV_VOFFSET + (y * 18)) {
						@Override
						public ItemStack onTake(Player playerIn, ItemStack stack) {
							if (!stack.isEmpty() && stack.getItem() instanceof ActivePendant) {
								if (Objects.equals(ActivePendant.lyonGetID(stack), ActivePendant.lyonGetID(pendant))) {
									//playerIn.closeScreen(); // Assumes just one player is looking
									valid = false;
								}
							}
							return stack;
						}
					});
				}
			}
			
			// Construct player hotbar
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(playerInv, x, GUI_HOTBAR_INV_HOFFSET + x * 18, GUI_HOTBAR_INV_VOFFSET) {
					@Override
					public ItemStack onTake(Player playerIn, ItemStack stack) {
						if (!stack.isEmpty() && stack.getItem() instanceof ActivePendant) {
							if (Objects.equals(ActivePendant.lyonGetID(stack), ActivePendant.lyonGetID(pendant))) {
								//playerIn.closeScreen(); // Assumes just one player is looking
								valid = false;
							}
						}
						return stack;
					}
				});
			}
			
			this.addSlot(new Slot(pendantInvWrapper, 0, GUI_PENDANT_INV_HOFFSET, GUI_PENDANT_INV_VOFFSET) {
				@Override
				public boolean mayPlace(@Nonnull ItemStack stack) {
					return this.container.canPlaceItem(this.getSlotIndex(), stack);
				}
			});
		}
		
		public static final ActivePendantContainer FromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf buffer) {
			final int slot = buffer.readVarInt();
			ItemStack stack = playerInv.getItem(slot);
			if (stack.isEmpty() || !(stack.getItem() instanceof ActivePendant)) {
				stack = new ItemStack(AetheriaItems.activePendant);
			}
			return new ActivePendantContainer(windowId, playerInv, stack);
		}
		
		public static final IPackedContainerProvider Make(int slot) {
			return ContainerUtil.MakeProvider(ID, (windowId, playerInv, player) -> {
				ItemStack stack = playerInv.getItem(slot);
				if (stack.isEmpty() || !(stack.getItem() instanceof ActivePendant)) {
					stack = new ItemStack(AetheriaItems.activePendant);
				}
				return new ActivePendantContainer(windowId, playerInv, stack);
			}, (buffer) -> {
				buffer.writeVarInt(slot);
			});
		}
		
		@Override
		public ItemStack quickMoveStack(Player playerIn, int fromSlot) {
			ItemStack prev = ItemStack.EMPTY;	
			Slot slot = (Slot) this.slots.get(fromSlot);
			
			if (slot != null && slot.hasItem()) {
				ItemStack cur = slot.getItem();
				prev = cur.copy();
				
				if (slot.container == this.pendantInvWrapper) {
					// Trying to take out the reagent
					if (playerIn.inventory.add(cur)) {
						slot.set(ItemStack.EMPTY);
						cur = slot.onTake(playerIn, cur);
					} else {
						prev = ItemStack.EMPTY;
					}
				} else {
					// shift-click in player inventory
					if (slot.mayPlace(cur)) {
						ItemStack leftover = Inventories.addItem(pendantInvWrapper, cur);
						slot.set(leftover.isEmpty() ? ItemStack.EMPTY : leftover);
						if (!leftover.isEmpty() && leftover.getCount() == prev.getCount()) {
							prev = ItemStack.EMPTY;
						}
					}
				}
				
			}
			
			return prev;
		}
		
		@Override
		public boolean canDragTo(Slot slotIn) {
			return true;
		}
		
		@Override
		public boolean stillValid(Player playerIn) {
			return valid;
		}
		
		public ItemStack getPendant() {
			return pendant;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ActivePendantGuiContainer extends AutoGuiContainer<ActivePendantContainer> {

		//private ActivePendantContainer container;
		
		public ActivePendantGuiContainer(ActivePendantContainer container, Inventory playerInv, Component name) {
			super(container, playerInv, name);
			//this.container = container;
			
			this.imageWidth = GUI_TEXT_WIDTH;
			this.imageHeight = GUI_TEXT_HEIGHT;
		}
		
		@Override
		public void init() {
			super.init();
		}
		
		@Override
		protected void renderBg(PoseStack matrixStackIn, float partialTicks, int mouseX, int mouseY) {
			int horizontalMargin = (width - imageWidth) / 2;
			int verticalMargin = (height - imageHeight) / 2;
			
			mc.getTextureManager().bind(TEXT);
			RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, horizontalMargin, verticalMargin, 0,0, GUI_TEXT_WIDTH, GUI_TEXT_HEIGHT, 256, 256);
			
		}
		
		@Override
		protected void renderLabels(PoseStack matrixStackIn, int mouseX, int mouseY) {
			;
		}
		
	}

}
