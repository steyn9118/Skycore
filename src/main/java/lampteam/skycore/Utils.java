package lampteam.skycore;

public class Utils {

    public static String convertSecondsToMinutesAndSeconds(int currentTime){
        int minutes = (int) Math.floor(Float.parseFloat(Integer.toString(currentTime)) / 60);
        int seconds = currentTime % 60;
        return minutes+":"+seconds;
    }

}
