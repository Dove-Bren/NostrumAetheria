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
import com.smanzana.nostrumaetheria.blocks.InfiniteAetherBlock;
import com.smanzana.nostrumaetheria.client.render.AetherBathRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherBatteryRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherRelayRenderer;
import com.smanzana.nostrumaetheria.client.render.RenderAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherDebugRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherInfuserRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityWispBlockRenderer;
import com.smanzana.nostrumaetheria.entity.EntityAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem;
import com.smanzana.nostrumaetheria.items.ActivePendant;
import com.smanzana.nostrumaetheria.items.AetherBatteryMinecartItem;
import com.smanzana.nostrumaetheria.items.AetherGem;
import com.smanzana.nostrumaetheria.items.PassivePendant;
import com.smanzana.nostrumaetheria.tiles.AetherBathTileEntity;
import com.smanzana.nostrumaetheria.tiles.AetherBatteryEntity;
import com.smanzana.nostrumaetheria.tiles.AetherInfuserTileEntity;
import com.smanzana.nostrumaetheria.tiles.AetherRelayEntity;
import com.smanzana.nostrumaetheria.tiles.InfiniteAetherBlockEntity;
import com.smanzana.nostrumaetheria.tiles.WispBlockTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

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
	public void registerAllModels(ModelRegistryEvent event) {
		registerModel(Item.getItemFromBlock(InfiniteAetherBlock.instance()),
				0,
				InfiniteAetherBlock.ID);
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
		ModelLoader.setCustomModelResourceLocation(item, meta,
    			new ModelResourceLocation(NostrumAetheria.MODID + ":" + modelName, "inventory"));
	}
	
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		for (ModelResourceLocation loc : AetherCloakItem.AllCapeModels) {
			ResourceLocation modelLoc = new ResourceLocation(loc.getNamespace(), loc.getPath() + ".obj");
			IUnbakedModel model = ModelLoaderRegistry.getModelOrLogError(modelLoc, "Failed to get obj model for " + modelLoc);
			
			if (model != null && model instanceof OBJModel) {
				IBakedModel bakedModel = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new BasicState(model.getDefaultState(), false), DefaultVertexFormats.ITEM);
				// Note: putting as ModelResourceLocation to match RenderObj. Note creating like the various RenderObj users do.
				event.getModelRegistry().put(loc, bakedModel);
			}
		}
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
		ClientRegistry.bindTileEntitySpecialRenderer(AetherInfuserTileEntity.class, new TileEntityAetherInfuserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(WispBlockTileEntity.class, new TileEntityWispBlockRenderer());
	}

	@Override
	public boolean isServer() {
		return false;
	}
	
	@Override
	public PlayerEntity getPlayer() {
		return Minecraft.getInstance().player;
	}
	
	@SubscribeEvent
	public void onClientConnect(EntityJoinWorldEvent event) {
//		if (event.getEntity() == Minecraft.getInstance().thePlayer) {
//			// Every time we join a world, request a copy of its networks
//			
//			NostrumFairies.logger.info("Requested automatic logistics network refresh");
//			NetworkHandler.getSyncChannel().sendToServer(new LogisticsUpdateRequest());
//		}
	}
	
}
