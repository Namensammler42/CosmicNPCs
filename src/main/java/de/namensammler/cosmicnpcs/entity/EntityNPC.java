/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmicnpcs.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.namensammler.cosmicnpcs.npcsystem.NPCAction;
import de.namensammler.cosmicnpcs.npcsystem.NPCActionTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityNPC extends EntityLiving {
	
	public boolean isInvulnerable = false;
	
	private int itemInUseCount = 0;
	public String DisplayName;
	
	int curblockDamage = 0;
	World world;

	public List<NPCAction> eventsList = Collections
			.synchronizedList(new ArrayList<NPCAction>());

	public EntityNPC(World par1World) {
		super(par1World);
		world = par1World;
		this.tasks.taskEntries.clear();
		this.targetTasks.taskEntries.clear();
	}
	
	@Override
	public boolean doesEntityNotTriggerPressurePlate() {
        return false;
    }
	
	@Override
	public boolean isEntityInvulnerable() {
		return isInvulnerable;
	}

	public void setItemInUseCount(int itemInUseCount) {
		this.itemInUseCount = itemInUseCount;
	}

	public int getItemInUseCount() {
		return itemInUseCount;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("isInvulnerable", isInvulnerable);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		isInvulnerable = compound.getBoolean("isInvulnerable");
	}

	private void replayShootArrow(NPCAction ma) {
		float f = (float) ma.arrowCharge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;

		if ((double) f < 0.1D) {
			return;
		}

		if (f > 1.0F) {
			f = 1.0F;
		}

		EntityArrow entityarrow = new EntityArrow(world, this, f * 2.0F);
		entityarrow.canBePickedUp = 1;
		this.playSound("random.bow", 1.0F,
				1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		world.spawnEntityInWorld(entityarrow);
	}

	private void processActions(NPCAction ma) {
		switch (ma.type) {
		case NPCActionTypes.CHAT: {
			ArrayList<EntityPlayerMP> temp = (ArrayList<EntityPlayerMP>) FMLCommonHandler
					.instance().getSidedDelegate().getServer()
					.getConfigurationManager().playerEntityList;

			for (EntityPlayerMP player : temp) {
				if (FMLCommonHandler.instance().getSidedDelegate().getServer()
						.getConfigurationManager()
						.func_152596_g(player.getGameProfile())) {
					ChatComponentText cmp = new ChatComponentText("<"+DisplayName+"> " + ma.message);

					player.addChatMessage(cmp);
				}
			}
			break;
		}

		case NPCActionTypes.SWIPE: {
			swingItem();
			
			/* Ref: attackTargetEntityWithCurrentItem EntityPlayer */
			EntityLivingBase mop = GetTargetEntityLiving(5);
			if (mop != null)
			{				
				attackEntityAsMob(mop);

				ItemStack theI = this.getHeldItem();
				float dmg = 1.0f;
				if(!world.isRemote && theI != null) {
					if (theI.getItem() instanceof ItemSword)
					{
						ItemSword itemsword = (ItemSword)theI.getItem();
						dmg = itemsword.func_150931_i();
						float f1 = EnchantmentHelper.getEnchantmentModifierLiving(this, mop);
						dmg += f1;					
					}
				}
				mop.attackEntityFrom(DamageSource.causeMobDamage(this), dmg);
			}
			break;
		}

		case NPCActionTypes.EQUIP: {
			if (ma.armorId == -1) {
				setCurrentItemOrArmor(ma.armorSlot, null);
			} else {
				ItemStack loadedEquip = ItemStack
						.loadItemStackFromNBT(ma.itemData);
				setCurrentItemOrArmor(ma.armorSlot, loadedEquip);
			}

			break;
		}

		case NPCActionTypes.DROP: {
			ItemStack foo = ItemStack.loadItemStackFromNBT(ma.itemData);
			EntityItem ea = new EntityItem(world, posX, posY
					- 0.30000001192092896D + (double) getEyeHeight(), posZ, foo);
			Random rand = new Random();
			float f = 0.3F;
			ea.motionX = (double) (-MathHelper.sin(rotationYaw / 180.0F
					* (float) Math.PI)
					* MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * f);
			ea.motionZ = (double) (MathHelper.cos(rotationYaw / 180.0F
					* (float) Math.PI)
					* MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * f);
			ea.motionY = (double) (-MathHelper.sin(rotationPitch / 180.0F
					* (float) Math.PI)
					* f + 0.1F);
			f = 0.02F;
			float f1 = rand.nextFloat() * (float) Math.PI * 2.0F;
			f *= rand.nextFloat();
			ea.motionX += Math.cos((double) f1) * (double) f;
			ea.motionY += (double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F);
			ea.motionZ += Math.sin((double) f1) * (double) f;
			world.spawnEntityInWorld(ea);
			break;
		}

		case NPCActionTypes.SHOOTARROW: {
			replayShootArrow(ma);
			break;
		}

		case NPCActionTypes.BREAKBLOCK: {
			Block aBlock = world.getBlock(ma.xCoord, ma.yCoord, ma.zCoord);
			if (aBlock != Blocks.air)
			{
				int i1 = world.getBlockMetadata(ma.xCoord, ma.yCoord, ma.zCoord);
				
		
				
				/* Play the visual effect associated with breaking this block + meta */
				world.playAuxSFX(2001, ma.xCoord, ma.yCoord, ma.zCoord, Block.getIdFromBlock(aBlock)
						+ (i1 << 12));

				world.setBlockToAir(ma.xCoord, ma.yCoord, ma.zCoord);
				aBlock.onBlockDestroyedByPlayer(world, ma.xCoord, ma.yCoord, ma.zCoord, i1);                
				//aBlock.dropBlockAsItem(world,  ma.xCoord, ma.yCoord, ma.zCoord, i1, 0);
			}
			break;
		}

		case NPCActionTypes.PLACEBLOCK: {
			ItemStack foo = ItemStack.loadItemStackFromNBT(ma.itemData);

			if (foo.getItem() instanceof ItemBlock) {
				ItemBlock f = (ItemBlock) foo.getItem();

				f.placeBlockAt(foo, null, world, ma.xCoord, ma.yCoord,
						ma.zCoord, 0, 0, 0, 0, foo.getItemDamage());

				/* Play the sound placing this block makes! */
				world.playSoundEffect((double)((float)ma.xCoord + 0.5F), (double)((float)ma.yCoord + 0.5F), (double)((float)ma.zCoord + 0.5F), f.field_150939_a.stepSound.func_150496_b(), (f.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, f.field_150939_a.stepSound.getPitch() * 0.8F);
}

			break;
		}
		
		case NPCActionTypes.INTERACTWITHBLOCK: {
			Block aBlock = world.getBlock(ma.xCoord, ma.yCoord, ma.zCoord);
			if (aBlock != Blocks.air)
			{
				EntityPlayer player = new EntityPlayer(this.world, new GameProfile(this.entityUniqueID, "NPCPlayer")) {
					
					@Override
					public ChunkCoordinates getPlayerCoordinates() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public void addChatMessage(IChatComponent p_145747_1_) {
						// TODO Auto-generated method stub
						
					}
				};
				world.setBlockMetadataWithNotify(ma.xCoord, ma.yCoord, ma.zCoord, ma.metaData, 3);  
			//	if (!(aBlock instanceof BlockContainer)) {
					aBlock.onBlockActivated(world, ma.xCoord, ma.yCoord, ma.zCoord, player, 0, 1.0f, 1.0f, 1.0f);
					world.notifyBlockChange(ma.xCoord, ma.yCoord, ma.zCoord, aBlock);
		//		}
			}
			break;
		}
		}
	}

	public void onLivingUpdate() {
		if (eventsList.size() > 0) {
			NPCAction ma = eventsList.remove(0);
			processActions(ma);
		}

		this.updateArmSwingProgress();

		if (this.newPosRotationIncrements > 0) {
			double d0 = this.posX + (this.newPosX - this.posX)
					/ (double) this.newPosRotationIncrements;
			double d1 = this.posY + (this.newPosY - this.posY)
					/ (double) this.newPosRotationIncrements;
			double d2 = this.posZ + (this.newPosZ - this.posZ)
					/ (double) this.newPosRotationIncrements;
			double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw
					- (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + d3
					/ (double) this.newPosRotationIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.newRotationPitch - (double) this.rotationPitch)
					/ (double) this.newPosRotationIncrements);
			--this.newPosRotationIncrements;
			this.setPosition(d0, d1, d2);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		} else if (!this.isClientWorld()) {
			this.motionX *= 0.98D;
			this.motionY *= 0.98D;
			this.motionZ *= 0.98D;
		}

		if (Math.abs(this.motionX) < 0.005D) {
			this.motionX = 0.0D;
		}

		if (Math.abs(this.motionY) < 0.005D) {
			this.motionY = 0.0D;
		}

		if (Math.abs(this.motionZ) < 0.005D) {
			this.motionZ = 0.0D;
		}

		if (!this.isClientWorld()) {
			this.rotationYawHead = this.rotationYaw;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d0 = this.posX - this.prevPosX;
		double d1 = this.posZ - this.prevPosZ;
		float f6 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

		if (f6 > 1.0F) {
			f6 = 1.0F;
		}

		this.limbSwingAmount += (f6 - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(20, "ridgedog");
	}

	public void setSkinSource(String par1Str) {
		this.dataWatcher.updateObject(20, par1Str);
	}

	public String getSkinSource() {
		return this.dataWatcher.getWatchableObjectString(20);
	}

	/*
	 * Modified from code found at:
	 * http://www.minecraftforge.net/forum/index.php?topic=4925.0
	 */
	public EntityLivingBase GetTargetEntityLiving(int scanRadius)
	{		
		double targetDistance = Math.pow(scanRadius,2);

		EntityLivingBase target = null;

		List lst = world.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(posX-scanRadius, posY-scanRadius, posZ-scanRadius, posX+scanRadius, posY+scanRadius, posZ+scanRadius));
		for (int i = 0; i < lst.size(); i ++)
		{
			Entity ent = (Entity) lst.get(i);
			if (ent instanceof EntityLivingBase && ent!=null && ent.boundingBox != null)
			{

				float distance = getDistanceToEntity(ent) + 0.1f;
				float angle = rotationYawHead;
				float pitch = rotationPitch;

				Vec3 look = getLookVec();
				Vec3 targetVec = Vec3.createVectorHelper(posX + look.xCoord * distance, (getEyeHeight()/2) + posY + look.yCoord * distance, posZ + look.zCoord * distance);

				if (ent.boundingBox.isVecInside(targetVec))
				{

					if (distance < targetDistance && distance > 0)
					{
						targetDistance = distance;
						target = (EntityLivingBase) ent;
					}
				}
			}
		}
		return target;
}
	
	public boolean isAIEnabled() {
		return false;
	}
	
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
	}

}