package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equals("log")) {
            User user = event.getUser();

            if (hasUserPermission(user)) {
                String filePath = "/home/medicoo/marten/logs/latest.log";
                StringBuilder content = new StringBuilder();

                // liest hier die Datei und fügt jede Zeile latest.log in meinen String hinzu
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = br.readLine()) != null) { // while schleife zum hinzufügen
                        // Das ist RegEX. Wird benutzt um ein bestimmtes Muster zu erkennen.
                        // Ich filter dort die wichtigen Sachen raus, bei jeder Zeile
                        Pattern pattern = Pattern.compile("\\[(\\d{2}[A-Za-z]{3}\\d{4} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\] \\[(.*?)\\]: (.*)");
                        Matcher matcher = pattern.matcher(line);

                        if (matcher.find()) {
                            String timestamp = matcher.group(1); // nimmt die Uhrzeit
                            String MCEvent = matcher.group(3); // nimmt das Ereignis

                            content.append("[" + timestamp + "] " + MCEvent).append("\n"); // und fügt es dem String hinzu
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // hier wird am Ende der String genommen und nur die letzten 4096 Zeichen verwendet
                String wholeContent = content.toString();
                int length = wholeContent.length();
                int startIndex = Math.max(length - 4096, 0);
                String last4096Chars = wholeContent.substring(startIndex);

                System.out.println(last4096Chars);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Logs");
                embed.setDescription(last4096Chars);
                embed.setFooter("Letzte 4096 Zeichen");

                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
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

    // nimmt alle Commands und fügt die dem System hinzu
    // theoretisch kann man das in einer seperaten klasse machen, für 2 Commands aber too much
    @Override
    public void onReady(ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        OptionData option = new OptionData(OptionType.STRING, "command", "Dein Command", true);
        commandData.add(Commands.slash("log", "Forge Logs"));
        commandData.add(Commands.slash("cmd", "Führe ein Command aus").addOptions(option));

        event.getJDA().updateCommands().addCommands(commandData).queue();

    }
}
