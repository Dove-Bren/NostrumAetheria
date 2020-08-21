package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.NostrumAetheria;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	
	//protected OverlayRenderer overlayRenderer;

	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public void preinit() {
		super.preinit();
		
		//StorageMonitor.StorageMonitorRenderer.init();
		
//		RenderingRegistry.registerEntityRenderingHandler(EntityTestFairy.class, new IRenderFactory<EntityTestFairy>() {
//			@Override
//			public Render<? super EntityTestFairy> createRenderFor(RenderManager manager) {
//				return new RenderTestFairy(manager, 1.0f);
//			}
//		});
	}
	
	@Override
	public void init() {
		super.init();
		
//		registerModel(Item.getItemFromBlock(StorageLogisticsChest.instance()),
//				0,
//				StorageLogisticsChest.ID);
	}
	
	@Override
	public void postinit() {
		super.postinit();
		
		//this.overlayRenderer = new OverlayRenderer();
	}
	
	public static void registerModel(Item item, int meta, String modelName) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
    	.register(item, meta,
    			new ModelResourceLocation(NostrumAetheria.MODID + ":" + modelName, "inventory"));
	}
	

	@Override
	public boolean isServer() {
		return false;
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	@SubscribeEvent
	public void onClientConnect(EntityJoinWorldEvent event) {
//		if (event.getEntity() == Minecraft.getMinecraft().thePlayer) {
//			// Every time we join a world, request a copy of its networks
//			
//			NostrumFairies.logger.info("Requested automatic logistics network refresh");
//			NetworkHandler.getSyncChannel().sendToServer(new LogisticsUpdateRequest());
//		}
	}
	
}
