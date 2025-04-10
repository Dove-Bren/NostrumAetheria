package com.smanzana.nostrumaetheria.network;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.network.messages.AetherBoilerModeChangeMessage;
import com.smanzana.nostrumaetheria.network.messages.AetherTileEntityMessage;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class NetworkHandler {

	private static SimpleChannel syncChannel;
	
	private static int discriminator = 0;
	
	private static final String CHANNEL_SYNC_NAME = "nostrumaether_net"; // must be <20 chars
	private static final String PROTOCOL = "1";
	
	
	public static SimpleChannel getSyncChannel() {
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
		
		syncChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(NostrumAetheria.MODID, CHANNEL_SYNC_NAME),
				() -> PROTOCOL,
				PROTOCOL::equals,
				PROTOCOL::equals
				);
		
		syncChannel.registerMessage(discriminator++, AetherTileEntityMessage.class, AetherTileEntityMessage::encode, AetherTileEntityMessage::decode, AetherTileEntityMessage::handle);
		syncChannel.registerMessage(discriminator++, AetherBoilerModeChangeMessage.class, AetherBoilerModeChangeMessage::encode, AetherBoilerModeChangeMessage::decode, AetherBoilerModeChangeMessage::handle);
	}
	
}
