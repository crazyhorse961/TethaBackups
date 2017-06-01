package com.crazyhoorse961.tbackups.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.crazyhoorse961.tbackups.TethaBackup;

import net.md_5.bungee.api.ChatColor;

public class BackupManager {

	public void executeBackup() throws Exception{
		@SuppressWarnings("unused")
		BukkitTask id = null;
		id = Bukkit.getScheduler().runTaskAsynchronously(TethaBackup.getTetha(), new Runnable(){

			public void run() {
				File dir = new File(Bukkit.getWorldContainer().getAbsoluteFile() + "\\backups");
				if(!dir.exists()){
					dir.mkdir();
				}
				Date date = new Date();
				String dateformatted = new SimpleDateFormat("yyyy-MM-dd").format(date);
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "\\backups\\" + TethaBackup.getTetha().getConfig().getString("backup-name").replace("%date%", dateformatted) + ".zip");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				ZipOutputStream stream = new ZipOutputStream(fos);
				for(String folders : TethaBackup.getTetha().getConfig().getStringList("backup-folders")){
					try {
						addDirToZipArchive(stream, new File(Bukkit.getWorldContainer() + "\\" + folders), null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					fos.flush();
					stream.flush();
					fos.close();
					stream.close();
				} catch (IOException e) {
				}
				if(TethaBackup.getTetha().getConfig().getBoolean("backup-msg-enabled")){
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', TethaBackup.getTetha().getConfig().getString("backup-msg")));
				}
				String tosend = Bukkit.getServer().getWorldContainer().getAbsolutePath() + "\\backups\\" + TethaBackup.getTetha().getConfig().getString("backup-name").replace("%date%", dateformatted) + ".zip";
				File f = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "\\backups\\" + TethaBackup.getTetha().getConfig().getString("backup-name").replace("%date%", dateformatted) + ".zip");
				if(TethaBackup.getTetha().getConfig().getBoolean("sftp.enabled")){
					TethaBackup.getTetha().getSFTP().send(tosend);
					if(TethaBackup.getTetha().getConfig().getBoolean("sql.enabled")){
						TethaBackup.getTetha().getSQLManager().exportSQL();
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "SQL Backup Started");
					}
				}
				if(TethaBackup.getTetha().getConfig().getBoolean("dropbox.enabled")){
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "DropBox Backup Started");
					TethaBackup.getTetha().getDropBox().uploadToBox(f, TethaBackup.getTetha().getConfig().getString("backup-name").replace("%date%", dateformatted) + ".zip");
				}
				if(TethaBackup.getTetha().getConfig().getBoolean("ftp.enabled")){
					FTPClient ftp = new FTPClient();
					try{
						ftp.connect(TethaBackup.getTetha().getConfig().getString("ftp.host"), TethaBackup.getTetha().getConfig().getInt("ftp.port"));
						ftp.login(TethaBackup.getTetha().getConfig().getString("ftp.username"), TethaBackup.getTetha().getConfig().getString("ftp.password"));
						ftp.enterLocalPassiveMode();
						ftp.setFileType(FTP.BINARY_FILE_TYPE);
						String name = f.getName();
						InputStream is = new FileInputStream(f);
						ftp.storeFile(name, is);
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "FTP Backup Started");
						is.close();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
				if(TethaBackup.getTetha().getConfig().getBoolean("dropbox.enabled") || TethaBackup.getTetha().getConfig().getBoolean("sftp.enabled") || TethaBackup.getTetha().getConfig().getBoolean("ftp.enabled")){
					f.setWritable(true);
					f.delete();
				}
			}
		});
	}

	private void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
		if (fileToZip == null || !fileToZip.exists()) {
			return;
		}

		String zipEntryName = fileToZip.getName();
		if (parrentDirectoryName!=null && !parrentDirectoryName.isEmpty()) {
			zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
		}

		if (fileToZip.isDirectory()) {
			for (File file : fileToZip.listFiles()) {
				addDirToZipArchive(zos, file, zipEntryName);
			}
		} else {
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(fileToZip);
			zos.putNextEntry(new ZipEntry(zipEntryName));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
	}

}
