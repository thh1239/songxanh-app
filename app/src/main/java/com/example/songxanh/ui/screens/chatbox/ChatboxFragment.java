package com.example.songxanh.ui.screens.chatbox;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.fragment.NavHostFragment;
import com.example.songxanh.data.repositories.GeminiService;
import com.example.songxanh.databinding.FragmentChatboxBinding;
import com.example.songxanh.data.models.ChatMessage;
import com.example.songxanh.data.adapters.ChatAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ChatboxFragment extends Fragment {

    private FragmentChatboxBinding binding;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> messages = new ArrayList<>();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public ChatboxFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatboxBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo RecyclerView và Adapter
        chatAdapter = new ChatAdapter(messages);
        binding.chatMessagesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatMessagesRecyclerview.setAdapter(chatAdapter);

        // Thiết lập OnClickListener cho nút gửi tin nhắn
        binding.sendMessageButton.setOnClickListener(v -> sendMessage());

        // Thiết lập OnClickListener cho toolbar để quay lại
        binding.chatToolbar.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void sendMessage() {
        String message = binding.messageEditText.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // Thêm tin nhắn của người dùng vào danh sách
        messages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
        binding.messageEditText.setText("");

        // Gửi yêu cầu đến GeminiService
        GeminiService.callGeminiAPI(message, new GeminiService.OnApiResponseListener() {
            @Override
            public void onResponse(String response) {
                // Xử lý phản hồi từ Gemini
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String aiMessage = jsonResponse.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

                    // Cập nhật giao diện trên luồng chính
                    mainHandler.post(() -> {
                        messages.add(new ChatMessage(aiMessage, false));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                    });

                } catch (JSONException e) {
                    mainHandler.post(() -> {
                        Log.e("ChatboxFragment", "Error parsing JSON response", e);
                        messages.add(new ChatMessage("Tôi xin lỗi, có lỗi xảy ra khi xử lý phản hồi.", false));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Xử lý lỗi từ API
                mainHandler.post(() -> {
                    Log.e("ChatboxFragment", "API Error: " + errorMessage);
                    messages.add(new ChatMessage("Có lỗi xảy ra, vui lòng thử lại sau.", false));
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}