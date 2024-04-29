package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.IOException;

public class ExecuteCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equals("cmd")) {
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

                System.out.println("Befehl erfolgreich an den Screen gesendet: " + command);
                event.reply("Der Befehl wurde an den Server gesendet.\nhttps://tenor.com/view/goofy-gif-27367825").setEphemeral(true).queue();
            } catch (IOException | InterruptedException e) { // falls waitFor() nicht erfolgreich war:
                e.printStackTrace(); // gibt den Fehler der dadurch entstanden in der Konsole aus
                event.reply("Irgendetwas ist schief gelaufen..").setEphemeral(true).queue();
            }
        }
    }
}
