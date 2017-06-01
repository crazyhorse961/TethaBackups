package com.crazyhoorse961.tbackups.days;

import java.util.Calendar;

import com.crazyhoorse961.tbackups.TethaBackup;

public class DayCheck {

	public void check(){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		int day = c.get(Calendar.DAY_OF_WEEK);
		System.out.println(day);
		String[] strindays = TethaBackup.getTetha().getConfig().getString("backup-days").split(",");
		for(String convertint : strindays){
			int backupday = Integer.valueOf(convertint);
			if(day == backupday){
				try {
					TethaBackup.getTetha().getManager().executeBackup();
					System.out.println("Backup done");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
