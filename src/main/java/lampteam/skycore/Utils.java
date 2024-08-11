package lampteam.skycore;

public class Utils {

    private static int lastArenaID;

    public static int generateArenaID(){
        lastArenaID += 1;
        return lastArenaID;
    }

    public static String convertSecondsToMinutesAndSeconds(int currentTime){
        int minutes = (int) Math.floor(Float.parseFloat(Integer.toString(currentTime)) / 60);
        int seconds = currentTime % 60;
        return minutes+":"+seconds;
    }

}
