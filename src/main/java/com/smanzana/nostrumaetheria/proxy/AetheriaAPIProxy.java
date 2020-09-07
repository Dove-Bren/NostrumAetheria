package com.smanzana.nostrumaetheria.proxy;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerItem;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerProvider;
import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.item.AetherItem;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.network.messages.AetherTileEntityMessage;
import com.smanzana.nostrummagica.NostrumMagica;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class AetheriaAPIProxy extends APIProxy {

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
			
			return start - amount;
		}
		
		return 0;
	}
	
}
