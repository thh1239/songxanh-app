package com.example.songxanh.data.models;

public class ChatMessage {
    private String content;
    private boolean isSent;

    public ChatMessage(String content, boolean isSent) {
        this.content = content;
        this.isSent = isSent;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }
}