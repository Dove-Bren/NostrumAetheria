package com.smanzana.nostrumaetheria.gui.container;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.network.messages.AetherBoilerModeChangeMessage;
import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity.BoilerBurnMode;
import com.smanzana.nostrummagica.client.gui.container.AutoContainer;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.utils.Inventories;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherBoilerGui {
	
	private static final ResourceLocation TEXT= new ResourceLocation(NostrumAetheria.MODID + ":textures/gui/container/aether_boiler.png");
	private static final int GUI_TEXT_WIDTH = 176;
	private static final int GUI_TEXT_HEIGHT = 132;
	private static final int GUI_TOP_INV_HOFFSET = 80;
	private static final int GUI_TOP_INV_VOFFSET = 18;
	private static final int GUI_PLAYER_INV_HOFFSET = 8;
	private static final int GUI_PLAYER_INV_VOFFSET = 50;
	private static final int GUI_HOTBAR_INV_HOFFSET = 8;
	private static final int GUI_HOTBAR_INV_VOFFSET = 108;
	
	private static final int GUI_FIRE_FIRE_WIDTH = 13;
	private static final int GUI_FIRE_FIRE_HEIGHT = 14;
	
	private static final int GUI_TOP_BAR_HOFFSET = 76;
	private static final int GUI_TOP_BAR_VOFFSET = 12;
	private static final int GUI_TOP_BAR_WIDTH = 24;
	private static final int GUI_TOP_BAR_HEIGHT = 3;
	
	private static final int GUI_MODE_BUTTON_UI_HOFFSET = 162;
	private static final int GUI_MODE_BUTTON_UI_VOFFSET = 4;
	private static final int GUI_MODE_BUTTON_UI_WIDTH = 10;
	private static final int GUI_MODE_BUTTON_UI_HEIGHT = 10;
	
	private static final int GUI_MODE_BUTTON_TEXT_HOFFSET = 176;
	private static final int GUI_MODE_BUTTON_TEXT_VOFFSET = 14;
	private static final int GUI_MODE_BUTTON_TEXT_WIDTH = 18;
	private static final int GUI_MODE_BUTTON_TEXT_HEIGHT = 18;
	
	private static final int GUI_MODE_ICON_TEXT_HOFFSET = 176;
	private static final int GUI_MODE_ICON_TEXT_VOFFSET = 32;
	private static final int GUI_MODE_ICON_WIDTH = 32;
	private static final int GUI_MODE_ICON_HEIGHT = 32;

	public static class AetherBoilerContainer extends AutoContainer {
		
		protected AetherBoilerBlockEntity chest;
		
		public AetherBoilerContainer(IInventory playerInv, AetherBoilerBlockEntity chest) {
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
		public ItemStack transferStackInSlot(PlayerEntity playerIn, int fromSlot) {
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
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class AetherBoilerGuiContainer extends AutoGuiContainer {

		private AetherBoilerContainer container;
		
		private ModeButton modeButton;
		
		public AetherBoilerGuiContainer(AetherBoilerContainer container) {
			super(container);
			this.container = container;
			
			this.xSize = GUI_TEXT_WIDTH;
			this.ySize = GUI_TEXT_HEIGHT;
		}
		
		@Override
		public void initGui() {
			super.initGui();
			
			modeButton = new ModeButton(1, guiLeft + GUI_MODE_BUTTON_UI_HOFFSET,
					guiTop + GUI_MODE_BUTTON_UI_VOFFSET);
			this.addButton(modeButton);
		}
		
		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			
			GlStateManager.color(1.0F,  1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(TEXT);
			
			Gui.drawModalRectWithCustomSizedTexture(horizontalMargin, verticalMargin, 0,0, GUI_TEXT_WIDTH, GUI_TEXT_HEIGHT, 256, 256);
			
			float progress = container.chest.getBurnProgress();
			if (progress > 0) {
				int y = (int) (14f * (1f - progress));
				Gui.drawModalRectWithCustomSizedTexture(horizontalMargin + 64, verticalMargin + GUI_TOP_INV_VOFFSET + 2 + y,
						176, y,
						GUI_FIRE_FIRE_WIDTH, GUI_FIRE_FIRE_HEIGHT - y, 256, 256);
				Gui.drawModalRectWithCustomSizedTexture(horizontalMargin + 101, verticalMargin + GUI_TOP_INV_VOFFSET + 2 + y,
						176, y,
						GUI_FIRE_FIRE_WIDTH, GUI_FIRE_FIRE_HEIGHT - y, 256, 256);
			}
			
			IAetherHandler boilerHandler = container.chest.getHandler();
			float myAether = 0f;
			
			if (boilerHandler != null) {
				myAether = (float) boilerHandler.getAether(null) / (float) boilerHandler.getMaxAether(null);
			}
			
			if (myAether > 0) {
				Gui.drawRect(horizontalMargin + GUI_TOP_BAR_HOFFSET, verticalMargin + GUI_TOP_BAR_VOFFSET,
						horizontalMargin + GUI_TOP_BAR_HOFFSET + (int) (GUI_TOP_BAR_WIDTH * myAether), verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT,
						0xA0909000);
			}
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			
			IAetherHandler boilerHandler = container.chest.getHandler();
			if (boilerHandler != null) {
				if (mouseX >= horizontalMargin + GUI_TOP_BAR_HOFFSET && mouseX <= horizontalMargin + GUI_TOP_BAR_HOFFSET + GUI_TOP_BAR_WIDTH
						&& mouseY >= verticalMargin + GUI_TOP_BAR_VOFFSET && mouseY <= verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT) {
					drawHoveringText(Lists.newArrayList(String.format("%.2f / %.2f", boilerHandler.getAether(null) * .01f, boilerHandler.getMaxAether(null) * .01f)),
							mouseX - horizontalMargin, mouseY - verticalMargin);
				}
			}
		}
		
		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			super.actionPerformed(button);
			
			if (button == this.modeButton) {
				// Send message requesting mode cycle
				BoilerBurnMode next;
				int ord = this.container.chest.getBoilerMode().ordinal() + 1;
				BoilerBurnMode[] modes = BoilerBurnMode.values();
				
				if (ord >= modes.length) {
					ord = 0;
				}
				
				next = modes[ord];
				NetworkHandler.getSyncChannel().sendToServer(new AetherBoilerModeChangeMessage(
						this.container.chest.getPos(), next
						));
			}
		}
		
		protected class ModeButton extends GuiButton {
			
			private boolean pressed;
			
			public ModeButton(int buttonId, int x, int y) {
				super(buttonId, x, y, GUI_MODE_BUTTON_UI_WIDTH, GUI_MODE_BUTTON_UI_HEIGHT, "");
				pressed = false;
			}
			
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				int textX = GUI_MODE_BUTTON_TEXT_HOFFSET;
				if (pressed) {
					textX += GUI_MODE_BUTTON_TEXT_WIDTH * 2;
				} else if (hovered) {
					textX += GUI_MODE_BUTTON_TEXT_WIDTH;
				}
				
				GlStateManager.color(1f, 1f, 1f, 1f);
				mc.getTextureManager().bindTexture(TEXT);
				GlStateManager.enableBlend();
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, 0);
				drawScaledCustomSizeModalRect(0, 0,
						textX, GUI_MODE_BUTTON_TEXT_VOFFSET,
						GUI_MODE_BUTTON_TEXT_WIDTH, GUI_MODE_BUTTON_TEXT_HEIGHT,
						GUI_MODE_BUTTON_UI_WIDTH, GUI_MODE_BUTTON_UI_HEIGHT,
						256, 256);
				
				// Draw actual mode overlay
				BoilerBurnMode mode = container.chest.getBoilerMode();
				drawBoilerMode(mc, mode);
				GlStateManager.popMatrix();
				
				// Draw tooltip if hovered
				if (this.hovered) {
					AetherBoilerGuiContainer.this.drawHoveringText(I18n.format("gui.aether_boiler.mode." + mode.getUnlocID()), mouseX + 8, mouseY + 8);
				}
			}
			
			@Override
			public void mouseReleased(int mouseX, int mouseY) {
				pressed = false;
				super.mouseReleased(mouseX, mouseY);
			}
			
			@Override
			public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
				boolean ret = super.mousePressed(mc, mouseX, mouseY);
				pressed = ret;
				return ret;
			}
			
			protected void drawBoilerMode(Minecraft mc, BoilerBurnMode mode) {
				int textX = GUI_MODE_ICON_TEXT_HOFFSET;
				int textY = GUI_MODE_ICON_TEXT_VOFFSET;
				switch (mode) {
				case FOCUS_AETHER:
				default:
					; // Default is correct
					break;
				case ALWAYS_ON:
					textX += GUI_MODE_ICON_WIDTH;
					break;
				case FOCUS_BOTH:
					textY += GUI_MODE_ICON_HEIGHT;
					break;
				case FOCUS_FURNACE:
					textX += GUI_MODE_ICON_WIDTH;
					textY += GUI_MODE_ICON_HEIGHT;
					break;
				}
				
				GlStateManager.color(1.0F,  1.0F, 1.0F, 1f);
				mc.getTextureManager().bindTexture(TEXT);
				GlStateManager.enableBlend();
				Gui.drawScaledCustomSizeModalRect(1, 1,
						textX, textY,
						GUI_MODE_ICON_WIDTH, GUI_MODE_ICON_HEIGHT,
						GUI_MODE_BUTTON_UI_WIDTH - 2, GUI_MODE_BUTTON_UI_HEIGHT - 2,
						256, 256);
			}
		}
		
	}
	
}
