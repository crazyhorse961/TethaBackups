package com.crazyhoorse961.tbackups.backup;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crazyhoorse961.tbackups.TethaBackup;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SQLManager 
{

	@SuppressWarnings("unused")
	private static int port = 3306;
	private static String host;
	private static String db_user;
	private static String db_pass;
	private static Channel channel;
	private static Session ssh;

	public void exportSQL(){
		port = TethaBackup.getTetha().getConfig().getInt("sql.port");
		host = TethaBackup.getTetha().getConfig().getString("sftp.host");
		db_user = TethaBackup.getTetha().getConfig().getString("sql.username");
		db_pass = TethaBackup.getTetha().getConfig().getString("sql.password");
		try {
			ssh = createSshSession(TethaBackup.getTetha().getConfig().getString("sftp.user"), TethaBackup.getTetha().getConfig().getString("sftp.password"),
					TethaBackup.getTetha().getConfig().getString("sftp.host"), TethaBackup.getTetha().getConfig().getInt("sftp.port"));
			channel = ssh.openChannel("exec");
			((ChannelExec) channel).setErrStream(System.err);
			
		} catch (JSchException e) {
			e.printStackTrace();
		}
		for(String dbs : TethaBackup.getTetha().getConfig().getString("sql.databases-to-save").split(";")){
			try {
				execQuery("mysqldump -u " + db_user + "-p -T " + TethaBackup.getTetha().getConfig().getString("sftp.backup-path") + " " + dbs + " > " + dbs + ".sql", dbs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}

	private  Session createSshSession(String sshUsername, String sshPassword, String host, int port) throws JSchException {
		Session session = new JSch().getSession(sshUsername, host, port);
		session.setPassword(sshPassword);
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();
		return session;
	}
	private String readResult(InputStream in) throws IOException {
		if (in.available() <= 0) {
			return null;
		}

		StringBuilder output = new StringBuilder();
		int nextByte;
		do {
			nextByte = in.read();
			output.append((char) nextByte);
		} while (nextByte != -1);

		return output.toString();
	}
	private ArrayList<ArrayList<String>> execQuery(String query, String database) throws SQLException {
		ArrayList<ArrayList<String>> formattedResult = null;
		try {
			((ChannelExec) channel).setCommand("mysql -u" + db_user + " -p" + db_pass + " -h" + host + " -e'" + query + "' " + database);
			InputStream in = channel.getInputStream();
			channel.connect();
			String result = readResult(in);
			if(result == null){
				throw new SQLException("Invalid SQL-Code");
			}
			formattedResult = new ArrayList<>();
			for(String row: result.split("\n")){
				ArrayList<String> fields = new ArrayList<>();
				fields.addAll(Arrays.asList(row.split("\t")));
				formattedResult.add(fields);
			}

			channel.disconnect();
		} catch (JSchException | IOException ex) {
			Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		return formattedResult;
	}
}
