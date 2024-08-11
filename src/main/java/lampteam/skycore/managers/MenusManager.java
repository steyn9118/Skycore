package lampteam.skycore.managers;

import me.flame.menus.menu.Menu;
import me.flame.menus.modifiers.Modifier;
import org.bukkit.entity.Player;

import java.util.EnumSet;

public class MenusManager {

    private static Menu arenaSelector = Menu.create("Выбор арены", 3);

    public static void init(){

    }

    public static void openArenaSelector(Player player){
        arenaSelector.open(player);
    }

}
