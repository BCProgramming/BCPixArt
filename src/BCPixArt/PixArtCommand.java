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

package BCPixArt;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Wool;

import BCPixArt.FMBlockListener.BlockFillMode;
import BCPixArt.FMBlockListener.BlockFillSetupData;
import BCPixArt.FMBlockListener.FMBlockListenerState;

/**
 * 
 * @author BC_Programming PixArtCommand- CommandExecutor Implementation- handles
 *         everything related to Pixel Art commands. Except for what it doesn't, of course... but that
 *         goes without saying.
 */
public class PixArtCommand implements CommandExecutor {
	// the X coordinate in the image will be the X coord in the world; Y coord
	// in the image will be Z.
	// X coord will be Y coord in world. Y Coord will be Z.
	public final ResourceCalculator rescalc = new ResourceCalculator(this);

	public enum BuildOrientationConstants {
		/**
		 * Art will be built with the X Coordinate of the image being used for
		 * the X coordinate in the world, and the Y Coordinate of the image will
		 * be used for the Z coordinate.
		 */
		Build_XZ,
		/**
		 * Art will be built with the X coordinate of the image being used for
		 * the X coordinate in the world, and the Y coordinate of the image will
		 * be used for the Y coordinate.
		 */
		Build_XY,

		/**
		 * X coordinate in image is used for Y. Y Coordinate in image is used
		 * for X.
		 */
		Build_YX,

		/**
		 * X coordinate in image is used for Y. Y Coordinate in image is used
		 * for Z.
		 */

		Build_YZ,

		/**
		 * X coordinate in image is used for Z coordinate in world. Y Coordinate
		 * in image is used for X coordinate in world.
		 */
		Build_ZX,
		/**
		 * X Coordinate in image is used for Z coordinate in world. Y Coordinate
		 * in image is used for Y Coordinate in world.
		 */
		Build_ZY

	}

	public class ColorEntryData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7571399858179547284L;
		public Color colorvalue;
		public int BlockID;
		public int BlockData;

