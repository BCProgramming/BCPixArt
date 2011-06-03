package BCPixArt;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.Block;

import BCPixArt.PixArtCommand.PixArtPlayerData;

/**
 * Class used for storing data about a rollback operation. 
 * @author BC_Programming
 *
 */
public class RollbackData {
	public final Thread InitialThread=Thread.currentThread();
	public class RollbackItem{
		//a rollback is pretty easy- it would just reset a block at a given location with the old type and damage attributes.
		private Location Blocklocation=null;
		private int OldID;
		private byte OldDamage;
		private PixArtPlayerData pxdata;
		//RollbackData's are instantiated by the renderblocks routine, which is usually performed on a separate thread.
		public RollbackItem(PixArtPlayerData pdata,Location pLocation,int pID,byte pDamage)
		{
			Blocklocation=pLocation;
			OldID=pID;
			OldDamage=pDamage;
			pxdata=pdata;
		}
		public RollbackItem(PixArtPlayerData pdata,Block originalstate)
		{
			Blocklocation = originalstate.getLocation();
			OldID=originalstate.getTypeId();
			OldDamage = originalstate.getData();
			pxdata=pdata;
			
			
		}
		public void PerformRollback(World toworld)
		{
			//performs the rollback operation- simply reset the options...
			//note, we also change our blockinformation so that another Rollback will in fact "revert" things.
			int tID;
			byte tDamage;
			Block changeblock = toworld.getBlockAt(Blocklocation);
			tID = changeblock.getTypeId();
			tDamage = changeblock.getData();
			//give the player back what is there now.
			//this is susceptible to problems if the structure has been fiddled with since, but can't really
			//be helped...
			ResourceCalculator.AddItemToPlayerInventory(pxdata.pPlayer,tID, tDamage);
			
			
			//revert...
			changeblock.setTypeId(OldID);
			changeblock.setData(OldDamage);
			//now, we change our options to the previous values so this rollback is itself rollback-able...
			OldID = tID;
			OldDamage = tDamage;
			
			
			
		}
		
	}
	private World rollbackWorld;
	//Rollbackdata only contains info on one "draw" operation.
	public World getWorld(){
		return rollbackWorld;
	}
	private ArrayList<RollbackItem> mRollbacks=new ArrayList<RollbackItem>();
	
	/**
	 * adds a rollbackitem to the list. originalblockstate needs to be the original block (before the operation that will be
	 * roll-backable)
	 * @param originalblockstate
	 */
	public void AddRollbackItem(PixArtPlayerData pxdata,Block originalblockstate){
		mRollbacks.add(new RollbackItem(pxdata,originalblockstate));
		rollbackWorld = originalblockstate.getWorld();
		//easy peasy...
		
	}
	
	public void DoRollback(World onworld)
	{
		for(RollbackItem roller:mRollbacks)
		{
			
			roller.PerformRollback(onworld);
			
		}
		
		
	}
	
	
	
	
	
}
