package com.smanzana.nostrumaetheria.proxy;

import net.minecraft.world.entity.player.Player;

public class CommonProxy {
	
	public CommonProxy() {
		
	}
	
	public Player getPlayer() {
		return null; // Doesn't mean anything on the server
	}
	
	public boolean isServer() {
		return true;
	}
}
