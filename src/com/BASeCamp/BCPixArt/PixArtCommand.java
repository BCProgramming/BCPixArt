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
//
//check giant tree
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
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

import com.BASeCamp.BCPixArt.FMBlockListener.BlockFillMode;
import com.BASeCamp.BCPixArt.FMBlockListener.BlockFillSetupData;
import com.BASeCamp.BCPixArt.FMBlockListener.FMBlockListenerState;


/**
 * 
 * @author BC_Programming PixArtCommand- CommandExecutor Implementation- handles
 *         everything related to Pixel Art commands. Except for what it doesn't,
 *         of course... but that goes without saying.
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

	private static Location getBlockPlacement(PixArtPlayerData papd,
			World mcworld, Location origin, int pixelX, int pixelY,
			int ImageWidth, int ImageHeight) {
		int X = 0, Y = 0, Z = 0;
		// currently supported modes, XY, XZ, YX, YZ, ZX, ZY
		// "AB" where the image X coord is translated to a world A coord and the
		// image Y coord is translated to a World B coord
		// I suspect this could be refactored...

		// check for "flips"...

		if (!papd.getFlipX()) {
			// also reversed the flip meaning. nobody needs to know...
			pixelX = ImageWidth - pixelX;

		}
		// changed: papd.FlipY now has a "reverse" meaning; flipped will
		// actually be the upside down one...
		if (!papd.getFlipY()) {

			pixelY = ImageHeight - pixelY;

		}

		switch (papd.getBuildOrientation()) {

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
	private static void rgb2lab(int R, int G, int B, double []lab) {
		//http://www.brucelindbloom.com
		  
		float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float Ls, as, bs;
		float eps = 216.f/24389.f;
		float k = 24389.f/27.f;
		   
		float Xr = 0.964221f;  // reference white D50
		float Yr = 1.0f;
		float Zr = 0.825211f;
		
		// RGB to XYZ
		r = R/255.f; //R 0..1
		g = G/255.f; //G 0..1
		b = B/255.f; //B 0..1
		
		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r/12;
		else
			r = (float) Math.pow((r+0.055)/1.055,2.4);
		
		if (g <= 0.04045)
			g = g/12;
		else
			g = (float) Math.pow((g+0.055)/1.055,2.4);
		
		if (b <= 0.04045)
			b = b/12;
		else
			b = (float) Math.pow((b+0.055)/1.055,2.4);
		
		
		X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
		Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
		Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;
		
		// XYZ to Lab
		xr = X/Xr;
		yr = Y/Yr;
		zr = Z/Zr;
				
		if ( xr > eps )
			fx =  (float) Math.pow(xr, 1/3.);
		else
			fx = (float) ((k * xr + 16.) / 116.);
		 
		if ( yr > eps )
			fy =  (float) Math.pow(yr, 1/3.);
		else
		fy = (float) ((k * yr + 16.) / 116.);
		
		if ( zr > eps )
			fz =  (float) Math.pow(zr, 1/3.);
		else
			fz = (float) ((k * zr + 16.) / 116);
		
		Ls = ( 116 * fy ) - 16;
		as = 500*(fx-fy);
		bs = 200*(fy-fz);
		
		lab[0] =  (2.55*Ls + .5);
		lab[1] =  (as + .5); 
		lab[2] =  (bs + .5);       
	} 
	
	private static float distance(double[] partsA,double[] partsB)
	{
	int maxindex = Math.min(partsA.length, partsB.length);
	double makesum = 0;
	for(int i=0;i<maxindex;i++)
	{
		
	makesum+=	
		Math.pow(partsA[i]-partsB[i],2);
	}
	
	makesum = Math.pow(makesum,1/(float)maxindex);	
		
		return (float) makesum;
	
	}
	
	/**
	 * returns the total difference between the two Colors. (that is, the total
	 * of the absolute differences between each component in the colours)
	 * 
	 * @param colorA
	 *            First Colour
	 * @param colorB
	 *            Second Colour
	 * @return total difference of the components in each colour.
	 */
	private static float getcolordiff(Color colorA, Color colorB) {
		double[] Alab = {0,0,0};
		double[] blab = {0,0,0};
		if(colorA==null || colorB==null)
			return Integer.MAX_VALUE;
		rgb2lab(colorA.getRed(),colorA.getGreen(),colorA.getBlue(),Alab);
		rgb2lab(colorB.getRed(),colorB.getGreen(),colorB.getBlue(),blab);
		double[] avalues = new double[4];
		double[] bvalues = new double[4];
		for(int i=0;i<2;i++)
		{
			avalues[i] = Alab[i];
			bvalues[i] = blab[i];
			
		}
		avalues[3] = colorA.getAlpha();
		bvalues[3] = colorB.getAlpha();
		return distance(avalues,bvalues);
		/*
		if (colorA == null || colorB == null)
			return Integer.MAX_VALUE;
		return Math.abs(colorB.getRed() - colorA.getRed())
				+ Math.abs(colorB.getGreen() - colorA.getGreen())
				+ Math.abs(colorB.getBlue() - colorA.getBlue()
						+ Math.abs(colorB.getAlpha() - colorA.getAlpha()));
*/
	}

	
	
	/**
	 * returns the nearest matching ColorEntryData to a given Color from an
	 * Array.
	 * 
	 * @param fromcolors
	 *            Array of ColorEntryData[] objects
	 * @param colorcheck
	 *            Color to approximate.
	 * @return the ColorEntryData object that most closely approximates the
	 *         given color.
	 */
	public static ColorEntryData getnearestcolor(ColorEntryData[] fromcolors,
			Color colorcheck) {

		float[] diffs = new float[fromcolors.length];
		int currminindex = -1;
		float currmin = Float.MAX_VALUE;
		for (int i = 0; i < diffs.length; i++) {
			diffs[i] = getcolordiff(fromcolors[i].colorvalue, colorcheck);
			if (diffs[i] < currmin) {
				currmin = diffs[i];
				currminindex = i;

			}
		}
		return fromcolors[currminindex];

	}

	// do we really need to javadoc this? simple overload that calls the above
	// routine with the aggregate EntryColours[] array in
	// the PixArtPlayerData class...
	public static ColorEntryData getnearestcolor(PixArtPlayerData pixdata,
			Color colorcheck) {
		return getnearestcolor(pixdata.EntryColours, colorcheck);
		
	}

	/**
	 * returns the PixArtPlayerData for the given player. If there is no entry
	 * in the Hashmap for that player, a new entry is created and returned.
	 * 
	 * @param p
	 * @return
	 */
	public PixArtPlayerData getPlayerPixArtData(Player p) {
		//System.out.println("GetPlayerPixArtData:" + p.getDisplayName());

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
	 * 
	 * @param inv
	 *            PlayerInventory to check
	 * @param checkfor
	 *            Material to check for
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
	 * converts a BlockID and Data to a string. (Currently just showing the
	 * BlockID and Data, but could be changed to show more descriptive
	 * strings...)
	 * 
	 * @param BlockID
	 *            BlockID of the material to retrieve a description for.
	 * @param BlockData
	 *            Data of the material to retrieve a description for.
	 * @return Description of the material.
	 */
	private String blockDataToString(int BlockID, int BlockData) {

		String itemname = new ItemStack(BlockID,BlockData).getType().toString();
		return "ID:" + BlockID + " Data:" + BlockData + "(" + itemname + ")";

	}

	/**
	 * given a PlayerData class instance and a string, tries to load/convert
	 * that string into an appropriate image. Applies permissions as well.
	 * returns null if file couldn't be loaded, an error occurs, or if
	 * permission is denied. in the last case, shows a message to the player
	 * stating as such.
	 * 
	 * @param dataobj
	 * @param pstrimage
	 * @return
	 */
	public static BufferedImage loadImage(PixArtPlayerData dataobj,
			String pstrimage) {

		// first step: if they aren't allowed URLs or file references, only
		// allow them to use mapped images.
		String strimage = pstrimage;
		if (strimage.startsWith("www.")) {
			// prepend strimage...
			strimage = "http://" + strimage;

		}

		if (!dataobj.getcanuseURL() && !dataobj.getcanusePath()) {
			if (!PixArtPlugin.getsettings().preMappedImages
					.containsKey(pstrimage)) {
				dataobj.getPlayer()
						.sendMessage("You do not have permissions to use URL or File references.");
				return null;

			}

		} else if (!dataobj.getcanuseURL()) {

			try {
				URL geturl = new URL(strimage);
				dataobj.getPlayer()
						.sendMessage("BCPIXART:You do not have permission to use URLs");

			} catch (MalformedURLException mue) {
				// ignore... could be valid... mapped image, or file ref, or
				// something.

			}

		} else if (!dataobj.getcanusePath()) {
			if (new File(strimage).exists()) {
				dataobj.getPlayer()
						.sendMessage("BCPIXART:you do not have permission to use Server File Paths.");

			}

		}
		strimage = PixArtPlugin.getsettings().getmappedImage(strimage);
		try {

			BufferedImage img = null;
			URL url = new URL(strimage);
			img = ImageIO.read(url);
			debugmessage("reading " + strimage);
			//reduce color depth to 256 (8-bit).
			//Commented out: quantization is broken... grrr....
			//ReduceImage(img,256);
			
			
			// scale it.
			if (dataobj.getXScalePercent() != 1 || dataobj.getYScalePercent() != 1)
				img = PixArtPlugin
						.toBufferedImage(img
								.getScaledInstance(
										(int) ((float) img.getWidth() + dataobj.getXScalePercent()),
										(int) ((float) img.getHeight() + dataobj.getYScalePercent()),
										Image.SCALE_FAST));

			return img;
		} catch (IOException e) {
			dataobj.getPlayer().sendMessage("Error attempting to acquire Image resource.");
			System.out.println("Exception:" + e.getMessage());
			return null;
		}

	}
	private static int[][] getbufferdata(BufferedImage bi)
	{
		
		int[][] returnit = new int[bi.getWidth()][bi.getHeight()];
		for(int x = 0;x<bi.getWidth();x++)
		{
			
			for(int y=0;y<bi.getHeight();y++)
			{
				
				returnit[x][y] = bi.getRGB(x, y);
				
				
				
			}
		}
			
		return returnit;	
	
		
		
		
	}
	public static void arraytobuffer(int[][] arrayelem,BufferedImage toimage)
	{
		
		
		for(int x=0;x<toimage.getWidth();x++)
		{
			
			for(int y = 0;y<toimage.getHeight();y++)
			{
				
					toimage.setRGB(x, y, arrayelem[x][y]);
				
				
			}
			
			
			
		}
		
		
		
		
	}
	public static void ReduceImage(BufferedImage reducethis,int maxcolors)
	{
		
		int[][] pixels = getbufferdata(reducethis);
		Quantize.quantizeImage(pixels, maxcolors); //we don't need the new palette so... just discard the results...
		
		arraytobuffer(pixels,reducethis);
		
		
	}
	
	public void showcommandhelp(Player p, String[] args) {
		String arg = args[0];
		if (args[0].equalsIgnoreCase("build")) {
			p.sendMessage(ChatColor.GOLD + "Build: builds a image.");
			p.sendMessage(ChatColor.GOLD + "syntax: /pixart build [image]");
			p
					.sendMessage(ChatColor.GOLD
							+ "[image]: image to build. can be a URL, server file path reference, or " +
							"one of the mapped names in the mappingfile. Permissions or Op are required for anything but those mapped names.");
			
		}

		else if (args[0].equalsIgnoreCase("calc")) {
			p.sendMessage(ChatColor.GOLD
					+ "calc: calculates required resources for an image");
			p.sendMessage(ChatColor.GOLD + "syntax: /pixart calc <imagename>");
			p.sendMessage(ChatColor.GOLD
					+ "[image]: image to calculate. can be a URL, server file path reference, or " +
					"one of the mapped names in the mappingfile. Permissions or Op are required for anything but those mapped names.");
	
		} else if (args[0].equalsIgnoreCase("ScaleX")
				|| args[0].equalsIgnoreCase("ScaleY")) {
			p.sendMessage(ChatColor.GOLD
					+ "ScaleX & ScaleY: changes scale factor of built image.");
			
			
			p.sendMessage(ChatColor.GOLD
							+ "syntax: /pixart Scale[X|Y] <value>");
			p.sendMessage(ChatColor.GOLD
							+ " changes scale for given pixel coordinate. if a negative & positive value is used"  
							+ "then the negative value will be changed to match the correct aspect ratio for the other coordinate scale.");
			
				

		} else if (args[0].equalsIgnoreCase("OffsetX")
				|| args[0].equalsIgnoreCase("OffsetY")
				|| args[0].equalsIgnoreCase("OffsetZ")) {
			p.sendMessage(ChatColor.GOLD
							+ "OffsetX & OffsetY: changes Offset values from player location");
			p.sendMessage(ChatColor.GOLD
					+ "syntax: /pixart Offset[X|Y|Z] <value> ");
			p.sendMessage(ChatColor.GOLD
					+ "changes offset for given coordinate (in blocks)");

		} else if (args[0].equalsIgnoreCase("FlipX")
				|| args[0].equalsIgnoreCase("FlipY")) {
			p
					.sendMessage(ChatColor.GOLD
							+ "FlipX & FlipY: flips given coordinate in resulting built image.");
			p.sendMessage(ChatColor.GOLD
					+ "Syntax: /pixart Flip[X|Y] [true|false]");
			p
					.sendMessage(ChatColor.GOLD
							+ "If no parameter value is given, specified coordinate will have it's flip value toggled.");

		} else if (args[0].equalsIgnoreCase("mode")) {
			p
					.sendMessage(ChatColor.GOLD
							+ "mode: changes how X,Y image pixels are mapped to 3-D world space");
			p.sendMessage(ChatColor.GOLD + "syntax: /pixart mode <mode>");
			p.sendMessage(ChatColor.GOLD
					+ "where <mode> is one of XY,XZ, YX,YZ,ZX,ZY.");
			p
					.sendMessage(ChatColor.GOLD
							+ "the X Coordinate of the image is mapped to the first coordinate in the pair; the Y coordinate to the second.");
			p
					.sendMessage(ChatColor.GOLD
							+ "example: XZ will map the image as horizontal, with X in the image being X in the world and Y in the image being Z.");

		} 
		else if(args[0].equalsIgnoreCase("viewmap"))
		{
		//viewmap
			p.sendMessage(ChatColor.GOLD + "viewmap: views current color->block mappings.");
			p.sendMessage(ChatColor.GOLD + "Syntax: /pixart viewmap");
			
			
			
		
		}
		else if (args[0].equalsIgnoreCase("resetmap"))
		{
			//reset mapping for player.
			p.sendMessage(ChatColor.GOLD + "resetmap: resets your color mapping to the default." );
			
			
			
		}
		else if (args[0].equalsIgnoreCase("add")) {
			// /pixart addcolordata R G B ID DAMAGE
			p
					.sendMessage(ChatColor.GOLD
							+ "add: adds a colour mapping from a specified colour to a material");
			p
					.sendMessage(ChatColor.GOLD
							+ "Syntax: /pixart add " + ChatColor.RED
							+ "<R>" + ChatColor.GREEN + "<G>" + ChatColor.BLUE
							+ "<B>" + ChatColor.GOLD + "<ID> <DAMAGE>");
			p
					.sendMessage(ChatColor.GOLD
							+ " R,G,B : Colour components of colour to match as the given material.");
			p.sendMessage(ChatColor.GOLD + "ID, DAMAGE: Material to map.");
			p.sendMessage(ChatColor.GOLD
					+ " Example: /pixart addcolordata 255 255 255 35 0");
			p
					.sendMessage(ChatColor.GOLD
							+ "would map white (RGB 255,255,255) to white wool (ID 35, damage 0)");

		}

	}

	public void showcommandhelp(Player p) {
		// shows the default help to that player.
		p.sendMessage(PixArtPlugin.versionheader);
		p.sendMessage("Pixel art creation tool");
		p.sendMessage("Syntax:");
		p.sendMessage("/pixart [option] [arguments]");
		p
				.sendMessage("supported Options- Build, Add, Calc, ScaleX,ScaleY,FlipX,FlipY,Mode,Resuse");
		p.sendMessage("use /pixart [option] help for help with each.");

	}
	public void DoSave()
	{
		for(PixArtPlayerData loopdata:ArtPlayerData.values()){
			debugmessage("saving for player:" + loopdata.getPlayer().getDisplayName());
			loopdata.Save();
			
		
		
		
		
		}
		
		
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (sender instanceof Player) {
			//System.out.println("OnCommand...");
			for (int i = 0; i < args.length; i++) {

				//System.out.println("argument #" + i + " " + args[i]);

			}
			Player p = (Player) sender;
			PixArtPlayerData papa = getPlayerPixArtData(p);
			String scmd = commandLabel.toLowerCase();
			Location startpos = p.getLocation();

			startpos = p.getWorld().getBlockAt(
					startpos.getBlockX() + papa.getOffsetX(),
					startpos.getBlockY() + papa.getOffsetY(),
					startpos.getBlockZ() + papa.getOffsetZ()).getLocation();

			//System.out.println(scmd);
			BufferedImage img = null;
			if (scmd.equalsIgnoreCase("pixart")) {
				if (args.length == 0)
				{
					showcommandhelp(p);
					return true;
				}
				else if (args[args.length - 1].equalsIgnoreCase("help")) {
					showcommandhelp(p, args);
					return true;
				}

				
	
				else if (args[0].equalsIgnoreCase("calc")) {
					//check perms.
					if (!PixArtPlugin.hasPermex(p, "BCPixArt.calc",false))
					{
					// no permissions for calc.
						p.sendMessage(ChatColor.RED + "BCPixArt:You do not have permissions to use the calc pixart command.");
					}
					if (args.length < 2)
					{
						p.sendMessage(ChatColor.YELLOW + "BCPixArt:insufficient arguments.");
						showcommandhelp(p,args);
						return false;
					}
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

								p.sendMessage(rescounts[i]
										+ " of item "
										+ blockDataToString(
												papa.EntryColours[i].BlockID,
												papa.EntryColours[i].BlockData)
										+ "("
										+ rescalc.getTotalCount(p,
												papa.EntryColours[i].BlockID,
												papa.EntryColours[i].BlockData)
										+ ")");

							}

						}

					}
				}
				else if(args[0].equalsIgnoreCase("viewmap"))
				{
					//view current player color mappings...
					p.sendMessage("Color mappings for player " + p.getDisplayName() + ":");
					for(ColorEntryData loopentry:papa.EntryColours){
						
						//format: <R>,<G>,<B>=>(BlockdatatoString)
						
						int r=loopentry.colorvalue.getRed();int g=loopentry.colorvalue.getGreen();int b=loopentry.colorvalue.getBlue();
						p.sendMessage(ChatColor.RED + String.valueOf(r) + "," + 
									ChatColor.GREEN + String.valueOf(g) + "," +
									ChatColor.BLUE + String.valueOf(b) + ChatColor.GOLD + " => " + 
									blockDataToString(loopentry.BlockID,loopentry.BlockData));
						
						
						
						
					}
					
					
					
				}
				else if(args[0].equalsIgnoreCase("mappings"))
				{
					//view file mappings; PixArtPlugin.getsettings().preMappedImages
					
					Hashtable<String,String> imagemap = PixArtPlugin.getsettings().getMappedImages();
					
					Enumeration<String> keysiterate = imagemap.keys();
					int tc = 0;
					while(keysiterate.hasMoreElements())
					{
						String nextitem = keysiterate.nextElement();
						
						String result = imagemap.get(nextitem);
						p.sendMessage(nextitem + " mapped to " + result);
						tc++;
						
						
						
						
					}
					p.sendMessage("Total mappings:" + tc);
					
					
				}
				else if (args[0].equalsIgnoreCase("mapreset"))
				{
					papa.EntryColours=papa.getdefaultcolordata();
					
					p.sendMessage("BCPixArt:Your colour mapping information has been reset.");
				}
				
				//this "resuse" segment has been commented out; it contains... issues... that I've yet to resolve.
				/*
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

				}*/ else if (args[0].equalsIgnoreCase("mode")) {
				//	System.out.println("mode");
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

				} else if (args[0].equalsIgnoreCase("add")) {
					// /pixart add R G B ID DAMAGE
					if (args.length < 6) {
						p.sendMessage("insufficient arguments.");
						p.sendMessage("/pixart add R G B ID DAMAGE");
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

				} else if (args[0].equalsIgnoreCase("undo")) {
					// UNDO;
					// FIRST: pop the rollback data for last item...
					RollbackData rbd = papa.UndoStack.pop();
					// now, find the thread and abort it...
					Thread it = rbd.InitialThread;
					if (it != Thread.currentThread()) {
						// abort it.
						it.interrupt();

						// remove it from genthreads..
						if (papa.genthreads.contains(it))
							papa.genthreads.remove(it);
					}
					rbd.DoRollback(rbd.getWorld());

				}
				// support pixart stuff:
				// /pixart build http://bc-programming.com/images/megaman.png
				else if (args[0].equalsIgnoreCase("build")) {
					
					if(PixArtPlugin.hasPermex(p, "BCPixArt.Build"))
					
					// URL url = new URL(args[1]);
						if(args.length==1)
						{
						//help
							p.sendMessage("BCPixArt.Build [url]");
							
						
						}
						else
						{
							renderImageToBlocksthreaded(args[1], p, papa, startpos,	rescalc);
						}
				} else {
					papa.processArguments(p, args);

				}

			}
			return true;
		}

		return false;
	}

	// private Thread spawnedthread;
	public void renderImageToBlocksthreaded(String imagestr, Player p,
			PixArtPlayerData papa, Location startpos, ResourceCalculator resq) {

		if (papa.genthreads.size() >= papa.maxconcurrentdraws) {
			p
					.sendMessage("PIXART: max concurrent draws reached. Wait until active draws complete, or cancel them.");
			return;
		}

		ThreadWork workerthread = new ThreadWork(imagestr, p,
				(PixArtPlayerData) papa.clone(), startpos, resq);
		Thread spawnedthread = new Thread(workerthread);
		//set to a low priority to prevent server overload.... or try to prevent it, anyway.
		spawnedthread.setPriority(Thread.MIN_PRIORITY+1);
		// add it to papa...
		papa.genthreads.add(spawnedthread);
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

			
			
			//first, make sure the image doesn't exceed the maximum size in the config.
			
			
			
			// Next, if resource use is enabled, make sure they
			// have sufficient resources, and if so, deplete those
			// resources.
			
			
			
			
			if (papa.getUsePlayerResources()) {
				if (!rescalc.hasResourcesDirect(p,
						papa.EntryColours, img)) {
					// failure... insufficient resources. note that
					// hasResourcesDirect will show the message.
					debugmessage("Player "
							+ p.getDisplayName()
							+ " tried to create image with insufficient resources.");
					return;
				} else {
					// deplete the player resources <now> rather
					// then after building it. although debatably it
					// could be an option.
					debugmessage("Player has sufficient resources-");
					
					//testing change: will deplete while being built...
					//rescalc.depleteResources(p, papa.EntryColours,
					//		img);
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
			
			RollbackData rbd = new RollbackData();
			//add it to the players list of undoables...
			papa.UndoStack.add(rbd);
			debugmessage("player at position X:" + px + " Y:"
					+ py + " Z:" + pz);
			try {
			for (int xpixel = 0; xpixel < img.getWidth(); xpixel++) {
				// int xpix = xpixel+px;
				for (int ypixel = 0; ypixel < img.getHeight(); ypixel++) {
					// int ypix = ypixel+py;
					totalblocks++;
					if(totalblocks%50==0) try {Thread.sleep(100);} catch(Exception e){}
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
						
						//add the rollbackitem
						
						rbd.AddRollbackItem(papa,blockpos);
						
						setblocktocolor(papa, blockpos, new Color(colourvalue,true),rescalc);
						Thread.sleep(PixArtPlugin.getsettings().blocksleep);
						
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

				} //for ypixel...
				Thread.sleep(PixArtPlugin.getsettings().blockrowdelay);
			
		
			
		}}
			catch(InterruptedException ie)
			{
				//the thread was interrupted- close down... (this is usually done when a player undoes or cancels a drawing)
				Thread.currentThread().interrupt();
			}
			
			
			finally{
			papa.genthreads.remove(Thread.currentThread());
			}
		} //img!=null
	}

	private static void sleepEx(long ms) {
		try {
			// sleep every single block for the given amount of time. defaults
			// to 500 ms.
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// don't care. bloody checked exceptions...
		}

	}

	private static void setblocktocolor(PixArtPlayerData pd, Block setblock,
			Color tocolor,ResourceCalculator rescalc) {
		boolean exceptionoccured = true;
		int exceptioncount = 0;
		World blockworld = setblock.getWorld();
		Chunk blockchunk = setblock.getChunk();
		if(!blockworld.isChunkLoaded(blockchunk))
			blockworld.loadChunk(blockchunk);
		if (!setblock.getType().equals(Material.AIR))
			return; // ignore attempts to replace anything but air...
		// HACK ALERT: was encountering a NullPointerException deep beneath is
		// within setTypeIdAndData (not my fault I swear).
		// I suspect it's a race condition. the hack is that it tries 5 times,
		// and sleeps for 100 milliseconds each
		// time it fails. After 5 times it just gives up and pretends all went
		// well to prevent
		// hurting the feelings of the calling routine.
		boolean successful=false;
		while (exceptionoccured && exceptioncount < 5) {
			try {
				exceptionoccured = false;
				ColorEntryData sd = getnearestcolor(pd, tocolor);
				setblock.setTypeIdAndData(sd.BlockID, (byte) sd.BlockData,
						false);
				successful=true;
			} catch (Exception e) {
				exceptionoccured = true;
				exceptioncount++;
				try {
					Thread.sleep(50 * exceptioncount);
				} catch (InterruptedException exx) {
					// ignore...

				}

				}
			}
		
		
		//if we were successful....
		if(successful)
		{
			if(pd.getUsePlayerResources())
			{
				ColorEntryData sdp = getnearestcolor(pd, tocolor);
			rescalc.depleteResource(pd.getPlayer(), sdp.BlockID,sdp.BlockData,1);
			}
			else
			{
				debugmessage("PIXART:WARNING- Failed to set block " + setblock.getLocation().toString());
				
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

		pdata.setBuildOrientation(bconst);
		Player p = pdata.getPlayer();

		p.sendMessage("BCPixArt Build Orientation changed to " + bconst);
		debugmessage("BCPixArt Build Orientation changed to " + bconst
				+ " for player " + p.getDisplayName());

	}

}
