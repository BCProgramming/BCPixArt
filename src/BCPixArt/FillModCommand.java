
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
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
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

public class FillModCommand implements CommandExecutor {


	private final PixArtPlugin owner;
	public FillModCommand(PixArtPlugin powner){
		owner = powner;
	}

	public int getTotalCount(PlayerInventory inv,Material checkfor)
	{
		int totalamount=0;
		for(ItemStack loopstack :  inv.all(checkfor).values())
		{
			totalamount+=loopstack.getAmount();


		}

		return totalamount;



	}
	private int getcolordiff(Color colorA,Color colorB)
	{
		if(colorA==null || colorB==null) return Integer.MAX_VALUE;
		return Math.abs(colorB.getRed()-colorA.getRed()) +
		Math.abs(colorB.getGreen()-colorA.getGreen()) +
		Math.abs(colorB.getBlue()-colorA.getBlue());


	}
	private int getnearestcolorindex(Color colorcheck, Color[] palette)
	{
		int[] diffs = new int[palette.length];
		int currminindex=-1,currmin=Integer.MAX_VALUE;
		for(int i=0;i<diffs.length;i++)
		{
			diffs[i] = getcolordiff(palette[i],colorcheck);
			if(diffs[i] < currmin)
			{
				currmin=diffs[i];
				currminindex=i;
				
			}
		}
		return currminindex;



	}
	
	
	
	//R:173   G:216   B:230 light blue
	//White,orange,magenta,Light blue, Yellow, Lime Green, Pink, Gray, Light Gray, Cyan, Purple,
	//Blue,Brown,Green,Red,Black,
	
	
	
	
		
	                                                                        
	                                                                        
	
	

    
	
		
	
	
	
	private Color[] colors=null;
	
	
	
	


	
	
	
	
	
	
	
	@Override

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		// TODO Auto-generated method stub
		if(sender instanceof Player)
		{
			Player p = (Player)sender;
			
			String scmd =commandLabel.toLowerCase();
			Location startpos = p.getLocation();
			if(scmd.equals("fill"))
			{
				BlockFillSetupData bfs = owner.BListener.getfilldata(p);

				if(args.length==0)
				{
					//with no args, set to "get first block" mode for the player.
					p.sendMessage(ChatColor.BLUE + "fill command detected");
					owner.BListener.SetState(p,FMBlockListenerState.FM_First);

					return true;
				}
				else {
					System.out.println("first arg:" + args[0]);
					if(args[0].equalsIgnoreCase("do")) //otherwise, if args were passed, inspect them.

					{

						if(owner.BListener.getfilldata(p).isComplete())
						{
							owner.BListener.PerformFill(p);

						}
						else
						{

							p.sendMessage("Fill bounds not set. use /Fill to set Bounds.");

						}
					}
					else if(args[0].toLowerCase().equals("type"))
					{
						//type to set to is in args[1].
						String checknewtype = args[1].toLowerCase();
						if(checknewtype.equalsIgnoreCase("solid"))
						{
							bfs.FillMode=BlockFillMode.BlockFill_SolidBlock;
							p.sendMessage("FillMode set to Solid");


						}
						else if(checknewtype.equalsIgnoreCase("hollow"))
						{
							bfs.FillMode=BlockFillMode.BlockFill_Hollow;
							p.sendMessage("FillMode set to Hollow");


						}
						else
						{

							p.sendMessage("unrecogized Fill Type.");


						}



					}
					else if(args[0].toLowerCase().startsWith("mat"))
					{
						// /fill set will set first block type, /fill set1 will set first as well, and /fill2 will set second, etc.
						int materialitem=0;
						//first, determine if there is a number at the end of args[0]....
						try {
							if((materialitem=Integer.parseInt(args[0].substring(3)))!=0)
							{

							}
							else
								materialitem=1;
						}
						catch(NumberFormatException e)
						{
							materialitem=1;
						}

						if(p.getItemInHand().getType().isBlock())
						{
							if(materialitem-1 < bfs.usematerials.length)
							{
								System.out.println("setting material type #" + materialitem + "for player " + p.getDisplayName());
								bfs.usematerials[materialitem-1]= p.getItemInHand().getType();



							}
							else
							{
								p.sendMessage("Index out of range- mat indices must be in range 1-" + bfs.usematerials.length);
							}
						}
						else
						{
							p.sendMessage("item " + p.getItemInHand().getType().name() + " is not a Block.");



						}




					}

				}
			}





		}
		return false;
	}


}
