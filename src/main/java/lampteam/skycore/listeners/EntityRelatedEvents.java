package lampteam.skycore.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;

public class EntityRelatedEvents implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){

    }

    @EventHandler
    public void onPotionHit(PotionSplashEvent e){
        if (e.getPotion().getItem().getType().equals(Material.LINGERING_POTION)){


            Location location = e.getPotion().getLocation();
            World world = location.getWorld();
            int x = (int) location.getX();
            int y = (int) location.getX();
            int z = (int) location.getX();

            world.getBlockAt(x,y-1,z).setType(Material.AIR);
            world.getBlockAt(x+1,y-1,z).setType(Material.AIR);
            world.getBlockAt(x-1,y-1,z).setType(Material.AIR);
            world.getBlockAt(x,y,z).setType(Material.AIR);
            world.getBlockAt(x,y-2,z).setType(Material.AIR);
            world.getBlockAt(x,y-1,z+1).setType(Material.AIR);
            world.getBlockAt(x,y-1,z-1).setType(Material.AIR);
        }
    }
}
