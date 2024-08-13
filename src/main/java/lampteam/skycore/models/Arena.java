package lampteam.skycore.models;

import lampteam.skycore.Skycore;
import lampteam.skycore.Utils;
import lampteam.skycore.models.waves.AWave;
import lampteam.skycore.models.waves.CreepRain;
import lampteam.skycore.models.waves.PotionRain;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {
    private final int id;
    private boolean gameActive = false;
    private final int wavesInterval; // Секунды
    private final String name;
    private final Arena arena;

    private final World world;
    private final Location lobbyLocation;
    private final List<Location> playerSpawnLocations;
    private final Location spectatorsSpawnPoint;
    private final BoundingBox borders;
    private final Location hubLocation;

    private final Skycore plugin = Skycore.getPlugin();
    private final BossBar timerBar = BossBar.bossBar(Component.text("00:00"), 0f, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_20);

    private final List<PlayerModel> members = new ArrayList<>();
    private final List<PlayerModel> spectators = new ArrayList<>();
    private final List<PlayerModel> players = new ArrayList<>();

    private final List<AWave> wavesPool = new ArrayList<>();
    private final List<AWave> waves = new ArrayList<>();

    private BukkitRunnable waveTimer;
    private BukkitRunnable mainTimer;

    private final HashMap<PlayerModel, Location> spectatorsSafeLocations = new HashMap<>();

    //геттеры
    public int getId(){
        return id;
    }
    public boolean isGameActive(){
        return gameActive;
    }
    public int getWavesInterval(){
        return wavesInterval;
    }
    public List<PlayerModel> getMembers() {
        return members;
    }
    public BoundingBox getBorders(){
        return borders;
    }
    public World getWorld(){
        return world;
    }

    public List<PlayerModel> getPlayers(){
        return players;
    }

    public Arena(
            int id,
            int wavesInterval,
            String name,
            World world,
            Location lobbyLocation,
            Location spectatorsSpawnPoint,
            List<Location> playerSpawnLocations,
            BoundingBox borders,
            Location hubLocation

    ){
        arena = this;

        this.id = id;
        this.wavesInterval = wavesInterval;
        this.name = name;
        this.world = world;
        this.lobbyLocation = lobbyLocation;
        this.spectatorsSpawnPoint = spectatorsSpawnPoint;
        this.playerSpawnLocations = playerSpawnLocations;
        this.borders = borders;
        this.hubLocation = hubLocation;

        wavesPool.add(new PotionRain());
        wavesPool.add(new CreepRain());
    }

    public void startGame(){
        gameActive = true;

        mainTimer = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                time++;

                setTimerDisplay(time);

                for (PlayerModel model : players){
                    model.incrementAliveTime(1);
                }

                for (PlayerModel model : spectators){
                    Player player = model.getPlayer();
                    if (borders.contains(player.getLocation().toVector())){
                        spectatorsSafeLocations.put(model, player.getLocation());
                    } else {
                        Location safeLocation = spectatorsSafeLocations.get(model);
                        if (safeLocation != null) player.teleport(safeLocation);
                        else player.teleport(spectatorsSpawnPoint);
                    }
                }
            }
        };
        mainTimer.runTaskTimer(plugin, 0, 20);

        waveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                startNewWave();
            }
        };
        waveTimer.runTaskTimer(plugin, wavesInterval * 20L, wavesInterval * 20L);



        int spawnCount = 0;
        for (PlayerModel model : players){
            model.resetAliveTime();

            model.getPlayer().teleport(playerSpawnLocations.get(spawnCount));
            spawnCount++;
        }

        for (PlayerModel model : members){
            model.getPlayer().showBossBar(timerBar);
        }
    }

    private void endGame(){
        mainTimer.cancel();
        waveTimer.cancel();

        players.clear();
        spectators.clear();
        members.clear();

        spectatorsSafeLocations.clear();

        // TODO ресет карты

        gameActive = false;
    }

    public void forceStop(){

    }

    private void startNewWave(){
        AWave currentWave = waves.get(0);
        waves.remove(0);
        currentWave.startWave(arena);
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
        Player player = model.getPlayer();
        player.teleport(spectatorsSpawnPoint);
        player.sendMessage(Component.text("Вы присоединились к арене как игрок"));
    }

    private void joinAsPlayer(PlayerModel model){
        players.add(model);
        Player player = model.getPlayer();
        player.sendMessage(Component.text("Вы присоединились к арене как наблюдатель"));
        player.teleport(lobbyLocation);
    }

    public void tryLeavePlayer(PlayerModel model){
        if (model == null) return;
        Player player = model.getPlayer();

        if (players.contains(model)){
            // Для игроков

            players.remove(model);
        }
        else {
            // Для наблюдателей

            spectators.remove(model);
        }
        members.remove(model);

        player.teleport(hubLocation);
    }

    public void playerDied(PlayerModel model){
        Player player = model.getPlayer();
        player.sendMessage(Component.text("Вы умерли!").color(NamedTextColor.RED));
        player.sendMessage(Component.text("Время жизни: " + Utils.convertSecondsToMinutesAndSeconds(model.getAliveTime())).color(NamedTextColor.WHITE));

        players.remove(model);
        joinAsSpectator(model);
    }




}
