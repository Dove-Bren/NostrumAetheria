package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.AetheriaBlocks;
import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.InfineAetherBlock;
import com.smanzana.nostrumaetheria.network.NetworkHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
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
    	//NetworkRegistry.INSTANCE.registerGuiHandler(NostrumFairies.instance, new NostrumFairyGui());
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
    }

	public EntityPlayer getPlayer() {
		return null; // Doesn't mean anything on the server
	}
	
	public boolean isServer() {
		return true;
	}
}
