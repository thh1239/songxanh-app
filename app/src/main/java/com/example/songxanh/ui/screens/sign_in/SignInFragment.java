package com.example.songxanh.ui.screens.sign_in;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.songxanh.R;
import com.example.songxanh.data.models.User;
import com.example.songxanh.databinding.FragmentSignInBinding;
import com.example.songxanh.ui.screens.MainActivity;
import com.example.songxanh.ui.screens.MainVM;
import com.example.songxanh.ui.screens.sign_up.SignUpFragment;
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
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private NavController navController;
    private MainVM mainVM;
    private ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
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
                            // Handle sign-in failure
                            Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(SignInFragment.this).get(SignInVM.class);
        binding.setSignInVM(viewModel);
        binding.setLifecycleOwner(this);

        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);

        // Configure Google sign-in options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
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
                if (s != null) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show();
                }
            }
        });

        viewModel.getSignInSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean signInSuccess) {
                if (signInSuccess) {
//                    NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.homeFragment);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    binding.loadingLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.loadingLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setOnClick() {
        binding.toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.signUpFragment);
            }
        });

        binding.forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.action_signInFragment_to_forgotPasswordFragment);
            }
        });

        binding.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(requireContext(), "Sign in successful", Toast.LENGTH_SHORT).show();

                            FirebaseConstants.usersRef.document(user.getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().exists()) {
                                                    mainVM.loadUser(new MainVM.UserLoadCallback() {
                                                        @Override
                                                        public void onUserLoaded(User user) {

                                                        }

                                                        @Override
                                                        public void onUserNotHaveInformation() {

                                                        }
                                                    });
//                                                    navController.navigate(R.id.action_signInFragment_to_homeFragment);
//                                                    NavHostFragment.findNavController(SignInFragment.this).navigate(R.id.homeFragment);
//                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivity(intent);
                                                } else {
                                                    // not have information -> Navigate to fill in information screen
                                                    navController.navigate(R.id.action_signInFragment_to_fillInPersonalInformationFragment);
                                                }
                                            } else {
                                                Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        // Sign in failed, display a message to the user
                        Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}