		public ColorEntryData(Color pcolorvalue, int pBlockID, int pBlockData) {
			colorvalue = pcolorvalue;
			BlockID = pBlockID;
			BlockData = pBlockData;

		}

	}
	/**
	 * 
	 * @author BC_Programming
	 * what was originally a data class used for storing player specific settings for building, such as orientation,scale, flip, etc
	 * and has evolved into a utility class that retrieves some of the relevant info itself.
	 * also for reasons I have yet to fathom some of the command processing is done here as well. Unsolved mysteries...
	 */
	public class PixArtPlayerData implements Cloneable {

		public Player pPlayer;
		public BuildOrientationConstants BuildOrientation = BuildOrientationConstants.Build_XZ;
		public float scaleXPercent = 1.0f;
		public float scaleYPercent = 1.0f;
		public Boolean FlipX,FlipY;
		public int OffsetX=0;
		public int OffsetY=0;
		public int OffsetZ=0;
		public final Boolean canuseURL;
		public final Boolean canusePath;
		private Boolean usePlayerResources = false;

		public void setResourceUse(Boolean value) {
			usePlayerResources = value;
			Cloneable px;
			
		}
		@Override
		public Object clone()
		{
			return new PixArtPlayerData(this);
			
		
		}
		public PixArtPlayerData(PixArtPlayerData clonethis){
			
			pPlayer=clonethis.pPlayer;
			BuildOrientation = clonethis.BuildOrientation;
			scaleXPercent = clonethis.scaleXPercent;
			scaleYPercent = clonethis.scaleYPercent;
			FlipX=clonethis.FlipX;
			FlipY=clonethis.FlipY;
			OffsetX = clonethis.OffsetX;
			OffsetY = clonethis.OffsetY;
			OffsetZ=clonethis.OffsetZ;
			canuseURL=clonethis.canuseURL;
			canusePath=clonethis.canusePath;
			usePlayerResources=clonethis.usePlayerResources;
			
		}
		public void setResourceUse(String value) {

			if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")
					|| Boolean.parseBoolean(value)) {
				usePlayerResources = true;

			} else {
				usePlayerResources = false;
			}

			pPlayer.sendMessage("Resource use set to " + usePlayerResources);

		}

		public Boolean getResourceUse() {

			return usePlayerResources;

		}

		public ColorEntryData[] EntryColours = new ColorEntryData[] {
				new ColorEntryData(Color.white, 35, 0),
				new ColorEntryData(Color.orange, 35, 1),
				new ColorEntryData(Color.magenta, 35, 2),
				new ColorEntryData(new Color(173, 216, 230), 35, 3),
				new ColorEntryData(Color.yellow, 35, 4),
				new ColorEntryData(new Color(50, 205, 50), 35, 5),
				new ColorEntryData(Color.pink, 35, 6),
				new ColorEntryData(Color.gray, 35, 7),
				new ColorEntryData(Color.lightGray, 35, 8),
				new ColorEntryData(Color.cyan, 35, 9),
				new ColorEntryData(new Color(120, 0, 120), 35, 10),
				new ColorEntryData(Color.blue, 35, 11),
				new ColorEntryData(new Color(165, 42, 42), 35, 12),
				new ColorEntryData(Color.green, 35, 13),
				new ColorEntryData(Color.red, 35, 14),
				new ColorEntryData(Color.black, 35, 15) };

		// new ColorEntryData(new Color(210,180,140),12,0) << Sand
		public PixArtPlayerData(Player p) {
			pPlayer = p;
			this.scaleXPercent= PixArtPlugin.pluginsettings.XScale;
			this.scaleXPercent= PixArtPlugin.pluginsettings.YScale;
			FlipX=false;
			FlipY=false;
			this.canuseURL= PixArtPlugin.hasPermex(pPlayer,"canuseURL");
			this.canusePath = PixArtPlugin.hasPermex(pPlayer, "canusePath");
			
		}
		public void processArguments(Player p,String[] args)
		{
			
			
			
			
			if(args[0].equalsIgnoreCase("ScaleX"))
			{
				if(args.length<2) {
					p.sendMessage("PIXART: ScaleX currently " + scaleXPercent);
					
					return;
				}
				this.scaleXPercent = Float.parseFloat(args[1]);
				p.sendMessage("XScale set to " + this.scaleXPercent);
				
			}
			else if(args[0].equalsIgnoreCase("ScaleY"))
			{
				if(args.length<2) {
					p.sendMessage("PIXART: ScaleY currently " + scaleYPercent);
					
					return;
				}
				this.scaleYPercent = Float.parseFloat(args[1]);
				p.sendMessage("YScale set to " + this.scaleYPercent);
				
			}
			else if(args[0].equalsIgnoreCase("FlipX"))
			{
				if(args.length<2) {
					FlipX=!FlipX;
					
					
					
				}
				else
				{
					FlipX = (Boolean.parseBoolean(args[1]) || args[1].equalsIgnoreCase("yes"));
				}	
					
				p.sendMessage("FlipX set to " + FlipX);
				
			}
			else if(args[0].equalsIgnoreCase("FlipY"))
			{
				if(args.length<2) 
					FlipY=!FlipY;
				else
					FlipY = (Boolean.parseBoolean(args[1]) || args[1].equalsIgnoreCase("yes"));
					
				
				p.sendMessage("FlipY set to " + FlipY);
			}
			else if (args[0].equalsIgnoreCase("OffsetX"))
			{
				if(args.length<2) {
					p.sendMessage("PIXART: OffsetX currently " + OffsetX);
					
					return;
				}
				try {
				OffsetX = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException e)
				{
					p.sendMessage("invalid Value.");
					
				}
				p.sendMessage("PIXART:X offset set to " + OffsetX);
			}
			else if (args[0].equalsIgnoreCase("OffsetY"))
			{
				if(args.length<2) {
					p.sendMessage("PIXART: OffsetY currently " + OffsetY);
					
					return;
				}
				try {
				OffsetY = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException e)
				{
					p.sendMessage("invalid Value.");
					
				}
				p.sendMessage("PIXART:Y offset set to " + OffsetY);
			}
			else if (args[0].equalsIgnoreCase("OffsetZ"))
			{
				if(args.length<2) {
					p.sendMessage("PIXART: OffsetZ currently " + OffsetZ);
					
					return;
				}
				try {
				OffsetY = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException e)
				{
					p.sendMessage("invalid Value.");
					
				}
				p.sendMessage("PIXART:Z offset set to " + OffsetZ);
			}
			
			
			
		}

	}

	final PixArtPlugin owner;
	public Hashtable<String, PixArtPlayerData> ArtPlayerData = new Hashtable<String, PixArtPlayerData>();

	// public BuildOrientationConstants
	// BuildOrientation=BuildOrientationConstants.Build_XZ;

	public PixArtCommand(PixArtPlugin powner) {

		owner = powner;

	}

	protected static void debugmessage(String message) {

		System.out.println(message);

	}

	private static Location getBlockPlacement(PixArtPlayerData papd, World mcworld,
			Location origin, int pixelX, int pixelY,int ImageWidth,int ImageHeight) {
		int X = 0, Y = 0, Z = 0;
		// currently supported modes, XY, XZ, YX, YZ, ZX, ZY
		// "AB" where the image X coord is translated to a world A coord and the
		// image Y coord is translated to a World B coord
		// I suspect this could be refactored...
		
		//check for "flips"...
		
		if(papd.FlipX)
		{
			pixelX = ImageWidth-pixelX;
			
		
		}
		if(papd.FlipY)
		{
			
			pixelY = ImageHeight-pixelY;
			
		}
		
		switch (papd.BuildOrientation) {

		case Build_XY:
			debugmessage("Build_XY");
			X = origin.getBlockX() + pixelX;
			Y = origin.getBlockY() + pixelY;
			Z = origin.getBlockZ();
			break;
		case Build_XZ:
			// return new
			// Location(origin.getBlockX()+pixelX,origin.getBlockY(),origin.getBlockZ()+pixelY);
			debugmessage("Build_XZ");
			X = origin.getBlockX() + pixelX;
			Y = origin.getBlockY();
			Z = origin.getBlockZ() + pixelY;
			break;

		case Build_YX:
			debugmessage("Build_YX");
			X = origin.getBlockX() + pixelY;
			Y = origin.getBlockY() + pixelX;
			Z = origin.getBlockZ();
			break;
		case Build_YZ:
			// X coord will be Y coord in world. Y Coord will be Z.
			debugmessage("Build_YZ");
			X = origin.getBlockX();
			Y = origin.getBlockY() + pixelX;
			Z = origin.getBlockZ() + pixelY;
			break;
		case Build_ZX:
			// X coord will be Z coord in world. Y Coord will be X.
			debugmessage("Build_ZX");
			X = origin.getBlockX() + pixelY;
			Y = origin.getBlockY();
			Z = origin.getBlockZ() + pixelX;
			break;

		case Build_ZY:
			// X coord in image will be Z coord in world;
			// Y coord in image will be X coord in world.
			debugmessage("Build_ZY");
			X = origin.getBlockX() + pixelY;
			Y = origin.getBlockY();
			Z = origin.getBlockZ() + pixelX;
			break;
		}

		debugmessage("returning " + X + " " + Y + " " + Z + " origin: X="
				+ origin.getBlockX() + " Y=" + origin.getBlockY() + " Z="
				+ origin.getBlockZ() + " for pixel location X=" + pixelX
				+ " and " + pixelY);

		return mcworld.getBlockAt(X, Y, Z).getLocation();

	}
	/**
	 * returns the total difference between the two Colors. (that is, the total of the absolute differences between
	 * each component in the colours)
	 * @param colorA First Colour
	 * @param colorB Second Colour
	 * @return total difference of the components in each colour.
	 */
	private static int getcolordiff(Color colorA, Color colorB) {
		if (colorA == null || colorB == null)
			return Integer.MAX_VALUE;
		return Math.abs(colorB.getRed() - colorA.getRed())
				+ Math.abs(colorB.getGreen() - colorA.getGreen())
				+ Math.abs(colorB.getBlue() - colorA.getBlue());

	}
	/**
	 * returns the nearest matching ColorEntryData to a given Color from an Array.
	 * @param fromcolors Array of ColorEntryData[] objects
	 * @param colorcheck Color to approximate.
	 * @return the ColorEntryData object that most closely approximates the given color.
	 */
	public static ColorEntryData getnearestcolor(ColorEntryData[] fromcolors,
			Color colorcheck) {

		int[] diffs = new int[fromcolors.length];
		int currminindex = -1, currmin = Integer.MAX_VALUE;
		for (int i = 0; i < diffs.length; i++) {
			diffs[i] = getcolordiff(fromcolors[i].colorvalue, colorcheck);
			if (diffs[i] < currmin) {
				currmin = diffs[i];
				currminindex = i;

			}
		}
		return fromcolors[currminindex];

	}
