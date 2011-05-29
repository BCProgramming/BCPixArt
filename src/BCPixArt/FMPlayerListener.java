package BCPixArt;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
public class FMPlayerListener extends PlayerListener  {

	private final PixArtPlugin owner;
	private config configuration;
	
	
	public void setConfig(config assignto)
	{
	configuration = assignto;	
		
	}
	
	public FMPlayerListener(PixArtPlugin powner){
		owner=powner;
	}
	
	
	
	
}
