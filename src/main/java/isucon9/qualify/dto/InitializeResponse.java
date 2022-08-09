package isucon9.qualify.dto;

public class InitializeResponse {
    /*
     * Campaign int `json:"campaign"`
     * Language string `json:"language"`
     */

    private int campaign;
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getCampaign() {
        return campaign;
    }

    public void setCampaign(int campaign) {
        this.campaign = campaign;
    }
}
