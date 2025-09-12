package com.example.songxanh.ui.screens.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.data.models.User;
import com.example.songxanh.databinding.FragmentProfileBinding;
import com.example.songxanh.ui.screens.MainVM;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {

    private ProfileVM profileVM;
    private FragmentProfileBinding binding;
    private ActivityResultLauncher<String> launcher;
    private MainVM mainVM;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileVM = new ViewModelProvider(requireActivity()).get(ProfileVM.class);
        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);
//        profileVM.getUserLiveData();
        profileVM.setUser(mainVM.getUser());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater,container,false);
        binding.setViewModel(profileVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        loadData();

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uploadImageToFb(uri);
            }
        });

        binding.personalInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_profileFragment_to_profilePersonalInfoFragment);
            }
        });

        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_profileFragment_to_editProfileFragment);
            }
        });

        binding.changeGoalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_profileFragment_to_profileChangeGoalsFragment);
            }
        });
        binding.caloriesHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_profileFragment_to_profileCaloriesHistoryFragment);
            }
        });
        binding.changeNotiTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_profileFragment_to_profileChangeNotiTimeFragment2);
            }
        });
        binding.settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_profileFragment_to_profileSettingFragment);
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });

        return binding.getRoot();
    }

    private void imagePicker() {
        launcher.launch("image/*");
    }

    private void loadData() {
        if (profileVM.getUser().getValue().getImageUrl() == null) {
            binding.profileImage.setImageResource(R.drawable.default_profile_image);
        } else {
            Glide.with(requireContext()).load(profileVM.getUser().getValue().getImageUrl()).into(binding.profileImage);
        }
    }

    private void uploadImageToFb(Uri uri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("user_profile_images/"+ profileVM.getUser().getValue().getName());
        imageRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                binding.profileImage.setImageURI(uri);
                                Glide.with(requireContext()).load(uri).into(binding.profileImage);
                                mainVM.updateUserProfileImage(uri);
                            }
                        });
                    }
                });
    }
}