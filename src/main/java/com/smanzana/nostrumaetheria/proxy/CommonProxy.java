package com.smanzana.nostrumaetheria.proxy;

import net.minecraft.entity.player.PlayerEntity;

public class CommonProxy {
	
	public CommonProxy() {
		
	}
	
	public PlayerEntity getPlayer() {
		return null; // Doesn't mean anything on the server
	}
	
	public boolean isServer() {
		return true;
	}
}
