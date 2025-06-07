package tool.gitter.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import tool.gitter.model.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class OptionsFactory {

    private static final Map<Action, Supplier<List<Option>>> optionsByActionMap;

    static {
        optionsByActionMap = new HashMap<>();
        optionsByActionMap.put(Action.commit, OptionsFactory::getCommitOptions);
        optionsByActionMap.put(Action.init, OptionsFactory::getInitOptions);
        optionsByActionMap.put(Action.add, OptionsFactory::getAddOptions);
        optionsByActionMap.put(Action.diff, OptionsFactory::getDiffOptions);
        optionsByActionMap.put(Action.help, OptionsFactory::getHelpOptions);
        optionsByActionMap.put(Action.log, OptionsFactory::getLogOptions);
        optionsByActionMap.put(Action.status, OptionsFactory::getStatusOptions);
        optionsByActionMap.put(Action.reset, OptionsFactory::getResetOptions);
    }

    private static List<Option> getResetOptions() {
        return new ArrayList<>();
    }

    private static List<Option> getLogOptions() {
        Option messageOption = Option.builder("n")
                .argName("numberOfLogs")
                .hasArg()
                .desc("Number of logs from HEAD backwards (inclusive)")
                .longOpt("number")
                .build();

        return List.of(messageOption);
    }

    private static List<Option> getCommitOptions() {
        Option messageOption = Option.builder("m")
                .argName("commitMessage")
                .hasArg()
                .desc("A message to describe the commit")
                .longOpt("message")
                .build();

        Option addFilesToIndexOption = Option.builder("a")
                .desc("add all files to index area before commit")
                .longOpt("all")
                .build();

        return List.of(messageOption, addFilesToIndexOption);
    }

    private static List<Option> getInitOptions() {
        return new ArrayList<>();
    }

    private static List<Option> getAddOptions() {
        return new ArrayList<>();
    }

    private static List<Option> getDiffOptions() {
        return new ArrayList<>();
    }

    private static List<Option> getHelpOptions() {
        return new ArrayList<>();
    }

    private static List<Option> getStatusOptions() {
        return new ArrayList<>();
    }

    public static Options getOptions(Action action) {
        Options options = new Options();
        Supplier<List<Option>> supplier = optionsByActionMap.get(action);
        List<Option> optionList = supplier.get();
        optionList.forEach(options::addOption);
        return options;
    }
}
