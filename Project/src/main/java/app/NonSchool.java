package app;

public class NonSchool {

    private int lgacode;
    private String state;
    private String indig;
    private String category;
    private int raw;
    private double prop;

    // For subtask 2B

    public NonSchool(int lgacode, String indig, String category, int raw, double prop) {
        this.lgacode = lgacode;
        this.indig = indig;
        this.category = category;
        this.raw = raw;
        this.prop = prop;
    }

    public NonSchool(int lgacode, String state, String indig, String category, int raw, double prop) {
        this.lgacode = lgacode;
        this.state = state;
        this.indig = indig;
        this.category = category;
        this.raw = raw;
        this.prop = prop;
    }

    public int getLGACode() {
        return lgacode;
    }

    public String getStateAbbr() {
        return state;
    }

    public String getIndigStatus() {
        return indig;
    }

    public String getComCategory() {
        return category;
    }

    public int getRawData() {
        return raw;
    }

    public double getProportionalData() {
        return prop;
    }
}
