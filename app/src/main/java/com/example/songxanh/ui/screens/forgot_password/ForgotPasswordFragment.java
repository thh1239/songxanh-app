package com.example.songxanh.ui.screens.forgot_password;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentForgotPasswordBinding;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.material.appbar.AppBarLayout;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private ForgotPasswordVM viewModel;

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentForgotPasswordBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(ForgotPasswordVM.class);
        binding.setForgotPasswordVM(viewModel);

        viewModel.getIsSent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSent) {
                if (isSent == true) {
                    displayDialog();
                }
            }
        });

        binding.toolBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(ForgotPasswordFragment.this);
            }
        });

        return binding.getRoot();
    }

    private void displayDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_forgot_password, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        alert.setView(dialogLayout);

        AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        AppCompatButton doneBtn = dialogLayout.findViewById(R.id.done_btn);
        AppCompatButton cancelBtn = dialogLayout.findViewById(R.id.cancel_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                alertDialog.dismiss();
                NavHostFragment.findNavController(ForgotPasswordFragment.this).navigate(R.id.signInFragment);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}