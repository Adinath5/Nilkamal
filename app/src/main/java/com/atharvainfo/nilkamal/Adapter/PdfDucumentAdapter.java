package com.atharvainfo.nilkamal.Adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PdfDucumentAdapter extends PrintDocumentAdapter {
    Context context;
    String path;

    public PdfDucumentAdapter(Context context, String path){
        this.context = context;
        this.path = path;
    }


    @Override
    public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {

        if(cancellationSignal.isCanceled()){
            layoutResultCallback.onLayoutCancelled();
        } else {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("file_name");
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build();

            layoutResultCallback.onLayoutFinished(builder.build(), !printAttributes.equals(printAttributes));
        }
    }


    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {

        InputStream in = null;
        OutputStream out = null;
        try
        {
            File file = new File(path);
            in = new FileInputStream(file);
            out = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            byte[] buff = new byte[16384];
            int size;
            while ((size = in.read(buff)) >=0 && !cancellationSignal.isCanceled()){
                out.write(buff, 0, size);
            }

            if (cancellationSignal.isCanceled())
                writeResultCallback.onWriteCancelled();
            else{
                writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            }

        } catch (Exception e) {
            writeResultCallback.onWriteFailed(e.getMessage());
            Log.e("PoultrySoft",e.getMessage());
            e.printStackTrace();
        }
        finally {
            try{
                in.close();
                out.close();
            }catch (IOException ex  ){
                Log.e("PoultrySoft", ex.getMessage());
            }
        }

    }
}
