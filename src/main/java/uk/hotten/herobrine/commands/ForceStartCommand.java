package uk.hotten.herobrine.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.hotten.herobrine.game.GameManager;
import uk.hotten.herobrine.game.runnables.StartingRunnable;
import uk.hotten.herobrine.lobby.GameLobby;
import uk.hotten.herobrine.lobby.LobbyManager;
import uk.hotten.herobrine.utils.GameState;
import uk.hotten.herobrine.utils.Message;
import uk.hotten.herobrine.world.WorldManager;

public class ForceStartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Message.send(sender, Message.format("&cYou are unable to use this command."));
            return true;
        }

        GameManager gm;
        WorldManager wm;
        GameLobby gl = LobbyManager.getInstance().getLobby((Player) sender);
        if (gl == null) {
            Message.send(sender, Message.format("&cYou must be in a lobby to do this."));
            return true;
        }

        gm = gl.getGameManager();
        wm = gl.getWorldManager();

        if (gm.getGameState() != GameState.WAITING) {
            Message.send(sender, Message.format("&cYou cannot run this command right now."));
            return true;
        }

        int startTime;
        if (args == null || args.length == 0) {
            startTime = wm.getEndVotingAt();
        } else {
            try {
                startTime = Integer.parseInt(args[0]);
            } catch (Exception e) {
                Message.send(sender, Message.format("&cCorrect Usage: /hbforcestart [time]"));
                return true;
            }
        }

        if (startTime < wm.getEndVotingAt())
            startTime = wm.getEndVotingAt();

        Message.send(sender, Message.format("&aThe game will start in " + startTime + " seconds unless the player count goes below 2."));
        gm.startTimer = startTime+1;
        gm.setGameState(GameState.STARTING);
        new StartingRunnable(gm, wm, true).runTaskTimerAsynchronously(gm.getPlugin(), 0, 20);
        return true;
    }
}