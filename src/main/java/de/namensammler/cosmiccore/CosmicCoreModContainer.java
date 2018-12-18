/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmiccore;

import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

public class CosmicCoreModContainer extends DummyModContainer {
	public CosmicCoreModContainer() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();		
		meta.modId = "cosmiccore";
		meta.name = "CosmicCore";
		meta.version = "1.0";
		meta.credits = "Namensammler42, EchebKeso";
		meta.authorList = Arrays.asList("Namensammler42, EchebKeso");
		meta.description = "Core ASM hooks for CosmicNPCs";
		meta.url = "";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}

	@Subscribe
	public void modConstruction(FMLConstructionEvent evt) {
	}

	@Subscribe
	public void init(FMLInitializationEvent evt) {
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent evt) {
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent evt) {
	}
}
