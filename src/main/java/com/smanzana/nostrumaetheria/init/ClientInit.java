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
import com.smanzana.nostrumaetheria.client.render.LensHolderBlockEntityRenderer;
import com.smanzana.nostrumaetheria.client.render.RenderAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherDebugRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityAetherInfuserRenderer;
import com.smanzana.nostrumaetheria.client.render.TileEntityWispBlockRenderer;
import com.smanzana.nostrumaetheria.entity.AetheriaEntityTypes;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.client.render.entity.WispRenderer;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Client handler for MOD bus events.
 * MOD bus is not game event bus.
 * @author Skyler
 *
 */
@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		MenuScreens.register(AetheriaContainers.ActivePendant, ActivePendantGui.ActivePendantGuiContainer::new);
		MenuScreens.register(AetheriaContainers.Boiler, AetherBoilerGui.AetherBoilerGuiContainer::new);
		MenuScreens.register(AetheriaContainers.Charger, AetherChargerGui.AetherChargerGuiContainer::new);
		MenuScreens.register(AetheriaContainers.Furnace, AetherFurnaceGui.AetherFurnaceGuiContainer::new);
		MenuScreens.register(AetheriaContainers.Repairer, AetherRepairerGui.AetherRepairerGuiContainer::new);
		MenuScreens.register(AetheriaContainers.Unraveler, AetherUnravelerGui.AetherUnravelerGuiContainer::new);
		MenuScreens.register(AetheriaContainers.WispBlock, WispBlockGui.WispBlockGuiContainer::new);
		
		registerBlockRenderLayers();
	}
	
	@SubscribeEvent
	public static final void registerTileEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		TileEntityAetherDebugRenderer.registerFor(event, AetheriaTileEntities.InfiniteBlock);
		TileEntityAetherDebugRenderer.registerFor(event, AetheriaTileEntities.Battery);
		event.registerBlockEntityRenderer(AetheriaTileEntities.Relay, AetherRelayRenderer::new);
		event.registerBlockEntityRenderer(AetheriaTileEntities.EnhancedRelay, AetherRelayRenderer::new);
		event.registerBlockEntityRenderer(AetheriaTileEntities.Battery,	AetherBatteryRenderer::new);
		event.registerBlockEntityRenderer(AetheriaTileEntities.Bath, AetherBathRenderer::new);
		event.registerBlockEntityRenderer(AetheriaTileEntities.AetherInfuserEnt, TileEntityAetherInfuserRenderer::new);
		event.registerBlockEntityRenderer(AetheriaTileEntities.WispBlockEnt, TileEntityWispBlockRenderer::new);
		event.registerBlockEntityRenderer(AetheriaTileEntities.LensHolder, LensHolderBlockEntityRenderer::new);
	}
	
	private static final void registerBlockRenderLayers() {
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.bath, RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.smallBattery, RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.mediumBattery, RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.largeBattery, RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.giantBattery, RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.boiler, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.charger, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.smallFurnace, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.mediumFurnace, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.largeFurnace, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.infuser, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.pump, RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.relay, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.repairer, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.unraveler, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.enhancedRelay, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.infiteAetherBlock, RenderType.solid());
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.wispBlock, RenderType.solid()); // actually invisible
		ItemBlockRenderTypes.setRenderLayer(AetheriaBlocks.lensHolder, RenderType.solid());
	}
	
	@SubscribeEvent
	public static final void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(AetheriaEntityTypes.batteryCart, RenderAetherBatteryMinecart::new);
		event.registerEntityRenderer(AetheriaEntityTypes.sentinelWisp, WispRenderer::new);
	}
	
	@SubscribeEvent
	public static void registerAllModels(ModelRegistryEvent event) {
		// Register extra models to load even if block and items don't reference them.
		for (ResourceLocation loc : AetherCloakItem.AetherCloakModels.AllCapeModels) {
			ForgeModelBakery.addSpecialModel(loc);
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
