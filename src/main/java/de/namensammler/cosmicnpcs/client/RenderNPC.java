package de.namensammler.cosmicnpcs.client;

import cpw.mods.fml.relauncher.SideOnly;
import de.namensammler.cosmicnpcs.CosmicNPCs;
import de.namensammler.cosmicnpcs.entity.EntityNPC;

import java.io.File;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderNPC extends RenderBiped
{
    private static final String __OBFID = "CL_00000984";
    
    Boolean renderEat = false;
    
    public static final ResourceLocation defaultTexture = new ResourceLocation("textures/entity/steve.png");

    public RenderNPC(ModelBiped p_i1253_1_, float p_i1253_2_)
    {
        super(p_i1253_1_, p_i1253_2_);
    }
    
    @Override
	protected void func_82420_a(EntityLiving par1EntityLiving,
			ItemStack par2ItemStack) {
		return;
	}

	/**
	 * Called from RenderBiper in renderEquippedItems, used to rotate item when
	 * eating / blocking.
	 */
	@Override
	protected void func_82422_c() {
		GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
		if (renderEat) {
			GL11.glTranslatef(0.175F, -0.1F, 0.02F);
			GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
		}
	}

	@Override
	public void doRender(EntityLiving par1EntityLiving, double par2,
			double par4, double par6, float par8, float par9) {
		EntityNPC par1EntityMocap = (EntityNPC) par1EntityLiving;
		ItemStack itemstack = par1EntityLiving.getHeldItem();
	//	GL11.glScalef(0.8f, 0.8f, 0.8f);
		this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = itemstack != null ? 1
				: 0;
		renderEat = false;

		if (itemstack != null && par1EntityMocap.isEating()) {
			EnumAction enumaction = itemstack.getItemUseAction();

			if (enumaction == EnumAction.block) {
				renderEat = true;
				this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = 3;
			}

			else if (enumaction == EnumAction.bow) {
				this.field_82423_g.aimedBow = this.field_82425_h.aimedBow = this.modelBipedMain.aimedBow = true;
			}
			

		}

		this.field_82423_g.isSneak = this.field_82425_h.isSneak = this.modelBipedMain.isSneak = par1EntityLiving
				.isSneaking();
		super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f)
	{
		float f1 = 0.9375F;
        GL11.glScalef(f1, f1, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		EntityNPC npc = (EntityNPC)entity;
		String path = CosmicNPCs.config.getConfigFile().getAbsolutePath();
    	path = path.substring(0, path.length() - 4 - 10);
		File textureFile = new File(path + "/assets/npctextures/"+npc.getSkinSource()+".png");
		
		if (npc.getSkinSource() != "" && textureFile.exists()) {
			return new ResourceLocation("npctextures", npc.getSkinSource() + ".png");
		}

		return defaultTexture;
	}

}