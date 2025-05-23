package com.smanzana.nostrumaetheria.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerItem;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerProvider;
import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.capability.AetherBurnableWrapper;
import com.smanzana.nostrumaetheria.api.capability.IAetherBurnable;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.event.LivingAetherDrawEvent;
import com.smanzana.nostrumaetheria.api.event.LivingAetherDrawEvent.Phase;
import com.smanzana.nostrumaetheria.api.item.AetherItem;
import com.smanzana.nostrumaetheria.api.item.IAetherVisionProvider;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;
import com.smanzana.nostrumaetheria.api.recipes.IAetherUnravelerRecipe;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.network.messages.AetherTileEntityMessage;
import com.smanzana.nostrumaetheria.recipes.RepairerRecipeManager;
import com.smanzana.nostrumaetheria.recipes.UnravelerRecipeManager;
import com.smanzana.nostrummagica.NostrumMagica;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;

public class AetheriaAPIProxy extends APIProxy {

	@Override
	public boolean handleIsEnabled() {
		return true;
	}
	
	@Override
	protected void handleSyncTEAether(AetherTileEntity te) {
		NetworkHandler.getSyncChannel().send(PacketDistributor.NEAR.with(() ->
				new TargetPoint(te.getBlockPos().getX(), te.getBlockPos().getY(), te.getBlockPos().getZ(), 64, te.getLevel().dimension())),
				new AetherTileEntityMessage(te));
	}

	@Override
	protected boolean handleIsBlockLoaded(Level world, BlockPos pos) {
		return NostrumMagica.isBlockLoaded(world, pos);
	}

	@Override
	protected int handleDrawFromInventory(@Nullable Level world, @Nullable Entity entity, Container inventory, int amount, @Nonnull ItemStack ignore) {
		final int origAmt = amount;
		
		if (entity instanceof LivingEntity) {
			LivingAetherDrawEvent event = new LivingAetherDrawEvent(Phase.BEFORE_EARLY, (LivingEntity) entity, ignore, origAmt, amount);
			MinecraftForge.EVENT_BUS.post(event);
			amount = event.getAmtRemaining();
		}
		
		if (amount > 0 && inventory != null) {
			final int start = amount;
			for (int i = 0; i < inventory.getContainerSize() && amount > 0; i++) {
				ItemStack inSlot = inventory.getItem(i);
				if (!inSlot.isEmpty() && inSlot != ignore) {
					if (inSlot.getItem() instanceof AetherItem) {
						AetherItem otherItem = (AetherItem) inSlot.getItem();
						if (otherItem.canBeDrawnFrom(inSlot, world, entity)) {
							amount -= otherItem.deductAether(inSlot, amount);
						}
					} else {
						IAetherHandler otherHandler = null;
						if (inSlot.getItem() instanceof IAetherHandler) {
							otherHandler = (IAetherHandler) inSlot.getItem();
						} else if (inSlot.getItem() instanceof IAetherHandlerProvider) {
							otherHandler = ((IAetherHandlerProvider) inSlot.getItem()).getHandler();
						} else if (inSlot.getItem() instanceof IAetherHandlerItem) {
							otherHandler = ((IAetherHandlerItem) inSlot.getItem()).getAetherHandler(inSlot);
						}
						
						if (otherHandler != null) {
							amount -= otherHandler.drawAether(null, amount);
						}
					}
				}
			}
			
			if (entity instanceof LivingEntity) {
				LivingAetherDrawEvent event = new LivingAetherDrawEvent(Phase.BEFORE_LATE, (LivingEntity) entity, ignore, origAmt, amount);
				MinecraftForge.EVENT_BUS.post(event);
				amount = event.getAmtRemaining();
			}
			
			if (entity instanceof LivingEntity) {
				LivingAetherDrawEvent event = new LivingAetherDrawEvent(Phase.AFTER, (LivingEntity) entity, ignore, origAmt, amount);
				MinecraftForge.EVENT_BUS.post(event);
			}
			
			return start - amount;
		}
		
		return 0;
	}
	
	@Override
	protected int handlePushToInventory(Level world, Entity entity, Container inventory, int amount) {
		
		if (amount > 0 && inventory != null) {
			final int start = amount;
			for (int i = 0; i < inventory.getContainerSize() && amount > 0; i++) {
				ItemStack inSlot = inventory.getItem(i);
				if (!inSlot.isEmpty()) {
					if (inSlot.getItem() instanceof AetherItem) {
						AetherItem otherItem = (AetherItem) inSlot.getItem();
						amount = otherItem.addAether(inSlot, amount);
					} else {
						IAetherHandler otherHandler = null;
						if (inSlot.getItem() instanceof IAetherHandler) {
							otherHandler = (IAetherHandler) inSlot.getItem();
						} else if (inSlot.getItem() instanceof IAetherHandlerProvider) {
							otherHandler = ((IAetherHandlerProvider) inSlot.getItem()).getHandler();
						} else if (inSlot.getItem() instanceof IAetherHandlerItem) {
							otherHandler = ((IAetherHandlerItem) inSlot.getItem()).getAetherHandler(inSlot);
						}
						
						if (otherHandler != null) {
							amount = otherHandler.addAether(null, amount);
						}
					}
				}
			}
			
			return start - amount;
		}
		
		return 0;
	}

	@Override
	protected IAetherHandlerComponent handleCreateHandlerComponent(@Nullable ResourceKey<Level> dimension, @Nullable BlockPos pos, IAetherComponentListener listener, int defaultAether,
			int defaultMaxAether) {
		return new AetherHandlerComponent(dimension, pos, listener, defaultAether, defaultMaxAether);
	}

	@Override
	protected void handleAddRepairerRecipe(IAetherRepairerRecipe recipe) {
		RepairerRecipeManager.instance().addRecipe(recipe);
	}

	@Override
	protected void handleAddUnravelerRecipe(IAetherUnravelerRecipe recipe) {
		UnravelerRecipeManager.instance().addRecipe(recipe);
	}

	@Override
	protected Player handleGetClientPlayer() {
		return NostrumAetheria.proxy.getPlayer();
	}

	@Override
	protected boolean handleHasAetherVision(Player player) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = player.getItemBySlot(slot);
			if (!stack.isEmpty() && stack.getItem() instanceof IAetherVisionProvider) {
				if (((IAetherVisionProvider) stack.getItem()).shouldProvideAetherVision(stack, player, slot)) {
					return true;
				}
			}
		}
		
		@Nullable Container curioInv = NostrumAetheria.curios.getCurios(player);
		if (curioInv != null) {
			for (int i = 0; i < curioInv.getContainerSize(); i++) {
				ItemStack stack = curioInv.getItem(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IAetherVisionProvider) {
					if (((IAetherVisionProvider) stack.getItem()).shouldProvideAetherVision(stack, player, null)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	protected IAetherBurnable handleMakeBurnable(int burnTicks, float aether) {
		return new AetherBurnableWrapper(burnTicks, aether);
	}
	
}
