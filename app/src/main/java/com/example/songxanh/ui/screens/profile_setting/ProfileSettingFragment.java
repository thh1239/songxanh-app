package com.example.songxanh.ui.screens.profile_setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentProfileSettingBinding;
import com.example.songxanh.ui.screens.MainActivity;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileSettingFragment extends Fragment {
    private FragmentProfileSettingBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileSettingBinding.inflate(inflater,container,false);
        binding.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(ProfileSettingFragment.this);
                navController.popBackStack();
            }
        });

        binding.changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileSettingFragment.this).navigate(R.id.action_profileSettingFragment_to_profileChangePassFragment2);
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
//                NavHostFragment.findNavController(ProfileSettingFragment.this).navigate(R.id.action_profileSettingFragment_to_signInFragment);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }
}