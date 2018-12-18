
/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmicnpcs;

import cpw.mods.fml.common.registry.EntityRegistry;
import de.namensammler.cosmicnpcs.entity.EntityNPC;
import net.minecraft.entity.EntityList;

public class EntityHandler {
	
	public static void mainRegistry(){
		registerEntity();
	}
	
	public static void registerEntity(){
		createEntityWithoutEgg(EntityNPC.class, "NPC");
	}
	
	public static void createEntity(Class entityClass, String entityName, int solidColour, int spotColour){
		int randomId = EntityRegistry.findGlobalUniqueEntityId();
		
		EntityRegistry.registerGlobalEntityID(entityClass, entityName, randomId);
		EntityRegistry.registerModEntity(entityClass, entityName, randomId, CosmicNPCs.instance, 64, 1, true);
		createEgg(randomId, solidColour, spotColour);
	}
	
	public static void createEntityWithoutEgg(Class entityClass, String entityName){
		int randomId = EntityRegistry.findGlobalUniqueEntityId();
		
		EntityRegistry.registerGlobalEntityID(entityClass, entityName, randomId);
		EntityRegistry.registerModEntity(entityClass, entityName, randomId, CosmicNPCs.instance, 64, 1, true);
	}

	private static void createEgg(int randomId, int solidColour, int spotColour) {
		EntityList.entityEggs.put(Integer.valueOf(randomId), new EntityList.EntityEggInfo(randomId, solidColour, spotColour));
	}
}