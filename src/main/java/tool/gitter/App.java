package tool.gitter;

import org.apache.commons.cli.*;
import tool.gitter.model.Action;
import tool.gitter.command.Command;
import tool.gitter.exception.ApplicationException;

import java.util.Optional;

public class App {

    public static void main(String[] ar) {
        try {
            App.run(ar);
        } catch (ApplicationException e) {
            System.out.println("ERROR!! Please fix the issues and try again\n\n");
        }
    }

    private static void run(String[] ar) {
        Optional<Action> actionOptional = Action.getAction(ar[0]);

        if (actionOptional.isEmpty()) {
            System.out.println("Please enter a valid command\n" +
                    "use 'git help' for documentation");
        }

        Action action = actionOptional.get();

        Command command = Command.get(action, ar);
        System.out.println(command.execute());
    }
}
