
/**
 * Copyright (c) 2011, Michael Burgwin
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the 
following conditions are met:
-Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
-Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */







package com.BASeCamp.BCPixArt;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;

import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
public class PixArtPlugin extends JavaPlugin {

	public final static String PluginName="BCPixArt";
	public final FMBlockListener BListener = new FMBlockListener(this);
	public final FMPlayerListener PListener = new FMPlayerListener(this);
	//public static final String versionheader="BCPixArt Version 0.1";
	
	public final PixArtCommand PicArtCListener = new PixArtCommand(this);
	
	
	//public FillModCommand CListener = new FillModCommand(this);
	public Boolean debug;
	  public static String pluginMainDir = "./plugins/BC Block Pixel Art";
	    public static String pluginConfigLocation = pluginMainDir + "/PixArtPlugin.cfg";
	private static config pluginsettings;
	
	
	public static config getsettings()
	{
		if(pluginsettings==null)
			{
			
			PixArtCommand.debugmessage("getsettings... pluginsettings is null...");
			
			}
		return pluginsettings;
		
	}
	
	
	public static String versionheader="BCPixArt Version 0.1";
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
		//Save 
		PicArtCListener.DoSave();
		
		
		System.out.print("BCPixArt Disabled");
	}
	
	public static boolean hasAlpha(Image image) {
	    // If buffered image, the color model is readily available
	    if (image instanceof BufferedImage) {
	        BufferedImage bimage = (BufferedImage)image;
	        return bimage.getColorModel().hasAlpha();
	    }

	    // Use a pixel grabber to retrieve the image's color model;
	    // grabbing a single pixel is usually sufficient
	     PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
	    try {
	        pg.grabPixels();
	    } catch (InterruptedException e) {
	    }

	    // Get the image's color model
	    ColorModel cm = pg.getColorModel();
	    return cm.hasAlpha();
	}
	public static BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	    }

	    // This code ensures that all the pixels in the image are loaded
	    image = new ImageIcon(image).getImage();

	    // Determine if the image has transparent pixels; for this method's
	    // implementation, see Determining If an Image Has Transparent Pixels
	    boolean hasAlpha = hasAlpha(image);

	    // Create a buffered image with a format that's compatible with the screen
	    BufferedImage bimage = null;
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        // Determine the type of transparency of the new buffered image
	        int transparency = Transparency.OPAQUE;
	        if (hasAlpha) {
	            transparency = Transparency.BITMASK;
	        }

	        // Create the buffered image
	        GraphicsDevice gs = ge.getDefaultScreenDevice();
	        GraphicsConfiguration gc = gs.getDefaultConfiguration();
	        bimage = gc.createCompatibleImage(
	            image.getWidth(null), image.getHeight(null), transparency);
	    } catch (HeadlessException e) {
	        // The system does not have a screen
	    }

	    if (bimage == null) {
	        // Create a buffered image using the default color model
	        int type = BufferedImage.TYPE_INT_RGB;
	        if (hasAlpha) {
	            type = BufferedImage.TYPE_INT_ARGB;
	        }
	        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	    }

	    // Copy image to buffered image
	    Graphics g = bimage.createGraphics();

	    // Paint the image onto the buffered image
	    g.drawImage(image, 0, 0, null);
	    g.dispose();

	    return bimage;
	}
	
	
	public static boolean hasPermex(Player p,String Node){
		return hasPermex(p,Node,true);
	}
	/**
	 * special "extended" hasPermissions routine that automatically adds multiworld support....
	 * @param p Player to check permissions for
	 * @param Node Name of setting/permission to check
	 * @param defaultOp Whether this setting, by default, requires Op status.
	 * @return
	 */
	public static boolean hasPermex(Player p,String Node,boolean defaultOp){
		
		
		
			return p.hasPermission(Node);
			//return Perms.has(p,PluginName + ".user." + Node);
	
	}

	
	
	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
		System.out.print("BCPixArt Enabled");
		
		  try {
	        	Properties preSettings = new Properties();
	        	if ((new File(pluginConfigLocation)).exists()) {
	        		preSettings.load(new FileInputStream(new File(pluginConfigLocation)));
	        		pluginsettings = new config(preSettings, this);
	        		debug = pluginsettings.debug;
	        	}
	        	else{
	        		PixArtCommand.debugmessage("recreating configuration...");
	        		config.recreateconfig();
	        		//bugfix: now it also loads the config it just made...
	        		preSettings.load(new FileInputStream(new File(pluginConfigLocation)));
	        		pluginsettings = new config(preSettings, this);
	        		debug = pluginsettings.debug;
	        	}
	        	
	        } catch (Exception e) {
	        	System.out.println("Could not load configuration! " + e);
	        	
	        }
	        BListener.setConfig(pluginsettings);
	        PListener.setConfig(pluginsettings);
	        
	        PluginManager pm = getServer().getPluginManager();
	        
	        
	        pm.registerEvents(BListener, this);
	        pm.registerEvents(PListener, this);
	         //pm.registerEvent(org.bukkit.event.block.BlockDamageEvent., BListener, Priority.Normal, this);
	         
	        	 /*
	        	 PluginCommand batchcommand = this.getCommand("fill");
	         batchcommand.setExecutor(CListener);
	         }
	         catch(NullPointerException e)
	         {
	        	 System.out.println("[FillMod] Another plugin is using /fill.");
	        	 
	         
	         }
	         */
	         //PicArtCListener.Load();
	         PluginCommand batchcommand = this.getCommand("pixart");
	         batchcommand.setExecutor(PicArtCListener);
	       
	 
	
	
	
	}}