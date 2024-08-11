package lampteam.skycore.managers;

import lampteam.skycore.models.PlayerModel;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerModelsManager {

    private static final List<PlayerModel> playerModels = new ArrayList<>();

    public static PlayerModel getModelOfPlayer(Player player){
        for (PlayerModel model : playerModels){
            if (model.getPlayer().equals(player)) return model;
        }
        return null;
    }

    public static void createPlayerModel(Player player){
        playerModels.add(new PlayerModel(player));
    }

    public static void removePlayerModel(Player player){
        playerModels.removeIf(playerModel -> playerModel.getPlayer().equals(player));
    }
}
