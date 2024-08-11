package lampteam.skycore.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholdersManager extends PlaceholderExpansion {

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "noplayer";

        /*
        String courseId = null;
        for (Arena course : Utils.getAllCourses()){
            if (params.contains(course.getId())){
                courseId = course.getId();
                break;
            }
        }
        if (courseId == null) return "Курс не найден";

        PlayerStat stat = StatsManager.getPlayerStatOnCourse(player.getName(), courseId);

        if (params.contains("seconds")) return String.valueOf(stat.getSeconds());
        else if (params.contains("attempts")) return String.valueOf(stat.getAttempts());
        */

        return "";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "skycore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Steyn91";
    }

    @Override
    public @NotNull String getVersion() {
        return "1";
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public boolean persist(){
        return true;
    }
}
