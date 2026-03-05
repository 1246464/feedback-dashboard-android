package com.example.visualizadorapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUploadHelper {
    private static final int MAX_IMAGE_SIZE = 1024; // 1024x1024
    private static final int COMPRESSION_QUALITY = 80;
    
    private FirebaseStorage storage;
    private Context context;
    
    public interface OnUploadListener {
        void onSuccess(String imageUrl);
        void onFailure(String error);
        void onProgress(int progress);
    }
    
    public ImageUploadHelper(Context context) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }
    
    public void uploadCardapioImage(Uri imageUri, String data, OnUploadListener listener) {
        try {
            // Comprimir imagem
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap compressedBitmap = compressImage(bitmap);
            
            // Converter para bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, baos);
            byte[] imageData = baos.toByteArray();
            
            // Upload for Firebase Storage
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "cardapio_" + data + "_" + timestamp + ".jpg";
            StorageReference storageRef = storage.getReference().child("cardapios/" + fileName);
            
            UploadTask uploadTask = storageRef.putBytes(imageData);
            uploadTask.addOnProgressListener(taskSnapshot -> {
                int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                if (listener != null) {
                    listener.onProgress(progress);
                }
            }).addOnSuccessListener(taskSnapshot -> {
                // Get download URL
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (listener != null) {
                        listener.onSuccess(uri.toString());
                    }
                }).addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure("Erro ao obter URL: " + e.getMessage());
                    }
                });
            }).addOnFailureListener(e -> {
                if (listener != null) {
                    listener.onFailure("Erro no upload: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailure("Erro ao processar imagem: " + e.getMessage());
            }
        }
    }
    
    private Bitmap compressImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        // Calcular nova escala
        float scale = Math.min(
            (float) MAX_IMAGE_SIZE / width,
            (float) MAX_IMAGE_SIZE / height
        );
        
        if (scale < 1) {
            int newWidth = Math.round(width * scale);
            int newHeight = Math.round(height * scale);
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }
        
        return bitmap;
    }
    
    public void deleteImage(String imageUrl, OnDeleteListener listener) {
        try {
            StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);
            storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e.getMessage());
                    }
                });
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }
    }
    
    public interface OnDeleteListener {
        void onSuccess();
        void onFailure(String error);
    }
}
