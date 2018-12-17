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