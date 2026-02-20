package org.beeholy.holyCore.commands.admin;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.chat.Colors;
import org.beeholy.holyCore.chat.Gradients;
import org.beeholy.holyCore.chat.Tags;
import org.beeholy.holyCore.items.Vouchers;
import org.beeholy.holyCore.utility.*;
import org.jspecify.annotations.Nullable;

public class ReloadCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        HolyCore.getInstance().reloadConfig();
        Colors.reload();
        Gradients.reload();
        Tags.reload();
        Language.reload();
        Crates.reload();
        Vouchers.reload();
        Scoreboard.reload();
        Quests.reload();

        commandSourceStack.getSender().sendMessage(TextUtils.deserialize(Language.get("config_reload")));
    }

    @Override
    public @Nullable String permission() {
        return "holycore.reload";
    }
}
