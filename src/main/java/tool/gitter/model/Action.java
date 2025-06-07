package tool.gitter.model;

import org.apache.commons.cli.Options;
import tool.gitter.cli.OptionsFactory;

import java.util.Optional;

import static tool.gitter.model.Constants.*;

public enum Action {

    help("get help with list of available commands and documenation\n" +
            "use gitter help <command> to get detailed documentation of the <command>"),
    init("create an empty gitter repository") {

        @Override
        public String getDocumentation() {
            return String.format(HELP_MANUAL_TEMPLATE,
                    "init- " + this.getDescription(),
                    "gitter init",
                    INIT_ACTION_DESCRIPTION);
        }
    },
    add("add file contents to the index") {

        @Override
        public String getDocumentation() {
            return String.format(HELP_MANUAL_TEMPLATE_OPTIONS,
                    "add- " + this.getDescription(),
                    "gitter add [<options>]",
                    ADD_ACTION_DESCRIPTION,
                    "'.' - add all modified files to stage\n" +
                            "<regex> to add files matching the regex to stage\n");
        }
    },
    status("show the working tree status") {

        @Override
        public String getDocumentation() {
            return String.format(HELP_MANUAL_TEMPLATE,
                    "status- " + this.getDescription(),
                    "gitter status",
                    STATUS_ACTION_DESCRIPTION);
        }
    },
    commit("record changes to the repository") {

        @Override
        public String getDocumentation() {
            return String.format(HELP_MANUAL_TEMPLATE_OPTIONS,
                    "commit- " + this.getDescription(),
                    "gitter commit -m [-a] <message>",
                    COMMIT_ACTION_DESCRIPTION,
                    "a - tell the command to automatically stage all tracked files that have been changed\n" +
                            "m - the given <message> as the commit message. If multiple -m options are given, their values are concatenated as separate paragraphs.\n\n");
        }
    },
    diff("show changes between commits, commit and working tree, etc") {

        @Override
        public String getDocumentation() {
            return String.format(HELP_MANUAL_TEMPLATE_OPTIONS,
                    "diff- " + this.getDescription(),
                    "gitter diff [<filepath>]",
                    DIFF_ACTION_DESCRIPTION,
                    "<file/directory path> If the given argument is a complete path then show diff only for that file.\n" +
                            "\t\tIf the given argument is a directory then show diff all unindexed files within the directory\n\n");
        }
    },
    log("show commit logs") {

        @Override
        public String getDocumentation() {
            return String.format(HELP_MANUAL_TEMPLATE,
                    "log- " + this.getDescription(),
                    "gitter log",
                    LOG_ACTION_DESCRIPTION);
        }
    },
    reset("go back to earlier commit") {

    };

    private final String description;

    Action(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Options getOptions() {
        return OptionsFactory.getOptions(this);
    }

    public static Optional<Action> getAction(String arg) {
        for(Action action : Action.values()) {
            if(action.name().equalsIgnoreCase(arg)) {
                return Optional.of(action);
            }
        }
        return Optional.empty();
    }

    public String getDocumentation() {
        return this.description;
    }
}
