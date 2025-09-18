package com.example.songxanh.data.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.songxanh.R;
import com.example.songxanh.data.models.ChatMessage;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_LOADING = 3;

    private ArrayList<ChatMessage> messages;

    public ChatAdapter(ArrayList<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
    public int getItemViewType(int position) {
        ChatMessage m = messages.get(position);
        boolean isLoading = (m.getContent() == null && !m.hasImage());
        if (isLoading) return VIEW_TYPE_LOADING;
        return m.isSent() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        int type = holder.getItemViewType();
        if (type == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (type == VIEW_TYPE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        } else {

        }
    }

    @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
    public int getItemCount() {
        return messages.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.sent_message_text_view);
            messageImageView = itemView.findViewById(R.id.sent_message_image_view);
        }

        void bind(ChatMessage message) {
            if (message.hasImage()) {
                Uri uri = message.getImageUri();
                messageImageView.setVisibility(View.VISIBLE);
                messageImageView.setImageURI(uri);
            } else {
                messageImageView.setVisibility(View.GONE);
            }

            String content = message.getContent();
            if (content != null && !content.isEmpty()) {
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText(content);
            } else {
                messageTextView.setVisibility(View.GONE);
            }
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.received_message_text_view);
            messageImageView = itemView.findViewById(R.id.received_message_image_view);
        }

        void bind(ChatMessage message) {
            if (message.hasImage()) {
                Uri uri = message.getImageUri();
                messageImageView.setVisibility(View.VISIBLE);
                messageImageView.setImageURI(uri);
            } else {
                messageImageView.setVisibility(View.GONE);
            }

            String content = message.getContent();
            if (content != null && !content.isEmpty()) {
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText(content);
            } else {
                messageTextView.setVisibility(View.GONE);
            }
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
