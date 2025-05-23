package com.smanzana.nostrumaetheria.client.gui.container;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.network.messages.AetherBoilerModeChangeMessage;
import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity.BoilerBurnMode;
import com.smanzana.nostrummagica.client.gui.container.AutoContainer;
import com.smanzana.nostrummagica.client.gui.container.AutoGuiContainer;
import com.smanzana.nostrummagica.util.ContainerUtil;
import com.smanzana.nostrummagica.util.ContainerUtil.IPackedContainerProvider;
import com.smanzana.nostrummagica.util.Inventories;
import com.smanzana.nostrummagica.util.RenderFuncs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
		
		public static final String ID = "aether_boiler";
		
		protected AetherBoilerBlockEntity chest;
		
		public AetherBoilerContainer(int windowId, Inventory playerInv, AetherBoilerBlockEntity chest) {
			super(AetheriaContainers.Boiler, windowId, chest);
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
		
		public static final AetherBoilerContainer FromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf buffer) {
			return new AetherBoilerContainer(windowId, playerInv, ContainerUtil.GetPackedTE(buffer));
		}
		
		public static IPackedContainerProvider Make(AetherBoilerBlockEntity te) {
			return ContainerUtil.MakeProvider(ID, (windowId, playerInv, player) -> {
				return new AetherBoilerContainer(windowId, playerInv, te);
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
					if (playerIn.getInventory().add(cur)) {
						slot.set(ItemStack.EMPTY);
						slot.onTake(playerIn, cur);
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
	public static class AetherBoilerGuiContainer extends AutoGuiContainer<AetherBoilerContainer> {

		private AetherBoilerContainer container;
		
		private ModeButton modeButton;
		
		public AetherBoilerGuiContainer(AetherBoilerContainer container, Inventory playerInv, Component name) {
			super(container, playerInv, name);
			this.container = container;
			
			this.imageWidth = GUI_TEXT_WIDTH;
			this.imageHeight = GUI_TEXT_HEIGHT;
		}
		
		@Override
		public void init() {
			super.init();
			
			modeButton = new ModeButton(leftPos + GUI_MODE_BUTTON_UI_HOFFSET, topPos + GUI_MODE_BUTTON_UI_VOFFSET, this);
			this.addRenderableWidget(modeButton);
		}
		
		@Override
		protected void renderBg(PoseStack matrixStackIn, float partialTicks, int mouseX, int mouseY) {
			int horizontalMargin = (width - imageWidth) / 2;
			int verticalMargin = (height - imageHeight) / 2;
			
			RenderSystem.setShaderTexture(0, TEXT);
			
			RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, horizontalMargin, verticalMargin, 0,0, GUI_TEXT_WIDTH, GUI_TEXT_HEIGHT, 256, 256);
			
			float progress = container.chest.getBurnProgress();
			if (progress > 0) {
				int y = (int) (14f * (1f - progress));
				RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, horizontalMargin + 64, verticalMargin + GUI_TOP_INV_VOFFSET + 2 + y,
						176, y,
						GUI_FIRE_FIRE_WIDTH, GUI_FIRE_FIRE_HEIGHT - y, 256, 256);
				RenderFuncs.drawModalRectWithCustomSizedTextureImmediate(matrixStackIn, horizontalMargin + 101, verticalMargin + GUI_TOP_INV_VOFFSET + 2 + y,
						176, y,
						GUI_FIRE_FIRE_WIDTH, GUI_FIRE_FIRE_HEIGHT - y, 256, 256);
			}
			
			IAetherHandler boilerHandler = container.chest.getHandler();
			float myAether = 0f;
			
			if (boilerHandler != null) {
				myAether = (float) boilerHandler.getAether(null) / (float) boilerHandler.getMaxAether(null);
			}
			
			if (myAether > 0) {
				RenderFuncs.drawRect(matrixStackIn, horizontalMargin + GUI_TOP_BAR_HOFFSET, verticalMargin + GUI_TOP_BAR_VOFFSET,
						horizontalMargin + GUI_TOP_BAR_HOFFSET + (int) (GUI_TOP_BAR_WIDTH * myAether), verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT,
						0xA0909000);
			}
		}
		
		@Override
		protected void renderLabels(PoseStack matrixStackIn, int mouseX, int mouseY) {
			int horizontalMargin = (width - imageWidth) / 2;
			int verticalMargin = (height - imageHeight) / 2;
			
			IAetherHandler boilerHandler = container.chest.getHandler();
			if (boilerHandler != null) {
				if (mouseX >= horizontalMargin + GUI_TOP_BAR_HOFFSET && mouseX <= horizontalMargin + GUI_TOP_BAR_HOFFSET + GUI_TOP_BAR_WIDTH
						&& mouseY >= verticalMargin + GUI_TOP_BAR_VOFFSET && mouseY <= verticalMargin + GUI_TOP_BAR_VOFFSET + GUI_TOP_BAR_HEIGHT) {
					renderTooltip(matrixStackIn, new TextComponent(String.format("%.2f / %.2f", boilerHandler.getAether(null) * .01f, boilerHandler.getMaxAether(null) * .01f)),
							mouseX - horizontalMargin, mouseY - verticalMargin);
				}
			}
		}
		
		protected void modeClicked(Button button) {
			// Send message requesting mode cycle
			BoilerBurnMode next;
			int ord = this.container.chest.getBoilerMode().ordinal() + 1;
			BoilerBurnMode[] modes = BoilerBurnMode.values();
			
			if (ord >= modes.length) {
				ord = 0;
			}
			
			next = modes[ord];
			NetworkHandler.getSyncChannel().sendToServer(new AetherBoilerModeChangeMessage(
					this.container.chest.getBlockPos(), next
					));
		}
		
		protected class ModeButton extends Button {
			
			private boolean pressed;
			
			public ModeButton(int x, int y, AetherBoilerGuiContainer gui) {
				super(x, y, GUI_MODE_BUTTON_UI_WIDTH, GUI_MODE_BUTTON_UI_HEIGHT, TextComponent.EMPTY, (b) -> {
					gui.modeClicked(b);
				});
				pressed = false;
			}
			
			@Override
			public void render(PoseStack matrixStackIn, int mouseX, int mouseY, float partialTicks) {
				this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				int textX = GUI_MODE_BUTTON_TEXT_HOFFSET;
				if (pressed) {
					textX += GUI_MODE_BUTTON_TEXT_WIDTH * 2;
				} else if (isHovered) {
					textX += GUI_MODE_BUTTON_TEXT_WIDTH;
				}
				
				RenderSystem.setShaderTexture(0, TEXT);
				RenderSystem.enableBlend();
				matrixStackIn.pushPose();
				matrixStackIn.translate(x, y, 0);
				RenderFuncs.drawScaledCustomSizeModalRectImmediate(matrixStackIn, 0, 0,
						textX, GUI_MODE_BUTTON_TEXT_VOFFSET,
						GUI_MODE_BUTTON_TEXT_WIDTH, GUI_MODE_BUTTON_TEXT_HEIGHT,
						GUI_MODE_BUTTON_UI_WIDTH, GUI_MODE_BUTTON_UI_HEIGHT,
						256, 256);
				
				// Draw actual mode overlay
				BoilerBurnMode mode = container.chest.getBoilerMode();
				drawBoilerMode(matrixStackIn, mc, mode);
				matrixStackIn.popPose();
				RenderSystem.disableBlend();
				
				// Draw tooltip if hovered
				if (this.isHovered) {
					AetherBoilerGuiContainer.this.renderTooltip(matrixStackIn, new TranslatableComponent("gui.aether_boiler.mode." + mode.getUnlocID()), mouseX + 8, mouseY + 8);
				}
			}
			
			@Override
			public void onRelease(double mouseX, double mouseY) {
				pressed = false;
				super.onRelease(mouseX, mouseY);
			}
			
			@Override
			public void onClick(double mouseX, double mouseY) {
				super.onClick(mouseX, mouseY);
				pressed = true;
			}
			
			protected void drawBoilerMode(PoseStack matrixStackIn, Minecraft mc, BoilerBurnMode mode) {
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
				
				RenderSystem.setShaderTexture(0, TEXT);
				RenderSystem.enableBlend();
				RenderFuncs.drawScaledCustomSizeModalRectImmediate(matrixStackIn, 1, 1,
						textX, textY,
						GUI_MODE_ICON_WIDTH, GUI_MODE_ICON_HEIGHT,
						GUI_MODE_BUTTON_UI_WIDTH - 2, GUI_MODE_BUTTON_UI_HEIGHT - 2,
						256, 256);
				RenderSystem.disableBlend();
			}
		}
		
	}
	
}
