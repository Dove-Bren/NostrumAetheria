package com.smanzana.nostrumaetheria.gui.container;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock.AetherFurnaceBlockEntity;
import com.smanzana.nostrummagica.client.gui.container.AutoContainer;
import com.smanzana.nostrummagica.utils.Inventories;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherFurnaceGui {
	
	private static final ResourceLocation TEXT_SMALL = new ResourceLocation(NostrumAetheria.MODID + ":textures/gui/container/aether_furnace_small.png");
	private static final ResourceLocation TEXT_MEDIUM = new ResourceLocation(NostrumAetheria.MODID + ":textures/gui/container/aether_furnace_medium.png");
	private static final ResourceLocation TEXT_LARGE = new ResourceLocation(NostrumAetheria.MODID + ":textures/gui/container/aether_furnace_large.png");
	private static final int GUI_TEXT_WIDTH = 176;
	private static final int GUI_TEXT_HEIGHT = 132;
	private static final int GUI_TOP_INV_HOFFSET = 62;
	private static final int GUI_TOP_INV_VOFFSET = 18;
	private static final int GUI_PLAYER_INV_HOFFSET = 8;
	private static final int GUI_PLAYER_INV_VOFFSET = 50;
	private static final int GUI_HOTBAR_INV_HOFFSET = 8;
	private static final int GUI_HOTBAR_INV_VOFFSET = 108;
	private static final int GUI_INV_CELL_LENGTH = 18;
	
	private static final int GUI_FIRE_FIRE_WIDTH = 13;
	private static final int GUI_FIRE_FIRE_HEIGHT = 14;

	public static class AetherFurnaceContainer extends AutoContainer {
		
		protected AetherFurnaceBlockEntity chest;
		
		public AetherFurnaceContainer(IInventory playerInv, AetherFurnaceBlockEntity chest) {
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
			
			int total = chest.getSizeInventory();
			for (int i = 0; i < total; i++) {
				int x = i - ((total - 3) / 2);
				
				this.addSlotToContainer(new Slot(chest, i, GUI_TOP_INV_HOFFSET + x * GUI_INV_CELL_LENGTH, GUI_TOP_INV_VOFFSET) {
					public boolean isItemValid(@Nullable ItemStack stack) {
				        return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
				    }
				});
			}
		}
		
		@Override
		public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
			ItemStack prev = null;	
			Slot slot = (Slot) this.inventorySlots.get(fromSlot);
			
			if (slot != null && slot.getHasStack()) {
				ItemStack cur = slot.getStack();
				prev = cur.copy();
				
				if (slot.inventory == this.chest) {
					// Trying to take one of our items
					if (playerIn.inventory.addItemStackToInventory(cur)) {
						slot.putStack(null);
						slot.onPickupFromSlot(playerIn, cur);
					} else {
						prev = null;
					}
				} else {
					// shift-click in player inventory
					ItemStack leftover = Inventories.addItem(chest, cur);
					slot.putStack(leftover != null && leftover.stackSize <= 0 ? null : leftover);
					if (leftover != null && leftover.stackSize == prev.stackSize) {
						prev = null;
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
	public static class AetherFurnaceGuiContainer extends GuiContainer {

		private AetherFurnaceContainer container;
		
		public AetherFurnaceGuiContainer(AetherFurnaceContainer container) {
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
			int fireX = 45;
			
			GlStateManager.color(1.0F,  1.0F, 1.0F, 1.0F);
			
			switch (container.chest.getType()) {
			case LARGE:
				mc.getTextureManager().bindTexture(TEXT_LARGE);
				fireX = (GUI_TOP_INV_HOFFSET - 9);
				break;
			case MEDIUM:
				mc.getTextureManager().bindTexture(TEXT_MEDIUM);
				fireX = (GUI_TOP_INV_HOFFSET - 27);
				break;
			case SMALL:
				mc.getTextureManager().bindTexture(TEXT_SMALL);
				fireX = (GUI_TOP_INV_HOFFSET - 45);
				break;
			}
			
			Gui.drawModalRectWithCustomSizedTexture(horizontalMargin, verticalMargin, 0,0, GUI_TEXT_WIDTH, GUI_TEXT_HEIGHT, 256, 256);
			
			float progress = container.chest.getBurnProgress();
			if (progress > 0) {
				//System.out.println("progress: " + progress);
				int y = (int) (14f * (1f - progress));
				Gui.drawModalRectWithCustomSizedTexture(horizontalMargin + GUI_TOP_INV_HOFFSET - fireX, verticalMargin + GUI_TOP_INV_VOFFSET + 2 + y,
						176, y,
						GUI_FIRE_FIRE_WIDTH, GUI_FIRE_FIRE_HEIGHT - y, 256, 256);
				Gui.drawModalRectWithCustomSizedTexture(horizontalMargin + GUI_TOP_INV_HOFFSET + (GUI_INV_CELL_LENGTH * 3) + -1 + (fireX - GUI_FIRE_FIRE_WIDTH), verticalMargin + GUI_TOP_INV_VOFFSET + 2 + y,
						176, y,
						GUI_FIRE_FIRE_WIDTH, GUI_FIRE_FIRE_HEIGHT - y, 256, 256);
			}
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			;
		}
		
	}
	
}
