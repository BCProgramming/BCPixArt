package com.BASeCamp.BCPixArt;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.BASeCamp.BCPixArt.PixArtCommand.BuildOrientationConstants;


/**
 * 
 * @author BC_Programming what was originally a data class used for storing
 *         player specific settings for building, such as orientation,scale,
 *         flip, etc and has evolved into a utility class that retrieves
 *         some of the relevant info itself. also for reasons I have yet to
 *         fathom some of the command processing is done here as well.
 *         Unsolved mysteries...
 */
public class PixArtPlayerData implements Cloneable,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2953858795233284398L;
	private Player pPlayer;
	private BuildOrientationConstants BuildOrientation = BuildOrientationConstants.Build_XZ;
	private float scaleXPercent = 1.0f;
	private float scaleYPercent = 1.0f;
	private boolean FlipX=false, FlipY=false;
	private int OffsetX = 0;
	private int OffsetY = 0;
	private int OffsetZ = 0;
	private boolean canuseURL=true;
	private boolean canusePath=true;
	public Stack<RollbackData> UndoStack = new Stack<RollbackData>();
	public Stack<RollbackData> RedoStack = new Stack<RollbackData>();
	private boolean usePlayerResources = false;
	public int maxconcurrentdraws = 2;
	public ArrayList<Thread> genthreads = new ArrayList<Thread>(); 
	
	public String datafiledir = PixArtPlugin.pluginMainDir + "/mappings";
	
	//setters and getters
	public Player getPlayer(){return pPlayer;}
	public void setPlayer(Player p){pPlayer=p;}
	public BuildOrientationConstants getBuildOrientation() {if(BuildOrientation==null) BuildOrientation = BuildOrientationConstants.Build_XZ; return BuildOrientation;}
	public void setBuildOrientation(BuildOrientationConstants p) { BuildOrientation=p;}
	public float getXScalePercent() { return scaleXPercent;}
	public void setXScalePercent(float value) { scaleXPercent=value;}
	public float getYScalePercent() { return scaleYPercent;}
	public void setYScalePercent(float value) { scaleYPercent=value;}
	public boolean getFlipX(){ return FlipX;}
	public void setFlipX(boolean value){ FlipX = value; } 
	public boolean getFlipY() { return FlipY;}
	public void setFlipY(boolean value){FlipY=value;}
	public int getOffsetX() { return OffsetX;}
	public void setOffsetX(int value) { OffsetX=value;}
	public int getOffsetY() { return OffsetY;}
	public void setOffsetY(int value) { OffsetY=value;}
	public int getOffsetZ() { return OffsetZ;}
	public void setOffsetZ(int value) { OffsetZ = value;}
	public boolean getcanuseURL() { return canuseURL;}
	public void setcanuseURL(boolean value) { canuseURL = value;}
	public boolean getcanusePath() { return canusePath;}
	public void setcanusePath(boolean value) { canusePath=value;}
	
	
	
	public String GetPlayerDataFile(String playername){
		return datafiledir + "/" + playername + ".properties";
		
		
	}
	public void Load()
	{
		String playerdatafile = GetPlayerDataFile(pPlayer.getDisplayName());
		if(! new File(playerdatafile).exists())
		{
			PixArtCommand.debugmessage("No persistent data for player " + pPlayer.getDisplayName());
			return;
			//nothing to load....
		}
		else
		{
		try {
			FileInputStream fi = new FileInputStream(playerdatafile);
			Properties readproperties = new Properties();
			readproperties.load(fi);
			Properties rp = readproperties; //shorthand alias
			
			try {setBuildOrientation((BuildOrientationConstants) rp.get("BuildOrientation"));} 
			catch(Exception exx1) { setBuildOrientation(BuildOrientationConstants.Build_XY);}
			try {scaleXPercent = (Float) rp.get("scaleXPercent");
			scaleYPercent = (Float) rp.get("scaleYPercent");
			}
			catch(Exception exx2) { scaleXPercent=scaleYPercent=1;}
			try {FlipX = (Boolean) rp.get("FlipX");
			FlipY = (Boolean) rp.get("FlipY");
			}
			catch(Exception exx3) { FlipX=FlipY=false;}
			try {
			OffsetX = (Integer) rp.get("OffsetX");
			OffsetY = (Integer) rp.get("OffsetY");
			OffsetZ = (Integer) rp.get("OffsetZ");
			EntryColours = (ColorEntryData[]) rp.get("ColorTable");
			}
			catch(Exception exx4) {}
			
		}
		catch(IOException ioe){
			
			ioe.printStackTrace();
			
		}
		
			
			
		
		}
		
		
	}
	public void Save()
	{
		//saves this PixArtPlayerData to the appropriate location PixArtPlugin.pluginMainDir +"/mappings/playername.properties"
		String playerdatafile = GetPlayerDataFile(pPlayer.getDisplayName());
		if(!new File(playerdatafile).exists())
		{
			new File(datafiledir).mkdirs();
			
			
			
		}
		//BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PixArtPlugin.pluginConfigLocation)));
		try {
			FileOutputStream fo = new FileOutputStream(playerdatafile);
			Properties saveproperties = new Properties();
			saveproperties.put("BuildOrientation", this.getBuildOrientation());
			saveproperties.put("scaleXPercent",scaleXPercent);
			saveproperties.put("ScaleYPercent",scaleYPercent);
			saveproperties.put("FlipX",FlipX);
			saveproperties.put("FlipY",FlipY);
			saveproperties.put("OffsetX",OffsetX);
			saveproperties.put("OffsetY",OffsetY);
			saveproperties.put("OffsetZ",OffsetZ);

			saveproperties.put("ColorTable",EntryColours);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public PixArtPlayerData(String sourcefilename){
		//loads this PixArtPlayerData from the given source filename.
		this.canuseURL = PixArtPlugin.hasPermex(pPlayer, "canuseURL");
		this.canusePath = PixArtPlugin.hasPermex(pPlayer, "canusePath");
		
		
	}
	// all the threads being used to gen this players pixel art.

	/**
	 * undoes the last rollbackdata, and adds it to the redo stack.
	 */
	public boolean UndoLast() {
		if (UndoStack.empty()) {
			return false; // can't undo, nothing to undo...
		}
		// pop off the last RollbackData...
		RollbackData lastitem = UndoStack.pop();
		// undo it...
		lastitem.DoRollback(lastitem.getWorld());
		// push it onto the Redo stack...
		RedoStack.push(lastitem);
		return true;

	}

	public boolean RedoLast() {
		if (RedoStack.empty()) {

			return false;

		}
		RollbackData redoitem = RedoStack.pop();
		// redo it.
		redoitem.DoRollback(redoitem.getWorld());

		UndoStack.push(redoitem);
		TrimStacks();
		return true;

	}

	private static final int maxundosize = 5;

	private void TrimStack(Stack<RollbackData> rollstack, int maxsize) {
		while (rollstack.size() > maxsize) {
			rollstack.remove(0);
		}

	}

	/**
	 * ensures the stack is no larger than the given size by removing older
	 * elements while it is larger.
	 */
	private void TrimStacks() {

		TrimStack(UndoStack, maxundosize);
		TrimStack(RedoStack, maxundosize);

	}

	public void setResourceUse(Boolean value) {
		setUsePlayerResources(value);
		Cloneable px;

	}

	@Override
	public Object clone() {
		return new PixArtPlayerData(this);

	}

	public PixArtPlayerData(PixArtPlayerData clonethis) {

		pPlayer = clonethis.pPlayer;
		setBuildOrientation(clonethis.getBuildOrientation());
		scaleXPercent = clonethis.scaleXPercent;
		scaleYPercent = clonethis.scaleYPercent;
		FlipX = clonethis.FlipX;
		FlipY = clonethis.FlipY;
		OffsetX = clonethis.OffsetX;
		OffsetY = clonethis.OffsetY;
		OffsetZ = clonethis.OffsetZ;
		canuseURL = clonethis.canuseURL;
		canusePath = clonethis.canusePath;
		setUsePlayerResources(clonethis.getUsePlayerResources());
		UndoStack = clonethis.UndoStack;
		RedoStack = clonethis.RedoStack;
		genthreads = clonethis.genthreads;

	}

	public void setResourceUse(String value) {

		if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")
				|| Boolean.parseBoolean(value)) {
			setUsePlayerResources(true);

		} else {
			setUsePlayerResources(false);
		}

		pPlayer.sendMessage("Resource use set to " + getUsePlayerResources());

	}

	public Boolean getResourceUse() {

		return getUsePlayerResources();

	}
	/*
	 * 
dirt 134,96,67 ID 3
ice 119,169,255,96 ID 79
emerald 76,226,118 ID 95
diamond block 105,223,57
gold block 255,241,68 ID 41
Iron Block 230,230,230 ID 42
Oak Plank 159,132,77  ID5 DT 0
Spruce plank 102,79,47 ID5 DT 1
birch plank 215,203,141 ID5 DT 2
Jungle plank 84,135,100 ID5 DT 3
nether brick 48,24,28 133
Lapis block 21,70,194 ID 22
mushroom 210,177,125 ID 99

	 */
	private static final ColorEntryData Data_Dirt = new ColorEntryData(new Color(134,96,67),3,0);
	private static final ColorEntryData Data_Ice = new ColorEntryData(new Color(119,169,255,96),79,0);
	private static final ColorEntryData Data_Emerald = new ColorEntryData(new Color(76,226,118),133,0);
	private static final ColorEntryData Data_DiamondBlock = new ColorEntryData(new Color(119,169,227,96),57,0);
	private static final ColorEntryData Data_GoldBlock = new ColorEntryData(new Color(225,241,68),41,0);
	private static final ColorEntryData Data_IronBlock = new ColorEntryData(new Color(230,230,230),42,0);
	private static final ColorEntryData Data_OakPlank = new ColorEntryData(new Color(159,132,77),5,0);
	private static final ColorEntryData Data_SprucePlank = new ColorEntryData(new Color(102,79,47),5,1);
	private static final ColorEntryData Data_BirchPlank = new ColorEntryData(new Color(215,203,141),5,2);
	private static final ColorEntryData Data_JunglePlank = new ColorEntryData(new Color(84,135,100),5,3);
	private static final ColorEntryData Data_NetherBrick = new ColorEntryData(new Color(48,24,28),112,0);
	private static final ColorEntryData Data_Lapis = new ColorEntryData(new Color(21,70,194),22,0);
	private static final ColorEntryData Data_Mushroom = new ColorEntryData(new Color(210,177,125),99,0);
	public ColorEntryData[] getdefaultcolordata()
	{
		return new ColorEntryData[] {
				new ColorEntryData(new Color(250,250,250), 35, 0),
				new ColorEntryData(new Color(221,133,75), 35, 1),
				new ColorEntryData(new Color(180,75,189), 35, 2),
				new ColorEntryData(new Color(86, 122, 194), 35, 3),
				new ColorEntryData(new Color(194,182,46), 35, 4),
				new ColorEntryData(new Color(66, 180, 58), 35, 5),
				new ColorEntryData(new Color(204,120,144), 35, 6),
				new ColorEntryData(new Color(70,70,70), 35, 7),
				new ColorEntryData(new Color(168,174,174), 35, 8),
				new ColorEntryData(new Color(51,129,149), 35, 9),
				new ColorEntryData(new Color(137,70,194), 35, 10),
				new ColorEntryData(new Color(50, 62, 154), 35, 11),
				new ColorEntryData(new Color(78, 49, 30), 35, 12),
				new ColorEntryData(new Color(57,76,30), 35, 13),
				new ColorEntryData(new Color(163,56,52), 35, 14),
				new ColorEntryData(new Color(27,23,23), 35, 15),
				new ColorEntryData(new Color(34,28,47),49,0),
				 new ColorEntryData(new Color(220,212,252),24,0),
				Data_Dirt,Data_Ice,Data_Emerald,Data_DiamondBlock,Data_GoldBlock,Data_IronBlock,Data_OakPlank,Data_SprucePlank,
				Data_BirchPlank,Data_JunglePlank,Data_NetherBrick,Data_Lapis,Data_Mushroom
				
				
				
				
		};

		
		
	
	}
	public ColorEntryData[] EntryColours = getdefaultcolordata();

	// << Sand
	public PixArtPlayerData(Player p) {
		pPlayer = p;
		this.scaleXPercent = 1;
		this.scaleXPercent = 1;
		FlipX = false;
		FlipY = false;
		this.canuseURL = PixArtPlugin.hasPermex(pPlayer, "BCPixArt.canuseURL");
		this.canusePath = PixArtPlugin.hasPermex(pPlayer, "BCPixArt.canusePath");
		this.maxconcurrentdraws = 2; 
		Load(); //load from file, if possible...

	}

	public void processArguments(Player p, String[] args) {

		if (args[0].equalsIgnoreCase("ScaleX")) {
			if (args.length < 2) {
				p.sendMessage("PIXART: ScaleX currently " + scaleXPercent);

				return;
			}
			this.scaleXPercent = Float.parseFloat(args[1]);
			p.sendMessage(ChatColor.YELLOW + "PIXART:XScale set to " + this.scaleXPercent);

		} else if (args[0].equalsIgnoreCase("ScaleY")) {
			if (args.length < 2) {
				p.sendMessage(ChatColor.YELLOW + "PIXART: ScaleY currently " + scaleYPercent);

				return;
			}
			this.scaleYPercent = Float.parseFloat(args[1]);
			p.sendMessage(ChatColor.YELLOW + "PIXART:YScale set to " + this.scaleYPercent);

		} else if (args[0].equalsIgnoreCase("FlipX")) {
			if (args.length < 2) {
				FlipX = !FlipX;

			} else {
				FlipX = (Boolean.parseBoolean(args[1]) || args[1]
						.equalsIgnoreCase("yes"));
			}

			p.sendMessage(ChatColor.YELLOW + "PIXART:FlipX set to " + FlipX);

		} else if (args[0].equalsIgnoreCase("FlipY")) {
			if (args.length < 2)
				FlipY = !FlipY;
			else
				FlipY = (Boolean.parseBoolean(args[1]) || args[1]
						.equalsIgnoreCase("yes"));

			p.sendMessage(ChatColor.YELLOW + "PIXART:FlipY set to " + FlipY);
		} else if (args[0].equalsIgnoreCase("OffsetX")) {
			if (args.length < 2) {
				p.sendMessage("PIXART: OffsetX currently " + OffsetX);

				return;
			}
			try {
				OffsetX = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(ChatColor.YELLOW + "PIXART:invalid Value.");

			}
			p.sendMessage("PIXART:X offset set to " + OffsetX);
		} else if (args[0].equalsIgnoreCase("OffsetY")) {
			if (args.length < 2) {
				p.sendMessage("PIXART: OffsetY currently " + OffsetY);

				return;
			}
			try {
				OffsetY = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(ChatColor.YELLOW + "PIXART:invalid Value.");

			}
			p.sendMessage("PIXART:Y offset set to " + OffsetY);
		} else if (args[0].equalsIgnoreCase("OffsetZ")) {
			if (args.length < 2) {
				p.sendMessage("PIXART: OffsetZ currently " + OffsetZ);

				return;
			}
			try {
				OffsetY = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(ChatColor.YELLOW + "PIXART:invalid Value.");

			}
			p.sendMessage("PIXART:Z offset set to " + OffsetZ);
		}

	}
	public void setUsePlayerResources(Boolean usePlayerResources) {
		this.usePlayerResources = usePlayerResources;
	}
	public Boolean getUsePlayerResources() {
		return usePlayerResources;
	}


}