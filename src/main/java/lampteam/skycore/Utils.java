package lampteam.skycore;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String convertSecondsToMinutesAndSeconds(int currentTime){
        int minutes = (int) Math.floor(Float.parseFloat(Integer.toString(currentTime)) / 60);
        int seconds = currentTime % 60;
        return minutes+":"+seconds;
    }

    // COPIUM
    public static final class Converter {
        public static String LocToStr(Location location, boolean saveDetails){
            StringBuilder stringBuilder = new StringBuilder();
            if (saveDetails){
                stringBuilder
                        .append(location.getWorld().getName())
                        .append(";")
                        .append(location.x())
                        .append(";")
                        .append(location.y())
                        .append(";")
                        .append(location.z())
                        .append(";")
                        .append(location.getPitch())
                        .append(";")
                        .append(location.getYaw());
            }
            else {
                stringBuilder
                        .append(location.getWorld().getName())
                        .append(";")
                        .append(location.getBlockX())
                        .append(";")
                        .append(location.getBlockY())
                        .append(";")
                        .append(location.getBlockZ())
                        .append(";");
            }
            return stringBuilder.toString();
        }

        public static Location LocFromStr(String string) throws Exception {
            String[] args = string.split(";");
            if (args.length == 4){
                return new Location(
                        Bukkit.getWorld(args[0]),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3])
                );
            }
            else if (args.length == 6){
                return new Location(
                        Bukkit.getWorld(args[0]),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3]),
                        Float.parseFloat(args[4]),
                        Float.parseFloat(args[5])
                );
            }
            else {
                throw new Exception("Неверное количество параметров при конвертации локации из строки! Получено " + args.length + ", допустимые значения: 4, 6");
            }
        }

        public static List<String> LocsListToStrsList(List<Location> locations, boolean saveDetails){
            List<String> strings = new ArrayList<>();
            for (Location location : locations){
                strings.add(LocToStr(location, saveDetails));
            }
            return strings;
        }

        public static List<Location> LocsListFromStrsList(List<String> strings) throws Exception {
            List<Location> locations = new ArrayList<>();
            for (String string : strings){
                locations.add(LocFromStr(string));
            }
            return locations;
        }
    }
}
