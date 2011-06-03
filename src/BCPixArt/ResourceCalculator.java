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
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;

import BCPixArt.PixArtCommand.ColorEntryData;
import BCPixArt.PixArtCommand.ColorEntryData.*;


/**
 * Wool resource calculator;
 * @author BC_Programming
 *
 */
//What does this class do?

//intended to facilitate the use of the players inventory to create the wool structures.
//obviously a prerequisite for that is to determine exactly what is required for the creation of said wool structures.
//the ColorEntryData[] array will contain necessary wool requirements. 




public class ResourceCalculator {

	
	
	private PixArtCommand pcommand;
	public ResourceCalculator(PixArtCommand ppcommand)
{
	pcommand=ppcommand;
	
}
	/**
	 * generic method used to find the index of a given item in an array of that items type.
	 * @param <T>
	 * @param arraycheck Array to search through.
	 * @param finditem Item to look for.
	 * @return
	 */
	private static <T> int ArrayIndexOf(T[] arraycheck,T finditem)
	{
		for(int i=0;i<arraycheck.length;i++)
		{
			if(arraycheck[i].equals(finditem))
			{
				return i;
			}
			
			
		}
		return -1;
		
	}
	/**
	 * 
	 * @param IDA ID of First item
	 * @param DataA Data of first item
	 * @param IDB ID of second item
	 * @param DataB Data of second item 
	 * @return
	 */
	//function is intended to be used for those weird cases where the inventory item for a block is actually a different block from what get's placed....
	
	private Boolean isEquivalent(int IDA, int DataA,int IDB,int DataB)
	{
		//returns true of both IDs and Data are the same. 
		return (IDA==IDB && DataA==DataB);
	
	}
	
	
	/**
	 * 
	 * @param p Player to check
	 * @param itemID ID of item to count
	 * @param itemdata data (damage) of item to count.
	 * @return
	 */
	public int getTotalCount(Player p,int itemID, int itemdata)
	{
		//ItemStack loopitem=null;
		int runningcount=0;
		ItemStack[] gotinventory = p.getInventory().getContents();
		for(ItemStack loopitem:gotinventory)
		{
			if(loopitem!=null)
			{
				int usedata=0;
				if(loopitem.getData()!=null) usedata = loopitem.getData().getData();
			//iterate through each item (duh), create a running total and add each element that is the right ID and itemdata...
			if(isEquivalent(itemID,itemdata,loopitem.getTypeId(),usedata))
				runningcount+=loopitem.getAmount();
			
			}
		}
		
		
		PixArtCommand.debugmessage("calculated total count of item (ID=" + itemID + " data=" + itemdata + ") for player " + p.getDisplayName() + " count was " + runningcount);
		return runningcount;
	}
	
	/**
	 * 
	 * @param p player to check
	 * @param itemID ID of the item to check
	 * @param itemdata Itemdata (damage) of the item to check for 
	 * @param mincount minimum number of the item that must be found.
	 * @return
	 */
	private Boolean hasEnough(Player p,int itemID,int itemdata,int mincount)
	{
		int countof = getTotalCount(p,itemID,itemdata);
		return (countof>=mincount);
		
		
	}
	/**
	 * depletes the given players inventory of numused items to pay for the use of the given number of itemID or itemdata items.
	 * @param p
	 * @param itemID
	 * @param itemdata
	 * @param numused
	 * @return true if depletion was successful-  false if player doesn't have enough (in which case nothing is taken)
	 */
	
