package app;

public class SchoolNon {

    private int lgacode;
    private String state;
    private String indig;
    private String category;
    private int raw;
    private double prop;

    // For subtask 2B

    public SchoolNon(int lgacode, String indig, String category, int raw, double prop) {
        this.lgacode = lgacode;
        this.indig = indig;
        this.category = category;
        this.raw = raw;
        this.prop = prop;
    }

    public SchoolNon(int lgacode, String state, String indig, String category, int raw, double prop) {
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

    public String getCategory() {
        return category;
    }

    public int getRawData() {
        return raw;
    }

    public double getProportionalData() {
        return prop;
    }
}
