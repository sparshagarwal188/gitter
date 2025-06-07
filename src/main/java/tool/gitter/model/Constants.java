package tool.gitter.model;

public abstract class Constants {

    public static final String GITTER = ".gitter";
    public static final String INDEX = "index";
    public static final String HEAD = "HEAD";
    public static final String EMPTY = "";
    public static final String OBJECTS = "objects";

    public static final String HELP_MANUAL_TEMPLATE_OPTIONS = "\nName:\n%s\n\nSynopsis:\n%s\n\nDescription:\n%s\n\nOptions:\n%s\n";
    public static final String HELP_MANUAL_TEMPLATE = "\nName:\n%s\n\nSynopsis:\n%s\n\nDescription:\n%s\n\n";

    public static final String INIT_ACTION_DESCRIPTION = "initializes a new gitter repository in the current directory. It creates a hidden .gitter folder that contains all the necessary metadata, configuration, and internal structures needed for version control. This command sets up the directory to begin tracking changes but does not start tracking any files until they are explicitly added.";
    public static final String STATUS_ACTION_DESCRIPTION = "displays the current state of the working directory and the staging area. It shows which changes have been staged for the next commit and which files have been modified but not yet staged. This command helps users understand what will be included in the next commit and what changes still need to be staged.";
    public static final String LOG_ACTION_DESCRIPTION = "shows the commit history of the current branch in reverse chronological order. It displays information about each commit, including the commit hash, author, date, and commit message. This command helps users review the project’s history, track changes over time, and identify specific commits.";
    public static final String COMMIT_ACTION_DESCRIPTION = "Create a new commit containing the current contents of the index and the given log message describing the changes. The new commit is a direct child of HEAD, usually the tip of the current branch, and the branch is updated to point to it.";
    public static final String ADD_ACTION_DESCRIPTION = "used to stage changes from the working directory into the index (staging area) in preparation for a commit. It captures a snapshot of the specified files at their current state and marks them to be included in the next commit. This command does not modify the repository history or commit anything by itself";
    public static final String DIFF_ACTION_DESCRIPTION = "displays the differences between the working directory and the last commit. It highlights line-by-line changes that have been made to tracked files but not yet staged or committed. This command helps understand what modifications are pending and review changes before staging or committing them.";
}
