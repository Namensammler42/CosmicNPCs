package de.namensammler.cosmicnpcs.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import de.namensammler.cosmicnpcs.CosmicNPCs;
import de.namensammler.cosmicnpcs.EntityHandler;
import de.namensammler.cosmicnpcs.EventHandler;
import de.namensammler.cosmicnpcs.client.GuiHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
	 
	public void preInit(FMLPreInitializationEvent e) {	
		EntityHandler.mainRegistry();
    }

    public void init(FMLInitializationEvent e) {
    	EventHandler handler = new EventHandler();
    	MinecraftForge.EVENT_BUS.register(handler);
    	FMLCommonHandler.instance().bus().register(handler);
    	
    }

    public void postInit(FMLPostInitializationEvent e) {

    }		
}