//do we really need to javadoc this? simple overload that calls the above routine with the aggregate EntryColours[] array in 
	//the PixArtPlayerData class...
	public static ColorEntryData getnearestcolor(PixArtPlayerData pixdata,
			Color colorcheck) {
		return getnearestcolor(pixdata.EntryColours, colorcheck);
		// refactored to another routine....
		/*
		 * int[] diffs = new int[pixdata.EntryColours.length]; int currminindex
		 * = -1, currmin = Integer.MAX_VALUE; for (int i = 0; i < diffs.length;
		 * i++) { diffs[i] = getcolordiff(pixdata.EntryColours[i].colorvalue,
		 * colorcheck); if (diffs[i] < currmin) { currmin = diffs[i];
		 * currminindex = i;
		 * 
		 * } } return pixdata.EntryColours[currminindex];
		 */
	}
/**
 * returns the PixArtPlayerData for the given player. If there is no entry in the Hashmap for that player,
 *  a new entry is created and returned.
 * @param p
 * @return
 */
	public PixArtPlayerData getPlayerPixArtData(Player p) {
		System.out.println("GetPlayerPixArtData:" + p.getDisplayName());

		PixArtPlayerData gotvalue = ArtPlayerData.get(p.getDisplayName());
		if (gotvalue == null) {
			gotvalue = new PixArtPlayerData(p);
			ArtPlayerData.put(p.getDisplayName(), gotvalue);
		}
		if (gotvalue == null) {
			System.out.println("gotvalue is null. we're screwed.");

		}
		return gotvalue;

	}
