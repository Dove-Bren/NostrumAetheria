package com.smanzana.nostrumaetheria.proxy;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerItem;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerProvider;
import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.event.LivingAetherDrawEvent;
import com.smanzana.nostrumaetheria.api.event.LivingAetherDrawEvent.Phase;
import com.smanzana.nostrumaetheria.api.item.AetherItem;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;
import com.smanzana.nostrumaetheria.api.recipes.IAetherUnravelerRecipe;
import com.smanzana.nostrumaetheria.component.AetherHandlerComponent;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.network.messages.AetherTileEntityMessage;
import com.smanzana.nostrumaetheria.recipes.RepairerRecipeManager;
import com.smanzana.nostrumaetheria.recipes.UnravelerRecipeManager;
import com.smanzana.nostrummagica.NostrumMagica;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class AetheriaAPIProxy extends APIProxy {

	@Override
	public boolean handleIsEnabled() {
		return true;
	}
	
	@Override
	protected void handleSyncTEAether(AetherTileEntity te) {
		NetworkHandler.getSyncChannel().sendToAllAround(new AetherTileEntityMessage(te),
				new TargetPoint(te.getWorld().provider.getDimension(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), 64));
	}

	@Override
	protected boolean handleIsBlockLoaded(World world, BlockPos pos) {
		return NostrumMagica.isBlockLoaded(world, pos);
	}

	@Override
	protected int handleDrawFromInventory(@Nullable World world, @Nullable Entity entity, IInventory inventory, int amount, @Nullable ItemStack ignore) {
		final int origAmt = amount;
		
		if (entity instanceof EntityLivingBase) {
			LivingAetherDrawEvent event = new LivingAetherDrawEvent(Phase.BEFORE_EARLY, (EntityLivingBase) entity, ignore, origAmt, amount);
			MinecraftForge.EVENT_BUS.post(event);
			amount = event.getAmtRemaining();
		}
		
		if (amount > 0 && inventory != null) {
			final int start = amount;
			for (int i = 0; i < inventory.getSizeInventory() && amount > 0; i++) {
				ItemStack inSlot = inventory.getStackInSlot(i);
				if (inSlot != null && inSlot != ignore) {
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
			
			if (entity instanceof EntityLivingBase) {
				LivingAetherDrawEvent event = new LivingAetherDrawEvent(Phase.BEFORE_LATE, (EntityLivingBase) entity, ignore, origAmt, amount);
				MinecraftForge.EVENT_BUS.post(event);
				amount = event.getAmtRemaining();
			}
			
			if (entity instanceof EntityLivingBase) {
				LivingAetherDrawEvent event = new LivingAetherDrawEvent(Phase.AFTER, (EntityLivingBase) entity, ignore, origAmt, amount);
				MinecraftForge.EVENT_BUS.post(event);
			}
			
			return start - amount;
		}
		
		return 0;
	}
	
	@Override
	protected int handlePushToInventory(World world, Entity entity, IInventory inventory, int amount) {
		
		if (amount > 0 && inventory != null) {
			final int start = amount;
			for (int i = 0; i < inventory.getSizeInventory() && amount > 0; i++) {
				ItemStack inSlot = inventory.getStackInSlot(i);
				if (inSlot != null) {
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
	protected IAetherHandlerComponent handleCreateHandlerComponent(IAetherComponentListener listener, int defaultAether,
			int defaultMaxAether) {
		return new AetherHandlerComponent(listener, defaultAether, defaultMaxAether);
	}

	@Override
	protected void handleAddRepairerRecipe(IAetherRepairerRecipe recipe) {
		RepairerRecipeManager.instance().addRecipe(recipe);
	}

	@Override
	protected void handleAddUnravelerRecipe(IAetherUnravelerRecipe recipe) {
		UnravelerRecipeManager.instance().addRecipe(recipe);
	}
	
}
