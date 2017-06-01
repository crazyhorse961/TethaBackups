package com.crazyhoorse961.tbackups.backup;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.crazyhoorse961.tbackups.TethaBackup;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPSession 
{
	public void send(String filename){
		String sftphost = TethaBackup.getTetha().getConfig().getString("sftp.host");
		int sftpport = TethaBackup.getTetha().getConfig().getInt("sftp.port");
		String sftpuser = TethaBackup.getTetha().getConfig().getString("sftp.user");
		String sftppass =TethaBackup.getTetha().getConfig().getString("sftp.password");
		String path = TethaBackup.getTetha().getConfig().getString("sftp.backup-path");
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		System.out.println("Starting connecting to the SFTP Server");
		try{
			JSch jsch = new JSch();
			session = jsch.getSession(sftpuser, sftphost, sftpport);
			session.setPassword(sftppass);
			Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Connecting...");
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("SFTP Channel opened and connected");
            channelSftp = (ChannelSftp) channel;
            channelSftp.mkdir(path);
            channelSftp.cd(path);
            File f = new File(filename);
            channelSftp.put(new FileInputStream(f), f.getName());
            System.out.println("File transferred with success");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("An error occured while transferring files to the SFTP Server");
		}
		finally{
			channelSftp.exit();
			System.out.println("SFTP Channel disconnected");
			channel.disconnect();
			System.out.println("Channel disconnected");
			session.disconnect();
			System.out.println("Session disconnected");
		}
	}
}
