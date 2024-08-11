package lampteam.skycore.models;

import lampteam.skycore.Skycore;
import lampteam.skycore.Utils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {
    private final int id;

    private Location lobbyLocation;
    private HashMap<Location, PlayerModel> playerSpawnLocations;
    private Location spectatorsSpawnPoint;

    private boolean gameActive = false;
    private int wavesInterval = 120; // Секунды
    private final Skycore plugin = Skycore.getPlugin();
    private final BossBar timerBar = BossBar.bossBar(Component.text("00:00"), 0f, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_20);

    private final List<PlayerModel> members = new ArrayList<>();
    private final List<PlayerModel> spectators = new ArrayList<>();
    private final List<PlayerModel> players = new ArrayList<>();

    private final HashMap<PlayerModel, Integer> playersAliveTime = new HashMap<PlayerModel, Integer>();

    public boolean isGameActive(){
        return gameActive;
    }
    public int getId(){
        return id;
    }

    public Arena(int id, int wavesInterval){
        this.id = id;
        this.wavesInterval = wavesInterval;
    }

    public void startGame(){
        gameActive = true;

        for (PlayerModel model : players){
            playersAliveTime.put(model, 0);
        }

        BukkitRunnable mainTimer = new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {
                time++;
                if (time % wavesInterval == 0){
                    startNewWave(time);
                }

                setTimerDisplay(time);

                for (PlayerModel model : players){
                    playersAliveTime.put(model, playersAliveTime.get(model)+1);
                }
            }
        };
        mainTimer.runTaskTimer(plugin, 0, 20);

        for (PlayerModel model : members){
            model.getPlayer().showBossBar(timerBar);
        }
    }

    private void endGame(){
        playersAliveTime.clear();
        players.clear();
        spectators.clear();
        members.clear();

        playerSpawnLocations.replaceAll((l, v) -> null);

        // TODO ресет карты

        gameActive = false;
    }

    public void forceStop(){


    }

    private void startNewWave(int currentTime){

    }

    private void setTimerDisplay(int currentTime){
        timerBar.name(Component.text(Utils.convertSecondsToMinutesAndSeconds(currentTime)).color(NamedTextColor.WHITE));
        timerBar.progress(1 - ((Float.parseFloat(Integer.toString(currentTime)) % 120) / 120));
    }

    public void tryJoinPlayer(PlayerModel model){
        members.add(model);

        if (gameActive || players.size() == playerSpawnLocations.size()) {
            joinAsSpectator(model);
            return;
        }

        joinAsPlayer(model);
    }

    private void joinAsSpectator(PlayerModel model){
        spectators.add(model);
        model.getPlayer().teleport(spectatorsSpawnPoint);
        model.getPlayer().sendMessage(Component.text("Вы присоединились к арене как игрок"));
    }

    private void joinAsPlayer(PlayerModel model){
        players.add(model);
        spawnPlayer(model);
        model.getPlayer().sendMessage(Component.text("Вы присоединились к арене как наблюдатель"));
    }

    public void tryLeavePlayer(PlayerModel model){
        if (model == null) return;

        if (players.contains(model)){
            // Для игроков

            players.remove(model);
        }
        else {
            // Для наблюдателей

            spectators.remove(model);
        }
        members.remove(model);
    }

    private void spawnPlayer(PlayerModel model){
        for (Location location : playerSpawnLocations.keySet()){
            if (playerSpawnLocations.get(location) == null){
                model.getPlayer().teleport(location);
                playerSpawnLocations.put(location, model);
                return;
            }
        }
        plugin.getLogger().severe("Невозможно заспавнить игрока!");
    }

    public void playerDied(PlayerModel model){
        Player player = model.getPlayer();
        player.sendMessage(Component.text("Вы умерли!").color(NamedTextColor.RED));
        player.sendMessage(Component.text("Время жизни: " + Utils.convertSecondsToMinutesAndSeconds(playersAliveTime.get(model))).color(NamedTextColor.WHITE));

        players.remove(model);
        joinAsSpectator(model);
    }




}
