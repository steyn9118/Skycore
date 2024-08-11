package lampteam.skycore.managers;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import lampteam.skycore.Skycore;

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

        new CommandAPICommand("skycore")
                .withArguments(new StringArgument("action")
                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(info ->
                                new IStringTooltip[] {
                                        StringTooltip.ofString("join", "")
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
    }

}
