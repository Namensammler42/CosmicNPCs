package de.namensammler.cosmicnpcs;

import java.io.IOException;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.namensammler.cosmiccore.LivingPlaceBlockEvent;
import de.namensammler.cosmicnpcs.client.GuiCommandScreen;
import de.namensammler.cosmicnpcs.npcsystem.NPCAction;
import de.namensammler.cosmicnpcs.npcsystem.NPCActionTypes;
import de.namensammler.cosmicnpcs.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class EventHandler {
	public static String[] command = new String[43];
	public static int commandCounter = 1;
	
	
	@SubscribeEvent
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side != Side.SERVER) return;
		
		if (event.action == event.action.RIGHT_CLICK_BLOCK) {
			if (event.entityPlayer instanceof EntityPlayerMP) {
				EntityPlayerMP thePlayer = (EntityPlayerMP) event.entityPlayer;
				List<NPCAction> aList = CosmicNPCs.instance
						.getActionListForPlayer(thePlayer);
				if (aList != null) {
					NPCAction ma = new NPCAction(
							NPCActionTypes.INTERACTWITHBLOCK);
					
					World worldObj = event.world;
					ma.xCoord = event.x;
					ma.yCoord = event.y;
					ma.zCoord = event.z;
					ma.metaData = worldObj.getBlockMetadata(ma.xCoord, ma.yCoord, ma.zCoord);
					aList.add(ma);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBreakEvent(BreakEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (event.world.isRemote) return;
		
		if (event.getPlayer() instanceof EntityPlayerMP) {
			EntityPlayerMP thePlayer = (EntityPlayerMP) event.getPlayer();
			List<NPCAction> aList = CosmicNPCs.instance
					.getActionListForPlayer(thePlayer);
			if (aList != null) {
				NPCAction ma = new NPCAction(
						NPCActionTypes.BREAKBLOCK);
				
				ma.xCoord = event.x;
				ma.yCoord = event.y;
				ma.zCoord = event.z;
				aList.add(ma);
			}
		}
		
	}
	
	@SubscribeEvent
	public void onLivingPlaceBlockEvent(LivingPlaceBlockEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			if (event.entityLiving instanceof EntityPlayerMP) {
				EntityPlayerMP thePlayer = (EntityPlayerMP) event.entityLiving;
				List<NPCAction> aList = CosmicNPCs.instance
						.getActionListForPlayer(thePlayer);

				if (aList != null) {
					NPCAction ma = new NPCAction(
							NPCActionTypes.PLACEBLOCK);
					event.theItem.writeToNBT(ma.itemData);
					ma.xCoord = event.xCoord;
					ma.yCoord = event.yCoord;
					ma.zCoord = event.zCoord;
					aList.add(ma);
				}
			}
		}
}
	
	@SubscribeEvent
	public void onArrowLooseEvent(ArrowLooseEvent ev) throws IOException {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			List<NPCAction> aList = CosmicNPCs.instance
					.getActionListForPlayer(ev.entityPlayer);

			if (aList != null) {
				NPCAction ma = new NPCAction(NPCActionTypes.SHOOTARROW);
				ma.arrowCharge = ev.charge;
				aList.add(ma);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemTossEvent(ItemTossEvent ev) throws IOException {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			List<NPCAction> aList = CosmicNPCs.instance
					.getActionListForPlayer(ev.player);

			if (aList != null) {
				NPCAction ma = new NPCAction(NPCActionTypes.DROP);
				ev.entityItem.getEntityItem().writeToNBT(ma.itemData);
				aList.add(ma);
			}
		}
	}
	
	@SubscribeEvent
	public void onServerChatEvent(ServerChatEvent ev) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			List<NPCAction> aList = CosmicNPCs.instance
					.getActionListForPlayer(ev.player);

			if (aList != null) {
				NPCAction ma = new NPCAction(NPCActionTypes.CHAT);
				ma.message = ev.message;
				aList.add(ma);
			}
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onEvent(KeyInputEvent event) {		
	    KeyBinding[] keyBindings = ClientProxy.keyBindings;
	    //Use Command
	    if (keyBindings[0].isPressed()) {
	    	if (command[commandCounter] != null) {
	    		if (command[commandCounter].startsWith("/")) {
		    		Minecraft.getMinecraft().thePlayer.sendChatMessage(command[commandCounter]);
		    		commandCounter++;
		    	} else {
		    		Minecraft.getMinecraft().thePlayer.sendChatMessage("/"+command[commandCounter]);
		    		commandCounter++;
		    	}
	    	}
	    }
	    //Set Command
	    if (keyBindings[1].isPressed()) {
	    	Minecraft.getMinecraft().displayGuiScreen(new GuiCommandScreen());
	    }
	}
}