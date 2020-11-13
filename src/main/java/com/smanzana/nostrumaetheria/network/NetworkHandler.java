package com.smanzana.nostrumaetheria.network;

import com.smanzana.nostrumaetheria.network.messages.AetherTileEntityMessage;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

	private static SimpleNetworkWrapper syncChannel;
	
	private static int discriminator = 0;
	
	private static final String CHANNEL_SYNC_NAME = "nostrumaether_net"; // must be <20 chars
	
	
	public static SimpleNetworkWrapper getSyncChannel() {
		getInstance();
		return syncChannel;
	}
	
	private static NetworkHandler instance;
	
	public static NetworkHandler getInstance() {
		if (instance == null)
			instance = new NetworkHandler();
		
		return instance;
	}
	
	public NetworkHandler() {
		
		syncChannel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL_SYNC_NAME);
		
		syncChannel.registerMessage(AetherTileEntityMessage.Handler.class, AetherTileEntityMessage.class, discriminator++, Side.CLIENT);
//		syncChannel.registerMessage(ClientCastMessage.Handler.class, ClientCastMessage.class, discriminator++, Side.SERVER);
	}
	
}
