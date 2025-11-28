package com.example.nutrisnap.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.nutrisnap.data.GeminiService;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.model.GeminiRequest;
import com.example.nutrisnap.data.model.GeminiResponse;
import com.example.nutrisnap.databinding.FragmentScanBinding;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private static final String TAG = "ScanFragment";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[] { Manifest.permission.CAMERA };
    private static final String GEMINI_API_KEY = "AIzaSyAWG5j1U45Yqrj0cbQ7khJJoc10_K7vnSM";

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),
                                selectedImage);
                        analyzeImage(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG, "Error loading image", e);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        binding.btnCapture.setOnClickListener(v -> takePhoto());
        binding.btnGallery.setOnClickListener(v -> openGallery());

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void takePhoto() {
        if (imageCapture == null) {
            Log.e(TAG, "Camera not ready");
            Toast.makeText(requireContext(), "Camera not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Taking photo...");
        File photoFile = new File(requireContext().getExternalCacheDir(),
                "scan_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.d(TAG, "Photo saved");
                        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        if (bitmap != null) {
                            analyzeImage(bitmap);
                        } else {
                            Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Capture failed", exception);
                        Toast.makeText(requireContext(), "Capture failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void analyzeImage(Bitmap bitmap) {
        Toast.makeText(requireContext(), "Analyzing...", Toast.LENGTH_SHORT).show();

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String base64Image = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        GeminiService service = RetrofitClient.getGeminiClient().create(GeminiService.class);

        GeminiRequest.InlineData inlineData = new GeminiRequest.InlineData("image/jpeg", base64Image);
        GeminiRequest.Part imagePart = new GeminiRequest.Part(inlineData);
        GeminiRequest.Part textPart = new GeminiRequest.Part(
                "Identify this food and estimate calories, protein, carbs, and fat per serving. Return ONLY JSON format: {\"food_name\": \"...\", \"calories\": 0, \"protein\": 0, \"carbs\": 0, \"fat\": 0}");

        GeminiRequest.Content content = new GeminiRequest.Content();
        content.parts = java.util.Arrays.asList(textPart, imagePart);

        GeminiRequest request = new GeminiRequest();
        request.contents = Collections.singletonList(content);

        service.generateContent(GEMINI_API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String resultText = response.body().candidates.get(0).content.parts.get(0).text;
                        resultText = resultText.replace("```json", "").replace("```", "").trim();

                        Intent intent = new Intent(requireContext(), MealApprovalActivity.class);
                        intent.putExtra("analysis_result", resultText);
                        intent.putExtra("IS_DEMO", requireActivity().getIntent().getBooleanExtra("IS_DEMO", false));
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(requireContext(), "Error parsing result", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Analysis failed: " + response.code());
                    Toast.makeText(requireContext(), "Analysis failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCamera() {
        Log.d(TAG, "Starting camera...");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider
                .getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                    Log.d(TAG, "✅ Camera started!");
                    Toast.makeText(requireContext(), "Camera ready", Toast.LENGTH_SHORT).show();
                } catch (Exception exc) {
                    Log.e(TAG, "Binding failed", exc);
                    Toast.makeText(requireContext(), "Camera error", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera init failed", e);
                Toast.makeText(requireContext(), "Camera initialization failed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}
