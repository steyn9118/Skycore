package lampteam.skycore.managers;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import me.flame.menus.builders.items.ItemBuilder;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.iterator.MenuIterator;
import me.flame.menus.modifiers.Modifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;

public class MenusManager {

    private static Menu arenaSelector = Menu.create("Выбор арены", 3);
    private static MenuItem itemForPlayingArena = ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).buildItem();
    private static MenuItem itemForNotStartedArena = ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE).buildItem();
    private static MenuItem itemArenaBorders = ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).buildItem();
    private static MenuIterator iterator = arenaSelector.iterator();


    public static void init(){

        arenaSelector.getFiller().fillBorders(itemArenaBorders);

        for (Arena arena : ArenasManager.getArenas()) {
            int membersAmount = arena.getMembers().size();

            //TODO сделать отображения таймера прочностью предмета и кол-во прожитых волн
            if (arena.isGameActive()) {
                itemForPlayingArena.editor()
                        .setName("Арена #" + arena.getId())
                        .setLore("Игроки: " + membersAmount)
                        .done();
                arenaSelector.setItem(arena.getId() + 9, itemForPlayingArena);
            }
            else {
                itemForNotStartedArena.editor()
                        .setName("Арена #" + arena.getId())
                        .setLore("Игроки: " + membersAmount)
                        .done();
                arenaSelector.setItem(arena.getId() + 9, itemForNotStartedArena);
            }
        }

        BukkitRunnable arenaSelectorRunnable = new BukkitRunnable() {
            @Override
            public void run() {

                for (Arena arena : ArenasManager.getArenas()) {
                    int membersAmount = arena.getMembers().size();
                    while (iterator.hasNext()) {
                        if (arena.isGameActive()) {
                            MenuItem item = iterator.next();
                            item.setItemStack(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE));
                            item.editor()
                                    .setName("Арена #" + arena.getId())
                                    .setLore("Игроки" + membersAmount)
                                    .done();
                            item.setClickAction();// нет whoclicked или что-то типо того
                        }
                        else {
                            MenuItem item = iterator.next();
                            item.setItemStack(new ItemStack(Material.LIME_STAINED_GLASS_PANE));
                            item.editor()
                                    .setName("Арена #" + arena.getId())
                                    .setLore("Игроки" + membersAmount)
                                    .done();
                            item.setClickAction(ArenasManager.joinArena(););// нет whoclicked или что-то типо того
                        }
                    }
                }
            }

    };arenaSelectorRunnable.runTaskTimer(Skycore.getPlugin(), 0, 20);

    }

    public static void openArenaSelector(Player player){
        arenaSelector.open(player);
    }

}
