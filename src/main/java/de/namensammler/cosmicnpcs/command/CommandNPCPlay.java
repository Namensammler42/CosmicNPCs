/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmicnpcs.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.namensammler.cosmicnpcs.CosmicNPCs;
import de.namensammler.cosmicnpcs.entity.EntityNPC;
import de.namensammler.cosmicnpcs.npcsystem.PlayThread;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public class CommandNPCPlay extends CommandBase {
	CosmicNPCs parent;
	ArrayList<PlayThread> playThreads;

	public CommandNPCPlay(CosmicNPCs _parent) {
		parent = _parent;
		playThreads = new ArrayList<PlayThread>();
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "npc-play";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "Usage: /npc-play <file> <name> <texture> [model], eg: /npc-play leyla1 Leyla leyla";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] args) {
		if (args.length < 3) {
			CosmicNPCs.instance.broadcastMsg(getCommandUsage(icommandsender));
			return;
		}
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		File file = null;
		
		if (side == Side.CLIENT) {
		
		String path = CosmicNPCs.config.getConfigFile().getAbsolutePath();
    	path = path.substring(0, path.length() - 4 - 10);
		
			file = new File(path + "/recordings/" + args[0] + ".action");

			if (!file.exists()) {
				CosmicNPCs.instance.broadcastMsg("Can't find " + args[0]
						+ ".action file!");
				return;
			}
		} else if (side == Side.SERVER) {
			
				file = new File("config/cosmicnpcs/recordings/" + args[0] + ".action");

				if (!file.exists()) {
					CosmicNPCs.instance.broadcastMsg("Can't find " + args[0]
							+ ".action file!");
					return;
				}
		}
		

		

		RandomAccessFile in;
		double x = 0;
		double y = 0;
		double z = 0;

		try {
			in = new RandomAccessFile(file, "r");
			short magic = in.readShort();

			if (magic != -5119) {
				CosmicNPCs.instance.broadcastMsg(args[0]
						+ " isn't a .action file.");
				in.close();
				return;
			}

			float yaw = in.readFloat();
			float pitch = in.readFloat();
			x = in.readDouble();
			y = in.readDouble();
			z = in.readDouble();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		World world = icommandsender.getEntityWorld();
			if (args[1] != null && args[2] != null) {
					/*if (args.length >= 4) {
						if (args[3].matches("Villager")) {
							EntityNPCVillager entity;
							entity = new EntityNPCVillager(world);
							entity.setPosition(x, y, z);
							entity.DisplayName = args[1];
							entity.setSkinSource(args[2]);
							entity.setCustomNameTag(args[1]);
							entity.setAlwaysRenderNameTag(true);
							if (args.length >= 5) {
								if (args[4].matches("Invulnerable")) {
									entity.isInvulnerable = true;
								}
							}
							world.spawnEntityInWorld(entity);
							
							for (Iterator<PlayThread> iterator = playThreads.iterator(); iterator
									.hasNext();) {
								PlayThread item = iterator.next();

								if (!item.t.isAlive()) {
									iterator.remove();
								}
							}

							playThreads.add(new PlayThread(entity, args[0]));
						} else if (args[3].matches("Chicken")) {
							EntityNPCChicken entity;
							entity = new EntityNPCChicken(world);
							entity.setPosition(x, y, z);
							entity.DisplayName = args[1];
							entity.setSkinSource(args[2]);
							if (args.length >= 5) {
								if (args[4].matches("Invulnerable")) {
									entity.isInvulnerable = true;
								}
							}
							world.spawnEntityInWorld(entity);
							
							for (Iterator<PlayThread> iterator = playThreads.iterator(); iterator
									.hasNext();) {
								PlayThread item = iterator.next();

								if (!item.t.isAlive()) {
									iterator.remove();
								}
							}

							playThreads.add(new PlayThread(entity, args[0]));
						} else if (args[3].matches("Witch")) {
							EntityNPCWitch entity;
							entity = new EntityNPCWitch(world);
							entity.setPosition(x, y, z);
							entity.DisplayName = args[1];
							entity.setSkinSource(args[2]);
							entity.setCustomNameTag(args[1]);
							entity.setAlwaysRenderNameTag(true);
							if (args.length >= 5) {
								if (args[4].matches("Invulnerable")) {
									entity.isInvulnerable = true;
								}
							}
							world.spawnEntityInWorld(entity);
							
							for (Iterator<PlayThread> iterator = playThreads.iterator(); iterator
									.hasNext();) {
								PlayThread item = iterator.next();

								if (!item.t.isAlive()) {
									iterator.remove();
								}
							}

							playThreads.add(new PlayThread(entity, args[0]));
						} else if (args[3].matches("Enderman")) {
							EntityNPCEnderman entity;
							entity = new EntityNPCEnderman(world);
							entity.setPosition(x, y, z);
							entity.DisplayName = args[1];
							entity.setSkinSource(args[2]);
							entity.setAlwaysRenderNameTag(false);
							if (args.length >= 5) {
								if (args[4].matches("Invulnerable")) {
									entity.isInvulnerable = true;
								}
							}
							world.spawnEntityInWorld(entity);
							
							for (Iterator<PlayThread> iterator = playThreads.iterator(); iterator
									.hasNext();) {
								PlayThread item = iterator.next();

								if (!item.t.isAlive()) {
									iterator.remove();
								}
							}

							playThreads.add(new PlayThread(entity, args[0])); 
							
						} else {	
							EntityNPC entity;
							entity = new EntityNPC(world);
							entity.setPosition(x, y, z);
							entity.DisplayName = args[1];
							entity.setSkinSource(args[2]);
							entity.setCustomNameTag(args[1]);
							entity.setAlwaysRenderNameTag(true);
							if (args.length >=4) {
								if (args[3].matches("Invulnerable")) {
									entity.isInvulnerable = true;
								}
							}
							world.spawnEntityInWorld(entity);*/

							/**
							 * Cleanup.
							 */
							/*for (Iterator<PlayThread> iterator = playThreads.iterator(); iterator
									.hasNext();) {
								PlayThread item = iterator.next();

								if (!item.t.isAlive()) {
									iterator.remove();
								}
							}

							playThreads.add(new PlayThread(entity, args[0]));
							}
					} else {*/
					
				EntityNPC entity;
				entity = new EntityNPC(world);
				entity.setPosition(x, y, z);
				entity.DisplayName = args[1];
				entity.setSkinSource(args[2]);
				entity.setCustomNameTag(args[1]);
				entity.setAlwaysRenderNameTag(true);
				if (args.length >=4) {
					if (args[3].matches("Invulnerable")) {
						entity.isInvulnerable = true;
					}
				}
				world.spawnEntityInWorld(entity);

				/**
				 * Cleanup.
				 */
				for (Iterator<PlayThread> iterator = playThreads.iterator(); iterator
						.hasNext();) {
					PlayThread item = iterator.next();

					if (!item.t.isAlive()) {
						iterator.remove();
					}
				}

				playThreads.add(new PlayThread(entity, args[0]));
				}
			//} else {
				//Genesis.instance.broadcastMsg("Not enough arguments.");
			//} 
		}
}
