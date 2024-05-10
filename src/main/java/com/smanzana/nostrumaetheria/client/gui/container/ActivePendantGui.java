package com.smanzana.nostrumaetheria.client.gui.container;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.items.ActivePendant;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.items.ReagentItem;
import com.smanzana.nostrummagica.utils.ContainerUtil;
import com.smanzana.nostrummagica.utils.ContainerUtil.IPackedContainerProvider;
import com.smanzana.nostrummagica.utils.Inventories;
import com.smanzana.nostrummagica.utils.RenderFuncs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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

	public static class ActivePendantContainer extends Container {
		
		public static final String ID = "active_pendant";
		
		protected final ItemStack pendant;
		protected final IInventory pendantInvWrapper;
		protected boolean valid = true;
		
		public ActivePendantContainer(int windowId, PlayerInventory playerInv, ItemStack pendant) {
			super(AetheriaContainers.ActivePendant, windowId);
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
					this.addSlot(new Slot(playerInv, x + y * 9 + 9, GUI_PLAYER_INV_HOFFSET + (x * 18), GUI_PLAYER_INV_VOFFSET + (y * 18)) {
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
				this.addSlot(new Slot(playerInv, x, GUI_HOTBAR_INV_HOFFSET + x * 18, GUI_HOTBAR_INV_VOFFSET) {
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
			
			this.addSlot(new Slot(pendantInvWrapper, 0, GUI_PENDANT_INV_HOFFSET, GUI_PENDANT_INV_VOFFSET) {
				@Override
				public boolean isItemValid(@Nonnull ItemStack stack) {
					return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
				}
			});
		}
		
		public static final ActivePendantContainer FromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer buffer) {
			final int slot = buffer.readVarInt();
			ItemStack stack = playerInv.getStackInSlot(slot);
			if (stack.isEmpty() || !(stack.getItem() instanceof ActivePendant)) {
				stack = new ItemStack(AetheriaItems.activePendant);
			}
			return new ActivePendantContainer(windowId, playerInv, stack);
		}
		
		public static final IPackedContainerProvider Make(int slot) {
			return ContainerUtil.MakeProvider(ID, (windowId, playerInv, player) -> {
				ItemStack stack = playerInv.getStackInSlot(slot);
				if (stack.isEmpty() || !(stack.getItem() instanceof ActivePendant)) {
					stack = new ItemStack(AetheriaItems.activePendant);
				}
				return new ActivePendantContainer(windowId, playerInv, stack);
			}, (buffer) -> {
				buffer.writeVarInt(slot);
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
	public static class ActivePendantGuiContainer extends AutoGuiContainer<ActivePendantContainer> {

		//private ActivePendantContainer container;
		
		public ActivePendantGuiContainer(ActivePendantContainer container, PlayerInventory playerInv, ITextComponent name) {
			super(container, playerInv, name);
			//this.container = container;
			
			this.xSize = GUI_TEXT_WIDTH;
			this.ySize = GUI_TEXT_HEIGHT;
		}
		
		@Override
		public void init() {
			super.init();
		}
		
		@Override
		protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStackIn, float partialTicks, int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			
			mc.getTextureManager().bindTexture(TEXT);
			RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, horizontalMargin, verticalMargin, 0,0, GUI_TEXT_WIDTH, GUI_TEXT_HEIGHT, 256, 256);
			
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(MatrixStack matrixStackIn, int mouseX, int mouseY) {
			;
		}
		
	}

}
