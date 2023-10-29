package com.smanzana.nostrumaetheria.gui.container;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.tiles.AetherUnravelerBlockEntity;
import com.smanzana.nostrummagica.client.gui.container.AutoContainer;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.utils.Inventories;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherUnravelerGui {
	
	private static final ResourceLocation TEXT= new ResourceLocation(NostrumAetheria.MODID + ":textures/gui/container/aether_unraveler.png");
	private static final int GUI_TEXT_WIDTH = 176;
	private static final int GUI_TEXT_HEIGHT = 132;
	private static final int GUI_TOP_INV_HOFFSET = 80;
	private static final int GUI_TOP_INV_VOFFSET = 18;
	private static final int GUI_PLAYER_INV_HOFFSET = 8;
	private static final int GUI_PLAYER_INV_VOFFSET = 50;
	private static final int GUI_HOTBAR_INV_HOFFSET = 8;
	private static final int GUI_HOTBAR_INV_VOFFSET = 108;
	private static final int GUI_TOP_BAR_HOFFSET = 76;
	private static final int GUI_TOP_BAR_VOFFSET = 12;
	private static final int GUI_TOP_BAR_WIDTH = 24;
	private static final int GUI_TOP_BAR_HEIGHT = 3;
	private static final int GUI_PROGRESS_BAR_HOFFSET = 29;
	private static final int GUI_PROGRESS_BAR_VOFFSET = 20;
	private static final int GUI_PROGRESS_BAR_TEXT_HOFFSET = 0;
	private static final int GUI_PROGRESS_BAR_TEXT_VOFFSET = 132;
	private static final int GUI_PROGRESS_BAR_WIDTH = 118;
	private static final int GUI_PROGRESS_BAR_HEIGHT = 26;

	public static class AetherUnravelerContainer extends AutoContainer {
		
		protected AetherUnravelerBlockEntity chest;
		
		public AetherUnravelerContainer(IInventory playerInv, AetherUnravelerBlockEntity chest) {
			super(chest);
			this.chest = chest;
						
			// Construct player inventory
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; x++) {
					this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, GUI_PLAYER_INV_HOFFSET + (x * 18), GUI_PLAYER_INV_VOFFSET + (y * 18)));
				}
			}
			
			// Construct player hotbar
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new Slot(playerInv, x, GUI_HOTBAR_INV_HOFFSET + x * 18, GUI_HOTBAR_INV_VOFFSET));
			}
			
			this.addSlotToContainer(new Slot(chest, 0, GUI_TOP_INV_HOFFSET, GUI_TOP_INV_VOFFSET) {
				public boolean isItemValid(@Nonnull ItemStack stack) {
			        return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
			    }
			});
		}
		
		@Override
		public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
			ItemStack prev = ItemStack.EMPTY;	
			Slot slot = (Slot) this.inventorySlots.get(fromSlot);
			
			if (slot != null && slot.getHasStack()) {
				ItemStack cur = slot.getStack();
				prev = cur.copy();
				
				if (slot.inventory == this.chest) {
					// Trying to take one of our items
					if (playerIn.inventory.addItemStackToInventory(cur)) {
						slot.putStack(ItemStack.EMPTY);
						cur = slot.onTake(playerIn, cur);
					} else {
						prev = ItemStack.EMPTY;
					}
				} else {
					// shift-click in player inventory
					ItemStack leftover = Inventories.addItem(chest, cur);
					slot.putStack(leftover.isEmpty() ? ItemStack.EMPTY : leftover);
					if (!leftover.isEmpty() && leftover.getCount() == prev.getCount()) {
						prev = ItemStack.EMPTY;
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
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class AetherUnravelerGuiContainer extends AutoGuiContainer {

		private AetherUnravelerContainer container;
		
		public AetherUnravelerGuiContainer(AetherUnravelerContainer container) {
			super(container);
			this.container = container;
			
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
			
			IAetherHandler unravelerHandler = container.chest.getHandler();
			
			float myAether = 0f;
			
			if (unravelerHandler != null) {
				myAether = (float) unravelerHandler.getAether(null) / (float) unravelerHandler.getMaxAether(null);
			}
			
			if (myAether > 0) {
				Gui.drawRect(horizontalMargin + GUI_TOP_BAR_HOFFSET, verticalMargin + GUI_TOP_BAR_VOFFSET,
						horizontalMargin + GUI_TOP_BAR_HOFFSET + (int) (GUI_TOP_BAR_WIDTH * myAether), verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT,
						0xA0909000);
			}
			
			float progress = ((float) container.chest.getField(1) / 100f);
			if (progress > 0f) {
				GlStateManager.color(.3f, .7f, .45f, 1f);
				final int drawW = (int) (GUI_PROGRESS_BAR_WIDTH * progress);
				Gui.drawModalRectWithCustomSizedTexture(horizontalMargin + GUI_PROGRESS_BAR_HOFFSET, verticalMargin + GUI_PROGRESS_BAR_VOFFSET,
						GUI_PROGRESS_BAR_TEXT_HOFFSET, GUI_PROGRESS_BAR_TEXT_VOFFSET,
						drawW, GUI_PROGRESS_BAR_HEIGHT,
						256, 256);
			}
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			
			IAetherHandler unravelerHandler = container.chest.getHandler();
			if (unravelerHandler != null) {
				if (mouseX >= horizontalMargin + GUI_TOP_BAR_HOFFSET && mouseX <= horizontalMargin + GUI_TOP_BAR_HOFFSET + GUI_TOP_BAR_WIDTH
						&& mouseY >= verticalMargin + GUI_TOP_BAR_VOFFSET && mouseY <= verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT) {
					drawHoveringText(Lists.newArrayList(String.format("%.2f / %.2f", unravelerHandler.getAether(null) * .01f, unravelerHandler.getMaxAether(null) * .01f)),
							mouseX - horizontalMargin, mouseY - verticalMargin);
				}
			}
		}
		
	}
	
}
