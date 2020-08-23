package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.network.messages.AetherTileEntityMessage;
import com.smanzana.nostrummagica.NostrumMagica;

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
	
}
