package com.BASeCamp.BCPixArt;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
public class FMBlockListener implements Listener {
	
	
	//valid states
	public enum FMBlockListenerState
	{
		FM_None,
		FM_First,
		FM_Second,
		FM_Both
		
		
		
	}
	public enum BlockFillMode
	{
	BlockFill_SolidBlock,   //fill solid	
	BlockFill_Hollow	
		
	
	}
	public class BlockFillSetupData
	{
		public BlockFillSetupData(Player p)
		{
			
			System.out.println("Creating BlockFillSetupData for " + p.getDisplayName());
			forPlayer=p;
			for(int i=0;i< usematerials.length;i++)
			{
				
				usematerials[i] = Material.AIR;
				
			}
		
		}
		public Block FirstBlock;
		public Block SecondBlock;
		public FMBlockListenerState Currstate=FMBlockListenerState.FM_None;
		public Player forPlayer;
		public BlockFillMode FillMode=BlockFillMode.BlockFill_SolidBlock;
		public Material[] usematerials = new Material[3]; 
		public Boolean allAir()
		{
			for( Material loopmaterial : usematerials)
			{
				if(!loopmaterial.equals(Material.AIR))
					return false;
				
			
			}
			
			return true;
			
		}
		public Boolean isComplete()
		{
			
			return FirstBlock!=null && SecondBlock!=null;
			
			
		}
		public Boolean PlayerhasResources()
		{
			//returns true if player has Resources to perform this action.
			//false otherwise.
			return true;
			
			
		}
		@Override
		public String toString()
		{
			
			StringBuilder buildit = new StringBuilder();
			buildit.append(" Fill Data Summary \n");
			buildit.append(" Player:" + forPlayer.getDisplayName() + "\n");
			
			
			return buildit.toString();
			
		}
		
		
	}
	//private FMBlockListenerState currstate=FMBlockListenerState.FM_None;
	
	
	private final PixArtPlugin owner;
	
private config configuration;
	protected HashMap<String,BlockFillSetupData> SetData = new HashMap<String,BlockFillSetupData>();
	
