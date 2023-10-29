package com.smanzana.nostrumaetheria.gui.container;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.items.ActivePendant;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.items.ReagentItem;
import com.smanzana.nostrummagica.utils.Inventories;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	public static class ActivePendantContainer extends Container {
		
		protected final ItemStack pendant;
		protected final IInventory pendantInvWrapper;
		protected boolean valid = true;
		
		public ActivePendantContainer(IInventory playerInv, ItemStack pendant) {
			this.pendant = pendant;
			// Preemptively make sure an ID has been generated
			ActivePendant.lyonGetID(pendant);
			ItemStack inv[] = new ItemStack[] {ActivePendant.lyonGetReagents(pendant)};
			pendantInvWrapper = new Inventories.ItemStackArrayWrapper(inv) {
				@Override
				public boolean isItemValidForSlot(int index, ItemStack stack) {
					return index == 0 && (stack.isEmpty() || stack.getItem() instanceof ReagentItem);
				}
				
				@Override
				public void markDirty() {
					// Update backing pendant item
					ActivePendant.lyonSetReagents(pendant, inv[0]);
				}
			};
						
			// Construct player inventory
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; x++) {
					this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, GUI_PLAYER_INV_HOFFSET + (x * 18), GUI_PLAYER_INV_VOFFSET + (y * 18)) {
						@Override
						public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
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
				this.addSlotToContainer(new Slot(playerInv, x, GUI_HOTBAR_INV_HOFFSET + x * 18, GUI_HOTBAR_INV_VOFFSET) {
					@Override
					public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
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
			
			this.addSlotToContainer(new Slot(pendantInvWrapper, 0, GUI_PENDANT_INV_HOFFSET, GUI_PENDANT_INV_VOFFSET) {
				@Override
				public boolean isItemValid(@Nonnull ItemStack stack) {
					return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
				}
			});
		}
		
		@Override
		public ItemStack transferStackInSlot(PlayerEntity playerIn, int fromSlot) {
			ItemStack prev = ItemStack.EMPTY;	
			Slot slot = (Slot) this.inventorySlots.get(fromSlot);
			
			if (slot != null && slot.getHasStack()) {
				ItemStack cur = slot.getStack();
				prev = cur.copy();
				
				if (slot.inventory == this.pendantInvWrapper) {
					// Trying to take out the reagent
					if (playerIn.inventory.addItemStackToInventory(cur)) {
						slot.putStack(ItemStack.EMPTY);
						cur = slot.onTake(playerIn, cur);
					} else {
						prev = ItemStack.EMPTY;
					}
				} else {
					// shift-click in player inventory
					if (slot.isItemValid(cur)) {
						ItemStack leftover = Inventories.addItem(pendantInvWrapper, cur);
						slot.putStack(leftover.isEmpty() ? ItemStack.EMPTY : leftover);
						if (!leftover.isEmpty() && leftover.getCount() == prev.getCount()) {
							prev = ItemStack.EMPTY;
						}
					}
				}
				
			}
			
			return prev;
		}
		
		@Override
		public boolean canDragIntoSlot(Slot slotIn) {
			return true;
		}
		
		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return valid;
		}
		
		public ItemStack getPendant() {
			return pendant;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ActivePendantGuiContainer extends AutoGuiContainer {

		//private ActivePendantContainer container;
		
		public ActivePendantGuiContainer(ActivePendantContainer container) {
			super(container);
			//this.container = container;
			
			this.xSize = GUI_TEXT_WIDTH;
			this.ySize = GUI_TEXT_HEIGHT;
		}
		
		@Override
		public void initGui() {
			super.initGui();
		}
		
		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			
			GlStateManager.color(1.0F,  1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(TEXT);
			
			Gui.drawModalRectWithCustomSizedTexture(horizontalMargin, verticalMargin, 0,0, GUI_TEXT_WIDTH, GUI_TEXT_HEIGHT, 256, 256);
			
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			;
		}
		
	}

}
