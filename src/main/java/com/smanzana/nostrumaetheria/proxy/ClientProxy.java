package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.InfineAetherBlock;
import com.smanzana.nostrumaetheria.client.render.AetherBatteryRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherRelayRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherDebugRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.obj.OBJLoader;
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
		
		TileEntityAetherDebugRenderer.registerFor(InfineAetherBlock.InfiniteAetherBlockEntity.class);
		TileEntityAetherDebugRenderer.registerFor(AetherBatteryBlock.AetherBatteryEntity.class);
		AetherRelayRenderer.init();
		AetherBatteryRenderer.init();
		
//		RenderingRegistry.registerEntityRenderingHandler(EntityTestFairy.class, new IRenderFactory<EntityTestFairy>() {
//			@Override
//			public Render<? super EntityTestFairy> createRenderFor(RenderManager manager) {
//				return new RenderTestFairy(manager, 1.0f);
//			}
//		});
		
		OBJLoader.INSTANCE.addDomain(NostrumAetheria.MODID);
	}
	
	@Override
	public void init() {
		super.init();
		
		registerModel(Item.getItemFromBlock(InfineAetherBlock.instance()),
				0,
				InfineAetherBlock.ID);
		registerModel(Item.getItemFromBlock(AetherBatteryBlock.small()),
				0,
				AetherBatteryBlock.small().getID());
		registerModel(Item.getItemFromBlock(AetherBatteryBlock.medium()),
				0,
				AetherBatteryBlock.medium().getID());
		registerModel(Item.getItemFromBlock(AetherBatteryBlock.large()),
				0,
				AetherBatteryBlock.large().getID());
		registerModel(Item.getItemFromBlock(AetherBatteryBlock.giant()),
				0,
				AetherBatteryBlock.giant().getID());
		registerModel(Item.getItemFromBlock(AetherRelay.instance()),
				0,
				AetherRelay.ID);
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
