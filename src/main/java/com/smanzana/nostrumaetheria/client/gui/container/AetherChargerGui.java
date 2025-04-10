package com.smanzana.nostrumaetheria.client.gui.container;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.tiles.AetherChargerBlockEntity;
import com.smanzana.nostrummagica.client.gui.container.AutoContainer;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.util.ContainerUtil;
import com.smanzana.nostrummagica.util.ContainerUtil.IPackedContainerProvider;
import com.smanzana.nostrummagica.util.Inventories;
import com.smanzana.nostrummagica.util.RenderFuncs;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
		
		public AetherChargerContainer(int windowId, Inventory playerInv, AetherChargerBlockEntity chest) {
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
				public boolean mayPlace(@Nonnull ItemStack stack) {
			        return this.container.canPlaceItem(this.getSlotIndex(), stack);
			    }
			});
		}
		
		public static final AetherChargerContainer FromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf buffer) {
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
		public ItemStack quickMoveStack(Player playerIn, int fromSlot) {
			ItemStack prev = ItemStack.EMPTY;	
			Slot slot = (Slot) this.slots.get(fromSlot);
			
			if (slot != null && slot.hasItem()) {
				ItemStack cur = slot.getItem();
				prev = cur.copy();
				
				if (slot.container == this.chest) {
					// Trying to take one of our items
					if (playerIn.inventory.add(cur)) {
						slot.set(ItemStack.EMPTY);
						cur = slot.onTake(playerIn, cur);
					} else {
						prev = ItemStack.EMPTY;
					}
				} else {
					// shift-click in player inventory
					ItemStack leftover = Inventories.addItem(chest, cur);
					slot.set(leftover.isEmpty() ? ItemStack.EMPTY : leftover);
					if (!leftover.isEmpty() && leftover.getCount() == prev.getCount()) {
						prev = ItemStack.EMPTY;
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
			return true;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class AetherChargerGuiContainer extends AutoGuiContainer<AetherChargerContainer> {

		private AetherChargerContainer container;
		
		public AetherChargerGuiContainer(AetherChargerContainer container, Inventory playerInv, Component name) {
			super(container, playerInv, name);
			this.container = container;
			
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
			
			IAetherHandler chargerHandler = container.chest.getHandler();
			
			float myAether = 0f;
			final float nestedAether = (float) container.chest.getAetherDisplay() / (float) container.chest.getMaxAetherDisplay();
			
			if (chargerHandler != null) {
				myAether = (float) chargerHandler.getAether(null) / (float) chargerHandler.getMaxAether(null);
			}
			
			if (myAether > 0) {
				RenderFuncs.drawRect(matrixStackIn, horizontalMargin + GUI_TOP_BAR_HOFFSET, verticalMargin + GUI_TOP_BAR_VOFFSET,
						horizontalMargin + GUI_TOP_BAR_HOFFSET + (int) (GUI_TOP_BAR_WIDTH * myAether), verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT,
						0xA0909000);
			}
			
			if (nestedAether > 0) {
				RenderFuncs.drawRect(matrixStackIn, horizontalMargin + GUI_BOTTOM_BAR_HOFFSET, verticalMargin + GUI_BOTTOM_BAR_VOFFSET,
						horizontalMargin + GUI_BOTTOM_BAR_HOFFSET + (int) (GUI_BOTTOM_BAR_WIDTH * nestedAether), verticalMargin + GUI_BOTTOM_BAR_VOFFSET + GUI_BOTTOM_BAR_HEIGHT,
						0xA0909000);
			}
		}
		
		@Override
		protected void renderLabels(PoseStack matrixStackIn, int mouseX, int mouseY) {
			int horizontalMargin = (width - imageWidth) / 2;
			int verticalMargin = (height - imageHeight) / 2;
			
			IAetherHandler chargerHandler = container.chest.getHandler();
			if (chargerHandler != null) {
				if (mouseX >= horizontalMargin + GUI_TOP_BAR_HOFFSET && mouseX <= horizontalMargin + GUI_TOP_BAR_HOFFSET + GUI_TOP_BAR_WIDTH
						&& mouseY >= verticalMargin + GUI_TOP_BAR_VOFFSET && mouseY <= verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT) {
					renderTooltip(matrixStackIn, new TextComponent(String.format("%.2f / %.2f", chargerHandler.getAether(null) * .01f, chargerHandler.getMaxAether(null) * .01f)),
							mouseX - horizontalMargin, mouseY - verticalMargin);
				}
			}
			
			final int maxAether = container.chest.getMaxAetherDisplay();
			final int aether = container.chest.getAetherDisplay();
			if (maxAether > 0) {
				if (mouseX >= horizontalMargin + GUI_BOTTOM_BAR_HOFFSET && mouseX <= horizontalMargin + GUI_BOTTOM_BAR_HOFFSET + GUI_BOTTOM_BAR_WIDTH
						&& mouseY >= verticalMargin + GUI_BOTTOM_BAR_VOFFSET && mouseY <= verticalMargin + GUI_BOTTOM_BAR_VOFFSET + GUI_BOTTOM_BAR_HEIGHT) {
					renderTooltip(matrixStackIn, new TextComponent(String.format("%.2f / %.2f", aether * .01f, maxAether * .01f)),
							mouseX - horizontalMargin, mouseY - verticalMargin);
				}
			}
		}
		
	}
	
}
