/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmicnpcs.proxy;

import java.io.File;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import de.namensammler.cosmicnpcs.CosmicNPCs;
import de.namensammler.cosmicnpcs.client.GuiHandler;
import de.namensammler.cosmicnpcs.client.RenderNPC;
import de.namensammler.cosmicnpcs.entity.EntityNPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.KeyBinding;

public class ClientProxy extends CommonProxy {
	
	int ModEntityID;
	
	public static KeyBinding[] keyBindings;
	
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		RenderingRegistry.registerEntityRenderingHandler(EntityNPC.class, new RenderNPC(new ModelBiped(), 0.5F));
		
		String path = CosmicNPCs.config.getConfigFile().getAbsolutePath();
    	path = path.substring(0, path.length() - 4 - 10);
    	
    	new File(path + "/assets/npctextures").mkdirs();
		
		List<IResourcePack> defaultResourcePacks = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao");

    	defaultResourcePacks.add(new FolderResourcePack(new File(path)));

    	Minecraft.getMinecraft().refreshResources();
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(CosmicNPCs.instance, new GuiHandler());
		
		keyBindings = new KeyBinding[2]; 
        keyBindings[0] = new KeyBinding("Use Command", Keyboard.KEY_NUMPAD9, "CosmicNPCs");
        keyBindings[1] = new KeyBinding("Set Command", Keyboard.KEY_NUMPAD3, "CosmicNPCs");
        for (int i = 0; i < keyBindings.length; ++i) 
        {
            ClientRegistry.registerKeyBinding(keyBindings[i]);
        }
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}
}