	public BlockFillSetupData getfilldata(Player p)
	{
	System.out.println("getfilldata for player " + p.getDisplayName());
	BlockFillSetupData grabit = SetData.get(p.getDisplayName());
	if(grabit==null)
	{
	grabit = new BlockFillSetupData(p);	
	
	SetData.put(p.getDisplayName(), grabit)	;
	
	}
	
	return grabit;	
		
	
	}
	public void SetState(Player p,FMBlockListenerState newstate)
	{
	System.out.println("SetState called...");
	BlockFillSetupData bfs = getfilldata(p);
	bfs.Currstate = newstate;
	//currstate = newstate;	
	switch(bfs.Currstate)
	{
	case FM_None:
		break;
	case FM_First:
		p.sendMessage("Select First Block.");
		break;
	case FM_Second:
		p.sendMessage("Select Second Block.");
		break;
	case FM_Both:
		p.sendMessage(ChatColor.YELLOW + "Both bounds set. use \"/fill do\" to fill.");
		break;
	
	
	}
	}
	public FMBlockListenerState GetState(Player p)
	{
	return getfilldata(p).Currstate;	
		
	
	}
	public void setConfig(config assignto)
	{
	configuration = assignto;	
		
	}
	private int totalvolume(Location firstspot,Location secondspot)
	{
		
		return (Math.abs(secondspot.getBlockX()-firstspot.getBlockX()) *
				Math.abs(secondspot.getBlockY()-firstspot.getBlockY()) *
				Math.abs(secondspot.getBlockZ()-firstspot.getBlockZ()));
		
		
		
	}
	private int getusedmatlength(Material[] mats)
	{
		
		for(int i=mats.length;i>0;i++)
		{
			
			if(!mats[i].equals(Material.AIR))
					{
					return i;
				
				
					}
			
			
		}
		
		return mats.length;
		
		
	}
	public void PerformFill(Player pp)
	{
	BlockFillSetupData bfs = getfilldata(pp);
	
	if(bfs.allAir()){
		//make first item the one the player has currently selected.
		if(pp.getItemInHand().getType().isBlock())
		bfs.usematerials[0] = pp.getItemInHand().getType();
		else
		{
			pp.sendMessage("use /fill set or /fill set# to set material types, or have the block you wish to use selected.");
		    return;
		}    
	}    
		    
		//step one: make sure they have the appropriate resources.
		    if(bfs.PlayerhasResources())
		    {
		    	pp.sendMessage("Perform fill between " + bfs.FirstBlock.getLocation().toString() + " and " + bfs.SecondBlock.getLocation().toString());
		    	
		    	Location startspot = bfs.FirstBlock.getLocation();
		    	Location endspot = bfs.SecondBlock.getLocation();
		    	int startx,starty,startz;
		    	int endx,endy,endz;
		    	startx = Math.min(startspot.getBlockX(),endspot.getBlockX());
		    	starty = Math.min(startspot.getBlockY(),endspot.getBlockY());
		    	startz = Math.min(startspot.getBlockZ(),endspot.getBlockZ());
		    	endx = Math.max(startspot.getBlockX(),endspot.getBlockX());
		    	endy = Math.max(startspot.getBlockY(),endspot.getBlockY());
		    	endz = Math.max(startspot.getBlockZ(),endspot.getBlockZ());
		    	
		    	//first determine the number of blocks that will be used.
		    	
		    	
		    	//int gotvolume = totalvolume(startspot,endspot);
		    	int totalcount=0;
		    	Boolean atends=false;
		    	switch(bfs.FillMode)
		    	{
		    	case BlockFill_Hollow:
		    	case BlockFill_SolidBlock:
		    		int deltax,deltay,deltaz;
		    		int matlength=getusedmatlength(bfs.usematerials);
		    		//currently only uses the first block type...
		    		for(int currX = startx;currX < endx;currX++)
		    		{
		    			deltax=currX-startx;
		    			for(int currY = starty;currY < endy;currY++)
		    			{
		    				deltay = currY-starty;
		    				for(int currZ =startz;currZ < endz;currZ++)
		    				{
		    					deltaz=currZ-startz;
		    					totalcount=currX+currY+currZ;
		    					World gotworld=pp.getWorld();
		    					atends=currX==startx||currX==endx-1||currY==starty||currY==endy-1||currZ==startz||currZ==endz-1;
		    					if((bfs.FillMode!=BlockFillMode.BlockFill_Hollow)||(bfs.FillMode==BlockFillMode.BlockFill_Hollow && atends))
		    					{
		    					
		    					//System.out.println("gotworld null?" + (gotworld==null));
		    					
		    					try {
		    					gotworld.getBlockAt(currX, currY, currZ).setType(bfs.usematerials[totalcount%matlength]);
		    					}
		    					catch(Exception q)
		    					{
		    						gotworld.getBlockAt(currX, currY, currZ).setType(Material.AIR);
		    						
		    					}
		    					}
		    					else if(bfs.FillMode==BlockFillMode.BlockFill_Hollow && !atends)
		    					{
		    						gotworld.getBlockAt(currX, currY, currZ).setType(Material.AIR);
		    						
		    						
		    					}
		    					}
		    					
		    				}
		    				
		    				
		    			}
		    			
		    			
		    		
		    		
		    		
		    		
		    		
		    		
		    		break;
		    	
		    	}
		    	
		    }
		    else
		    {
		    	pp.sendMessage("insufficient resources for selected range.");
		    	
		    }
		
	
	
	
	
	
	
	
		
	
	}
	public FMBlockListener(PixArtPlugin powner){
		owner=powner;
	}
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event)
	{
		Location loc = event.getBlock().getLocation();
		Player pp = event.getPlayer();
		BlockFillSetupData bfs = getfilldata(pp);
		switch(bfs.Currstate)
		{
		case FM_None:
			break;
		case FM_First:
			System.out.println("First block chosen...");
			bfs.FirstBlock = event.getBlock();
			SetState(pp, FMBlockListenerState.FM_Second);
			
			break;
		case FM_Second:
			if(event.getBlock().getLocation()==bfs.FirstBlock.getLocation())
			{
				pp.sendMessage("Second Location cannot be the same as the first.");
				//reset the state, so we show the message...
				SetState(pp,FMBlockListenerState.FM_Second);
				
				
			}
			bfs.SecondBlock=event.getBlock();
			System.out.println("Second block chosen...");
			SetState(pp, FMBlockListenerState.FM_Both);
			break;
			
		
		}
		
	
	}
}
