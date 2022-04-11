package igentuman.immersivenuclear.common.util.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandUtils{
	static void sendHelp(CommandSource commandSource, String subIdent){
		commandSource.sendFeedback(new TranslationTextComponent("chat.immersivenuclear.command.reservoir" + subIdent + ".help"), true);
	}
}
