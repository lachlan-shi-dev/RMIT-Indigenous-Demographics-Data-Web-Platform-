package app;

public class Persona {

    private String name;
    private String attributes;
    private String needs;
    private String goals;
    private String skills;
    private String imageFilePath;

    public Persona(String name, String attributes, String needs, String goals, String skills, String imageFilePath) {
        this.name = name;
        this.attributes = attributes;
        this.needs = needs;
        this.goals = goals;
        this.skills = skills;
        this.imageFilePath = imageFilePath;
    }

    public String getName() {
        return name;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getNeeds() {
        return needs;
    }

    public String getGoals() {
        return goals;
    }

    public String getSkills() {
        return skills;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }
}
