package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetherBathBlock;
import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock;
import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherChargerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock;
import com.smanzana.nostrumaetheria.blocks.AetherPumpBlock;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.AetherRepairerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherUnravelerBlock;
import com.smanzana.nostrumaetheria.blocks.InfineAetherBlock;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherBathTileEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherBatteryEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherRelayEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.InfiniteAetherBlockEntity;
import com.smanzana.nostrumaetheria.client.render.AetherBathRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherBatteryRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherRelayRenderer;
import com.smanzana.nostrumaetheria.client.render.RenderAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherDebugRenderer;
import com.smanzana.nostrumaetheria.entities.EntityAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.items.ActivePendant;
import com.smanzana.nostrumaetheria.items.AetherBatteryMinecartItem;
import com.smanzana.nostrumaetheria.items.AetherGem;
import com.smanzana.nostrumaetheria.items.PassivePendant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	
	//protected OverlayRenderer overlayRenderer;

	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public void preinit() {
		super.preinit();
		
		OBJLoader.INSTANCE.addDomain(NostrumAetheria.MODID);
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void postinit() {
		super.postinit();
		
		//this.overlayRenderer = new OverlayRenderer();
	}
	
	@SubscribeEvent
	private void registerAllModels(ModelRegistryEvent event) {
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
		for (AetherFurnaceBlock.Type type : AetherFurnaceBlock.Type.values()) {
			registerModel(Item.getItemFromBlock(AetherFurnaceBlock.instance()),
					type.ordinal(),
					AetherFurnaceBlock.ID);
		}
		registerModel(Item.getItemFromBlock(AetherBoilerBlock.instance()),
				0,
				AetherBoilerBlock.ID);
		registerModel(ActivePendant.instance(),
				0,
				ActivePendant.ID);
		registerModel(PassivePendant.instance(),
				0,
				PassivePendant.ID);
		registerModel(Item.getItemFromBlock(AetherBathBlock.instance()),
				0,
				AetherBathBlock.ID);
		registerModel(AetherGem.instance(),
				0,
				AetherGem.ID);
		registerModel(Item.getItemFromBlock(AetherChargerBlock.instance()),
				0,
				AetherChargerBlock.ID);
		registerModel(Item.getItemFromBlock(AetherRepairerBlock.instance()),
				0,
				AetherRepairerBlock.ID);
		registerModel(Item.getItemFromBlock(AetherUnravelerBlock.instance()),
				0,
				AetherUnravelerBlock.ID);
		registerModel(AetherBatteryMinecartItem.instance(),
				0,
				AetherBatteryMinecartItem.ID);
		registerModel(Item.getItemFromBlock(AetherPumpBlock.instance()),
				0,
				AetherPumpBlock.ID);
		
		registerEntityRenderers();
		registerTileEntityRenderers();
	}
	
	public static void registerModel(Item item, int meta, String modelName) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
    	.register(item, meta,
    			new ModelResourceLocation(NostrumAetheria.MODID + ":" + modelName, "inventory"));
	}
	
	private void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityAetherBatteryMinecart.class, new IRenderFactory<EntityAetherBatteryMinecart>() {
			@Override
			public Render<? super EntityAetherBatteryMinecart> createRenderFor(RenderManager manager) {
				return new RenderAetherBatteryMinecart(manager);
			}
		});
	}
	
	private void registerTileEntityRenderers() {
		TileEntityAetherDebugRenderer.registerFor(InfiniteAetherBlockEntity.class);
		TileEntityAetherDebugRenderer.registerFor(AetherBatteryEntity.class);
		ClientRegistry.bindTileEntitySpecialRenderer(AetherRelayEntity.class,
				new AetherRelayRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(AetherBatteryEntity.class,
				new AetherBatteryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(AetherBathTileEntity.class,
				new AetherBathRenderer());
	}

	@Override
	public boolean isServer() {
		return false;
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
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