/**
 * acquires the total number in a players inventory of a specified Material.
 * @param inv PlayerInventory to check
 * @param checkfor Material to check for
 * @return
 */
	public int getTotalCount(PlayerInventory inv, Material checkfor) {
		int totalamount = 0;
		for (ItemStack loopstack : inv.all(checkfor).values()) {
			totalamount += loopstack.getAmount();

		}

		return totalamount;

	}
	/**
	 * converts a BlockID and Data to a string. (Currently just showing the BlockID and Data, but could be changed to
	 * show more descriptive strings...)
	 * @param BlockID BlockID of the material to retrieve a description for.
	 * @param BlockData Data of the material to retrieve a description for.
	 * @return Description of the material.
	 */
	private String blockDataToString(int BlockID, int BlockData) {

		return "ID:" + BlockID + " Data:" + BlockData;

	}
	/**
	 * given a PlayerData class instance and a string, tries to load/convert that string into an
	 * appropriate image. Applies permissions as well. returns null if file couldn't be loaded, an error occurs, or if permission is denied.
	 * in the last case, shows a message to the player stating as such.
	 * @param dataobj
	 * @param pstrimage
	 * @return
	 */
	public static BufferedImage loadImage(PixArtPlayerData dataobj, String pstrimage) {
		
		//first step: if they aren't allowed URLs or file references, only allow them to use mapped images.
		String strimage=pstrimage;
		if(strimage.startsWith("www."))
		{
			//prepend strimage...
			strimage = "http://" + strimage;
			
			
		}
		
		if(!dataobj.canuseURL && !dataobj.canusePath)
		{
			if(!PixArtPlugin.pluginsettings.preMappedImages.containsKey(pstrimage))
			{
				dataobj.pPlayer.sendMessage("You do not have permissions to use URL or File references.");
				return null;
				
			}
			
			
		}
		else if(!dataobj.canuseURL)
		{
			
			try {URL geturl = new URL(strimage);
				dataobj.pPlayer.sendMessage("BCPIXART:You do not have permission to use URLs");
				
			
			}
			catch(MalformedURLException mue)
			{
				//ignore... could be valid... mapped image, or file ref, or something.
				
			}
			
			
			
		}
		else if(!dataobj.canusePath)
		{
			if(new File(strimage).exists())
			{
				dataobj.pPlayer.sendMessage("BCPIXART:you do not have permission to use Server File Paths.");
				
				
			}
			
			
		}
		strimage = PixArtPlugin.pluginsettings.getmappedImage(strimage);
		try {
			
			
			
			BufferedImage img = null;
			URL url = new URL(strimage);
			img = ImageIO.read(url);
			
			
			
			//scale it.
			if(dataobj.scaleXPercent!=1 || dataobj.scaleYPercent!=1)
				img = PixArtPlugin.toBufferedImage(img.getScaledInstance((int)((float)img.getWidth() + dataobj.scaleXPercent), (int)((float)img.getHeight() + dataobj.scaleYPercent),Image.SCALE_FAST));
			
			
			return img;
		} catch (IOException e) {
			dataobj.pPlayer.sendMessage("Error...");
			System.out.println("Exception:" + e.getMessage());
			return null;
		}
		
	}
	public void showcommandhelp(Player p,String[] args)
	{
		String arg = args[0];
		if(args[0].equalsIgnoreCase("build"))
		{
		p.sendMessage(ChatColor.GOLD+ "Build: builds a image.");
		p.sendMessage(ChatColor.GOLD+ "syntax: /pixart build [image]");
		p.sendMessage(ChatColor.GOLD+ "[image]: image to build. can be a URL, server file path reference, or ");
		p.sendMessage(ChatColor.GOLD+"one of the mapped names in the mappingfile. Permissions or Op are required for anything");
		p.sendMessage(ChatColor.GOLD+ "but the mapped reference names.");
		}
			
		else if(args[0].equalsIgnoreCase("calc"))
		{
			p.sendMessage(ChatColor.GOLD+ "calc: calculates required resources for an image");
			p.sendMessage(ChatColor.GOLD+ "syntax: /pixart calc <imagename>");
			p.sendMessage(ChatColor.GOLD+ "[image]: image to calculate. can be a URL, server file path reference, or ");
			p.sendMessage(ChatColor.GOLD+"one of the mapped names in the mappingfile. Permissions or Op are required for anything");
			p.sendMessage(ChatColor.GOLD+"results of the calculation are shown. either as either text, or direct ID <damage> values");
			p.sendMessage(ChatColor.GOLD+"brackets at the end will display the amount of each that you have in your inventory.");
			
		}
		else if (args[0].equalsIgnoreCase("ScaleX")||args[0].equalsIgnoreCase("ScaleY"))
		{
			p.sendMessage(ChatColor.GOLD + "ScaleX & ScaleY: changes scale factor of built image.");
			p.sendMessage(ChatColor.GOLD + "syntax: /pixart Scale[X|Y] <value>");
			p.sendMessage(ChatColor.GOLD + " changes scale for given pixel coordinate. if a negative & positive value is used");
			p.sendMessage(ChatColor.GOLD + "then the negative value will be changed to match the correct aspect ratio for the other coordinate scale.");
			
			
		}
		else if(args[0].equalsIgnoreCase("OffsetX")||args[0].equalsIgnoreCase("OffsetY") || args[0].equalsIgnoreCase("OffsetZ"))
		{
			p.sendMessage(ChatColor.GOLD + "OffsetX & OffsetY: changes Offset values from player location");
			p.sendMessage(ChatColor.GOLD + "syntax: /pixart Offset[X|Y|Z] <value> ");
			p.sendMessage(ChatColor.GOLD + "changes offset for given coordinate (in blocks)");
			
			
			
		}
		else if(args[0].equalsIgnoreCase("FlipX") || args[0].equalsIgnoreCase("FlipY"))
		{
			p.sendMessage(ChatColor.GOLD + "FlipX & FlipY: flips given coordinate in resulting built image.");
			p.sendMessage(ChatColor.GOLD + "Syntax: /pixart Flip[X|Y] [true|false]");
			p.sendMessage(ChatColor.GOLD+"If no parameter value is given, specified coordinate will have it's flip value toggled.");
			
			
		}
		else if(args[0].equalsIgnoreCase("mode"))
		{
			p.sendMessage(ChatColor.GOLD+"mode: changes how X,Y image pixels are mapped to 3-D world space");
			p.sendMessage(ChatColor.GOLD+"syntax: /pixart mode <mode>");
			p.sendMessage(ChatColor.GOLD+"where <mode> is one of XY,XZ, YX,YZ,ZX,ZY.");
			p.sendMessage(ChatColor.GOLD+"the X Coordinate of the image is mapped to the first coordinate in the pair; the Y coordinate to the second.");
			p.sendMessage(ChatColor.GOLD+"example: XZ will map the image as horizontal, with X in the image being X in the world and Y in the image being Z.");
			
			
		}
		else if (args[0].equalsIgnoreCase("addcolordata"))
		{
			///pixart addcolordata R G B ID DAMAGE
			p.sendMessage(ChatColor.GOLD+"addcolordata: adds a colour mapping from a specified colour to a material");
			p.sendMessage(ChatColor.GOLD+"Syntax: /pixart addcolordata " + ChatColor.RED + "<R>" + 
					ChatColor.GREEN + "<G>" + ChatColor.BLUE + "<B>" + ChatColor.GOLD +  "<ID> <DAMAGE>");
			p.sendMessage(ChatColor.GOLD+ " R,G,B : Colour components of colour to match as the given material.");
			p.sendMessage(ChatColor.GOLD + "ID, DAMAGE: Material to map.");
			p.sendMessage(ChatColor.GOLD+" Example: /pixart addcolordata 255 255 255 35 0");
			p.sendMessage(ChatColor.GOLD+"would map white (RGB 255,255,255) to white wool (ID 35, damage 0)");
			
			
			
		}
		
		
	}
	public void showcommandhelp(Player p)
	{
		//shows the default help to that player.
		p.sendMessage(PixArtPlugin.versionheader);
		p.sendMessage("Pixel art creation tool");
		p.sendMessage("Syntax:");
		p.sendMessage("/pixart [option] [arguments]");
		p.sendMessage("supported Options- build, calc, ScaleX,ScaleY,FlipX,FlipY,Mode");
		p.sendMessage("use /pixart [option] help for help with each.");
		
		
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (sender instanceof Player) {
			System.out.println("OnCommand...");
			for (int i = 0; i < args.length; i++) {

				System.out.println("argument #" + i + " " + args[i]);

			}
			Player p = (Player) sender;
			PixArtPlayerData papa = getPlayerPixArtData(p);
			String scmd = commandLabel.toLowerCase();
			Location startpos = p.getLocation();
			
			startpos = p.getWorld().getBlockAt(startpos.getBlockX()+papa.OffsetX,
					startpos.getBlockY() + papa.OffsetY,
					startpos.getBlockZ() + papa.OffsetZ).getLocation();
			
			
			System.out.println(scmd);
			
			if (scmd.equalsIgnoreCase("pixart")) {
				if(args.length==0)
					showcommandhelp(p);
				else if (args[args.length-1].equalsIgnoreCase("help")) showcommandhelp(p,args);
				BufferedImage img = null;
				if (args[0].equalsIgnoreCase("calc")) {
					if (args.length < 2)
						p.sendMessage("insufficient arguments.");

					debugmessage("loading image:" + args[1]);
					img = loadImage(papa, args[1]);
					if (img == null) {

					} else {
						// calculate required items.
						// rescalc.
						int[] rescounts = rescalc.getResourceCounts(
								papa.EntryColours, img);
						// now we can tell the player what they need.
						p.sendMessage("Resource requirements for image \""
								+ args[1] + "\":");

						for (int i = 0; i < rescounts.length; i++) {
							if (rescounts[i] != 0) {

								p
										.sendMessage(rescounts[i]
												+ " of item "
												+ blockDataToString(
														papa.EntryColours[i].BlockID,
														papa.EntryColours[i].BlockData)
												+ "("
												+ rescalc
														.getTotalCount(
																p,
																papa.EntryColours[i].BlockID,
																papa.EntryColours[i].BlockData) + ")");

							}

						}

					}
				}
				
				
				else if (args[0].equalsIgnoreCase("resuse")) {
					// set a "property"...
					// args[1] will be the actual property name. currently
					// supported properties
					// are:
					// resuse
					// which can be yes or no.

					// require permissions.
					if (PixArtPlugin.hasPermex(p, "setResourceUse")) {
						// we support true,false, yes, and no.
						if (args.length < 2) {
							// p.sendMessage("insufficient arguments");
							papa.setResourceUse(!papa.getResourceUse());

						} else {
							papa.setResourceUse(args[1]);

						}

					} else {
						p
						.sendMessage("You do not have setResourceUse permissions in this world.");
					}



				}
				else if (args[0].equalsIgnoreCase("mode")) {
					System.out.println("mode");
					// modes: //pixart mode XZ, pixart mode YZ, etc

					String pixartmode = args[1].toLowerCase();
					/*
					 * System.out.println("pixartmode=" + pixartmode); if
					 * (pixartmode.equalsIgnoreCase("xz"))
					 * setBuildOrientation(papa,
					 * BuildOrientationConstants.Build_XZ);
					 * 
					 * else if (pixartmode.equalsIgnoreCase("yz"))
					 * setBuildOrientation(papa,
					 * BuildOrientationConstants.Build_YZ);
					 */

					setBuildOrientation(papa, bocfromstring(args[1]));

				} else if (args[0].equalsIgnoreCase("addcolordata")) {
					// /pixart addcolordata R G B ID DAMAGE
					if (args.length < 6) {
						p.sendMessage("insufficient arguments.");
						p.sendMessage("/pixart addcolordata R G B ID DAMAGE");
						// 1=R,2=G,3=B,4=ID,5=DAMAGE...

						int R = Integer.parseInt(args[1]);
						int G = Integer.parseInt(args[2]);
						int B = Integer.parseInt(args[3]);
						int ID = Integer.parseInt(args[4]);
						int DAMAGE = Integer.parseInt(args[5]);
						ColorEntryData newcol = new ColorEntryData(new Color(R,
								G, B), ID, DAMAGE);
						ColorEntryData[] shiftover = new ColorEntryData[papa.EntryColours.length + 1];
						for (int i = 0; i < papa.EntryColours.length; i++) {
							shiftover[i] = papa.EntryColours[i];

						}
						shiftover[shiftover.length - 1] = newcol;

						papa.EntryColours = shiftover;
						p.sendMessage("Color Table Entry Added RGB=" + R + ","
								+ G + "," + B);
					}

				}
				// support pixart stuff:
				// /pixart http://bc-programming.com/images/megaman.png
				else if (args[0].equalsIgnoreCase("build")) {

					// URL url = new URL(args[1]);
					if (args.length == 1 || args[1] == "")
						args[1] = "http://www.bc-programming.com/images/mm/megaman.png";
					//renderImageToBlocks(args[1], p, papa, startpos,rescalc);
					renderImageToBlocksthreaded(args[1], p, papa, startpos,rescalc);

				}
				else
				{
					papa.processArguments(p,args);
					
					
					
					
				}

			}

		}

		return false;
	}
	
	//private Thread spawnedthread;
	public void renderImageToBlocksthreaded(String imagestr,Player p, PixArtPlayerData papa,Location startpos,ResourceCalculator resq)
	{
		ThreadWork workerthread = new ThreadWork(imagestr,p,(PixArtPlayerData) papa.clone(),startpos,resq);
		Thread spawnedthread = new Thread(workerthread);
		spawnedthread.start();
			
		
		
		
	}
	public static void renderImageToBlocks(String imagestr, Player p,
			PixArtPlayerData papa, Location startpos,ResourceCalculator rescalc) {
		BufferedImage img;
		img = loadImage(papa, imagestr);
		// img.getScaledInstance(width, height, hints)
		debugmessage("Image height:" + img.getHeight()
				+ " Image width:" + img.getWidth());

		if (img != null) {

			// TODO: resize the image based on values set in the
			// PixArtPlayerData class...

			// based on location of player.
			// now iterate through the pixels in the image...

			// First, if resource use is enabled, make sure they
			// have sufficient resources, and if so, deplete those
			// resources.

			if (papa.usePlayerResources) {
				if (!rescalc.hasResourcesDirect(p,
						papa.EntryColours, img)) {
					// failure... insufficient resources. note that
					// hasResourcesDirect will show the message.
					debugmessage("Player "
							+ p.getDisplayName()
							+ " tried to create image with insufficient resources.");

				} else {
					// deplete the player resources <now> rather
					// then after building it. although debatably it
					// could be an option.
					debugmessage("Player has sufficient resources-");
					rescalc.depleteResources(p, papa.EntryColours,
							img);
				}

			} else {
				debugmessage("Player "
						+ p.getDisplayName()
						+ " does not require resources to create pixel art");
			}

			int px = startpos.getBlockX();
			int py = startpos.getBlockY();
			int pz = startpos.getBlockZ();
			int totalblocks=0;
			debugmessage("player at position X:" + px + " Y:"
					+ py + " Z:" + pz);
			for (int xpixel = 0; xpixel < img.getWidth(); xpixel++) {
				// int xpix = xpixel+px;
				for (int ypixel = 0; ypixel < img.getHeight(); ypixel++) {
					// int ypix = ypixel+py;
					totalblocks++;
					if(totalblocks%50==0) try {Thread.sleep(1000);} catch(Exception e){}
					debugmessage("processing pixel:X:"
							+ xpixel + " Y:" + ypixel);
					// use x and z coords. (make it flat)

					int colourvalue = img.getRGB(xpixel, ypixel);

					// Color grabcolour = new Color(colourvalue);

					int red = (colourvalue & 0x00ff0000) >> 16;
					int green = (colourvalue & 0x0000ff00) >> 8;
					int blue = colourvalue & 0x000000ff;
					int alpha = (colourvalue >> 24) & 0xff;

					// Material usematerial = ColorToMaterial(new
					// Color(colourvalue));
					World usew = p.getWorld();
					Location usespot = getBlockPlacement(papa, p
							.getWorld(), startpos, xpixel, ypixel,img.getWidth(),img.getHeight());
					// System.out.println("creatingblock at location X="
					// + xpix + " Y=" + p.getLocation().getBlockY()
					// + " Z=" + ypix);
					debugmessage("creatingblock at location X="
									+ usespot.getBlockX() + " Y="
									+ usespot.getBlockY() + " Z="
									+ usespot.getBlockZ());
					// Block blockpos =
					// usew.getBlockAt(xpix,p.getLocation().getBlockY(),ypix
					// );

					Block blockpos = usew.getBlockAt(usespot);

					// System.out.println("setting material to " +
					// usematerial.toString());
					// int colorvalue=ColorToDyeColor(new
					// Color(colourvalue));
					// System.out.println("colorvalue=" +
					// colorvalue);
					if (alpha > 0) {
						setblocktocolor(papa, blockpos, new Color(
								colourvalue));
						try {
							//sleep every single block for the given amount of time. defaults to 500 ms.
							Thread.sleep(PixArtPlugin.pluginsettings.blocksleep);
						} catch (InterruptedException e) {
							//don't care. bloody checked exceptions...
						}
					} else {
						//TODO: make configurable?
					//	blockpos.setType(Material.AIR); // assume
						// transparent.
					}
					// blockpos.setTypeIdAndData(35,
					// (byte)(colorvalue), false);
					// Material forcewool = Material.WOOL;
					// forcewool.getNewData((byte) 4);
					// blockpos.setTypeIdAndData(35, (byte) 7,true);

					// setblocktocolor(blockpos,new
					// Color(colourvalue));
					// blockpos.setType(usematerial);

				}

			}

		}
	}

	private static void setblocktocolor(PixArtPlayerData pd, Block setblock,
			Color tocolor) {
		boolean exceptionoccured=true;
		int exceptioncount=0;
		//HACK ALERT: was encountering a NullPointerException deep beneath is within setTypeIdAndData (not my fault I swear).
		//I suspect it's a race condition. the hack is that it tries 5 times, and sleeps for 100 milliseconds each
		//time it fails. After 5 times it just gives up and pretends all went well to prevent 
		//hurting the feelings of the calling routine.
		while(exceptionoccured && exceptioncount < 5)
		{
        try {
        	exceptionoccured=false;
		ColorEntryData sd = getnearestcolor(pd, tocolor);
		setblock.setTypeIdAndData(sd.BlockID, (byte) sd.BlockData, false);
        }
        catch(NullPointerException e)
        {
        	exceptionoccured=true;
        	exceptioncount++;
        	try {
        	Thread.sleep(100*exceptioncount);
        	}
        	catch(InterruptedException exx)
        	{
        		//ignore...
        		
        	}
        }
		}
	}

	/**
	 * 
	 * @param strorientation
	 *            string orientation- (eg. xz, yz, zy), which will be converted
	 *            to a proper BuildOrientationConstants entry.
	 * @return
	 */
	public BuildOrientationConstants bocfromstring(String strorientation) {
		// iterate through each value in the enumeration...

		for (BuildOrientationConstants loopconst : BuildOrientationConstants
				.values()) {
			// get the string representation for this
			// BuildOrientationConstant...
			String stringrep = loopconst.toString();

			// is it equal (ignoring case) with the parameter?
			if (stringrep.equalsIgnoreCase(strorientation)) {
				// if so, return the enumerated value.
				return loopconst;

			} else if (stringrep.equalsIgnoreCase("Build_" + strorientation)) {
				// same story; in this case the parameter was something like
				// "XZ".
				return loopconst;

			}

		}
		return BuildOrientationConstants.Build_XZ;

	}

	public void setBuildOrientation(PixArtPlayerData pdata,
			BuildOrientationConstants bconst) {

		pdata.BuildOrientation = bconst;
		Player p = pdata.pPlayer;

		p.sendMessage("BCPixArt Build Orientation changed to " + bconst);
		debugmessage("BCPixArt Build Orientation changed to " + bconst
				+ " for player " + p.getDisplayName());

	}
	
	
}
