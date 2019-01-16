package com.ziq.base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by john on 06/09/2017.
 */

public class PDFUtils {

    private static final String TAG = "PDFUtils";

    public interface Callback<T> {
        void success(T t);

        void failed();
    }

    public static void createPdf(final File outPutFile, final Callback<File> callback, final ViewGroup parentView, View... views) {
        Context context = parentView.getContext();
        int resultHeight = 0;
        final int parentMeasuredWidth = parentView.getMeasuredWidth();
        parentView.measure(View.MeasureSpec.makeMeasureSpec(parentView.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(parentView.getMeasuredHeight(), View.MeasureSpec.UNSPECIFIED));
        if (views.length > 0) {
            for (View view : views) {
                view.measure(View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.UNSPECIFIED));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                resultHeight += view.getMeasuredHeight();
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            final PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(DeviceInfoUtil.getDisplayWidth(context), resultHeight, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            parentView.draw(page.getCanvas());
            parentView.requestLayout();
            parentView.layout(0, 0, parentMeasuredWidth, resultHeight);
            document.finishPage(page);

            new Thread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    try {
                        File file = outPutFile;
                        String dest = file.getAbsolutePath();
                        document.writeTo(new FileOutputStream(dest));
                        callback.success(file);
                    } catch (Exception e) {
                        callback.failed();
                    } finally {
                        document.close();
                    }
                }
            }).start();

        } else {
            final Bitmap bmp = Bitmap.createBitmap(parentMeasuredWidth, resultHeight, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bmp);
            c.drawColor(Color.WHITE);
            parentView.draw(c);
            parentView.requestLayout();
            parentView.layout(0, 0, parentMeasuredWidth, resultHeight);
            final int finalResultHeight = resultHeight;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.i(TAG, "pdf create start:");
                        File file = outPutFile;
                        createPdf(file, bmp, parentMeasuredWidth, finalResultHeight > 6000 ? 6000 : finalResultHeight);
                        callback.success(file);
                    } catch (IOException | DocumentException e) {
                        callback.failed();
                    }
                    LogUtil.i(TAG, "pdf create end:");
                }
            }).start();
        }
    }

    public static void createPdf(File file, Bitmap imageBitmap, int documentPageWidth, int documentPageHeight) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));

        int imageBitmapH = imageBitmap.getHeight();
        int pageCount = imageBitmapH / documentPageHeight;
        int remainder = imageBitmapH % documentPageHeight;
        if (remainder != 0) {
            pageCount++;
        }

        document.open();
        for (int i = 0; i < pageCount; i++) {
            int y = i * documentPageHeight;
            int height = y + documentPageHeight > imageBitmapH ? remainder : documentPageHeight;
            Bitmap temp = Bitmap.createBitmap(imageBitmap, 0, y, documentPageWidth, height);
            Image tempImage = Image.getInstance(bitmap2Bytes(temp));
            document.setPageSize(tempImage);
            document.newPage();
            tempImage.setAbsolutePosition(0, 0);
            document.add(tempImage);
            temp.recycle();
        }
        document.close();
        imageBitmap.recycle();
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
        return baos.toByteArray();
    }

}
