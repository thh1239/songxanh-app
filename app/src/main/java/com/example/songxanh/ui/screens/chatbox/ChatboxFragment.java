package com.example.songxanh.ui.screens.chatbox;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.songxanh.data.adapters.ChatAdapter;
import com.example.songxanh.data.models.ChatMessage;
import com.example.songxanh.data.repositories.GeminiService;
import com.example.songxanh.databinding.FragmentChatboxBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatboxFragment extends Fragment {

    private FragmentChatboxBinding binding;
    private ChatAdapter chatAdapter;
    private final ArrayList<ChatMessage> messages = new ArrayList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ActivityResultLauncher<String> pickAnyFileLauncher;

    private Uri pendingImageUri = null;

    public ChatboxFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatboxBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatAdapter = new ChatAdapter(messages);
        binding.chatMessagesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatMessagesRecyclerview.setAdapter(chatAdapter);

        pickAnyFileLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> { if (uri != null) handlePickedUri(uri); }
        );

        binding.sendMessageButton.setOnClickListener(v -> sendMessage());

        if (binding.uploadButton != null) {
            binding.uploadButton.setOnClickListener(v -> pickAnyFileLauncher.launch("*/*"));
        }

        if (binding.imagePreviewContainer != null && binding.imagePreviewContainer.clearPreviewButton != null) {
            binding.imagePreviewContainer.clearPreviewButton.setOnClickListener(v -> clearImagePreview());
        }

        if (binding.imagePreviewContainer != null) {
            binding.imagePreviewContainer.getRoot().setVisibility(View.GONE);
            if (binding.imagePreviewContainer.imagePreview != null) {
                binding.imagePreviewContainer.imagePreview.setImageDrawable(null);
            }
        }

        binding.chatToolbar.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void handlePickedUri(@NonNull Uri uri) {
        try {
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            requireContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
        } catch (Exception ignored) {}

        String mime = null;
        try { mime = requireContext().getContentResolver().getType(uri); } catch (Exception ignored) {}

        if (mime != null && mime.startsWith("image/")) {
            pendingImageUri = uri;
            if (binding.imagePreviewContainer != null) {
                if (binding.imagePreviewContainer.imagePreview != null) {
                    binding.imagePreviewContainer.imagePreview.setImageURI(uri);
                }
                binding.imagePreviewContainer.getRoot().setVisibility(View.VISIBLE);
            }
        } else {
            messages.add(new ChatMessage("Chỉ hỗ trợ tệp hình ảnh", false));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
        }
    }

    private void clearImagePreview() {
        pendingImageUri = null;
        if (binding.imagePreviewContainer != null) {
            if (binding.imagePreviewContainer.imagePreview != null) {
                binding.imagePreviewContainer.imagePreview.setImageDrawable(null);
            }
            binding.imagePreviewContainer.getRoot().setVisibility(View.GONE);
        }
    }

    private @Nullable String queryDisplayName(@NonNull Uri uri) {
        String result = null;
        Cursor cursor = null;
        try {
            cursor = requireContext().getContentResolver().query(
                    uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) result = cursor.getString(idx);
            }
        } catch (Exception e) {
            Log.w("ChatboxFragment", "queryDisplayName failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }

    private void sendMessage() {
        String message = binding.messageEditText.getText().toString().trim();
        if (message.isEmpty()) {
            binding.messageEditText.requestFocus();
            return;
        }

        if (pendingImageUri != null) {
            Uri imageToSend = pendingImageUri;
            messages.add(new ChatMessage(message, true, imageToSend));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
            binding.messageEditText.setText("");

            clearImagePreview();

            ChatMessage loading = new ChatMessage(true);
            messages.add(loading);
            int loadingIndex = messages.size() - 1;
            chatAdapter.notifyItemInserted(loadingIndex);
            binding.chatMessagesRecyclerview.scrollToPosition(loadingIndex);

            GeminiService.callGeminiAPIWithImage(requireContext(), imageToSend, message,
                    new GeminiService.OnApiResponseListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                String aiMessage = json.getJSONArray("candidates")
                                        .getJSONObject(0)
                                        .getJSONObject("content")
                                        .getJSONArray("parts")
                                        .getJSONObject(0)
                                        .getString("text");

                                mainHandler.post(() -> {
                                    if (loadingIndex >= 0 && loadingIndex < messages.size()
                                            && messages.get(loadingIndex).isLoading()) {
                                        messages.remove(loadingIndex);
                                        chatAdapter.notifyItemRemoved(loadingIndex);
                                    }
                                    messages.add(new ChatMessage(aiMessage, false));
                                    chatAdapter.notifyItemInserted(messages.size() - 1);
                                    binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                                });
                            } catch (JSONException e) {
                                mainHandler.post(() -> {
                                    if (loadingIndex >= 0 && loadingIndex < messages.size()
                                            && messages.get(loadingIndex).isLoading()) {
                                        messages.remove(loadingIndex);
                                        chatAdapter.notifyItemRemoved(loadingIndex);
                                    }
                                    messages.add(new ChatMessage("Không đọc được phản hồi từ Gemini.", false));
                                    chatAdapter.notifyItemInserted(messages.size() - 1);
                                    binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                                });
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            mainHandler.post(() -> {
                                if (loadingIndex >= 0 && loadingIndex < messages.size()
                                        && messages.get(loadingIndex).isLoading()) {
                                    messages.remove(loadingIndex);
                                    chatAdapter.notifyItemRemoved(loadingIndex);
                                }
                                messages.add(new ChatMessage("Gửi ảnh thất bại: " + errorMessage, false));
                                chatAdapter.notifyItemInserted(messages.size() - 1);
                                binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                            });
                        }
                    });

        } else {
            messages.add(new ChatMessage(message, true));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
            binding.messageEditText.setText("");

            ChatMessage loading = new ChatMessage(true);
            messages.add(loading);
            int loadingIndex = messages.size() - 1;
            chatAdapter.notifyItemInserted(loadingIndex);
            binding.chatMessagesRecyclerview.scrollToPosition(loadingIndex);

            GeminiService.callGeminiAPI(message, new GeminiService.OnApiResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String aiMessage = jsonResponse.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        mainHandler.post(() -> {
                            if (loadingIndex >= 0 && loadingIndex < messages.size()
                                    && messages.get(loadingIndex).isLoading()) {
                                messages.remove(loadingIndex);
                                chatAdapter.notifyItemRemoved(loadingIndex);
                            }
                            messages.add(new ChatMessage(aiMessage, false));
                            chatAdapter.notifyItemInserted(messages.size() - 1);
                            binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                        });

                    } catch (JSONException e) {
                        mainHandler.post(() -> {
                            if (loadingIndex >= 0 && loadingIndex < messages.size()
                                    && messages.get(loadingIndex).isLoading()) {
                                messages.remove(loadingIndex);
                                chatAdapter.notifyItemRemoved(loadingIndex);
                            }
                            messages.add(new ChatMessage("Tôi xin lỗi, có lỗi xảy ra khi xử lý phản hồi.", false));
                            chatAdapter.notifyItemInserted(messages.size() - 1);
                            binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    mainHandler.post(() -> {
                        if (loadingIndex >= 0 && loadingIndex < messages.size()
                                && messages.get(loadingIndex).isLoading()) {
                            messages.remove(loadingIndex);
                            chatAdapter.notifyItemRemoved(loadingIndex);
                        }
                        messages.add(new ChatMessage("Có lỗi xảy ra, vui lòng thử lại sau.", false));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        binding.chatMessagesRecyclerview.scrollToPosition(messages.size() - 1);
                    });
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
