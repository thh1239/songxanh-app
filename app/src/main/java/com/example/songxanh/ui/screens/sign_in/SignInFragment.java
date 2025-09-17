package com.example.songxanh.ui.screens.sign_in;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.songxanh.R;
import com.example.songxanh.data.models.User;
import com.example.songxanh.databinding.FragmentSignInBinding;
import com.example.songxanh.ui.screens.MainActivity;
import com.example.songxanh.ui.screens.MainVM;
import com.example.songxanh.utils.FirebaseConstants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
    private SignInVM viewModel;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private NavController navController;
    private MainVM mainVM;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent data = result.getData();
                                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                                try {
                                    GoogleSignInAccount account = task.getResult(ApiException.class);
                                    firebaseAuthWithGoogle(account);
                                } catch (ApiException e) {
                                    viewModel.getIsLoading().setValue(false);
                                    Toast.makeText(requireContext(),
                                            "Google sign-in failed: " + e.getStatusCode(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                viewModel.getIsLoading().setValue(false);
                            }
                        }
                    });

    public SignInFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SignInVM.class);
        binding.setSignInVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        navController = NavHostFragment.findNavController(this);

        setOnClick();
        setObservables();

        return binding.getRoot();
    }

    private void setObservables() {
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSignInSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean signInSuccess) {
                if (Boolean.TRUE.equals(signInSuccess)) {
                    startMain();
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                binding.loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setOnClick() {
        binding.toSignUpBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.signUpFragment)
        );

        binding.forgotPasswordBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        );

        binding.googleBtn.setOnClickListener(v -> {
            viewModel.getIsLoading().setValue(true);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (!isAdded()) {
                        viewModel.getIsLoading().setValue(false);
                        return;
                    }
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            viewModel.getIsLoading().setValue(false);
                            Toast.makeText(requireContext(), "User is null.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FirebaseConstants.usersRef.document(user.getUid()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> t) {
                                        viewModel.getIsLoading().setValue(false);
                                        if (!t.isSuccessful()) {
                                            Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        DocumentSnapshot doc = t.getResult();
                                        if (doc != null && doc.exists()) {
                                            // ĐÃ có hồ sơ -> tái sử dụng flow chung
                                            viewModel.getToastMessage().setValue("Đăng nhập thành công");
                                            viewModel.getSignInSuccess().setValue(true);
                                        } else {
                                            // CHƯA có hồ sơ -> chuyển sang điền thông tin (safe navigate)
                                            navigateToFillInfoIfNeeded();
                                        }
                                    }
                                });
                    } else {
                        viewModel.getIsLoading().setValue(false);
                        Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startMain() {
        if (!isAdded()) return;
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void navigateToFillInfoIfNeeded() {
        if (!isAdded() || navController == null || navController.getCurrentDestination() == null) return;
        if (navController.getCurrentDestination().getId() == R.id.fillInPersonalInformationFragment) return;
        try {
            navController.navigate(R.id.fillInPersonalInformationFragment);
        } catch (IllegalArgumentException ignored) { /* safe */ }
    }
}
