package org.springblade.energy.diagram.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VideoProcessUtils {

	/**
	 *
	 * @param videoAccount
	 * @param videoPwd
	 * @param ip
	 * @param logName
	 * @param mediaServerIp
	 * @return
	 * @throws Exception
	 * 启动视频的命令
	 * 1：进入到ffmpeg/bin目录和启动进程目录，两个命令
	 * 2：通过进程信息获取pid的时候首先获取的是  1中复合命令的pid. 因此需要停止两次才能把进程杀死。故以下特殊处理
	 * 3：三种解决方式：1：将ffmpeg设为全局变量，2：以下处理方式， 3:第一个pid+1
	 */
	public static String getPID(String videoAccount, String videoPwd,String ip,String logName,String mediaServerIp) throws Exception {
		System.out.println("用法示例:"
			+ "ps -ef|grep -v grep|grep \"./ffmpeg -re -rtsp_transport tcp -i rtsp://admin:abcd1234@169.254.0.26:554/h264/ch1/main/av_stream -b:v 400k -s 720x576 -r 20 -f flv rtmp://169.254.93.102:1935/oflaDemo/zx1 >./zx1.log\" ");
		Process process = null;
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		sb.append("./ffmpeg -re -rtsp_transport tcp -i rtsp://").append(videoAccount).append(":").append(videoPwd).append("@")
			.append(ip)
			.append(":554/h264/ch1/main/av_stream -b:v 400k -s 720x576 -r 20 -f flv rtmp://").append(mediaServerIp).append(":1935/oflaDemo/")
			.append(logName);
//			.append(" > ./")
//			.append(logName)
//			.append(".log");
		try {
			//第一步新进入作业目录--ffmpeg的安装目录 ------找进程不需要进入目录
//			String startDir = "cd /usr/local/ffmpeg/bin";
			//第二步执行的操作
			String exeStr = "ps -ef |grep -v grep |grep "+"\""+sb+"\"";
			System.out.println("获取进程的PID:::"+exeStr);
			String[] linuxCommand = {"/bin/sh","-c",  exeStr };
			// 显示所有进程
			process = Runtime.getRuntime().exec(linuxCommand);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuffer backPidInfo = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains(sb)) {
					System.out.println("找到视频进程信息："+line);
					String[] strs = line.split("\\s+");
//					return strs[1];
					backPidInfo.append(strs[1]).append(",");
				}
			}
			System.out.println("======********PID项目******======:"+backPidInfo);
			return backPidInfo.length()>0?backPidInfo.substring(0,backPidInfo.length()-1).toString():null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (process != null) {
				process.destroy();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}

	}

	/**
	 * 关闭Linux进程
	 *
	 * @param Pid 进程的PID
	 */
	public static void closeLinuxProcess(String Pid) {
		Process process = null;
		BufferedReader reader = null;
		try {
			String[] linuxCommand = {"/bin/sh","-c", "kill -9 " + Pid };
			// 杀掉进程
			System.out.println("准备执行 kill -9 " + Pid);
			process = Runtime.getRuntime().exec(linuxCommand);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println("kill PID return info -----> " + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}
	}


	/**
	 * 启动Linux进程
	 *
	 * @param videoAccount
	 * @param videoPwd
	 * @param ip
	 * @param logName
	 * @param mediaServerIp
	 *
	 */
	public static void startLinuxProcess(String videoAccount, String videoPwd, String ip, String logName,String mediaServerIp) throws Exception {
//		Process process = null;
		BufferedReader reader = null;

		StringBuffer sb = new StringBuffer();
		sb.append("./ffmpeg -re -rtsp_transport tcp -i rtsp://").append(videoAccount).append(":").append(videoPwd).append("@")
			.append(ip)
			.append(":554/h264/ch1/main/av_stream -b:v 400k -s 720x576 -r 20 -f flv rtmp://").append(mediaServerIp).append(":1935/oflaDemo/")
			.append(logName)
			.append(" >./")
			.append(logName)
			.append(".log");
		// 启动进程
		System.out.println("准备启动的命令: " + sb);
		//第一步进入作业目录--ffmpeg的安装目录
		String startDir = "cd /usr/local/ffmpeg/bin";
		//第二步执行的操作
		String exeStr = "nohup " + sb +" &";
		System.out.println("启动要执行的操作:::"+(startDir +" && "+ exeStr));
		String[] linuxCommand = {"/bin/sh","-c", startDir +" && "+ exeStr };
		final Process process = Runtime.getRuntime().exec(linuxCommand);
		try {
//			printMessage(process.getInputStream());
//    		printMessage(process.getErrorStream());
//    		int value = process.waitFor();
//    		System.out.println("=======*****waitFor******=========="+value);

			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder buf = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buf.append(line);
			}
			System.out.println("命令:["+sb+"]启动成功!");
//			return "1";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
//			return null;
		}
	}

	private static void printMessage(final InputStream input) {
		new Thread(new Runnable() {
			public void run() {
				InputStreamReader reader = new InputStreamReader(input);
				BufferedReader bf = new BufferedReader(reader);
				String line = null;
				try {
					while((line=bf.readLine())!=null) {
						System.out.println(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}



