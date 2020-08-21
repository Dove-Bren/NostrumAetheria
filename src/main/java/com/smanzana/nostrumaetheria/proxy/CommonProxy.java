package com.smanzana.nostrumaetheria.proxy;

import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {
	
	public void preinit() {
		//NetworkHandler.getInstance();
    	
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
//    	GameRegistry.register(StorageLogisticsChest.instance(),
//    			new ResourceLocation(NostrumFairies.MODID, StorageLogisticsChest.ID));
//    	GameRegistry.register(
//    			(new ItemBlock(StorageLogisticsChest.instance())).setRegistryName(StorageLogisticsChest.ID));
//    	StorageLogisticsChest.init();
    	
    }

	public EntityPlayer getPlayer() {
		return null; // Doesn't mean anything on the server
	}
	
	public boolean isServer() {
		return true;
	}
}
