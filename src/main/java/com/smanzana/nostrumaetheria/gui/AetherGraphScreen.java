package com.smanzana.nostrumaetheria.gui;

import com.smanzana.nostrumaetheria.api.aether.stats.AetherStatInstance;

import net.minecraft.client.gui.GuiScreen;

/**
 * Screen for showing aether statistics.
 * @author Skyler
 *
 */
public class AetherGraphScreen extends GuiScreen {

	public interface IAetherDataFetcher {
		public void requestRefresh();
	}
	
	private AetherStatInstance data;
	private long lastRefreshReq;
	private final IAetherDataFetcher fetcher;
	
	public AetherGraphScreen(IAetherDataFetcher fetcher) {
		data = new AetherStatInstance();
		this.fetcher = fetcher;
	}
	
	public void setData(AetherStatInstance data) {
		this.data = data;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void updateScreen() {
		if (System.currentTimeMillis() - lastRefreshReq > (1000 * 1)) {
			fetcher.requestRefresh();
			lastRefreshReq = System.currentTimeMillis();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
	}
	
}
