/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
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