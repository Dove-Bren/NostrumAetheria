package com.smanzana.nostrumaetheria;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.integration.curios.CuriosClientProxy;
import com.smanzana.nostrumaetheria.integration.curios.CuriosProxy;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrumaetheria.proxy.AetheriaAPIProxy;
import com.smanzana.nostrumaetheria.proxy.ClientProxy;
import com.smanzana.nostrumaetheria.proxy.CommonProxy;
import com.smanzana.nostrummagica.research.NostrumResearch;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosAPI;

@Mod(NostrumAetheria.MODID)
public class NostrumAetheria
{
    public static final String MODID = "nostrumaetheria";
    public static final String VERSION = "1.14.4-1.2.0";
    public static NostrumAetheria instance;
    public static CommonProxy proxy;
    public static CuriosProxy curios;
    public static Logger logger = LogManager.getLogger(MODID);
    public static Random random = new Random();
    
    public NostrumAetheria() {
    	instance = this;
    	
    	proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    	curios = DistExecutor.runForDist(() -> CuriosClientProxy::new, () -> CuriosProxy::new);
    	
    	APIProxy.handler = new AetheriaAPIProxy();
    	
    	APIProxy.creativeTab = new ItemGroup(MODID){
	    	@Override
	    	@OnlyIn(Dist.CLIENT)
	        public ItemStack createIcon(){
	    		return new ItemStack(AetheriaBlocks.infiteAetherBlock);
	        }
	    };
		
		if (ModList.get().isLoaded(CuriosAPI.MODID)) {
			curios.enable();
		}
		
	    //InfineAetherBlock.instance().setCreativeTab(APIProxy.creativeTab);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	    
    	proxy.preinit();
    	curios.preInit();
    	APIProxy.ResearchTab = new NostrumResearch.NostrumResearchTab("aether", new ItemStack(AetheriaItems.aetherGem));
    }
    
    
    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
    	proxy.init();
    	curios.init();
    	
    	proxy.postinit();
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
//    public static @Nullable World getWorld(int dimension) {
//		for (World world : DimensionManager.getWorlds()) {
//			if (world.getDimension().getType().getId() == dimension) {
//				return world;
//			}
//		}
//    	
//    	return null;
//    }
}
