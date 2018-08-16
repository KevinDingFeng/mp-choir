package com.shenghesun.util.audio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

 /**
  * 音乐剪切和拼接
  * @ClassName: CutMusic 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月16日 下午6:17:30  
  */
public class CutMusic {
	
	public static final int BUFFER_SIZE = 512;

	public static void cut1(File f1, File f2, File f) {
		BufferedInputStream bis1 = null;
		BufferedInputStream bis2 = null;
		BufferedOutputStream bos = null;
		// 第一首歌剪切部分起始字节
		int start1 = 16384;// 320kbps（比特率）*58s*1024/8=2375680 比特率可以查看音频属性获知
		int end1 = 81920;// 320kbps*120s*1024/8=4915200

		// 第二首歌剪切部分起始字节，计算方式同上
		int start2 = 81920;
		int end2 = 163840;

		int tatol1 = 0;
		int tatol2 = 0;
		try {
			// 两个输入流
			bis1 = new BufferedInputStream(new FileInputStream(f1));
			bis2 = new BufferedInputStream(new FileInputStream(f2));
			// 缓冲字节输出流（true表示可以在流的后面追加数据，而不是覆盖！！）
			bos = new BufferedOutputStream(new FileOutputStream(f, true));

			// 第一首歌剪切、写入
			byte[] b1 = new byte[512];
			int len1 = 0;
			while ((len1 = bis1.read(b1)) != -1) {
				tatol1 += len1; // 累积tatol
				if (tatol1 < start1) { // tatol小于起始值则跳出本次循环
					continue;
				}
				//System.out.println("===="+tatol1);
				bos.write(b1); // 写入的都是在我们预先指定的字节范围之内
				if (tatol1 >= end1) { // 当tatol的值超过预先设定的范围，则立刻刷新bos流对象，并结束循环
					bos.flush();
					break;
				}

			}
			System.out.println("第一首歌剪切完成！");

			// 第二首歌剪切、写入，原理同上
			byte[] b2 = new byte[512];
			int len2 = 0;
			while ((len2 = bis2.read(b2)) != -1) {
				tatol2 += len2;
				if (tatol2 < start2) {
					continue;
				}
				bos.write(b2);
				if (tatol2 >= end2) {
					bos.flush();
					break;
				}

			}
			System.out.println("第二首歌剪切完成！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {// 切记要关闭流！！
				if (bis1 != null)
					bis1.close();
				if (bis2 != null)
					bis2.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建文件
	 * @Title: createFile 
	 * @Description: TODO 
	 * @param path
	 * @param fileName
	 * @return  File 
	 * @author yangzp
	 * @date 2018年8月16日下午6:12:34
	 **/ 
	public static File createFile(String path, String fileName) {
		// String path= "G:\\yuchao\\测试";//所创建文件的路径
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();// 创建目录
		}
		// String fileName = "abc.txt";//文件名及类型
		File file = new File(path, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	
	/**
	 * @param targetFile 输出的文件
	 * @param sourceFile 读取的文件
	 * @param buffer       输入输出的缓存容器
	 * @param offset     读入文件时seek的偏移值
	 */
	private static void writeSourceToTargetFile(RandomAccessFile targetFile, 
	                            RandomAccessFile sourceFile,
	                            byte buffer[], long offset) throws Exception {
	    sourceFile.seek(offset);
	    sourceFile.read(buffer);
	    long fileLength = targetFile.length();
	    // 将写文件指针移到文件尾。
	    targetFile.seek(fileLength);
	    targetFile.write(buffer);
	}
	
	/**
	 * 写入数据
	 * @Title: writeSourceToTargetFileWithBuffer 
	 * @Description: TODO 
	 * @param targetFile
	 * @param sourceFile
	 * @param totalSize
	 * @param offset
	 * @throws Exception  void 
	 * @author yangzp
	 * @date 2018年8月16日下午4:26:26
	 **/ 
	private static void writeSourceToTargetFileWithBuffer(RandomAccessFile targetFile, RandomAccessFile sourceFile,
			long totalSize, long offset) throws Exception {
		// 缓存大小，每次写入指定数据防止内存泄漏
		int buffersize = BUFFER_SIZE;
		long count = totalSize / buffersize;
		if (count <= 1) {
			// 文件总长度小于小于缓存大小情况
			writeSourceToTargetFile(targetFile, sourceFile, new byte[(int) totalSize], offset);
		} else {
			// 计算出整除后剩余的数据数
			long remainSize = totalSize % buffersize;
			byte data[] = new byte[buffersize];
			// 读入文件时seek的偏移量
			for (int i = 0; i < count; i++) {
				writeSourceToTargetFile(targetFile, sourceFile, data, offset);
				offset += BUFFER_SIZE;
			}
			// 写入剩余数据
			if (remainSize > 0) {
				writeSourceToTargetFile(targetFile, sourceFile, new byte[(int) remainSize], offset);
			}
		}
	}
	
	/**
	 * 生成目标mp3文件
	 * @Title: generateTargetMp3File 
	 * @Description: TODO 
	 * @param targetFile 目标文件
	 * @param sourceFile 
	 * @param beginByte 开始字节
	 * @param endByte 结束字节
	 * @param firstFrameByte  头字节 
	 * @author yangzp
	 * @date 2018年8月16日下午4:24:26
	 **/ 
//	private static void generateTargetMp3File_(RandomAccessFile targetFile, RandomAccessFile sourceFile,
//	                                   long beginByte, long endByte, long firstFrameByte){
//	    //RandomAccessFile sourceFile = new RandomAccessFile("mSourceMp3File", "rw");
//	    try {
//	        //write mp3 header info
//	        writeSourceToTargetFileWithBuffer(targetFile, sourceFile, firstFrameByte, 0);
//	        //write mp3 frame info
//	        int size = (int) (endByte - beginByte);
//	        writeSourceToTargetFileWithBuffer(targetFile, sourceFile, size, beginByte);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    } finally {
//	    	try {
//	    		if (sourceFile != null)
//					sourceFile.close();
//	    		if(targetFile != null)
//	    			targetFile.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//	    }
//	}
	
	/**
	 * 生成目标mp3文件
	 * @Title: generateTargetMp3File 
	 * @Description: TODO 
	 * @param targetFile 
	 * @param sourceFile
	 * @param beginMsec
	 * @param endMsec  
	 * void 
	 * @author yangzp
	 * @date 2018年8月16日下午6:15:31
	 **/ 
	public static void generateTargetMp3File(File targetFile, File sourceFile, long beginMsec,long endMsec) {
		// RandomAccessFile sourceFile = new RandomAccessFile("mSourceMp3File", "rw");
		
		try {
			RandomAccessFile tarRaf = new RandomAccessFile(targetFile,"rw");
			RandomAccessFile sourRaf = new RandomAccessFile(sourceFile,"rw");
			
			MP3File mp3File = (MP3File) AudioFileIO.read(sourceFile);
			MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
			//头字节
			long firstFrameByte = audioHeader.getMp3StartByte();
			//根据元数据获取比特率
			long bitRateKbps = audioHeader.getBitRateAsNumber();
			//计算出开始字节位置  (根据比特率计算 ：千字节/毫秒 bitRateKbps *1024L / 8L * 时间(毫秒) / 1000L)
			long bitRatebpm = bitRateKbps *1024L / 8L * beginMsec /1000L; 
			//计算出头字节+开始字节位置
			long beginByte = firstFrameByte + bitRatebpm;
			//计算出结束字节位置
			long endByte = beginByte + bitRateKbps *1024L / 8L * (endMsec-beginMsec) /1000L;
			
			// write mp3 header info
			writeSourceToTargetFileWithBuffer(tarRaf, sourRaf, firstFrameByte, 0);
			// write mp3 frame info
			int size = (int) (endByte - beginByte);
			
			writeSourceToTargetFileWithBuffer(tarRaf, sourRaf, size, beginByte);
			
			try {
				if (sourRaf != null)
					sourRaf.close();
				if (tarRaf != null)
					tarRaf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) throws Exception {
		// f1,f2分别为需要剪切的歌曲路径
		File f1 = new File("C:\\Users\\yangzp\\Music\\贝瓦儿歌 - 我是小海军.mp3");
		File f2 = new File("C:\\Users\\yangzp\\Music\\贝瓦儿歌 - 小毛驴.mp3");
		// f为合并的歌曲
		File f = createFile("C:/Users/yangzp/Music","MergeMusic22.mp3");
		cut1(f2, f1, f);
//		
//		getMp3Time(f2);
		
		generateTargetMp3File(f, f2,1000, 5000);
	}
}