package de.namensammler.cosmicnpcs.npcsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.namensammler.cosmicnpcs.CosmicNPCs;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.server.MinecraftServer;

public class RecordThread implements Runnable {
	public Thread t;
	EntityPlayer player;
	public Boolean capture = false;
	RandomAccessFile in;
	Boolean lastTickSwipe = false;
	int itemsEquipped[] = new int[5];
	List<NPCAction> eventList;

	public RecordThread(EntityPlayer _player, String capname) {
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		// Create a new, second thread
		try {
			if (side == Side.CLIENT) {
			
			String path = CosmicNPCs.config.getConfigFile().getAbsolutePath();
	    	path = path.substring(0, path.length() - 4 - 10);
			
				File file = new File(path + "/recordings");

				if (!file.exists()) {
					file.mkdirs();
				}
				
				File file2 = new File(Minecraft.getMinecraft().mcDataDir+"/CosmicNPCs/NPCTextures/assets/npctextures/textures/entity/npc");

				if (!file2.exists()) {
					file2.mkdirs();
				}
				
				in = new RandomAccessFile(file.getAbsolutePath() + "/" + capname
						+ ".action", "rw");
				in.setLength(0);
			} else if (side == Side.SERVER) {
				File file = new File("config/cosmicnpcs/recordings");

				if (!file.exists()) {
					file.mkdirs();
				}
				
				in = new RandomAccessFile(file.getAbsolutePath() + "/" + capname
						+ ".action", "rw");
				in.setLength(0);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player = _player;
		capture = true;
		eventList = CosmicNPCs.instance.getActionListForPlayer(player);
		t = new Thread(this, "NPC Record Thread");
		t.start();
	}

	// This is the entry point for the second thread.
	public void run() {
		try {
			in.writeShort(0xEC01);

			while (capture) {
				trackAndWriteMovement();
				trackSwing();
				trackHeldItem();
				trackArmor();
				writeActions();
				Thread.sleep(50);

				if (player.isDead) {
					capture = false;
					CosmicNPCs.instance.recordThreads.remove(player);
					CosmicNPCs.instance.broadcastMsg("Stopped recording "
							+ player.getDisplayName() + ". RIP.");
				}
			}

			in.close();
		} catch (InterruptedException e) {
			System.out.println("Child interrupted.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Exiting child thread.");
	}

	private void trackAndWriteMovement() throws IOException {
		in.writeFloat(player.rotationYaw);
		in.writeFloat(player.rotationPitch);
		in.writeDouble(player.posX);
		in.writeDouble(player.posY);
		in.writeDouble(player.posZ);
		in.writeDouble(player.motionX);
		in.writeDouble(player.motionY);
		in.writeDouble(player.motionZ);
		in.writeFloat(player.fallDistance);
		in.writeBoolean(player.isAirBorne);
		in.writeBoolean(player.isSneaking());
		in.writeBoolean(player.isSprinting());
		in.writeBoolean(player.onGround);
		in.writeBoolean((player.getDataWatcher().getWatchableObjectByte(0) & 1 << 4) != 0);
	}

	private void trackArmor() {
		/*
		 * Track armor equipped.
		 */
		// TODO: Sequential equipping of same item id but different type =
		// problem.
		for (int ci = 1; ci < 5; ci++) {
			if (player.inventory.armorInventory[ci - 1] != null) {
				if (Item.getIdFromItem(player.inventory.armorInventory[ci - 1]
						.getItem()) != itemsEquipped[ci]) {
					itemsEquipped[ci] = Item
							.getIdFromItem(player.inventory.armorInventory[ci - 1]
									.getItem());
					NPCAction ma = new NPCAction(NPCActionTypes.EQUIP);
					ma.armorSlot = ci;
					ma.armorId = itemsEquipped[ci];
					ma.armorDmg = player.inventory.armorInventory[ci - 1]
							.getItemDamage();
					player.inventory.armorInventory[ci - 1]
							.writeToNBT(ma.itemData);
					eventList.add(ma);
				}
			} else {
				// TODO
				if (itemsEquipped[ci] != -1) {
					itemsEquipped[ci] = -1;
					NPCAction ma = new NPCAction(NPCActionTypes.EQUIP);
					ma.armorSlot = ci;
					ma.armorId = itemsEquipped[ci];
					ma.armorDmg = 0;
					eventList.add(ma);
				}
			}
		}
	}

	private void trackHeldItem() {
		if (player.getHeldItem() != null) {
			if (Item.getIdFromItem(player.getHeldItem().getItem()) != itemsEquipped[0]) {
				itemsEquipped[0] = Item.getIdFromItem(player.getHeldItem()
						.getItem());
				NPCAction ma = new NPCAction(NPCActionTypes.EQUIP);
				ma.armorSlot = 0;
				ma.armorId = itemsEquipped[0];
				ma.armorDmg = player.getHeldItem().getItemDamage();
				player.getHeldItem().writeToNBT(ma.itemData);
				eventList.add(ma);
			}
		} else {
			if (itemsEquipped[0] != -1) {
				itemsEquipped[0] = -1;
				NPCAction ma = new NPCAction(NPCActionTypes.EQUIP);
				ma.armorSlot = 0;
				ma.armorId = itemsEquipped[0];
				ma.armorDmg = 0;
				eventList.add(ma);
			}
		}
	}

	private void trackSwing() {
		/*
		 * Track "Swings" weapon / fist.
		 */
		if (player.isSwingInProgress) {
			if (!lastTickSwipe) {
				lastTickSwipe = true;
				eventList.add(new NPCAction(NPCActionTypes.SWIPE));
			}
		} else {
			lastTickSwipe = false;
		}
	}

	private void writeActions() throws IOException {
		// Any actions?
		if (eventList.size() > 0) {
			in.writeBoolean(true);
			NPCAction ma = eventList.get(0);
			in.writeByte(ma.type);

			switch (ma.type) {
			case NPCActionTypes.CHAT: {
				in.writeUTF(ma.message);
				break;
			}

			case NPCActionTypes.SWIPE: {
				break;
			}

			case NPCActionTypes.DROP: {
				CompressedStreamTools.write(ma.itemData, in);
				break;
			}

			case NPCActionTypes.EQUIP: {
				in.writeInt(ma.armorSlot);
				in.writeInt(ma.armorId);
				in.writeInt(ma.armorDmg);

				if (ma.armorId != -1) {
					CompressedStreamTools.write(ma.itemData, in);
				}

				break;
			}

			case NPCActionTypes.SHOOTARROW: {
				in.writeInt(ma.arrowCharge);
				break;
			}

			case NPCActionTypes.PLACEBLOCK: {
				in.writeInt(ma.xCoord);
				in.writeInt(ma.yCoord);
				in.writeInt(ma.zCoord);
				CompressedStreamTools.write(ma.itemData, in);
				break;
			}
			
			case NPCActionTypes.BREAKBLOCK: {
				in.writeInt(ma.xCoord);
				in.writeInt(ma.yCoord);
				in.writeInt(ma.zCoord);				
				break;
			}
			
			case NPCActionTypes.INTERACTWITHBLOCK: {
				in.writeInt(ma.xCoord);
				in.writeInt(ma.yCoord);
				in.writeInt(ma.zCoord);	
				in.writeInt(ma.metaData);
				break;
			}

			case NPCActionTypes.LOGOUT: {
				CosmicNPCs.instance.recordThreads.remove(player);
				CosmicNPCs.instance.broadcastMsg("Stopped recording "
						+ player.getDisplayName() + ". Bye!");
				capture = false;
				break;
			}
			}

			eventList.remove(0);
		} else {
			in.writeBoolean(false);
		}
	}
}