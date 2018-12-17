package de.namensammler.cosmicnpcs.npcsystem;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.namensammler.cosmicnpcs.CosmicNPCs;
import de.namensammler.cosmicnpcs.entity.EntityNPC;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.CompressedStreamTools;

public class PlayThread implements Runnable {
	public Thread t;
	EntityNPC replayEntity;	
	DataInputStream in;

	public PlayThread(EntityLiving _player, String recfile) {
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		try {
			if (side == Side.CLIENT) {
			
			String path = CosmicNPCs.config.getConfigFile().getAbsolutePath();
	    	path = path.substring(0, path.length() - 4 - 10);
			
				File file = new File(path + "/recordings");

				in = new DataInputStream(new FileInputStream(file.getAbsolutePath() + "/" + recfile + ".action"));	
			} else if (side == Side.SERVER) {
				File file = new File("config/cosmicnpcs/recordings");

				in = new DataInputStream(new FileInputStream(file.getAbsolutePath() + "/" + recfile + ".action"));	
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		replayEntity = (EntityNPC) _player;
		t = new Thread(this, "NPC Playback Thread");
		t.start();
	}

	// This is the entry point for the second thread.
	public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			short magic = in.readShort();

			if (magic != -5119) {
				throw new Exception("Not an action file.");
			}

			while (true) {
					float yaw = in.readFloat();
					float pitch = in.readFloat();
					double x = in.readDouble();
					double y = in.readDouble();
					double z = in.readDouble();
					double mx = in.readDouble();
					double my = in.readDouble();
					double mz = in.readDouble();
					float fd = in.readFloat();
					Boolean iab = in.readBoolean();
					Boolean isn = in.readBoolean();
					Boolean isp = in.readBoolean();
					Boolean iog = in.readBoolean();
					Boolean ie = in.readBoolean();
					/* Set Position Fields in Entity */
					replayEntity.isAirBorne = iab;
					replayEntity.motionX = mx;
					replayEntity.motionY = my;
					replayEntity.motionZ = mz;
					replayEntity.fallDistance = fd;
					replayEntity.setSneaking(isn);
					replayEntity.setSprinting(isp);
					replayEntity.onGround = iog;
					replayEntity.setPositionAndRotation(x, y, z, yaw, pitch);
					replayEntity.setEating(ie);

					Boolean hasAction = in.readBoolean();

					/* Post actions to queue for Entity to run on main Tick thread */

					if (hasAction) {
						byte type = in.readByte();

						switch (type) {
						case NPCActionTypes.CHAT: {
							String msg = in.readUTF();
							NPCAction ma = new NPCAction(NPCActionTypes.CHAT);
							ma.message = msg;
							replayEntity.eventsList.add(ma);
							break;
						}

						case NPCActionTypes.EQUIP: {
							NPCAction ma = new NPCAction(NPCActionTypes.EQUIP);
							int aSlot = in.readInt();
							int aId = in.readInt();
							int aDmg = in.readInt();

							/* If not a "Clear Slot" event, load item data. */
							if (aId != -1) {
								ma.itemData = CompressedStreamTools.read(in);
							}

							ma.armorSlot = aSlot;
							ma.armorId = aId;
							ma.armorDmg = aDmg;
							replayEntity.eventsList.add(ma);
							break;
						}

						case NPCActionTypes.SWIPE: {
							NPCAction ma = new NPCAction(NPCActionTypes.SWIPE);
							replayEntity.eventsList.add(ma);
							break;
						}

						case NPCActionTypes.DROP: {
							NPCAction ma = new NPCAction(NPCActionTypes.DROP);
							ma.itemData = CompressedStreamTools.read(in);

							replayEntity.eventsList.add(ma);
							break;
						}

						case NPCActionTypes.SHOOTARROW: {
							int aCharge = in.readInt();
							NPCAction ma = new NPCAction(
									NPCActionTypes.SHOOTARROW);
							ma.arrowCharge = aCharge;
							replayEntity.eventsList.add(ma);
							break;
						}

						case NPCActionTypes.BREAKBLOCK: {
							NPCAction ma = new NPCAction(NPCActionTypes.BREAKBLOCK);
							ma.xCoord = in.readInt();
							ma.yCoord = in.readInt();
							ma.zCoord = in.readInt();

							replayEntity.eventsList.add(ma);
							break;
						}
						
						case NPCActionTypes.PLACEBLOCK: {
							NPCAction ma = new NPCAction(
									NPCActionTypes.PLACEBLOCK);
							ma.xCoord = in.readInt();
							ma.yCoord = in.readInt();
							ma.zCoord = in.readInt();
							ma.itemData = CompressedStreamTools.read(in);
							replayEntity.eventsList.add(ma);
							break;
						}
						
						case NPCActionTypes.INTERACTWITHBLOCK: {
							NPCAction ma = new NPCAction(
									NPCActionTypes.INTERACTWITHBLOCK);
							ma.xCoord = in.readInt();
							ma.yCoord = in.readInt();
							ma.zCoord = in.readInt();
							ma.metaData = in.readInt();
							replayEntity.eventsList.add(ma);
							break;
						}
						}
					}

					Thread.sleep(50);
				}
		}
		catch (EOFException e) {
//			System.out.println("Replay thread completed.");
			// "Normal" exception (I kinda hate these ;)
		} catch (Exception e) {
			System.out.println("Replay thread interrupted.");
			CosmicNPCs.instance
			.broadcastMsg("Error loading action file, not an action file.");
			e.printStackTrace();
		}
		
		replayEntity.setDead();

		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}