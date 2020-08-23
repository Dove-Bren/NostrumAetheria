package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.network.messages.AetherTileEntityMessage;

import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class AetheriaAPIProxy extends APIProxy {

	@Override
	protected void handleSyncTEAether(AetherTileEntity te) {
		NetworkHandler.getSyncChannel().sendToAllAround(new AetherTileEntityMessage(te),
				new TargetPoint(te.getWorld().provider.getDimension(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), 64));
	}
	
}
