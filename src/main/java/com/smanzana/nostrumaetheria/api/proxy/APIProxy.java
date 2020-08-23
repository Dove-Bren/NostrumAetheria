package com.smanzana.nostrumaetheria.api.proxy;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;

public abstract class APIProxy {

	public static APIProxy handler = null;
	
	protected abstract void handleSyncTEAether(AetherTileEntity te);
	public static void syncTEAether(AetherTileEntity te) {
		if (handler != null) {
			handler.handleSyncTEAether(te);
		}
	}
	
}
