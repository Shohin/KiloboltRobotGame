package com.shohin.framework.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.shohin.framework.FileIO;

public class AndroidFileIO implements FileIO {
	
	Context context;
	AssetManager assets;
	String externalStoragePath;
	
	public AndroidFileIO (Context context) {
		this.context = context;
		this.assets = context.getAssets();
		this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}
	
	@Override
	public InputStream readAsset(String file) throws IOException {
		// TODO Auto-generated method stub
		return assets.open(file);
	}
	
	@Override
	public InputStream readFile(String file) throws IOException {
		// TODO Auto-generated method stub
		return new FileInputStream(externalStoragePath + file);
	}
	
	@Override
	public OutputStream writeFile(String file) throws IOException {
		// TODO Auto-generated method stub
		return new FileOutputStream(externalStoragePath + file);
	}
	
	@Override
	public SharedPreferences getSharedPref() {
		// TODO Auto-generated method stub
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
