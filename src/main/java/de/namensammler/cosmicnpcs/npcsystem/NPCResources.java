/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmicnpcs.npcsystem;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.namensammler.cosmicnpcs.CosmicNPCs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class NPCResources implements IResourcePack {
	
	public static final Set defaultResourceDomains = ImmutableSet.of("npctextures");

	@Override
	public InputStream getInputStream(ResourceLocation arg0) throws IOException {
		String path = CosmicNPCs.config.getConfigFile().getAbsolutePath();
    	path = path.substring(0, path.length() - 4 - 10);
    	return new FileInputStream(new File(path));
	}

	@Override
	public BufferedImage getPackImage() throws IOException {
		return null;
	}

	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer arg0, String arg1) {
		return null;
	}

	@Override
	public String getPackName() {
		return "npctextures";
	}

	@Override
	public Set getResourceDomains() {
		return defaultResourceDomains;
	}

	@Override
	public boolean resourceExists(ResourceLocation arg0) {
		return true;
	}

}