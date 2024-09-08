package lampteam.skycore.models;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import lampteam.skycore.Skycore;
import lampteam.skycore.Utils;
import lampteam.skycore.managers.WavesManager;
import lampteam.skycore.models.waves.AWave;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Arena {
    private final int id;
    private final int wavesInterval; // Секунды
    private final String name;
    private final String arenaSchematicName;
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

    private boolean gameActive = false;
    private boolean lobbyTimerActive = false;

    private final Skycore plugin = Skycore.getPlugin();
    private final BossBar wavesTimerBar = BossBar.bossBar(Component.text("00:00"), 0f, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_6);
    private final BossBar lobbyTimerBar = BossBar.bossBar(Component.text("Ожидаем игроков..."), 1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);

    private final List<PlayerModel> members = new ArrayList<>();
    private final List<PlayerModel> spectators = new ArrayList<>();
    private final List<PlayerModel> players = new ArrayList<>();

    private final Collection<AWave> wavesCollection;
    private final List<AWave> wavesQueue = new ArrayList<>();
    private final List<AWave> activeWaves = new ArrayList<>();

    private BukkitRunnable waveTimer;
    private BukkitRunnable mainTimer;
    private BukkitRunnable lobbyTimer;

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
            int lobbyTimerDuration,
            int minPlayers,
            String arenaSchematicName
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
        this.arenaSchematicName = arenaSchematicName;

        wavesCollection = WavesManager.getAllWaves();

        createWavesQueue();
    }

    private void initWavesTimer(){
        waveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                startNewWave();
            }
        };
    }

    private void initLobbyTimer(){
        lobbyTimer = new BukkitRunnable() {
            int counter = lobbyTimerDuration;
            @Override
            public void run() {
                if (players.size() < minPlayers) {
                    this.cancel();
                    lobbyTimerActive = false;
                    resetLobbyTimerDisplay();
                    return;
                }

                if (counter == 0) {
                    this.cancel();
                    startGame();
                    lobbyTimerActive = false;
                    resetLobbyTimerDisplay();
                    return;
                }

                setLobbyTimerDisplay(counter);
                counter--;
            }
        };
    }

    private void initMainTimer(){
        mainTimer = new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {
                if (players.isEmpty()){
                    endGame(10);
                    this.cancel();
                }

                time++;

                setWavesTimerDisplay(time);

                for (PlayerModel model : players){
                    model.incrementAliveTime(1);

                    Player player = model.getPlayer();
                    if (borders.getMaxY() >= player.getLocation().getBlockY()){
                        model.setLastSafeLocation(player.getLocation());
                    } else {
                        Location safeLocation = model.getLastSafeLocation();
                        if (safeLocation != null) player.teleport(safeLocation);
                        else player.teleport(spectatorsSpawnPoint);
                    }
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
    }

    public void startGame(){
        gameActive = true;

        initMainTimer();
        initWavesTimer();
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

    private void endGame(int delaySeconds){
        try {
            mainTimer.cancel();
            waveTimer.cancel();
            lobbyTimer.cancel();
        } catch (IllegalStateException ignored){}

        for (AWave wave : activeWaves) wave.stopWave();
        activeWaves.clear();

        BukkitRunnable delayedEndGame = new BukkitRunnable() {
            @Override
            public void run() {
                for (PlayerModel model : spectators){
                    Player player = model.getPlayer();
                    clearPlayer(model);

                    player.hideBossBar(lobbyTimerBar);
                    player.hideBossBar(wavesTimerBar);
                    player.teleport(hubLocation);
                    player.sendMessage(Component.text("Вы покинули арену"));
                }

                players.clear();
                spectators.clear();
                members.clear();

                // ресет карты
                BukkitRunnable mapReset = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Clipboard clipboard;
                        File file = new File(plugin.getDataFolder().getPath() + "/Schematics/" + arenaSchematicName);

                        ClipboardFormat format = ClipboardFormats.findByFile(file);
                        assert format != null;
                        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                            clipboard = reader.read();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                            Operation operation = new ClipboardHolder(clipboard)
                                    .createPaste(editSession)
                                    .to(BlockVector3.at(centerCore.getBlockX(), centerCore.getBlockY(), centerCore.getBlockZ()))
                                    .build();
                            Operations.complete(operation);
                        }
                    }
                };
                mapReset.runTaskAsynchronously(plugin);

                lobbyTimerActive = false;
                resetLobbyTimerDisplay();
                createWavesQueue();
                Collections.shuffle(playerSpawnLocations);
                gameActive = false;
            }
        };
        delayedEndGame.runTaskLater(plugin, delaySeconds * 20L);
    }

    public void forceStop(){
        if (!gameActive) return;
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

        endGame(0);
        plugin.getLogger().info("Остановка арены " + arena.id);
    }

    private void startNewWave(){
        if (wavesQueue.isEmpty()) {
            wavesCollection.stream().toList().get(random.nextInt(0, wavesCollection.size())).startWave(this);
        } else {
            AWave currentWave = wavesQueue.getFirst();
            wavesQueue.removeFirst();
            activeWaves.add(currentWave);
            currentWave.startWave(arena);
        }

        for (PlayerModel playerModel : players) playerModel.survivedWave();

        for (PlayerModel model : members) model.getPlayer().showTitle(Title.title(Component.text("Началась новая волна!").color(NamedTextColor.RED), Component.text("")));
    }

    public void forceStartWave(AWave wave){
        wave.startWave(this);
        activeWaves.add(wave);
        plugin.getLogger().info("Волна была вызвана на арене " + arena.id);
    }

    private void setWavesTimerDisplay(int currentTime){
        wavesTimerBar.name(Component.text("Следующая волна через " + Utils.convertSecondsToMinutesAndSeconds(wavesInterval - currentTime % wavesInterval)).color(NamedTextColor.WHITE));
        wavesTimerBar.progress(1 - ((Float.parseFloat(Integer.toString(currentTime)) % wavesInterval) / wavesInterval)  );
    }

    private void setLobbyTimerDisplay(int currentTime){
        lobbyTimerBar.name(Component.text("До начала игры: " + currentTime + " секунд").color(NamedTextColor.WHITE));
        lobbyTimerBar.progress((Float.parseFloat(Integer.toString(currentTime)) / lobbyTimerDuration));
    }

    private void resetLobbyTimerDisplay(){
        lobbyTimerBar.name(Component.text("Ожидаем игроков...").color(NamedTextColor.WHITE));
        lobbyTimerBar.progress(1f);
    }

    public void tryJoinPlayer(PlayerModel model){
        if (model.getCurrentArena() != null) return;

        members.add(model);
        model.setCurrentArena(this);
        if (gameActive) model.getPlayer().showBossBar(wavesTimerBar);
        else model.getPlayer().showBossBar(lobbyTimerBar);

        if (gameActive || players.size() == playerSpawnLocations.size()) {
            joinAsSpectator(model, false);
            return;
        }

        joinAsPlayer(model);
    }

    private void joinAsSpectator(PlayerModel model, boolean afterDeath){
        spectators.add(model);
        Player player = model.getPlayer();
        if (!afterDeath) player.teleport(spectatorsSpawnPoint);
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(Component.text("Вы присоединились к арене как наблюдатель"));
        player.getInventory().setItem(0, new ItemStack(Material.RED_BANNER));
        player.getInventory().setItem(4, new ItemStack(Material.IRON_DOOR));
        player.getInventory().setItem(8, new ItemStack(Material.GREEN_BANNER));
    }

    private void joinAsPlayer(PlayerModel model){
        players.add(model);
        Player player = model.getPlayer();
        player.sendMessage(Component.text("Вы присоединились к арене как игрок"));
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
        joinAsSpectator(model, true);
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
        player.clearActivePotionEffects();
        player.setLevel(0);
        player.setExp(0f);
        player.hideBossBar(lobbyTimerBar);
        player.hideBossBar(wavesTimerBar);

        model.resetData();
    }

    private void createWavesQueue(){
        wavesQueue.clear();

        List<AWave> tempCollection = new ArrayList<>(wavesCollection);

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
            wavesQueue.add(selectedWave);
        }
    }

    private void tryStartLobbyTimer(){
        if (!lobbyTimerActive && players.size() >= minPlayers) {
            initLobbyTimer();
            lobbyTimer.runTaskTimer(plugin, 0, 20);
            lobbyTimerActive = true;
        }
    }

    public void debugValues(Player player){
        player.sendMessage("GameActive " + gameActive);
        player.sendMessage("LobbyTimerActive " + lobbyTimerActive);
        player.sendMessage("Members " + Arrays.toString(members.toArray()));
        player.sendMessage("Players " + Arrays.toString(players.toArray()));
        player.sendMessage("Spectators " + Arrays.toString(spectators.toArray()));
        player.sendMessage("WavesQueue: " + Arrays.toString(wavesQueue.toArray()));
    }
}
