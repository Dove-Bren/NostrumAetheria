package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.client.render.AetherBathRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherBatteryRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherRelayRenderer;
import com.smanzana.nostrumaetheria.client.render.RenderAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherDebugRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherInfuserRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityWispBlockRenderer;
import com.smanzana.nostrumaetheria.entity.EntityAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem;
import com.smanzana.nostrumaetheria.tiles.AetherBathTileEntity;
import com.smanzana.nostrumaetheria.tiles.AetherBatteryEntity;
import com.smanzana.nostrumaetheria.tiles.AetherInfuserTileEntity;
import com.smanzana.nostrumaetheria.tiles.AetherRelayEntity;
import com.smanzana.nostrumaetheria.tiles.InfiniteAetherBlockEntity;
import com.smanzana.nostrumaetheria.tiles.WispBlockTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy extends CommonProxy {
	
	//protected OverlayRenderer overlayRenderer;

	public ClientProxy() {
		super();
	}
	
	@Override
	public void preinit() {
		super.preinit();
		MinecraftForge.EVENT_BUS.register(this);
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
		registerEntityRenderers();
		//registerTileEntityRenderers();
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
		
		// 
	}
	
	@SubscribeEvent
	public void stitchEventPre(TextureStitchEvent.Pre event) {
		// Note: called multiple times for different texture atlases.
		// Using what Botania does
		if(event.getMap() != Minecraft.getInstance().getTextureMap()) {
			return;
		}
		
		event.addSprite(new ResourceLocation(
				NostrumAetheria.MODID, "models/armor/aether_cloak_decor"));
		event.addSprite(new ResourceLocation(
				NostrumAetheria.MODID, "models/armor/aether_cloak_inside"));
		event.addSprite(new ResourceLocation(
				NostrumAetheria.MODID, "models/armor/aether_cloak_outside"));
	}
	
	private void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityAetherBatteryMinecart.class, new IRenderFactory<EntityAetherBatteryMinecart>() {
			@Override
			public EntityRenderer<? super EntityAetherBatteryMinecart> createRenderFor(EntityRendererManager manager) {
				return new RenderAetherBatteryMinecart(manager);
			}
		});
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event) {
		OBJLoader.INSTANCE.addDomain(NostrumAetheria.MODID);
		
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
		Minecraft mc = Minecraft.getInstance();
		return mc.player;
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
