package de.namensammler.cosmicnpcs.npcsystem;

import net.minecraft.nbt.NBTTagCompound;

public class NPCAction {
	public NPCAction(byte chat) {
		super();
		this.type = chat;
		itemData = new NBTTagCompound();
	}

	public byte type;

	/* Chat */
	public String message;

	/* Equip */
	public int armorId;
	public int armorSlot;
	public int armorDmg;

	/* Drop, Equip */
	public NBTTagCompound itemData;

	/* Arrow Shoot */
	public int arrowCharge;

	/* Place Block */
	public int xCoord;
	public int yCoord;
	public int zCoord;
	
	//Interact with Block
	public int metaData;
}