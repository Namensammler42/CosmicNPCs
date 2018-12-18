/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmicnpcs.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiCheckBox;
import de.namensammler.cosmicnpcs.CosmicNPCs;
import de.namensammler.cosmicnpcs.EventHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiCommandScreen extends GuiScreen {
	private int CommandScreenHeight = 83;
    private final int CommandScreenWidth = 176;
    private static ResourceLocation CommandScreenBaseTexture = new ResourceLocation(CosmicNPCs.MODID + ":" + "textures/gui/command_screen.png");
    private static String[] stringText = new String[5];
    private GuiButton buttonDone;
    private GuiButton buttonPrevPage;
    private GuiButton buttonNextPage;
    private GuiButton buttonOptionStart;
    private GuiTextField textField;
    private int page = 1;
    
    public GuiCommandScreen()
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() 
    {
        buttonList.clear();
        
        stringText[0] = "Set a Command:";
    	stringText[1] = "-"+page+"-";
        
        Keyboard.enableRepeatEvents(true);

        buttonDone = new GuiButton(0, width / 2 -25, this.height/2+15, 50, 20, I18n.format("gui.done", new Object[0]));
        buttonPrevPage = new GuiButton(1, width / 2 -80, this.height/2+15, 20, 20, I18n.format("<", new Object[0]));
        buttonNextPage = new GuiButton(2, width / 2 +60, this.height/2+15, 20, 20, I18n.format(">", new Object[0]));
        buttonOptionStart = new GuiCheckBox(3, width / 2 -80, this.height/2, "", false);
        int offsetFromScreenLeft = (width - CommandScreenWidth) / 2;
        buttonList.add(buttonDone);
        buttonList.add(buttonPrevPage);
        buttonList.add(buttonNextPage);
        buttonList.add(buttonOptionStart);
        
        this.textField = new GuiTextField(this.fontRendererObj, this.width / 2 - 83, this.height/2-25, 166, 20);
        textField.setMaxStringLength(242);
        if (EventHandler.command[page] != null) {
        	textField.setText(EventHandler.command[page]);
        } else {
        	textField.setText("/npc-play <file> <name> <texture>");
        }
        this.textField.setFocused(true);
    }
    
    protected void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.textField.textboxKeyTyped(par1, par2);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() 
    {
        buttonDone.visible = true;
        buttonPrevPage.visible = true;
        buttonNextPage.visible = true;
        buttonOptionStart.visible = true;
        this.textField.updateCursorCounter();
        
        if (page <= 1) {
        	buttonPrevPage.enabled = false;
        }
        if (page >= 42) {
        	buttonNextPage.enabled = false;
        }
        if (EventHandler.commandCounter == page) {
        	buttonOptionStart.enabled = true;
        }
    }
 
    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int parWidth, int parHeight, float p_73863_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(CommandScreenBaseTexture);
        int offsetFromScreenLeft = (width - CommandScreenWidth ) / 2;
        drawTexturedModalRect(offsetFromScreenLeft, (height - CommandScreenHeight) / 2, 0, 0, CommandScreenWidth, CommandScreenHeight);
        int widthOfString;
        String stringIndicator = I18n.format("Indicator", 

             new Object[] {Integer.valueOf(1), 5});

        widthOfString = fontRendererObj.getStringWidth(stringIndicator);
        fontRendererObj.drawSplitString(stringText[0], offsetFromScreenLeft + 50, this.height/2-36, 116, 0);
        if (page >= 10){
        	fontRendererObj.drawSplitString(stringText[1], offsetFromScreenLeft + 76, this.height/2, 160, 0);
        } else {
        	fontRendererObj.drawSplitString(stringText[1], offsetFromScreenLeft + 79, this.height/2, 160, 0);
        }
        
        this.textField.drawTextBox();
        
        super.drawScreen(parWidth, parHeight, p_73863_3_);
    }
    
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
        this.textField.mouseClicked(x, y, btn);
    }
    

    /**
     * Called when a mouse button is pressed and the mouse is moved around. 

     * Parameters are : mouseX, mouseY, lastButtonClicked & 

     * timeSinceMouseClick.
     */
    @Override
    protected void mouseClickMove(int parMouseX, int parMouseY, 

          int parLastButtonClicked, long parTimeSinceMouseClick) 

    {
     
    }

    @Override
    protected void actionPerformed(GuiButton parButton) 
    {
     if (parButton == buttonDone)
     {
    	 EventHandler.command[page] = textField.getText();
         mc.displayGuiScreen((GuiScreen)null);
     }  
     if (parButton == buttonPrevPage)
     {
    	 EventHandler.command[page] = textField.getText();
    	 if (page > 1) {
    		 page--;
    		 initGui();
    	 }
     } 
     if (parButton == buttonNextPage)
     {
    	 EventHandler.command[page] = textField.getText();
    	 if (page < 42) {
    		 page++;
    		 initGui();
    	 }
     } 
     if (parButton == buttonOptionStart) {
    	 EventHandler.commandCounter = page;
     }
   }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat 

     * events
     */
    @Override
    public void onGuiClosed() 
    {
     
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in 

     * single-player
     */
    @Override
    public boolean doesGuiPauseGame()
    {
        return true;
    }
}