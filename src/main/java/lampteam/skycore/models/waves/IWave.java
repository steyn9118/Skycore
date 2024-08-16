package lampteam.skycore.models.waves;

import lampteam.skycore.models.Arena;

public interface IWave {

    double getWeight();
    //void loadProperties();
    void startWave(Arena arena);
    //void stopWave();

}
