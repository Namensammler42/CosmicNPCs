package de.namensammler.cosmicnpcs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.namensammler.cosmicnpcs.command.CommandNPCPlay;
import de.namensammler.cosmicnpcs.command.CommandNPCRec;
import de.namensammler.cosmicnpcs.npcsystem.NPCAction;
import de.namensammler.cosmicnpcs.npcsystem.NPCRecorder;
import de.namensammler.cosmicnpcs.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = CosmicNPCs.MODID, version = CosmicNPCs.MOD_VERSION, name = CosmicNPCs.MOD_NAME, acceptedMinecraftVersions = CosmicNPCs.MC_VERSION, dependencies = CosmicNPCs.DEPENDENCIES)
public class CosmicNPCs 
{
				
    public static final String MODID = "cosmicnpcs";
    public static final String MOD_NAME = "CosmicNPCs";
    public static final String MC_VERSION = "1.7.10";
    public static final String MOD_VERSION = "1.1.0";
    public static final String DEPENDENCIES = "required-after:cosmiccore";
    
    @Instance(MODID)
	public static CosmicNPCs instance;
	
	@SidedProxy(clientSide="de.namensammler.cosmicnpcs.proxy.ClientProxy",serverSide="de.namensammler.cosmicnpcs.proxy.CommonProxy")
    public static CommonProxy proxy;
	public static final int guiIDCommandScreen = 4; 
	
	public static Configuration config;
	
	public Map<EntityPlayer, NPCRecorder> recordThreads = Collections
			.synchronizedMap(new HashMap<EntityPlayer, NPCRecorder>());
	
	public List<NPCAction> getActionListForPlayer(EntityPlayer ep) {
		NPCRecorder aRecorder = recordThreads.get(ep);

		if (aRecorder == null) {
			return null;
		}

		return aRecorder.eventsList;
}
	
	public void broadcastMsg(String msg) {
		ArrayList<EntityPlayerMP> temp = (ArrayList<EntityPlayerMP>) FMLCommonHandler
				.instance().getSidedDelegate().getServer()
				.getConfigurationManager().playerEntityList;

		for (EntityPlayerMP player : temp) {
			if (FMLCommonHandler.instance().getSidedDelegate().getServer()
					.getConfigurationManager()
					.func_152596_g(player.getGameProfile())) {
				ChatComponentText cmp = new ChatComponentText("["+MOD_NAME+"]: " + msg);

				player.addChatMessage(cmp);
			}
		}
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandNPCRec(this));
		event.registerServerCommand(new CommandNPCPlay(this));
	}
	
    @EventHandler
    public void preinit(FMLPreInitializationEvent e)
    {
    	config = new Configuration(new File(e.getModConfigurationDirectory() + "/cosmicnpcs/" + CosmicNPCs.MODID + ".cfg"));
    	proxy.preInit(e);
    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent e)
    {
    	FMLCommonHandler.instance().bus().register(instance);
    	proxy.init(e);
    }
    
    @EventHandler
    public void postinit(FMLPostInitializationEvent e)
    {
    	proxy.postInit(e);
    } 
    
    public static EntityPlayerMP getPlayerForName(String name) {
		/* Try an exact match first */
		{
			EntityPlayerMP tempPlayer = FMLCommonHandler.instance()
					.getMinecraftServerInstance().getConfigurationManager()
					.func_152612_a(name); /* 1.7.10 func_152612_a is getPlayerByName */

			if (tempPlayer != null) {
				return tempPlayer;
			}
		}
		
		// now try getting others
		List<EntityPlayerMP> possibles = new LinkedList<EntityPlayerMP>();
		ArrayList<EntityPlayerMP> temp = (ArrayList<EntityPlayerMP>) FMLCommonHandler
				.instance().getSidedDelegate().getServer()
				.getConfigurationManager().playerEntityList;

		for (EntityPlayerMP player : temp) {
			if (player.getCommandSenderName().equalsIgnoreCase(name)) {
				return player;
			}

			if (player.getCommandSenderName().toLowerCase()
					.contains(name.toLowerCase())) {
				possibles.add(player);
			}
		}

		if (possibles.size() == 1) {
			return possibles.get(0);
		}

		return null;
}
}