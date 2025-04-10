package com.smanzana.nostrumaetheria.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	
	//protected OverlayRenderer overlayRenderer;

	public ClientProxy() {
		super();
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public boolean isServer() {
		return false;
	}
	
	@Override
	public Player getPlayer() {
		Minecraft mc = Minecraft.getInstance();
		return mc.player;
	}
	
	@SubscribeEvent
	public void onClientConnect(EntityJoinWorldEvent event) {
//		if (event.getEntity() == Minecraft.getInstance().thePlayer) {
//			// Every time we join a world, request a copy of its networks
//			
//			NostrumFairies.logger.info("Requested automatic logistics network refresh");
//			NetworkHandler.getSyncChannel().sendToServer(new LogisticsUpdateRequest());
//		}
	}
	
}
