package com.example.songxanh.ui.screens;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.example.songxanh.R;
import com.example.songxanh.data.models.User;
import com.example.songxanh.databinding.ActivityMainBinding;
import com.example.songxanh.ui.screens.home.HomeFragment;
import com.example.songxanh.ui.screens.notification.mealNotificationReceiver;
import com.example.songxanh.ui.screens.notification.workoutNotificationReceiver;
import com.example.songxanh.ui.screens.workout.WorkoutVM;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    String[] permissions = new String[]{
            Manifest.permission.POST_NOTIFICATIONS
    };
    private ActivityMainBinding binding;
    private MainVM viewModel;
    private WorkoutVM workoutVM;
    private NavController navController;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private BeginSignInRequest signInRequest;
    static private FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean permission_post_notification = false;
    private static final String CHANNEL_WORKOUT_ID = "my_channel";
    private static final String CHANNEL_WORKOUT_NAME = "Notification Channel";
    private static final String CHANNEL_WORKOUT_DESC = "This is my notification channel";
    private static final int NOTIFICATION_WORKOUT_ID = 1;
    private static final int REQUEST_CODE = 123;

//    private static final String CHANNEL_MEAL_ID = "my_channel";
//    private static final String CHANNEL_MEAL_NAME = "My Channel";
//    private static final String CHANNEL_MEAL_DESC = "This is my notification channel";
//    private static final int NOTIFICATION_MEAL_ID = 2;
//    private static final int REQUEST_CODE_MEAL = 123;

    private NotificationManager notificationManager;
    private AlarmManager alarmManager;
    private PendingIntent notificationIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(HomeFragment.PREF_FILE_NAME, MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean(HomeFragment.THEME_KEY, false);
        if(isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Log.d("mode", "onCreate: mode 1");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Log.d("mode", "onCreate: mode 2");
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();

        viewModel = new ViewModelProvider(this).get(MainVM.class);
        binding.setMainVM(viewModel);

        // Hide navbar
        binding.navBar.setVisibility(View.GONE);
        binding.adminNavBar.setVisibility(View.GONE);

        if (firebaseAuth.getCurrentUser() == null) {
            navController.navigate(R.id.signUpFragment);
        } else {
            viewModel.loadUser(new MainVM.UserLoadCallback() {
                @Override
                public void onUserLoaded(User user) {
                }

                @Override
                public void onUserNotHaveInformation() {
                }
            });
        }

        viewModel.getState().observe(this, new Observer<UserState>() {
            @Override
            public void onChanged(UserState userState) {
                switch (userState) {
                    case loaded:
                        navController.navigate(R.id.homeFragment);
                        setUpNavbar();
                        setUpInitialFragment();
                        break;
                    case loading:
                        navController.navigate(R.id.splashFragment);
//                        hideNavBar();
                        break;
                    case notHaveInformation:
                        navController.navigate(R.id.fillInPersonalInformationFragment);
                        break;
                    default:
                        navController.navigate(R.id.signUpFragment);
                }
            }
        });

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        scheduleWorkoutNotification(getNotificationWorkoutHour(), getNotificationWorkoutMinute(), getNotificationWorkoutSecond());
        scheduleMealNotification(getNotificationMealHour(), getNotificationMealMinute(), getNotificationMealSecond());

        if (!permission_post_notification) {
            requestPermissionNotification();
        } else {
            Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpInitialFragment() {
        Log.i("Call home fragment", "CALL");
        boolean isNormalUser = viewModel.getUser().getValue().getType().equals("NORMAL_USER");
        if(isNormalUser) {
            navController.navigate(R.id.homeFragment);
        } else {
            navController.navigate(R.id.adminIngredientFragment);
        }
    }

    private void scheduleWorkoutNotification(int hour, int minute, int second) {
        // Set the desired time for the notification
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour); // Hour in 24-hour format
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        // Create an explicit intent for the notification receiver
        Intent intent = new Intent(this, workoutNotificationReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            notificationIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Schedule the notification
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notificationIntent);
    }

    private void scheduleMealNotification(int hour, int minute, int second) {
        // Set the desired time for the notification
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour); // Hour in 24-hour format
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        // Create an explicit intent for the notification receiver
        Intent intent = new Intent(this, mealNotificationReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            notificationIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Schedule the notification
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notificationIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_WORKOUT_ID, CHANNEL_WORKOUT_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_WORKOUT_DESC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int getNotificationWorkoutHour() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("notification_workout_hour", 17); // 17 là giá trị mặc định nếu không tìm thấy khóa
    }

    private int getNotificationWorkoutMinute() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("notification_workout_minute", 10); // 0 là giá trị mặc định nếu không tìm thấy khóa
    }

    private int getNotificationWorkoutSecond() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("notification_workout_second", 00); // 0 là giá trị mặc định nếu không tìm thấy khóa
    }

    private int getNotificationMealHour() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("notification_meal_hour", 7); // 17 là giá trị mặc định nếu không tìm thấy khóa
    }

    private int getNotificationMealMinute() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("notification_meal_minute", 10); // 0 là giá trị mặc định nếu không tìm thấy khóa
    }

    private int getNotificationMealSecond() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("notification_meal_second", 00); // 0 là giá trị mặc định nếu không tìm thấy khóa
    }

    private void requestPermissionNotification() {
        if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            permission_post_notification = true;
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
            }
            requestPermissiongLauncherNotification.launch(permissions[0]);
        }
    }

    private ActivityResultLauncher<String> requestPermissiongLauncherNotification =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    permission_post_notification = true;
                } else {
                    permission_post_notification = false;
                    showPermissiongDialog("Notification Permission");
                }
            });

    public void showPermissiongDialog(String permissiong_desc) {
        new AlertDialog.Builder(
                this
        ).setTitle("Alert for Permission")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent rintent = new Intent();
                        rintent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        rintent.setData(uri);
                        startActivity(rintent);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void setNavbarItem(int itemId) {
        binding.navBar.setItemSelected(itemId, true);
    }

    private void setUpNavbar() {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination,
                                             @Nullable Bundle arguments) {
                switch (destination.getId()) {
                    case R.id.homeFragment:
                        setNavbarItem(R.id.nav_home);
                        setNavBarVisibility();

                        break;
                    case R.id.menuFragment:
                        setNavbarItem(R.id.nav_menu);
                        setNavBarVisibility();

                        break;
                    case R.id.workoutFragment:
                        setNavbarItem(R.id.nav_workout);
                        setNavBarVisibility();

                        break;
                    case R.id.communityFragment:
                        setNavbarItem(R.id.nav_community);
                        setNavBarVisibility();

                        break;
                    case R.id.profileFragment:
                        setNavbarItem(R.id.nav_profile);
                        setNavBarVisibility();

                        break;
                    case R.id.adminCommunityFragment:
                        binding.adminNavBar.setItemSelected(R.id.nav_community_admin, true);
                        setNavBarVisibility();

                        break;
                    case R.id.adminIngredientFragment:
                        binding.adminNavBar.setItemSelected(R.id.nav_ingredient_admin, true);
                        setNavBarVisibility();

                        break;
                    case R.id.adminWorkoutFragment:
                        binding.adminNavBar.setItemSelected(R.id.nav_workout_admin, true);
                        setNavBarVisibility();

                        break;
                    case R.id.adminSettingFragment:
                        binding.adminNavBar.setItemSelected(R.id.nav_profile_admin,  true);
                        setNavBarVisibility();
                        break;
                    default:
                        binding.navBar.setVisibility(View.GONE);
                        binding.adminNavBar.setVisibility(View.GONE);
                }
            }
        });
        binding.navBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.nav_home:
                        navController.navigate(R.id.homeFragment);
                        break;
                    case R.id.nav_menu:
                        navController.navigate(R.id.menuFragment);
                        break;
                    case R.id.nav_workout:
                        navController.navigate(R.id.workoutFragment);
                        break;
                    case R.id.nav_community:
                        navController.navigate(R.id.communityFragment);
                        break;
                    case R.id.nav_profile:
                        navController.navigate(R.id.profileFragment);
                        break;
                }
            }
        });

        binding.adminNavBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.nav_ingredient_admin:
                        navController.navigate(R.id.adminIngredientFragment);
                        break;
                    case R.id.nav_community_admin:
                        navController.navigate(R.id.adminCommunityFragment);
                        break;
                    case R.id.nav_workout_admin:
                        navController.navigate(R.id.adminWorkoutFragment);
                        break;
                    case R.id.nav_profile_admin:
                        navController.navigate(R.id.adminSettingFragment);
                        break;
                }
            }
        });
    }

    private void setNavBarVisibility() {
        boolean isNormalUser = viewModel.getUser().getValue().getType().equals("NORMAL_USER");
        binding.navBar.setVisibility(isNormalUser ? View.VISIBLE : View.GONE);
        binding.adminNavBar.setVisibility(isNormalUser ? View.GONE : View.VISIBLE);
    }

    private void hideNavBar() {
        binding.navBar.setVisibility(View.GONE);
        binding.adminNavBar.setVisibility(View.GONE);
    }

    public static FirebaseFirestore getDb() {
        return db;
    }
}