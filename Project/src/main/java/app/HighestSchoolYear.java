package app;

public class HighestSchoolYear {
    private int LGACode;
    private String indigStatus;
    private String sex;
    private String category;
    private int rawData;

    // For 3A subtask
    public HighestSchoolYear(int LGACode, String indigStatus, String sex, String category, int rawData) {
        this.LGACode = LGACode;
        this.indigStatus = indigStatus;
        this.sex = sex;
        this.category = category;
        this.rawData = rawData;
    }

    public int getLGACode() {
        return LGACode;
    }

    public String getIndigStatus() {
        return indigStatus;
    }

    public String getSex() {
        return sex;
    }

    public String getCategory() {
        return category;
    }

    public int getRawData() {
        return rawData;
    }
}
