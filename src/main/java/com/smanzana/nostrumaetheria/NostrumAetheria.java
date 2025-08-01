package com.smanzana.nostrumaetheria;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.integration.curios.CuriosClientProxy;
import com.smanzana.nostrumaetheria.integration.curios.CuriosAetheriaProxy;
import com.smanzana.nostrumaetheria.proxy.AetheriaAPIProxy;
import com.smanzana.nostrumaetheria.proxy.ClientProxy;
import com.smanzana.nostrumaetheria.proxy.CommonProxy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(NostrumAetheria.MODID)
public class NostrumAetheria
{
    public static final String MODID = "nostrumaetheria";
    public static final String VERSION = "1.16.5-1.3.0";
    public static NostrumAetheria instance;
    public static CommonProxy proxy;
    public static CuriosAetheriaProxy curios;
    public static Logger logger = LogManager.getLogger(MODID);
    public static Random random = new Random();
    
    public NostrumAetheria() {
    	instance = this;
    	
    	proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    	curios = DistExecutor.unsafeRunForDist(() -> CuriosClientProxy::new, () -> CuriosAetheriaProxy::new);
    	
    	APIProxy.handler = new AetheriaAPIProxy();
    	
    	APIProxy.creativeTab = new CreativeModeTab(MODID){
	    	@Override
	    	@OnlyIn(Dist.CLIENT)
	        public ItemStack makeIcon(){
	    		return new ItemStack(AetheriaBlocks.infiteAetherBlock);
	        }
	    };
		
		if (ModList.get().isLoaded(CuriosApi.MODID)) {
			curios.enable();
		}
		
	    //InfineAetherBlock.instance().setCreativeTab(APIProxy.creativeTab);
		//FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

	public static ResourceLocation Loc(String string) {
		return new ResourceLocation(MODID, string);
	}
}
