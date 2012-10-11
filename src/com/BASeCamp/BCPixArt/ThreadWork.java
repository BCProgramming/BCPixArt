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

import org.bukkit.Location;
import org.bukkit.entity.Player;



public class ThreadWork implements Runnable {

	
	public String strImage;
	public Player pPlayer;
	public PixArtPlayerData pdata;
	public Location spos;
	public ResourceCalculator rescalc;
	
	public ThreadWork(String imagestr,Player p, PixArtPlayerData papa,Location startpos,ResourceCalculator resq)
	{
		
		strImage = imagestr;
		pPlayer = p;
		pdata = papa;
		spos = startpos;
		rescalc = resq;
		
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		PixArtCommand.debugmessage("BCPIXART: In threadwork...");
		PixArtCommand.renderImageToBlocks(strImage, pPlayer, pdata, spos, rescalc);
		
		
		
		
	}

}
