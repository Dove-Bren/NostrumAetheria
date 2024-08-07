package com.smanzana.nostrumaetheria.client.gui.container;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.tiles.WispBlockTileEntity;
import com.smanzana.nostrummagica.client.gui.container.AutoContainer;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.item.ReagentItem;
import com.smanzana.nostrummagica.item.SpellScroll;
import com.smanzana.nostrummagica.spell.Spell;
import com.smanzana.nostrummagica.util.ContainerUtil;
import com.smanzana.nostrummagica.util.ContainerUtil.IPackedContainerProvider;
import com.smanzana.nostrummagica.util.RenderFuncs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WispBlockGui {
	
	private static final ResourceLocation TEXT = new ResourceLocation(NostrumAetheria.MODID + ":textures/gui/container/wisp_block.png");

	private static final int GUI_WIDTH = 176;
	private static final int GUI_HEIGHT = 175;
	private static final int PLAYER_INV_HOFFSET = 8;
	private static final int PLAYER_INV_VOFFSET = 93;
	
	private static final int SCROLL_SLOT_INPUT_HOFFSET = 80;
	private static final int SCROLL_SLOT_INPUT_VOFFSET = 25;
	
	private static final int REAGENT_SLOT_INPUT_HOFFSET = 80;
	private static final int REAGENT_SLOT_INPUT_VOFFSET = 54;
	
	private static final int PROGRESS_WIDTH = 128;
	private static final int PROGRESS_HEIGHT = 3;
	private static final int PROGRESS_GUI_HOFFSET = 24;
	private static final int PROGRESS_GUI_VOFFSET = 75;
	
	private static final int WISP_SOCKET_LENGTH = 9;
	private static final int WISP_SOCKET_HOFFSET = 176;
	private static final int WISP_SOCKET_VOFFSET = 0;
	
	public static class WispBlockContainer extends AutoContainer {
		
		public static final String ID = "wisp_block";
		
		// Kept just to report to server which TE is doing crafting
		protected BlockPos pos;
		protected PlayerEntity player;
		
		// Actual container variables as well as a couple for keeping track
		// of crafting state
		protected WispBlockTileEntity table;
		protected Slot scrollSlot;
		protected Slot reagentSlot;
		
		public WispBlockContainer(int windowId, PlayerInventory playerInv, WispBlockTileEntity table) {
			super(AetheriaContainers.WispBlock, windowId, table);
			this.player = playerInv.player;
			this.pos = table.getPos();
			this.table = table;
			this.scrollSlot = new Slot(table, 0, SCROLL_SLOT_INPUT_HOFFSET, SCROLL_SLOT_INPUT_VOFFSET) {
				
				@Override
				public boolean isItemValid(@Nonnull ItemStack stack) {
					return stack.isEmpty() ||
							(stack.getItem() instanceof SpellScroll && SpellScroll.GetSpell(stack) != null);
				}
//				
//				@Override
//				public void putStack(@Nullable ItemStack stack) {
//					table.setScroll(stack);
//					this.onSlotChanged();
//				}
//				
//				@Override
//				public ItemStack getStack() {
//					return table.getScroll();
//				}
//				
//				@Override
//				public void onSlotChanged() {
//					table.markDirty();
//				}
//				
//				public int getSlotStackLimit() {
//					return 1;
//				}
//				
//				public ItemStack decrStackSize(int amount) {
//					ItemStack item = table.getScroll();
//					if (item != null) {
//						if (table.setScroll(null))
//							return item.copy();
//					}
//					
//					return null;
//				}
//				
//				public boolean isHere(IInventory inv, int slotIn) {
//					return false;
//				}
//				
//				public boolean isSameInventory(Slot other) {
//					return false;
//				}
//				
//				public void onPickupFromSlot(PlayerEntity playerIn, ItemStack stack) {
//					//table.onTakeItem(playerIn);
//					super.onPickupFromSlot(playerIn, stack);
//				}
			};
			
			this.addSlot(scrollSlot);
			
			this.reagentSlot = new Slot(table, 1, REAGENT_SLOT_INPUT_HOFFSET, REAGENT_SLOT_INPUT_VOFFSET) {
				
				@Override
				public boolean isItemValid(@Nonnull ItemStack stack) {
					return stack.isEmpty() || stack.getItem() instanceof ReagentItem;
				}
//				
//				@Override
//				public void putStack(@Nullable ItemStack stack) {
//					table.setReagent(stack);
//					this.onSlotChanged();
//				}
//				
//				@Override
//				public ItemStack getStack() {
//					return table.getReagent();
//				}
//				
//				@Override
//				public void onSlotChanged() {
//					table.markDirty();
//				}
//				
//				public int getSlotStackLimit() {
//					return 64;
//				}
//				
//				public ItemStack decrStackSize(int amount) {
//					ItemStack item = table.getReagent();
//					if (item != null) {
//						if (table.setReagent(null))
//							return item.copy();
//					}
//					
//					return null;
//				}
//				
//				public boolean isHere(IInventory inv, int slotIn) {
//					return false;
//				}
//				
//				public boolean isSameInventory(Slot other) {
//					return false;
//				}
//				
//				public void onPickupFromSlot(PlayerEntity playerIn, ItemStack stack) {
//					//table.onTakeItem(playerIn);
//					super.onPickupFromSlot(playerIn, stack);
//				}
				
			};
			
			this.addSlot(reagentSlot);
			
			// Construct player inventory
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; x++) {
					this.addSlot(new Slot(playerInv, x + y * 9 + 9, PLAYER_INV_HOFFSET + (x * 18), PLAYER_INV_VOFFSET + (y * 18)));
				}
			}
			// Construct player hotbar
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(playerInv, x, PLAYER_INV_HOFFSET + x * 18, 58 + (PLAYER_INV_VOFFSET)));
			}
			
		}
		
		public static final WispBlockContainer FromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer buffer) {
			return new WispBlockContainer(windowId, playerInv, ContainerUtil.GetPackedTE(buffer));
		}
		
		public static IPackedContainerProvider Make(WispBlockTileEntity te) {
			return ContainerUtil.MakeProvider(ID, (windowId, playerInv, player) -> {
				return new WispBlockContainer(windowId, playerInv, te);
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
				
				if (slot == this.scrollSlot) {
					// Trying to take our scroll
					if (playerIn.inventory.addItemStackToInventory(cur)) {
						scrollSlot.putStack(ItemStack.EMPTY);
						scrollSlot.onTake(playerIn, cur);
					} else {
						prev = ItemStack.EMPTY;
					}
				} else if (slot == this.reagentSlot) {
					// Trying to take our reagent
					if (playerIn.inventory.addItemStackToInventory(cur)) {
						reagentSlot.putStack(ItemStack.EMPTY);
						reagentSlot.onTake(playerIn, cur);
					} else {
						prev = ItemStack.EMPTY;
					}
				} else {
					// Trying to add an item
					if (!scrollSlot.getHasStack()
							&& scrollSlot.isItemValid(cur)) {
						ItemStack stack = cur.split(1);
						scrollSlot.putStack(stack);
					} else if (!reagentSlot.getHasStack()
							&& reagentSlot.isItemValid(cur)) {
						ItemStack stack = cur.split(cur.getMaxStackSize());
						reagentSlot.putStack(stack);
					} else {
						prev = ItemStack.EMPTY;
					}
				}
				
			}
			
			return prev;
		}
		
		@Override
		public boolean canDragIntoSlot(Slot slotIn) {
			return slotIn != scrollSlot && slotIn != reagentSlot;
		}
		
		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}
		
		public void setScroll(ItemStack item) {
			scrollSlot.putStack(item);
		}
		
		public void setReagent(ItemStack item) {
			reagentSlot.putStack(item);
		}

	}
	
	@OnlyIn(Dist.CLIENT)
	public static class WispBlockGuiContainer extends AutoGuiContainer<WispBlockContainer> {

		private WispBlockContainer container;
		
		public WispBlockGuiContainer(WispBlockContainer container, PlayerInventory playerInv, ITextComponent name) {
			super(container, playerInv, name);
			this.container = container;
			
			this.xSize = GUI_WIDTH;
			this.ySize = GUI_HEIGHT;
		}
		
		@Override
		public void init() {
			super.init();
		}
		
		@Override
		protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStackIn, float partialTicks, int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			@Nonnull ItemStack scroll = container.table.getScroll();
			int color = 0xFFFFFFFF;
			
			if (!scroll.isEmpty()) {
				Spell spell = SpellScroll.GetSpell(scroll);
				if (spell != null) {
					color = spell.getPrimaryElement().getColor();
				}
			}
			float R = (float) ((color & 0x00FF0000) >> 16) / 256f;
			float G = (float) ((color & 0x0000FF00) >> 8) / 256f;
			float B = (float) ((color & 0x000000FF) >> 0) / 256f;
			
			mc.getTextureManager().bindTexture(TEXT);
			
			RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, horizontalMargin, verticalMargin, 0,0, GUI_WIDTH, GUI_HEIGHT, 256, 256);
			
			float fuel = container.table.getPartialReagent();
			if (fuel > 0f) {
				int x = (int) (fuel * PROGRESS_WIDTH);
				RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, 
						horizontalMargin + PROGRESS_GUI_HOFFSET,
						verticalMargin + PROGRESS_GUI_VOFFSET,
						0, GUI_HEIGHT, x, PROGRESS_HEIGHT, 256, 256,
						R, G, B, 1f);
			}
			
			int max = container.table.getMaxWisps();
			int filled = container.table.getWispCount();
			for (int i = 0; i < max; i++) {
				final int centerx = horizontalMargin + (GUI_WIDTH / 2);
				final int xspace = 20;
				final int leftx = centerx - ((xspace / 2) * (max - 1));
				final int x = leftx + (xspace * i) - (WISP_SOCKET_LENGTH / 2);
				final int y = verticalMargin + PROGRESS_GUI_VOFFSET + 7;
				RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, x, y,
						WISP_SOCKET_HOFFSET,
						WISP_SOCKET_VOFFSET,
						WISP_SOCKET_LENGTH,
						WISP_SOCKET_LENGTH,
						256, 256);
				if (i < filled) {
					RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, x + 2, y + 2,
							WISP_SOCKET_HOFFSET,
							WISP_SOCKET_VOFFSET + WISP_SOCKET_LENGTH,
							5,
							5,
							256, 256,
							R, G, B, 1f);
				}
			}
			
			
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(MatrixStack matrixStackIn, int mouseX, int mouseY) {
			int horizontalMargin = (width - xSize) / 2;
			int verticalMargin = (height - ySize) / 2;
			/*
			 * horizontalMargin + PROGRESS_GUI_HOFFSET,
						verticalMargin + PROGRESS_GUI_VOFFSET,
						0, GUI_HEIGHT, x, PROGRESS_HEIGHT, 256, 256);
			 */
			if (mouseX >= horizontalMargin + PROGRESS_GUI_HOFFSET
					&& mouseX <= horizontalMargin + PROGRESS_GUI_HOFFSET + PROGRESS_WIDTH
					&& mouseY >= verticalMargin + PROGRESS_GUI_VOFFSET
					&& mouseY <= verticalMargin + PROGRESS_GUI_VOFFSET + PROGRESS_HEIGHT) {
				this.renderTooltip(matrixStackIn, new StringTextComponent(((int) (container.table.getPartialReagent() * 100.0)) + "%"), mouseX - horizontalMargin, mouseY - verticalMargin);
			}
		}
		
	}
}