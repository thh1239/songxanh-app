package com.example.songxanh.data.models;

import android.net.Uri;

public class ChatMessage {
    private String content;
    private boolean isSent;
    private Uri imageUri;
    private boolean loading;

    public ChatMessage(String content, boolean isSent) {
        this.content = content;
        this.isSent = isSent;
        this.imageUri = null;
        this.loading = false;
    }

    public ChatMessage(String content, boolean isSent, Uri imageUri) {
        this.content = content;
        this.isSent = isSent;
        this.imageUri = imageUri;
        this.loading = false;
    }

    public ChatMessage(boolean loading) {
        this.content = null;
        this.isSent = false;
        this.imageUri = null;
        this.loading = loading;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public boolean hasImage() {
        return imageUri != null;
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public boolean isLoading() {
        return loading;
    }
}
