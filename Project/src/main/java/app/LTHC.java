package app;

public class LTHC {
    private int LGACode;
    private String stateAbbr;
    private String indigStatus;
    private String sex;
    private String condition;
    private int rawData;
    private double proportionalData;

    // This is for view by LGAs
    public LTHC(int LGACode, String indigStatus, String condition, int rawData, double proportionalData) {
        this.LGACode = LGACode;
        this.indigStatus = indigStatus;
        this.condition = condition;
        this.rawData = rawData;
        this.proportionalData = proportionalData;
    }

    // This is for view by state
    public LTHC(int LGACode, String stateAbbr, String indigStatus, String condition, int rawData, double proportionalData) {
        this.LGACode = LGACode;
        this.stateAbbr = stateAbbr;
        this.indigStatus = indigStatus;
        this.condition = condition;
        this.rawData = rawData;
        this.proportionalData = proportionalData;
    }

    // For 3A subtask
    public LTHC(int LGACode, String indigStatus, String sex, String condition, int rawData) {
        this.LGACode = LGACode;
        this.indigStatus = indigStatus;
        this.sex = sex;
        this.condition = condition;
        this.rawData = rawData;
    }

    public int getLGACode() {
        return LGACode;
    }

    public String getStateAbbr() {
        return stateAbbr;
    }

    public String getCondition() {
        return condition;
    }

    public String getIndigStatus() {
        return indigStatus;
    }

    public String getSex() {
        return sex;
    }

    public int getRawData() {
        return rawData;
    }

    public double getProportionalData() {
        return proportionalData;
    }
}
