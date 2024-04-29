package commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.IOException;

public class ExecuteCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        User user = event.getUser();

        if (command.equals("cmd")) {
            if (hasUserPermission(user)) {
                OptionMapping cmdOption = event.getOption("command");
                String cmd = cmdOption.getAsString();

                String screenName = "forge_server"; // Der Name des Screens

                // Erstellt eine Instanz vom ProcessBuilder mit dem Befehl den ich an den Server senden will
                // Dieser Command schreibt in der Minecraft Konsole den Befehl, den ich mit cmd übergebe.
                ProcessBuilder processBuilder = new ProcessBuilder("screen", "-S", screenName, "-X", "stuff", cmd + "\n");
                processBuilder.redirectErrorStream(true);

                try {
                    Process process = processBuilder.start(); // schreibt genau hier in der Konsole
                    process.waitFor(); // wartet bis erfolgreich

                    System.out.println("Befehl erfolgreich an den Screen gesendet: " + cmd);
                    event.reply("Der Befehl wurde an den Server gesendet.\nhttps://tenor.com/view/goofy-gif-27367825").setEphemeral(true).queue();
                } catch (IOException | InterruptedException e) { // falls waitFor() nicht erfolgreich war:
                    e.printStackTrace(); // gibt den Fehler der dadurch entstanden in der Konsole aus
                    event.reply("Irgendetwas ist schief gelaufen..").setEphemeral(true).queue();
                }
            } else {
                event.reply("Du hast keine Permission!").setEphemeral(true).queue();
            }
        }
    }

    public boolean hasUserPermission(User user) {
        String teamRoleID = "1234638273398177824";

        long guildID = 1174794176005677067L;
        Guild guild = user.getJDA().getGuildById(guildID);

        if (guild != null) {
            try {
                Member member = guild.retrieveMemberById(user.getId()).complete();

                if (member != null) {
                    for (Role role : member.getRoles()) {
                        if (teamRoleID.contains(role.getId())) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
