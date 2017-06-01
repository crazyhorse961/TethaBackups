package com.crazyhoorse961.tbackups;

import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.crazyhoorse961.tbackups.backup.BackupManager;
import com.crazyhoorse961.tbackups.backup.SFTPSession;
import com.crazyhoorse961.tbackups.backup.SQLManager;
import com.crazyhoorse961.tbackups.backup.models.DropBox;
import com.crazyhoorse961.tbackups.commands.DropBoxCommand;
import com.crazyhoorse961.tbackups.commands.Sequence;
import com.crazyhoorse961.tbackups.commands.TethaCommand;
import com.crazyhoorse961.tbackups.days.DayCheck;

public class TethaBackup extends JavaPlugin
{
	private static TethaBackup tetha;
	private BackupManager manager;
	private DayCheck daycheck;
	private DropBox dropBox;
	private SFTPSession sftp;
	private SQLManager sqlManager;
	private int backupDones = 0;

	@Override
	public void onEnable(){
		tetha = this;
		saveDefaultConfig();
		new TethaCommand(this);
		new DropBoxCommand(this);
		manager = new BackupManager();
		daycheck = new DayCheck();
		dropBox = new DropBox();
		sqlManager = new SQLManager();
		sftp = new SFTPSession();
		loadMetrics();
		getCommand("sequence").setExecutor(new Sequence());
		if(getConfig().getString("backup-days").isEmpty()){
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				public void run() {
					try {
						manager.executeBackup();
						backupDones++;
					} catch (Exception e) {
						e.printStackTrace();
					}	
				}
			},0L, getConfig().getLong("backup-time"));
		}else{
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				
				@Override
				public void run() {
					daycheck.check();
					
				}
			}, 0L, 1728000);
		}
	}

	@Override
	public void onDisable(){
		saveConfig();
	}
	
	private void loadMetrics(){
		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("backups"){
			@Override
			public String getValue() {
				return String.valueOf(backupDones);
			}
		});
	}

	public static TethaBackup getTetha() {
		return tetha;
	}
	public BackupManager getManager() {
		return manager;
	}
	public DropBox getDropBox() {
		return dropBox;
	}
	public SFTPSession getSFTP() {
		return sftp;
	}
	public SQLManager getSQLManager() {
		return sqlManager;
	}

}
