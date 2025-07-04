package com.embroidermodder.viewer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.embroidermodder.library.EmbPattern;
import com.embroidermodder.library.Format;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

public class ThumbnailAdapter extends BaseAdapter implements View.OnClickListener {
    final File path;
    final FilenameFilter filter;
    final MainActivity activity;

    public ThumbnailAdapter(MainActivity activity, File path) {
        this.activity = activity;
        this.path = path;
        this.filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String s) {
                File file = new File(dir, s);
                return !file.isDirectory() && (Format.getReaderByFilename(s) != null);
            }
        };
    }

    @Override
    public int getCount() {
        if (path.isFile()) return 1;
        return path.listFiles(filter).length;
    }

    @Override
    public Object getItem(int i) {
        return path.listFiles(filter)[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ThumbnailView thumbnailView;
        if (convertView == null) {
            thumbnailView = new ThumbnailView(parent.getContext());
            thumbnailView.setOnClickListener(this);
        } else {
            thumbnailView = (ThumbnailView) convertView;
        }
        thumbnailView.clear();
        thumbnailView.load((File)getItem(i));
        return thumbnailView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onClick(View view) {
        ThumbnailView thumbnailView = (ThumbnailView) view;
        File file = thumbnailView.file;
        if (file == null) return;
        Format.Reader reader = Format.getReaderByFilename(file.getName());
        if (reader == null) return;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            EmbPattern pattern = new EmbPattern();
            reader.read(pattern, in);
            activity.setPattern(pattern);
            activity.dialogDismiss();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
