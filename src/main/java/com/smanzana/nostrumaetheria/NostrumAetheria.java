package com.smanzana.nostrumaetheria;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smanzana.nostrumaetheria.proxy.CommonProxy;
import com.smanzana.nostrummagica.NostrumMagica;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = NostrumAetheria.MODID, version = NostrumAetheria.VERSION,
dependencies="required-after:" + NostrumMagica.MODID + "@[" + NostrumMagica.VERSION + ",)")
public class NostrumAetheria
{
    public static final String MODID = "nostrumaetheria";
    public static final String VERSION = "1.0";
    public static NostrumAetheria instance;
    @SidedProxy(clientSide="com.smanzana.nostrumaetheria.proxy.ClientProxy", serverSide="com.smanzana.nostrumaetheria.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static Logger logger = LogManager.getLogger(MODID);
    public static CreativeTabs creativeTab;
    public static Random random = new Random();
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
    	instance = this;
    	
//	    if (Loader.isModLoaded("Baubles")) {
//	    	baubles.enable();
//	    }
    	
    	NostrumAetheria.creativeTab = new CreativeTabs(MODID){
	    	@Override
	        @SideOnly(Side.CLIENT)
	        public Item getTabIconItem(){
	    		return Items.REDSTONE;
	        }
	    };
	    
    	proxy.preinit();
    }
    
    @EventHandler
    public void postinit(FMLPostInitializationEvent event) {
    	proxy.postinit();
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static @Nullable World getWorld(int dimension) {
		for (World world : DimensionManager.getWorlds()) {
			if (world.provider.getDimension() == dimension) {
				return world;
			}
		}
    	
    	return null;
    }
}
