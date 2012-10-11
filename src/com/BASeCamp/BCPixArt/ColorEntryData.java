package com.BASeCamp.BCPixArt;

import java.awt.Color;
import java.io.Serializable;

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