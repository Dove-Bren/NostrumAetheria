package com.smanzana.nostrumaetheria.client.gui.container;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.tiles.AetherChargerBlockEntity;
import com.smanzana.nostrummagica.client.gui.container.AutoContainer;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.utils.ContainerUtil;
import com.smanzana.nostrummagica.utils.ContainerUtil.IPackedContainerProvider;
import com.smanzana.nostrummagica.utils.Inventories;
import com.smanzana.nostrummagica.utils.RenderFuncs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherChargerGui {
	
	private static final ResourceLocation TEXT= new ResourceLocation(NostrumAetheria.MODID + ":textures/gui/container/aether_charger.png");
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
	private static final int GUI_BOTTOM_BAR_HOFFSET = 80;
	private static final int GUI_BOTTOM_BAR_VOFFSET = 37;
	private static final int GUI_BOTTOM_BAR_WIDTH = 16;
	private static final int GUI_BOTTOM_BAR_HEIGHT = 2;
	

	public static class AetherChargerContainer extends AutoContainer {
		
		public static final String ID = "aether_charger";
		
		protected AetherChargerBlockEntity chest;
		
		public AetherChargerContainer(int windowId, PlayerInventory playerInv, AetherChargerBlockEntity chest) {
			super(AetheriaContainers.Charger, windowId, chest);
			this.chest = chest;
						
			// Construct player inventory
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; x++) {
					this.addSlot(new Slot(playerInv, x + y * 9 + 9, GUI_PLAYER_INV_HOFFSET + (x * 18), GUI_PLAYER_INV_VOFFSET + (y * 18)));
				}
			}
			
			// Construct player hotbar
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(playerInv, x, GUI_HOTBAR_INV_HOFFSET + x * 18, GUI_HOTBAR_INV_VOFFSET));
			}
			
			this.addSlot(new Slot(chest, 0, GUI_TOP_INV_HOFFSET, GUI_TOP_INV_VOFFSET) {
				public boolean isItemValid(@Nonnull ItemStack stack) {
			        return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
			    }
			});
		}
		
		public static final AetherChargerContainer FromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer buffer) {
			return new AetherChargerContainer(windowId, playerInv, ContainerUtil.GetPackedTE(buffer));
		}
		
		public static IPackedContainerProvider Make(AetherChargerBlockEntity te) {
			return ContainerUtil.MakeProvider(ID, (windowId, playerInv, player) -> {
				return new AetherChargerContainer(windowId, playerInv, te);
			}, (buffer) -> {
				ContainerUtil.PackTE(buffer, te);
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
	
	@OnlyIn(Dist.CLIENT)
	public static class AetherChargerGuiContainer extends AutoGuiContainer<AetherChargerContainer> {

		private AetherChargerContainer container;
		
		public AetherChargerGuiContainer(AetherChargerContainer container, PlayerInventory playerInv, ITextComponent name) {
			super(container, playerInv, name);
			this.container = container;
			
			this.xSize = GUI_TEXT_WIDTH;
			this.ySize = GUI_TEXT_HEIGHT;
		}
		
		@Override
		public void init() {
			super.init();
		}
		
		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			
			GlStateManager.color4f(1.0F,  1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(TEXT);
			
			RenderFuncs.drawModalRectWithCustomSizedTexture(horizontalMargin, verticalMargin, 0,0, GUI_TEXT_WIDTH, GUI_TEXT_HEIGHT, 256, 256);
			
			IAetherHandler chargerHandler = container.chest.getHandler();
			
			float myAether = 0f;
			final float nestedAether = (float) container.chest.getAetherDisplay() / (float) container.chest.getMaxAetherDisplay();
			
			if (chargerHandler != null) {
				myAether = (float) chargerHandler.getAether(null) / (float) chargerHandler.getMaxAether(null);
			}
			
			if (myAether > 0) {
				RenderFuncs.drawRect(horizontalMargin + GUI_TOP_BAR_HOFFSET, verticalMargin + GUI_TOP_BAR_VOFFSET,
						horizontalMargin + GUI_TOP_BAR_HOFFSET + (int) (GUI_TOP_BAR_WIDTH * myAether), verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT,
						0xA0909000);
			}
			
			if (nestedAether > 0) {
				RenderFuncs.drawRect(horizontalMargin + GUI_BOTTOM_BAR_HOFFSET, verticalMargin + GUI_BOTTOM_BAR_VOFFSET,
						horizontalMargin + GUI_BOTTOM_BAR_HOFFSET + (int) (GUI_BOTTOM_BAR_WIDTH * nestedAether), verticalMargin + GUI_BOTTOM_BAR_VOFFSET + GUI_BOTTOM_BAR_HEIGHT,
						0xA0909000);
			}
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			
			IAetherHandler chargerHandler = container.chest.getHandler();
			if (chargerHandler != null) {
				if (mouseX >= horizontalMargin + GUI_TOP_BAR_HOFFSET && mouseX <= horizontalMargin + GUI_TOP_BAR_HOFFSET + GUI_TOP_BAR_WIDTH
						&& mouseY >= verticalMargin + GUI_TOP_BAR_VOFFSET && mouseY <= verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT) {
					renderTooltip(Lists.newArrayList(String.format("%.2f / %.2f", chargerHandler.getAether(null) * .01f, chargerHandler.getMaxAether(null) * .01f)),
							mouseX - horizontalMargin, mouseY - verticalMargin);
				}
			}
			
			final int maxAether = container.chest.getMaxAetherDisplay();
			final int aether = container.chest.getAetherDisplay();
			if (maxAether > 0) {
				if (mouseX >= horizontalMargin + GUI_BOTTOM_BAR_HOFFSET && mouseX <= horizontalMargin + GUI_BOTTOM_BAR_HOFFSET + GUI_BOTTOM_BAR_WIDTH
						&& mouseY >= verticalMargin + GUI_BOTTOM_BAR_VOFFSET && mouseY <= verticalMargin + GUI_BOTTOM_BAR_VOFFSET + GUI_BOTTOM_BAR_HEIGHT) {
					renderTooltip(Lists.newArrayList(String.format("%.2f / %.2f", aether * .01f, maxAether * .01f)),
							mouseX - horizontalMargin, mouseY - verticalMargin);
				}
			}
		}
		
	}
	
}
