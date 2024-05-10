package com.smanzana.nostrumaetheria.init;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.client.gui.container.ActivePendantGui;
import com.smanzana.nostrumaetheria.client.gui.container.AetherBoilerGui;
import com.smanzana.nostrumaetheria.client.gui.container.AetherChargerGui;
import com.smanzana.nostrumaetheria.client.gui.container.AetherFurnaceGui;
import com.smanzana.nostrumaetheria.client.gui.container.AetherRepairerGui;
import com.smanzana.nostrumaetheria.client.gui.container.AetherUnravelerGui;
import com.smanzana.nostrumaetheria.client.gui.container.AetheriaContainers;
import com.smanzana.nostrumaetheria.client.gui.container.WispBlockGui;
import com.smanzana.nostrumaetheria.client.render.AetherBathRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherBatteryRenderer;
import com.smanzana.nostrumaetheria.client.render.AetherRelayRenderer;
import com.smanzana.nostrumaetheria.client.render.RenderAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherDebugRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherInfuserRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityWispBlockRenderer;
import com.smanzana.nostrumaetheria.entity.AetheriaEntityTypes;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Client handler for MOD bus events.
 * MOD bus is not game event bus.
 * @author Skyler
 *
 */
@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		TileEntityAetherDebugRenderer.registerFor(AetheriaTileEntities.InfiniteBlock);
		TileEntityAetherDebugRenderer.registerFor(AetheriaTileEntities.Battery);
		ClientRegistry.bindTileEntityRenderer(AetheriaTileEntities.Relay, (manager) -> new AetherRelayRenderer(manager));
		ClientRegistry.bindTileEntityRenderer(AetheriaTileEntities.EnhancedRelay, (manager) -> new AetherRelayRenderer(manager));
		ClientRegistry.bindTileEntityRenderer(AetheriaTileEntities.Battery,	(manager) -> new AetherBatteryRenderer(manager));
		ClientRegistry.bindTileEntityRenderer(AetheriaTileEntities.Bath, (manager) -> new AetherBathRenderer(manager));
		ClientRegistry.bindTileEntityRenderer(AetheriaTileEntities.AetherInfuserEnt, (manager) -> new TileEntityAetherInfuserRenderer(manager));
		ClientRegistry.bindTileEntityRenderer(AetheriaTileEntities.WispBlockEnt, (manager) -> new TileEntityWispBlockRenderer(manager));
		
		ScreenManager.registerFactory(AetheriaContainers.ActivePendant, ActivePendantGui.ActivePendantGuiContainer::new);
		ScreenManager.registerFactory(AetheriaContainers.Boiler, AetherBoilerGui.AetherBoilerGuiContainer::new);
		ScreenManager.registerFactory(AetheriaContainers.Charger, AetherChargerGui.AetherChargerGuiContainer::new);
		ScreenManager.registerFactory(AetheriaContainers.Furnace, AetherFurnaceGui.AetherFurnaceGuiContainer::new);
		ScreenManager.registerFactory(AetheriaContainers.Repairer, AetherRepairerGui.AetherRepairerGuiContainer::new);
		ScreenManager.registerFactory(AetheriaContainers.Unraveler, AetherUnravelerGui.AetherUnravelerGuiContainer::new);
		ScreenManager.registerFactory(AetheriaContainers.WispBlock, WispBlockGui.WispBlockGuiContainer::new);
		
		registerBlockRenderLayers();
		registerEntityRenderers();
	}
	
	private static final void registerBlockRenderLayers() {
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.bath, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.smallBattery, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.mediumBattery, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.largeBattery, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.giantBattery, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.boiler, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.charger, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.smallFurnace, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.mediumFurnace, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.largeFurnace, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.infuser, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.pump, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.relay, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.repairer, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.unraveler, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.enhancedRelay, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.infiteAetherBlock, RenderType.getSolid());
		RenderTypeLookup.setRenderLayer(AetheriaBlocks.wispBlock, RenderType.getSolid()); // actually invisible
	}
	
	private static final void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(AetheriaEntityTypes.batteryCart, (manager) -> new RenderAetherBatteryMinecart(manager));
	}
	
	@SubscribeEvent
	public static void registerAllModels(ModelRegistryEvent event) {
		// Register extra models to load even if block and items don't reference them.
		for (ResourceLocation loc : AetherCloakItem.AetherCloakModels.AllCapeModels) {
			ModelLoader.addSpecialModel(loc);
		}
	}
	
	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		// Replace or inject baked models into the bakery after baking happens
	}
	
	@SubscribeEvent
	public static void stitchEventPre(TextureStitchEvent.Pre event) {
//		// Note: called multiple times for different texture atlases.
//		// Using what Botania does
//		if(event.getMap() != Minecraft.getInstance().getTextureMap()) {
//			return;
//		}
//		
//		event.addSprite(new ResourceLocation(
//				NostrumAetheria.MODID, "models/armor/aether_cloak_decor"));
//		event.addSprite(new ResourceLocation(
//				NostrumAetheria.MODID, "models/armor/aether_cloak_inside"));
//		event.addSprite(new ResourceLocation(
//				NostrumAetheria.MODID, "models/armor/aether_cloak_outside"));
	}

}
