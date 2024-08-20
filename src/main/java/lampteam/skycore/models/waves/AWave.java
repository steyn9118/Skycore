package lampteam.skycore.models.waves;

public abstract class AWave implements IWave {

    protected static int weight;
    protected static String name;

    public int getWeight(){
        return weight;
    }

    public String getName(){
        return name;
    }

}
