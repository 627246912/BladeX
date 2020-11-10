package org.springblade.energy.diagram.utils;

import org.springblade.util.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class NewVideoProcessUtils {

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
	public static String getAllPID(String videoAccount, String videoPwd,String ip,String logName,String mediaServerIp) throws Exception {
		System.out.println("用法示例:"
			+ "ps -ef|grep -v grep|grep \"./ffmpeg -re -rtsp_transport tcp -i rtsp://admin:abcd1234@169.254.0.26:554/h264/ch1/main/av_stream -b:v 400k -s 720x576 -r 20 -f flv rtmp://169.254.93.102:1935/oflaDemo/zx1 >./zx1.log\" ");
		Process process = null;
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		sb.append("./ffmpeg -re -rtsp_transport tcp -i rtsp://").append(videoAccount).append(":").append(videoPwd).append("@")
			.append(ip)
//			.append(":554/h264/ch1/main/av_stream -b:v 400k -s 720x576 -r 20 -f flv rtmp://").append(mediaServerIp).append(":1935/oflaDemo/")
			//中车用下边这个参数
			.append(":554/h264/ch1/main/av_stream -ar 22050 -s 720x576 -r 20 -f flv rtmp://").append(mediaServerIp).append(":1935/oflaDemo/")
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
		StringBuffer sb = new StringBuffer();
		sb.append("./ffmpeg -re -rtsp_transport tcp -i rtsp://").append(videoAccount).append(":").append(videoPwd).append("@")
			.append(ip)
//			.append(":554/h264/ch1/main/av_stream -b:v 400k -s 720x576 -r 20 -f flv rtmp://").append(mediaServerIp).append(":1935/oflaDemo/")
			//中车用下边这个参数
			.append(":554/h264/ch1/main/av_stream -ar 22050 -s 720x576 -r 20 -f flv rtmp://").append(mediaServerIp).append(":1935/oflaDemo/")
			.append(logName);
//			.append(" > ./")
//			.append(logName)
//			.append(".log");
		String exeStr = "ps -ef |grep -v grep |grep "+"\""+sb+"\"";
		System.out.println("=======获取PID信息:=========="+exeStr);
//		String[] commands = new String[] {"ifconfig","--help"};//OK
		List<String> commands = new ArrayList<String>();
		commands.add("sh");
		commands.add("-c");
		commands.add(exeStr);
		return executeCommand(commands);
	}

	/**
	 * 关闭Linux进程
	 *
	 * @param Pid 进程的PID
	 */
	public static String closeLinuxProcess(String Pid) {
		List<String> commands = new ArrayList<String>();
		commands .add("kill");
		commands .add("-9");
		commands .add(Pid);
		return executeCommand(commands);
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
	public static String startLinuxProcess(String videoAccount, String videoPwd, String ip, String logName,String mediaServerIp) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("./ffmpeg -re -rtsp_transport tcp -i rtsp://").append(videoAccount).append(":").append(videoPwd).append("@")
			.append(ip)
//			.append(":554/h264/ch1/main/av_stream -b:v 400k -s 720x576 -r 20 -f flv rtmp://").append(mediaServerIp).append(":1935/oflaDemo/")
			.append(":554/h264/ch1/main/av_stream -ar 22050 -s 720x576 -r 20 -f flv rtmp://").append(mediaServerIp).append(":1935/oflaDemo/")
			.append(logName);
//			.append(" > ./")
//			.append(logName)
//			.append(".log");
		List<String> commands = new ArrayList<String>();
		commands.add("sh");
		commands.add("-c");
		commands.add(sb.toString());
		return executeCommand(commands);
	}


	/**
	 * 执行FFmpeg命令
	 * @param commonds FFmpeg命令
	 * @return FFmpeg执行命令过程中产生的各信息，执行出错时返回null
	 */
	public static String executeCommand(List<String> commonds) {
		if (CollectionUtils.isEmpty(commonds)) {
			System.out.println("--- 指令执行失败，因为要执行的FFmpeg指令为空！ ---");
		}
		LinkedList<String> ffmpegCmds = new LinkedList<>(commonds);

		Runtime runtime = Runtime.getRuntime();
		Process ffmpeg = null;
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.directory(new File("/usr/local/ffmpeg/bin"));//执行自定义的一个命令,该命令放在/usr/local/ffmpeg/bin
			builder.command(ffmpegCmds);
			ffmpeg = builder.start();
			String result = waitForExcute(ffmpeg);

			// 输出执行的命令信息
			System.out.println("=============result****************============="+result);
			String cmdStr = Arrays.toString(ffmpegCmds.toArray()).replace(",", "");
			String resultStr = result != null ? "正常" : "【异常】";
			System.out.println("--- FFmepg命令： ---" + cmdStr + " 已执行完毕,执行结果：" + resultStr);
			return result;

		} catch (IOException e) {
			System.out.println("--- FFmpeg命令执行出错！ --- 出错信息： " + e.getMessage());
			return null;

		} finally {
			if (null != ffmpeg) {
				ProcessKiller ffmpegKiller = new ProcessKiller(ffmpeg);
				// JVM退出时，先通过钩子关闭FFmepg进程
				runtime.addShutdownHook(ffmpegKiller);
			}
		}
	}

	/**
	 * FFmpeg进程执行输出，必须使用此函数，否则会出现进程阻塞现象
	 * 当FFmpeg进程执行完所有命令后，本函数返回FFmpeg进程退出时的状态值；
	 * @param process
	 * @return 进程执行命令过程中产生的各种信息，执行命令过程出错时返回null
	 */
	private static String waitForExcute(Process process) {
		InputStream inputStream = null;
		InputStream errorStream = null;
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		StringBuffer returnStr = new StringBuffer(); // 存储FFmpeg执行命令过程中产生的信息
		int exitValue = -1;
		try {
			inputStream = process.getInputStream();
			errorStream = process.getErrorStream();
			br1 = new BufferedReader(new InputStreamReader(inputStream));
			br2 = new BufferedReader(new InputStreamReader(errorStream));
			boolean finished = false;
			while (!finished) {
				try { // while内部使用一个try-catch块，这样当某一次循环读取抛出异常时，可以结束当次读取，返回条件处开始下一次读取
					String line1 = null;
					String line2 = null;
					while ((line1 = br1.readLine()) != null) {
						System.out.println(line1);
					}
					while ((line2 = br2.readLine()) !=  null) {
						System.out.println(line2);
						returnStr.append(line2 + "\n");
					}
					exitValue = process.exitValue();
					finished = true;
				} catch (IllegalThreadStateException e) { // 防止线程的start方法被重复调用
					System.out.println("--- 本次读取标准输出流或错误流信息出错 --- 错误信息： " + e.getMessage());
					Thread.sleep(500);
				} catch (Exception e2) {
					System.out.println("--- 本次读取标准输出流或错误流信息出错 --- 错误信息： " + e2.getMessage());
				}
			}
			return returnStr.toString();
		} catch (Exception e) {
			System.out.println("--- 执行FFmpeg程序时读取标准输出或错误流的信息出错 ---");
			return null;
		} finally {
			try {
				if (null != br1) {
					br1.close();
				}
				if (null != br2) {
					br2.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != errorStream) {
					errorStream.close();
				}
			} catch (IOException e) {
				System.out.println("--- 关闭读取的标准输出流或错误流时出错 ---");
			}
		}
	}

	/**
	 * 在程序退出前结束已有的FFmpeg进程
	 */
	private static class ProcessKiller extends Thread {
		private Process process;
		public ProcessKiller(Process process) {
			this.process = process;
		}
		@Override
		public void run() {
			this.process.destroy();
			System.out.println("--- 已销毁FFmpeg进程 --- 进程名： " + process.toString());
		}
	}

}
