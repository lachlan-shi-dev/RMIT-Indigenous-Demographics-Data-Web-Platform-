package app;

public class AgeDemographics {

    private int LGACode;
    private String stateAbbr;
    private String indigStatus;
    private String sex;
    private String ageCategory;
    private int rawData;
    private double proportionalData;

    // This is for view by LGAs
    public AgeDemographics(int LGACode, String indigStatus, String ageCategory, int rawData, double proportionalData) {
        this.LGACode = LGACode;
        this.indigStatus = indigStatus;
        this.ageCategory = ageCategory;
        this.rawData = rawData;
        this.proportionalData = proportionalData;
    }

    // This is for view by state
    public AgeDemographics(int LGACode, String stateAbbr, String indigStatus, String ageCategory, int rawData, double proportionalData) {
        this.LGACode = LGACode;
        this.stateAbbr = stateAbbr;
        this.indigStatus = indigStatus;
        this.ageCategory = ageCategory;
        this.rawData = rawData;
        this.proportionalData = proportionalData;
    }

    // For 3A subtask
    public AgeDemographics(int LGACode, String indigStatus, String sex, String ageCategory, int rawData) {
        this.LGACode = LGACode;
        this.indigStatus = indigStatus;
        this.sex = sex;
        this.ageCategory = ageCategory;
        this.rawData = rawData;
    }

    public int getLGACode() {
        return LGACode;
    }

    public String getStateAbbr() {
        return stateAbbr;
    }

    public String getIndigStatus() {
        return indigStatus;
    }

    public String getSex() {
        return sex;
    }

    public String getAgeCategory() {
        return ageCategory;
    }

    public int getRawData() {
        return rawData;
    }

    public double getProportionalData() {
        return proportionalData;
    }
}
