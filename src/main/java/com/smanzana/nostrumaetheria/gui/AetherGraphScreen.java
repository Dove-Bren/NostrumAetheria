package com.smanzana.nostrumaetheria.gui;

import com.smanzana.nostrumaetheria.api.aether.stats.AetherStatInstance;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

/**
 * Screen for showing aether statistics.
 * @author Skyler
 *
 */
public class AetherGraphScreen extends Screen {

	public interface IAetherDataFetcher {
		public void requestRefresh();
	}
	
	private AetherStatInstance data;
	private long lastRefreshReq;
	private final IAetherDataFetcher fetcher;
	
	public AetherGraphScreen(IAetherDataFetcher fetcher) {
		super(new StringTextComponent("Aether Graph"));
		data = new AetherStatInstance();
		this.fetcher = fetcher;
	}
	
	public void setData(AetherStatInstance data) {
		this.data = data;
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	public void tick() {
		if (System.currentTimeMillis() - lastRefreshReq > (1000 * 1)) {
			fetcher.requestRefresh();
			lastRefreshReq = System.currentTimeMillis();
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
	}
	
}
