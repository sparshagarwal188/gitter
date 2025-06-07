package tool.gitter.comparison;

public class ComparisonProvider {

    private static Diff_Match_Patch comparatorUtil;

    public static Diff_Match_Patch instance() {
        if(comparatorUtil == null) {
            comparatorUtil = new Diff_Match_Patch();
        }
        return comparatorUtil;
    }
}
