package com.crazyhoorse961.tbackups.backup.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import com.crazyhoorse961.tbackups.TethaBackup;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v1.DbxWriteMode;

@SuppressWarnings("deprecation")
public class DropBox {
	
	private static String key;
	private static String secret;
	private static DbxAppInfo app;
	private static DbxRequestConfig conf;
	private static DbxWebAuthNoRedirect auth;

	public void uploadToBox(File f, String name) {
		if(conf == null){
			conf = new DbxRequestConfig(TethaBackup.getTetha().getConfig().getString("dropbox.sequence"));
		}
		DbxClientV1 client = new DbxClientV1(conf, TethaBackup.getTetha().getConfig().getString("dropbox.access-token"));
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try{
			@SuppressWarnings("unused")
			DbxEntry.File uploaded = client.uploadFile("/backups/" + name, DbxWriteMode.add(), f.length(), fis);
		}catch(Throwable t){
			t.printStackTrace();
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		TethaBackup.getTetha().getLogger().log(Level.INFO, "DropBox backup executed!");
	}
	
	public String generateURLToken(){
		key = TethaBackup.getTetha().getConfig().getString("dropbox.api-key");
		secret = TethaBackup.getTetha().getConfig().getString("dropbox.api-secret");
		app = new DbxAppInfo(key, secret);
		conf = new DbxRequestConfig(TethaBackup.getTetha().getConfig().getString("dropbox.sequence"));
		auth = new DbxWebAuthNoRedirect(conf, app);
		final String authurl = auth.start();
		return authurl;
	}
	public void validate(String code){
		DbxAuthFinish authFinish = null;
		try {
			authFinish = auth.finish(code);
			String accessToken = authFinish.getAccessToken();
			TethaBackup.getTetha().getConfig().set("dropbox.access-token", accessToken);
			TethaBackup.getTetha().saveConfig();
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}
	public String sequence(){
		String reqconf = UUID.randomUUID().toString().replace("-", "");
		TethaBackup.getTetha().getConfig().set("dropbox.sequence", reqconf);
		TethaBackup.getTetha().saveConfig();
		return reqconf;
	}
}
