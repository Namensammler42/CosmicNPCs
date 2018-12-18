/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmiccore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LivingPlaceBlockEvent extends LivingEvent {
	public ItemStack theItem;
	public int xCoord;
	public int yCoord;
	public int zCoord;

	public LivingPlaceBlockEvent(EntityLivingBase entity, ItemStack theItem,
			int x, int y, int z) {
		super(entity);
		this.theItem = theItem;
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}
}