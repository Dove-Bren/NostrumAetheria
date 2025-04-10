package com.smanzana.nostrumaetheria.api.capability;

public class AetherBurnableWrapper implements IAetherBurnable {
	
	private int burnTicks;
	private float aetherYield;
	
	public AetherBurnableWrapper(int burnTicks, float aetherYield) {
		this.burnTicks = burnTicks;
		this.aetherYield = aetherYield;
	}
	
	public AetherBurnableWrapper() {
		this(20, 100);
	}

	@Override
	public int getBurnTicks() {
		return burnTicks;
	}

	@Override
	public float getAetherYield() {
		return aetherYield;
	}
}
