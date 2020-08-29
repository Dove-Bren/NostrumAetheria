package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.AetheriaBlocks;
import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock;
import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.InfineAetherBlock;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.network.NetworkHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	
	public void preinit() {
		NetworkHandler.getInstance();
    	
//    	int entityID = 0;
//    	EntityRegistry.registerModEntity(EntityTestFairy.class, "test_fairy",
//    			entityID++,
//    			NostrumFairies.instance,
//    			128,
//    			1,
//    			false
//    			);

    	registerItems();
    	registerBlocks();
	}
	
	public void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(NostrumAetheria.instance, new NostrumAetheriaGui());
	}
	
	public void postinit() {
		;
	}
    
    private void registerItems() {
//    	GameRegistry.register(
//    			FeyStone.instance().setRegistryName(FeyStone.ID));
//    	FeyStone.init();
    }
    
    private void registerBlocks() {
    	GameRegistry.register(InfineAetherBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, InfineAetherBlock.ID));
    	GameRegistry.register(
    			(new ItemBlock(InfineAetherBlock.instance()).setRegistryName(InfineAetherBlock.ID)
    					.setCreativeTab(NostrumAetheria.creativeTab).setUnlocalizedName(InfineAetherBlock.ID))
    			);
    	InfineAetherBlock.init();
    	AetheriaBlocks.InfiniteAetherBlock = InfineAetherBlock.instance();
    	
    	for (AetherBatteryBlock block : new AetherBatteryBlock[]{
    			AetherBatteryBlock.small(),
    			AetherBatteryBlock.medium(),
    			AetherBatteryBlock.large(),
    			AetherBatteryBlock.giant()
    	}) {
    		GameRegistry.register(block,
        			new ResourceLocation(NostrumAetheria.MODID, block.getID()));
        	GameRegistry.register(
        			(new ItemBlock(block).setRegistryName(block.getID())
        					.setCreativeTab(NostrumAetheria.creativeTab).setUnlocalizedName(block.getID()))
        			);
        	
    	}
    	AetherBatteryBlock.init();
    	AetheriaBlocks.AetherBatterySmallBlock = AetherBatteryBlock.small();
    	AetheriaBlocks.AetherBatteryMediumBlock = AetherBatteryBlock.medium();
    	AetheriaBlocks.AetherBatteryLargeBlock = AetherBatteryBlock.large();
    	AetheriaBlocks.AetherBatteryGiantBlock = AetherBatteryBlock.giant();
    	
    	GameRegistry.register(AetherRelay.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherRelay.ID));
    	GameRegistry.register(
    			(new ItemBlock(AetherRelay.instance()).setRegistryName(AetherRelay.ID)
    					.setCreativeTab(NostrumAetheria.creativeTab).setUnlocalizedName(AetherRelay.ID))
    			);
    	AetherRelay.init();
    	AetheriaBlocks.AetherRelay = AetherRelay.instance();
    	
    	GameRegistry.register(AetherFurnaceBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherFurnaceBlock.ID));
    	ItemBlock furnaceItem = new ItemBlock(AetherFurnaceBlock.instance());
    	furnaceItem.setRegistryName(AetherFurnaceBlock.ID)
				.setCreativeTab(NostrumAetheria.creativeTab)
				.setUnlocalizedName(AetherFurnaceBlock.ID)
				.setHasSubtypes(true);
    	furnaceItem.addPropertyOverride(new ResourceLocation("on"), AetherFurnaceBlock.ON_GETTER);
    	furnaceItem.addPropertyOverride(new ResourceLocation("size"), AetherFurnaceBlock.SIZE_GETTER);
    	GameRegistry.register(furnaceItem);
    	
    	AetherFurnaceBlock.init();
    	AetheriaBlocks.AetherFurnaceBlock = AetherFurnaceBlock.instance();
    	
    	GameRegistry.register(AetherBoilerBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherBoilerBlock.ID));
    	GameRegistry.register(
    			(new ItemBlock(AetherBoilerBlock.instance()).setRegistryName(AetherBoilerBlock.ID)
    					.setCreativeTab(NostrumAetheria.creativeTab).setUnlocalizedName(AetherBoilerBlock.ID))
    			);
    	AetherBoilerBlock.init();
    	AetheriaBlocks.AetherBoilerBlock = AetherBoilerBlock.instance();
    }

	public EntityPlayer getPlayer() {
		return null; // Doesn't mean anything on the server
	}
	
	public boolean isServer() {
		return true;
	}
}
