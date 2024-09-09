package lampteam.skycore.models;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public abstract class BetterRunnable extends BukkitRunnable {

    protected boolean isRunning;

    public boolean isRunning(){
        return isRunning;
    }

    @Override
    public void cancel(){
        if (!isRunning) return;
        isRunning = false;
        super.cancel();
    }

    @Override
    public @NotNull BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period){
        isRunning = true;
        return super.runTaskTimer(plugin, delay, period);
    }

    @Override
    public @NotNull BukkitTask runTaskLater(@NotNull Plugin plugin, long delay){
        isRunning = true;
        return super.runTaskLater(plugin, delay);
    }

    @Override
    public @NotNull BukkitTask runTask(@NotNull Plugin plugin){
        isRunning = true;
        return super.runTask(plugin);
    }

    @Override
    public @NotNull BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period){
        isRunning = true;
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }

    @Override
    public @NotNull BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay){
        isRunning = true;
        return super.runTaskLaterAsynchronously(plugin, delay);
    }

    @Override
    public @NotNull BukkitTask runTaskAsynchronously(@NotNull Plugin plugin){
        isRunning = true;
        return super.runTaskAsynchronously(plugin);
    }
}
