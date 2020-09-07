package com.smanzana.nostrumaetheria.api.component;

import java.util.List;

import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler.AetherFlowConnection;

public interface IAetherComponentListener {

	/**
	 * Called any time the handler's underlying aether values change. Any time this is called, the handler
	 * should be saved.
	 */
	public void dirty();
	
	/**
	 * Called to fetch additional connections that this handler should use when looking for or attempting to push aether.
	 * The default tile entity implementation, for example, returns any handlers that are in adjacent block positions.
	 * @param connections
	 */
	public void addConnections(List<AetherFlowConnection> connections);
	
	/**
	 * If the handler is being ticked, this method serves as a callback for any tick where aether is drawn out or added to the handler.
	 * @param diff
	 * @param added
	 * @param taken
	 */
	public void onAetherFlowTick(int diff, boolean added, boolean taken);
	
}
