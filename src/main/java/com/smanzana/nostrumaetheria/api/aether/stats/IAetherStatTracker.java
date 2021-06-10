package com.smanzana.nostrumaetheria.api.aether.stats;

public interface IAetherStatTracker {

	public void reportTotal(long tick, int totalAether);
	public void reportInput(long tick, int incomingAether);
	public void reportOutput(long tick, int outgoingAether);
	
	public AetherStatInstance getInstance(long tick);
	
}
