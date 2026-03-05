package com.example.visualizadorapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.visualizadorapp.R;

public class LoadingHelper {
    private Dialog loadingDialog;
    private Context context;
    
    public LoadingHelper(Context context) {
        this.context = context;
    }
    
    public void showLoading() {
        showLoading("Carregando...");
    }
    
    public void showLoading(String message) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }
        
        loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setCancelable(false);
        
        // Criar layout programaticamente ou inflar um layout customizado
        View view = LayoutInflater.from(context).inflate(
            android.R.layout.simple_list_item_1, null
        );
        
        // Alternativamente, criar um layout simples
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setGravity(android.view.Gravity.CENTER);
        
        ProgressBar progressBar = new ProgressBar(context);
        layout.addView(progressBar);
        
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setPadding(0, 20, 0, 0);
        textView.setTextSize(16);
        layout.addView(textView);
        
        loadingDialog.setContentView(layout);
        
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        try {
            if (context instanceof Activity && !((Activity) context).isFinishing()) {
                loadingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            try {
                loadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean isShowing() {
        return loadingDialog != null && loadingDialog.isShowing();
    }
    
    // Show/Hide loading on a specific view
    public static void showViewLoading(ViewGroup container, ProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (container != null) {
            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                if (child != progressBar) {
                    child.setAlpha(0.3f);
                    child.setEnabled(false);
                }
            }
        }
    }
    
    public static void hideViewLoading(ViewGroup container, ProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (container != null) {
            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                child.setAlpha(1f);
                child.setEnabled(true);
            }
        }
    }
}
