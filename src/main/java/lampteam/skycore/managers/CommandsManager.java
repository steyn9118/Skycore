package lampteam.skycore.managers;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.PlayerModel;
import lampteam.skycore.models.waves.AWave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class CommandsManager {

    public static void init(){

        new CommandAPICommand("skycoreadmin")
                .withArguments(new StringArgument("action")
                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(info ->
                                new IStringTooltip[] {
                                        StringTooltip.ofString("reload", "Перезагружает плагин")
                                }
                        )))
                .withPermission(CommandPermission.OP)
                .executes((sender, args) -> {
                    String action = (String) args.get("action");
                    assert action != null;
                    if (action.equals("reload")){
                        Skycore.reloadPlugin();
                        return;
                    }

                })
                .register();

        new CommandAPICommand("skycoredebug")
                .withArguments(new StringArgument("action")
                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(info ->
                                new IStringTooltip[] {
                                        StringTooltip.ofString("forcewave", "Вызывает волну"),
                                        StringTooltip.ofString("forcestop", "Останавливает игру на арене")
                                }
                        )))
                .withArguments(new IntegerArgument("arenaid")
                        .replaceSuggestions(ArgumentSuggestions.strings(
                                ArenasManager.getAllIDs()
                        )))
                .withOptionalArguments(new StringArgument("waveName")
                        .replaceSuggestions(ArgumentSuggestions.strings(WavesManager.getWavesNames())))
                .withPermission(CommandPermission.OP)
                .executes((sender, args) -> {
                    String action = (String) args.get("action");
                    assert action != null;
                    Arena arena = ArenasManager.getArenaByID((Integer) args.get("arenaid"));
                    if (arena == null){
                        sender.sendMessage(Component.text("Арена с таким ID не найдена!").color(NamedTextColor.RED));
                        return;
                    }

                    switch (action){
                        case "forcestop" -> arena.forceStop();
                        case "forcewave" -> {
                            AWave wave = WavesManager.getWaveByName((String) args.get("waveName"));
                            if (wave == null){
                                sender.sendMessage(Component.text("Волна с таким именем не найдена!").color(NamedTextColor.RED));
                                return;
                            }
                            arena.forceStartWave(wave);
                        }
                    }
                })
                .register();

        new CommandAPICommand("skycore")
                .withArguments(new StringArgument("action")
                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(info ->
                                new IStringTooltip[] {
                                        StringTooltip.ofString("join", "")
                                }
                        )))
                .withArguments(new IntegerArgument("arenaid")
                        .replaceSuggestions(ArgumentSuggestions.strings(
                                ArenasManager.getAllIDs()
                        )))
                .executes((sender, args) -> {
                    String action = (String) args.get("action");
                    assert action != null;
                    Arena arena = ArenasManager.getArenaByID((Integer) args.get("arenaid"));
                    assert arena != null;
                    if (action.equals("join")){
                        if (sender instanceof Player){
                            PlayerModel model = PlayerModelsManager.getModelOfPlayer((Player) sender);
                            assert model != null;
                            arena.tryJoinPlayer(model);
                        }
                        return;
                    }

                })
                .register();
    }

}
