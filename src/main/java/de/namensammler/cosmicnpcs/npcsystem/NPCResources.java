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