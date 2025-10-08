package com.example.decoration.dto;

public class ContactRequest {
    private String fromName;
    private String fromEmail;
    private String fromMobile;
    private String message;

    // Getters and Setters
    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }

    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }

    public String getFromMobile() { return fromMobile; }
    public void setFromMobile(String fromMobile) { this.fromMobile = fromMobile; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
