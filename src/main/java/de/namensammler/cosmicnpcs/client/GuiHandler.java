package de.namensammler.cosmicnpcs.client;

import cpw.mods.fml.common.network.IGuiHandler;
import de.namensammler.cosmicnpcs.CosmicNPCs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			switch(ID) {
			case CosmicNPCs.guiIDCommandScreen:
				 return null;
			}
			return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			switch(ID) {
			case CosmicNPCs.guiIDCommandScreen:
				return new GuiCommandScreen();
			}
			return null;
	}

}