	public Boolean depleteResource(Player p,int itemID,int itemdata,int numused)
	{
		PixArtCommand.debugmessage("depleting " + numused + " of (" + itemID + "," + itemdata + ")" + " from " + p.getDisplayName());
		if(!hasEnough(p,itemID,itemdata,numused))
			return false;
		else
		{
			int numremaining=numused;
			ItemStack[] gotinventory = p.getInventory().getContents();
			for(ItemStack loopitem:gotinventory)
			{
				//iterate through each item (duh), create a running total and add each element that is the right ID and itemdata...
				if(loopitem!=null&&loopitem.getData()!=null)
						{
					
					if(isEquivalent(itemID,itemdata,loopitem.getTypeId(),loopitem.getData().getData()))
						
				{
					
					if(loopitem.getAmount()> numremaining)
					{
						loopitem.setAmount(loopitem.getAmount()-numremaining);
						numremaining=0;
					
					}
					else 
					{
						//deplete it entirely by removing it.
					numremaining-=loopitem.getAmount();
					loopitem.setAmount(0);
					}
				}
				
			}
				
			}
			
			
			
			
			return true;
		}
		
		
		
	}
	
	
	/**
	 * depletes the players resources to "pay" for building the given image.
	 * @param p player to deplete
	 * @param thecolors colorEntryData of available colors
	 * @param imagedata
	 * @return
	 */
	
	
public Boolean depleteResources(Player p, ColorEntryData[] thecolors,BufferedImage imagedata)
{
	
	
	int[] resourcecounts = getResourceCounts(thecolors,imagedata);
	if(!hasResourcesDirect(p,thecolors,imagedata))
		return false;
	else{
		//otherwise- deplete!
		PixArtCommand.debugmessage("depleting player resources...");
			for(int i=0;i<thecolors.length;i++)
		{
			ColorEntryData iterate = thecolors[i];
			depleteResource(p,iterate.BlockID,iterate.BlockData,resourcecounts[i]);		
		
		}
			return true;
	}
		
	
	
	
}
/**
 * adds the given item to the player's inventory. (used for rollbacks...)
 * @param p player to receive the inventory item.
 * @param BlockID blockID of the item to add
 * @param BlockData blockdata to add.
 */
public static void AddItemToPlayerInventory(Player p,int BlockID,byte BlockData)
{
	//first look for existing stacks of that item/Data...
	boolean didincrement=false;
	ItemStack newstack=new ItemStack(BlockID,BlockData);
	for(ItemStack loopitem : p.getInventory().all(newstack).values()){
		//if that item has an amount less than 64...
		if(loopitem.getAmount()< 64){
			//increment it. then set the flag and break out of the loop.
			loopitem.setAmount(loopitem.getAmount()+1);
			didincrement=true;
			break;
		}
		
		
		
		
	}
		
	//if the flag is still false, then we will add a new item.
	if(!didincrement)
		p.getInventory().addItem(newstack);
}
	/**
	 * 
	 * @param p Player
	 * @param thecolors ColorEntryData[] array of available colours
	 * @param imagedata image to calculate resources for
	 * @return returns whether the given player has the resources to create the structure.
	 */
public Boolean hasResourcesDirect(Player p, ColorEntryData[] thecolors,BufferedImage imagedata)
{
	//get a total count for each index in thecolors that are required to create the image "imagedata"...
	
	
	int[] totalcounts =	getResourceCounts(thecolors, imagedata);
	ArrayList<Integer> faileditems= new ArrayList<Integer>();
	Boolean hasfailed=false;
	//current status: now we have the totalcounts[] array that corresponds to each item in the passed thecolors[] array, and 
	// we can now check the players inventory to make sure they have at least that much of each given material.
	for(int i=0;i<totalcounts.length;i++)
	{
		PixArtCommand.debugmessage("player needs at least " + totalcounts[i] + " of ItemID:" + thecolors[i].BlockID + " data:" + thecolors[i].BlockData);
		if(hasEnough(p,thecolors[i].BlockID,thecolors[i].BlockData,totalcounts[i]))
		{
			//good...
			PixArtCommand.debugmessage("player has enough ID " + thecolors[i].BlockID + " data=" + thecolors[i].BlockData);
			
			
		}
		else
		{
			//we could fail here, but instead, we will instead add the element index to a list that we can then tell the player after the loop.
			faileditems.add(i);
			hasfailed=true;
		}
		
	}
	
	//now we can tell the player <why> they don't have enough resources.
	if(hasfailed)
	{
		//also: use a StringBuilder, build a message string, and output it as a single message, rather
		//than spamming the player with a crapload of messages.
		StringBuilder messagebuild = new StringBuilder();
	messagebuild.append("requirements not met(ID,Data):");
	for(int loopit:faileditems)
	{
		
		messagebuild.append(" " + thecolors[loopit].BlockID + " " + thecolors[loopit].BlockData + ",");
		
		
		
		
		
	}
		
		
	
	
	p.sendMessage(messagebuild.toString());
	
	return false;
	}
	else
		return true;
	
	
	
	
}
	public int[] getResourceCounts(ColorEntryData[] thecolors,
			BufferedImage imagedata) {
		
		if(imagedata==null) return new int[thecolors.length];
		int[] totalcounts = new int[thecolors.length];
		for(int x=0;x<imagedata.getWidth();x++)
		{
			
			for(int y=0;y<imagedata.getHeight();y++)
			{
				int colourvalue = imagedata.getRGB(x,y);
				//int red = (colourvalue & 0x00ff0000) >> 16;
				//int green = (colourvalue & 0x0000ff00) >> 8;
				//int blue = colourvalue & 0x000000ff;
				int alpha = (colourvalue >> 24) & 0xff;
				//if it exceeds the minimum alpha...
				if(alpha>0) //TODO: change alpha comparison to compare to configuration value...
				{
				//we only get here if the alpha value is larger than 0 (transparent pixels in the image are ignored...
				ColorEntryData entrydat = pcommand.getnearestcolor(thecolors, new Color(colourvalue));
				//now, find the index of the returned ColorEntryData item in the array...
				int indexfound = ArrayIndexOf(thecolors,entrydat);
				//increment the corresponding totalcounts item...
				totalcounts[indexfound]++;
				
				
				}
				
			}
			
		}
		return totalcounts;
	}
	

	
	
}
