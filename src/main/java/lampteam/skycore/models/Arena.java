package lampteam.skycore.models;

import lampteam.skycore.Skycore;
import lampteam.skycore.Utils;
import lampteam.skycore.managers.WavesManager;
import lampteam.skycore.models.waves.AWave;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Arena {
    private final int id;
    private boolean gameActive = false;
    private boolean lobbyTimerActive = false;
    private final int wavesInterval; // Секунды
    private final String name;
    private final Arena arena;
    private final World world;
    private final Location lobbyLocation;
    private final List<Location> playerSpawnLocations;
    private final Location spectatorsSpawnPoint;
    private final Location centerCore;
    private final BoundingBox borders;
    private final Location hubLocation;
    private final int lobbyTimerDuration;
    private final int minPlayers;

    private final Skycore plugin = Skycore.getPlugin();
    private final BossBar wavesTimerBar = BossBar.bossBar(Component.text("00:00"), 0f, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_20);
    private final BossBar lobbyTimerBar = BossBar.bossBar(Component.text(""), 1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);

    private final List<PlayerModel> members = new ArrayList<>();
    private final List<PlayerModel> spectators = new ArrayList<>();
    private final List<PlayerModel> players = new ArrayList<>();

    private final List<AWave> wavesCollection;
    private final List<AWave> wavesPool = new ArrayList<>();
    private final List<AWave> activeWaves = new ArrayList<>();

    private final BukkitRunnable waveTimer;
    private final BukkitRunnable mainTimer;
    private final BukkitRunnable asyncWavesPoolCreator;
    private final BukkitRunnable lobbyTimer;

    private final Random random = new Random();

    //геттеры
    public List<PlayerModel> getSpectators() {
        return spectators;
    }
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
    public Location getCenterCore(){
        return centerCore;
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
            Location centerCore,
            BoundingBox borders,
            Location hubLocation,
            int lobbyTimerDuration, int minPlayers
    ){
        arena = this;

        this.id = id;
        this.wavesInterval = wavesInterval;
        this.name = name;
        this.world = world;
        this.lobbyLocation = lobbyLocation;
        this.spectatorsSpawnPoint = spectatorsSpawnPoint;
        this.playerSpawnLocations = playerSpawnLocations;
        this.centerCore = centerCore;
        this.borders = borders;
        this.hubLocation = hubLocation;
        this.lobbyTimerDuration = lobbyTimerDuration;
        this.minPlayers = minPlayers;

        wavesCollection = WavesManager.getAllWaves();

        mainTimer = new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {
                if (players.isEmpty()) endGame();

                time++;

                setWavesTimerDisplay(time);

                for (PlayerModel model : players){
                    model.incrementAliveTime(1);
                }

                for (PlayerModel model : spectators){
                    Player player = model.getPlayer();
                    if (borders.contains(player.getLocation().toVector())){
                        model.setLastSafeLocation(player.getLocation());
                    } else {
                        Location safeLocation = model.getLastSafeLocation();
                        if (safeLocation != null) player.teleport(safeLocation);
                        else player.teleport(spectatorsSpawnPoint);
                    }
                }
            }
        };

        waveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                startNewWave();
            }
        };

        lobbyTimer = new BukkitRunnable() {
            int counter = lobbyTimerDuration;
            @Override
            public void run() {
                if (players.size() < minPlayers) this.cancel();

                if (counter == 0) {
                    startGame();
                    lobbyTimerActive = false;
                }

                setLobbyTimerDisplay(counter);
                counter--;
            }
        };

        asyncWavesPoolCreator = new BukkitRunnable() {
            @Override
            public void run() {
                wavesPool.clear();

                List<AWave> tempCollection = new ArrayList<>();
                Collections.copy(tempCollection, wavesCollection);

                int counter = 1;
                while (true){
                    int chance = random.nextInt(0, 101);

                    int diff;

                    if (chance < 50) diff = 0;
                    else if (chance < 75) diff = -1;
                    else diff = 1;

                    if (counter + diff != 0) counter += diff;

                    int finalCounter = counter;

                    List<AWave> wavesCandidates = tempCollection.stream().filter(wave -> wave.getWeight() == finalCounter).toList();
                    if (wavesCandidates.isEmpty()) wavesCandidates = tempCollection.stream().filter(wave -> wave.getWeight() == finalCounter - 1).toList();
                    if (wavesCandidates.isEmpty()) wavesCandidates = tempCollection.stream().filter(wave -> wave.getWeight() == finalCounter + 1).toList();
                    if (wavesCandidates.isEmpty()) break;

                    AWave selectedWave = wavesCandidates.get(random.nextInt(0, wavesCandidates.size()));
                    tempCollection.remove(selectedWave);
                    wavesPool.add(selectedWave);
                    counter++;
                }
            }
        };
    }

    public void startGame(){
        gameActive = true;

        mainTimer.runTaskTimer(plugin, 0, 20);
        waveTimer.runTaskTimer(plugin, wavesInterval * 20L, wavesInterval * 20L);

        int spawnCount = 0;
        for (PlayerModel model : players){
            model.getPlayer().setGameMode(GameMode.SURVIVAL);

            model.getPlayer().teleport(playerSpawnLocations.get(spawnCount));
            spawnCount++;
        }

        for (PlayerModel model : members){
            model.getPlayer().hideBossBar(lobbyTimerBar);
            model.getPlayer().showBossBar(wavesTimerBar);
        }
    }

    private void endGame(){
        mainTimer.cancel();
        waveTimer.cancel();
        lobbyTimer.cancel();

        players.clear();
        spectators.clear();
        members.clear();

        for (AWave wave : activeWaves) wave.stopWave();
        activeWaves.clear();

        // TODO ресет карты

        gameActive = false;
        lobbyTimerActive = false;
        createWavesPool();
    }

    public void forceStop(){
        // всё то же что и в tryLeavePlayer но без удаления из списков
        for (PlayerModel model : members){
            Player player = model.getPlayer();

            if (players.contains(model)){
                players.remove(model);
            }
            else {
                spectators.remove(model);
            }

            clearPlayer(model);

            player.teleport(hubLocation);
            player.sendMessage(Component.text("Игра была остановлена"));
        }

        endGame();
    }

    private void startNewWave(){
        AWave currentWave = wavesPool.getFirst();
        wavesPool.removeFirst();
        activeWaves.add(currentWave);
        currentWave.startWave(arena);

        for (PlayerModel playerModel : players) playerModel.survivedWave();
    }

    public void forceStartWave(AWave wave){
        wave.startWave(this);
        plugin.getLogger().info("Волна была вызвана на арене " + arena.id);
    }

    private void setWavesTimerDisplay(int currentTime){
        wavesTimerBar.name(Component.text(Utils.convertSecondsToMinutesAndSeconds(currentTime)).color(NamedTextColor.WHITE));
        wavesTimerBar.progress(1 - ((Float.parseFloat(Integer.toString(currentTime)) % 120) / 120));
    }

    private void setLobbyTimerDisplay(int currentTime){
        lobbyTimerBar.name(Component.text("До начала игры: " + currentTime + " секунд").color(NamedTextColor.WHITE));
        wavesTimerBar.progress((Float.parseFloat(Integer.toString(currentTime)) / lobbyTimerDuration));
    }

    public void tryJoinPlayer(PlayerModel model){
        if (model.getCurrentArena() != null) return;

        members.add(model);
        model.setCurrentArena(this);
        if (gameActive) model.getPlayer().showBossBar(wavesTimerBar);
        else model.getPlayer().showBossBar(lobbyTimerBar);

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
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(Component.text("Вы присоединились к арене как игрок"));
        player.getInventory().setItem(0, new ItemStack(Material.RED_BANNER));
        player.getInventory().setItem(4, new ItemStack(Material.IRON_DOOR));
        player.getInventory().setItem(8, new ItemStack(Material.GREEN_BANNER));
    }

    private void joinAsPlayer(PlayerModel model){
        players.add(model);
        Player player = model.getPlayer();
        player.sendMessage(Component.text("Вы присоединились к арене как наблюдатель"));
        player.teleport(lobbyLocation);

        tryStartLobbyTimer();
    }

    public void tryLeavePlayer(PlayerModel model){
        if (model == null) return;
        Player player = model.getPlayer();

        if (players.contains(model)){
            players.remove(model);
        }
        else {
            spectators.remove(model);
        }

        members.remove(model);
        clearPlayer(model);

        player.hideBossBar(lobbyTimerBar);
        player.hideBossBar(wavesTimerBar);
        player.teleport(hubLocation);
        player.sendMessage(Component.text("Вы покинули арену"));
    }

    public void playerDied(PlayerModel model){
        Player player = model.getPlayer();
        player.sendMessage(Component.text("Вы умерли!").color(NamedTextColor.RED));
        player.sendMessage(Component.text("Время жизни: " + Utils.convertSecondsToMinutesAndSeconds(model.getAliveTime())).color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("Пережито волн: " + model.getWavesSurvived()));
        if (players.size() != 1) player.sendMessage(Component.text("Осталось игроков: " + (players.size() - 1)));

        players.remove(model);
        joinAsSpectator(model);
    }

    public void spectatePlayer(PlayerModel model, LinearDirection direction){
        int spectateOnID = model.getLastViewedPlayerID() + direction.getValue();
        if (spectateOnID >= players.size()) spectateOnID = 0;
        if (spectateOnID < 0) spectateOnID = players.size() - 1;
        model.setLastViewedPlayerID(spectateOnID);

        Player player = model.getPlayer();
        Player spectatedPlayer = players.get(spectateOnID).getPlayer();
        player.teleport(spectatedPlayer.getLocation());
        player.sendActionBar(Component.text("Сейчас наблюдаете за игроком: " + spectatedPlayer.getName()).color(NamedTextColor.YELLOW));
    }

    private void clearPlayer(PlayerModel model){
        Player player = model.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getActivePotionEffects().clear();

        model.resetData();
    }

    private void createWavesPool(){
        asyncWavesPoolCreator.runTaskAsynchronously(plugin);
    }

    private void tryStartLobbyTimer(){
        if (!lobbyTimerActive) {
            lobbyTimer.runTaskTimer(plugin, 0, 20);
            lobbyTimerActive = true;
        }
    }
}
