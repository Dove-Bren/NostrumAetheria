package com.smanzana.nostrumaetheria.client.gui.container;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.tiles.AetherFurnaceBlockEntity;
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
	private static final int GUI_TOP_BAR_HOFFSET = 76;
	private static final int GUI_TOP_BAR_VOFFSET = 12;
	private static final int GUI_TOP_BAR_WIDTH = 24;
	private static final int GUI_TOP_BAR_HEIGHT = 3;
	
	private static final int GUI_FIRE_FIRE_WIDTH = 13;
	private static final int GUI_FIRE_FIRE_HEIGHT = 14;

	public static class AetherFurnaceContainer extends AutoContainer {
		
		public static final String ID = "aether_furnace";
		
		protected AetherFurnaceBlockEntity chest;
		
		public AetherFurnaceContainer(int windowId, PlayerInventory playerInv, AetherFurnaceBlockEntity chest) {
			super(AetheriaContainers.Furnace, windowId, chest);
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
			
			int total = chest.getSizeInventory();
			for (int i = 0; i < total; i++) {
				int x = i - ((total - 3) / 2);
				
				this.addSlot(new Slot(chest, i, GUI_TOP_INV_HOFFSET + x * GUI_INV_CELL_LENGTH, GUI_TOP_INV_VOFFSET) {
					public boolean isItemValid(@Nonnull ItemStack stack) {
				        return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
				    }
				});
			}
		}
		
		public static final AetherFurnaceContainer FromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer buffer) {
			return new AetherFurnaceContainer(windowId, playerInv, ContainerUtil.GetPackedTE(buffer));
		}
		
		public static IPackedContainerProvider Make(AetherFurnaceBlockEntity te) {
			return ContainerUtil.MakeProvider(ID, (windowId, playerInv, player) -> {
				return new AetherFurnaceContainer(windowId, playerInv, te);
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
	public static class AetherFurnaceGuiContainer extends AutoGuiContainer<AetherFurnaceContainer> {

		private AetherFurnaceContainer container;
		
		public AetherFurnaceGuiContainer(AetherFurnaceContainer container, PlayerInventory playerInv, ITextComponent name) {
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
			int fireX = 45;
			
			GlStateManager.color4f(1.0F,  1.0F, 1.0F, 1.0F);
			
			switch (container.chest.getFurnceType()) {
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
			
			RenderFuncs.drawModalRectWithCustomSizedTexture(horizontalMargin, verticalMargin, 0,0, GUI_TEXT_WIDTH, GUI_TEXT_HEIGHT, 256, 256);
			
			float progress = container.chest.getBurnProgress();
			if (progress > 0) {
				//System.out.println("progress: " + progress);
				int y = (int) (14f * (1f - progress));
				RenderFuncs.drawModalRectWithCustomSizedTexture(horizontalMargin + GUI_TOP_INV_HOFFSET - fireX, verticalMargin + GUI_TOP_INV_VOFFSET + 2 + y,
						176, y,
						GUI_FIRE_FIRE_WIDTH, GUI_FIRE_FIRE_HEIGHT - y, 256, 256);
				RenderFuncs.drawModalRectWithCustomSizedTexture(horizontalMargin + GUI_TOP_INV_HOFFSET + (GUI_INV_CELL_LENGTH * 3) + -1 + (fireX - GUI_FIRE_FIRE_WIDTH), verticalMargin + GUI_TOP_INV_VOFFSET + 2 + y,
						176, y,
						GUI_FIRE_FIRE_WIDTH, GUI_FIRE_FIRE_HEIGHT - y, 256, 256);
			}
			
			GlStateManager.color4f(1f, 1f, 1f, 1f);
			
			IAetherHandler furnaceHandler = container.chest.getHandler();
			float myAether = 0f;
			
			if (furnaceHandler != null) {
				myAether = (float) furnaceHandler.getAether(null) / (float) furnaceHandler.getMaxAether(null);
			}
			
			if (myAether > 0) {
				RenderFuncs.drawRect(horizontalMargin + GUI_TOP_BAR_HOFFSET, verticalMargin + GUI_TOP_BAR_VOFFSET,
						horizontalMargin + GUI_TOP_BAR_HOFFSET + (int) (GUI_TOP_BAR_WIDTH * myAether), verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT,
						0xA0909000);
			}
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			
			IAetherHandler furnaceHandler = container.chest.getHandler();
			if (furnaceHandler != null) {
				if (mouseX >= horizontalMargin + GUI_TOP_BAR_HOFFSET && mouseX <= horizontalMargin + GUI_TOP_BAR_HOFFSET + GUI_TOP_BAR_WIDTH
						&& mouseY >= verticalMargin + GUI_TOP_BAR_VOFFSET && mouseY <= verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT) {
					renderTooltip(Lists.newArrayList(String.format("%.2f / %.2f", furnaceHandler.getAether(null) * .01f, furnaceHandler.getMaxAether(null) * .01f)),
							mouseX - horizontalMargin, mouseY - verticalMargin);
				}
			}
		}
		
	}
	
}
