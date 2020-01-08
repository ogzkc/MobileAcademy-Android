package com.kc.Util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.Wsdl2Code.WebServices.MAService.Document;
import com.github.johnpersano.supertoasts.SuperCardToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.kc.mobileacademy.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Oguz on 19.02.2016.
 */
public class UtilitiesKC {


    public static void showInfoToast(String text, FragmentActivity ctx, OnClickWrapper onClickWrapper) {
        SuperCardToast superToast = new SuperCardToast(ctx, SuperToast.Type.BUTTON);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setBackground(SuperToast.Background.BLUE);
        superToast.setText(text);
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setIcon(R.drawable.ic_information, SuperToast.IconPosition.LEFT);
        superToast.setButtonIcon(R.drawable.ic_ok_hand, "OK");
        if(onClickWrapper != null){
            superToast.setOnClickWrapper(onClickWrapper);
        }
        superToast.setSwipeToDismiss(true);
        superToast.show();
    }

    public static void showInfoToastForDocumentTempPage(String text, FragmentActivity ctx, OnClickWrapper onClickWrapper) {
        SuperCardToast superToast = new SuperCardToast(ctx, SuperToast.Type.BUTTON);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setBackground(SuperToast.Background.BLUE);
        superToast.setText(text);
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setIcon(R.drawable.ic_information, SuperToast.IconPosition.LEFT);
        superToast.setButtonIcon(R.drawable.ic_book_with_opened_pages, "Go to Beginning");
        if(onClickWrapper != null){
            superToast.setOnClickWrapper(onClickWrapper);
        }
        superToast.setSwipeToDismiss(true);
        superToast.show();
    }

    public static void showWarningToast(String text, FragmentActivity ctx) {
        SuperCardToast superToast = new SuperCardToast(ctx, SuperToast.Type.STANDARD);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setBackground(SuperToast.Background.ORANGE);
        superToast.setText(text);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setIcon(R.drawable.ic_warn2, SuperToast.IconPosition.LEFT);
        superToast.setSwipeToDismiss(true);
        superToast.show();
    }

    public static void showErrorToast(String text, FragmentActivity ctx) {
        SuperCardToast superToast = new SuperCardToast(ctx, SuperToast.Type.STANDARD);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setBackground(SuperToast.Background.RED);
        superToast.setText(text);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setIcon(R.drawable.ic_error2, SuperToast.IconPosition.LEFT);
        superToast.setSwipeToDismiss(true);
        superToast.show();
    }

    public static void showSuccessToast(String text, FragmentActivity ctx) {
        SuperCardToast superToast = new SuperCardToast(ctx, SuperToast.Type.STANDARD);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setBackground(SuperToast.Background.GREEN);
        superToast.setText(text);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setIcon(R.drawable.ic_succes2, SuperToast.IconPosition.LEFT);
        superToast.setSwipeToDismiss(true);
        superToast.show();
    }


    public static void showErrorClassicToast(String text, FragmentActivity ctx){
        SuperToast superToast = new SuperToast(ctx);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setBackground(SuperToast.Background.RED);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setText(text);
        superToast.setIcon(R.drawable.ic_error2, SuperToast.IconPosition.LEFT);
        superToast.show();
    }

    public static void showWarningClassicToast(String text, FragmentActivity ctx){
        SuperToast superToast = new SuperToast(ctx);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setBackground(SuperToast.Background.ORANGE);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setText(text);
        superToast.setIcon(R.drawable.ic_warn2, SuperToast.IconPosition.LEFT);
        superToast.show();
    }


    public static SweetAlertDialog showErrorDialog(String title, String message, String buttonText, Context ctx) {
        SweetAlertDialog alert = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE);
        alert.setTitleText(title)
                .setContentText(message)
                .setConfirmText(buttonText)
                .show();
        return alert;
    }

    public static SweetAlertDialog showSuccesDialog(String title, String message, String buttonText, Context ctx) {
        SweetAlertDialog alert = new SweetAlertDialog(ctx, SweetAlertDialog.SUCCESS_TYPE);
        alert.setTitleText(title)
                .setContentText(message)
                .setConfirmText(buttonText)
                .show();
        return alert;
    }

    public static SweetAlertDialog showProgressDialog(String message, Context ctx) {
        SweetAlertDialog alert = new SweetAlertDialog(ctx, SweetAlertDialog.PROGRESS_TYPE);
        alert.setTitleText(message)
                .setCancelable(false);
        alert.show();
        return alert;
    }

    public static SweetAlertDialog showWarningDialog(String title, String message, String buttonText, Context ctx) {
        SweetAlertDialog alert = new SweetAlertDialog(ctx, SweetAlertDialog.WARNING_TYPE);
        alert.setTitleText(title);
        alert.setContentText(message);
        alert.setConfirmText(buttonText);
        alert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        alert.show();
        return alert;
    }

    public static SweetAlertDialog showWarningDialogCustomClickListener(String title, String message, String buttonText, Context ctx, SweetAlertDialog.OnSweetClickListener listener) {
        SweetAlertDialog alert = new SweetAlertDialog(ctx, SweetAlertDialog.WARNING_TYPE);
        alert.setTitleText(title);
        alert.setContentText(message);
        alert.setConfirmText(buttonText);
        alert.setConfirmClickListener(listener);
        alert.show();
        return alert;
    }


    public static String getPDFName(Document doc) {
        return doc.documentName + "_V" + doc.version + "_" + doc.docId + ".pdf";
    }

    public static String getPDFPath(Context ctx, Document doc) {
        ContextWrapper cw = new ContextWrapper(ctx);
        return cw.getFilesDir().getPath() + "/" + doc.departmentName + "/";
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
