package com.shenghesun.util.audio;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 分割歌词,分割背景歌曲
 * 
 * @ClassName: CutSong
 * @Description: TODO
 * @author: yangzp
 * @date: 2018年8月30日 下午5:22:46
 */
public class CutSong {
	
	public static void main(String[] args) {
		cutLrc("[00:22.73],[00:38.59]");
//		String str = "[03:13.28]一根藤上七朵花。";
//		System.out.println(str.substring(10));
//		int lrcTimeInt = parseLrcTime2Int(str);
//		String lrcStr = parseInt2LrcTime(lrcTimeInt);
//		System.out.println("lrcStr=" + lrcStr);
//		
//		String ss = getSubtractLrcTime("[03:13.28]","[02:59.24]");
//		System.out.println("ss=="+ss);
	}
	
	private static String addZero(int i) {
		if(i<10) {
			return "0"+i;
		}
		return i+"";
	}
	/**
	 * 把毫秒转换成歌词格式的时间
	 * @Title: parseInt2LrcTime 
	 * @Description: TODO 
	 * @param lrcTimeInt
	 * @return  String [03:13.28]
	 * @author yangzp
	 * @date 2018年8月31日下午1:33:27
	 **/ 
	private static String parseInt2LrcTime(int lrcTimeInt) {
		
		int minute = (int)(lrcTimeInt / 60000);
		lrcTimeInt %= 60000;
		int second = (int)(lrcTimeInt / 1000);
		lrcTimeInt %= 1000;
		int ms = (int)(lrcTimeInt / 10);
		//"[03:13.28]一根藤上七朵花。"
		String result = "["+addZero(minute)+":" + addZero(second) + "." + addZero(ms) + "]";
		
		return result;
	}
	
	/**
	 * 把歌词时间转换成int毫秒
	 * @Title: parseLrcTime2Int 
	 * @Description: TODO 
	 * @param lrcTime [03:13.28]
	 * @return  int 
	 * @author yangzp
	 * @date 2018年8月31日下午1:32:44
	 **/ 
	private static int parseLrcTime2Int(String lrcTime) {
		int minute = Integer.parseInt(lrcTime.substring(1,3));
		int second = Integer.parseInt(lrcTime.substring(4,6));
		int ms = Integer.parseInt(lrcTime.substring(7,9));
		
		int time = minute * 60 * 1000 + second * 1000 + ms * 10;
		
		return time;
	}
	
	/**
	 * 计算分割后的时间
	 * @Title: getSubtractLrcTime 
	 * @Description: TODO 
	 * @param sourceTime 分割后的原时间
	 * @param subtractTime 需要减去的时间
	 * @return  String 
	 * @author yangzp
	 * @date 2018年8月31日下午1:44:10
	 **/ 
	private static String getSubtractLrcTime(String sourceTime,String subtractTime) {
		int sourctTemeInt = parseLrcTime2Int(sourceTime);
		int subtractTimeInt = parseLrcTime2Int(subtractTime);
		int resultInt = sourctTemeInt - subtractTimeInt;
		
		return parseInt2LrcTime(resultInt);
	}

	public static void cutLrc(String timeQuantum ) {
		
		timeQuantum = "[00:13.28],[00:16.76],[00:19.75],[00:22.73],[00:26.11],[00:38.59]";
		String [] timeArry = timeQuantum.split(",");
		
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = "";
			StringBuffer strBfr = new StringBuffer();
			fis = new FileInputStream("F:\\文档\\小程序\\美拍合唱团\\背景音乐\\歌词\\葫芦娃.lrc");// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
			while ((str = br.readLine()) != null) {//// 当读取的一行不为空时,把读到的str的值赋给str1
				strBfr.append(str+"@###@%%%@");
			}
			//System.out.println(strBfr.toString());// 打印出str1
			String [] lrcArry = strBfr.toString().split("@###@%%%@");
			
			String result = "";
			int j = 0;
			for(int i=0;i<timeArry.length;i++) {
				String subStr = "";
				for(;j<lrcArry.length;j++) {
					if(i==0) {//第一段，不做修改
						subStr += lrcArry[j]+"\n";
					}else {
						String lrcParagraph = lrcArry[j];
						//System.out.println("lrcParagraph"+j+"=="+lrcParagraph);
						//分段时间
						String time = timeArry[i-1];
						//System.out.println("time"+j+"=="+time);
						String subTime =  getSubtractLrcTime(lrcParagraph,time);
						//String subTime = "";
//						if(time.equals(lrcParagraph.substring(0, 10))) {
//							subTime =  "[00:00.00]";
//						}else {
//							subTime =  getSubtractLrcTime(lrcParagraph,lrcArry[j-1]);
//						}
						//System.out.println("subTime="+j+"=="+subTime);
						String subtract = subTime + lrcParagraph.substring(time.length());
						subStr += subtract+"\n";
					}
					
					if(lrcArry[j].indexOf(timeArry[i])>-1) {
						subStr = subStr.substring(0,subStr.lastIndexOf("]")+1)+"\n";
						break;
					}
				}
				System.out.println(i+":");
				System.out.println(subStr);
				result += subStr + "333";
			}
			
			//System.out.println(result);
			
		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
		} catch (IOException e) {
			System.out.println("读取文件失败");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
