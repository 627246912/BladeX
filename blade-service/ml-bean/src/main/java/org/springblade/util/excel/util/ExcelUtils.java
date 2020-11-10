/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.util.excel.util;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.SneakyThrows;
import org.apache.commons.codec.Charsets;
import org.springblade.util.excel.listener.DataListener;
import org.springblade.util.excel.listener.ImportListener;
import org.springblade.util.excel.support.ExcelException;
import org.springblade.util.excel.support.ExcelImporter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * Excel工具类
 *
 * @author Chill
 * @apiNote https://www.yuque.com/easyexcel/doc/easyexcel
 */
public class ExcelUtils {

	/**
	 * 读取excel的所有sheet数据
	 *
	 * @param excel excel文件
	 * @return List<Object>
	 */
	public static <T> List<T> read(MultipartFile excel, Class<T> clazz) {
		DataListener<T> dataListener = new DataListener<>();
		ExcelReaderBuilder builder = getReaderBuilder(excel, dataListener, clazz);
		if (builder == null) {
			return null;
		}
		builder.doReadAll();
		return dataListener.getDataList();
	}

	/**
	 * 读取excel的指定sheet数据
	 *
	 * @param excel   excel文件
	 * @param sheetNo sheet序号(从0开始)
	 * @return List<Object>
	 */
	public static <T> List<T> read(MultipartFile excel, int sheetNo, Class<T> clazz) {
		return read(excel, sheetNo, 1, clazz);
	}

	/**
	 * 读取excel的指定sheet数据
	 *
	 * @param excel         excel文件
	 * @param sheetNo       sheet序号(从0开始)
	 * @param headRowNumber 表头行数
	 * @return List<Object>
	 */
	public static <T> List<T> read(MultipartFile excel, int sheetNo, int headRowNumber, Class<T> clazz) {
		DataListener<T> dataListener = new DataListener<>();
		ExcelReaderBuilder builder = getReaderBuilder(excel, dataListener, clazz);
		if (builder == null) {
			return null;
		}
		builder.sheet(sheetNo).headRowNumber(headRowNumber).doRead();
		return dataListener.getDataList();
	}

	/**
	 * 读取并导入数据
	 *
	 * @param excel    excel文件
	 * @param importer 导入逻辑类
	 * @param <T>      泛型
	 */
	public static <T> void save(MultipartFile excel, ExcelImporter<T> importer, Class<T> clazz) {
		ImportListener<T> importListener = new ImportListener<>(importer);
		ExcelReaderBuilder builder = getReaderBuilder(excel, importListener, clazz);
		if (builder != null) {
			builder.doReadAll();
		}
	}

	/**
	 * 导出excel
	 *
	 * @param response 响应类
	 * @param dataList 数据列表
	 * @param clazz    class类
	 * @param <T>      泛型
	 */
	@SneakyThrows
	public static <T> void export(HttpServletResponse response, List<T> dataList, Class<T> clazz) {
		export(response, DateUtils.format(new Date(), DateUtils.DATE_FORMAT_14), "导出数据", dataList, clazz);
	}

	/**
	 * 导出excel
	 *
	 * @param response  响应类
	 * @param fileName  文件名
	 * @param sheetName sheet名
	 * @param dataList  数据列表
	 * @param clazz     class类
	 * @param <T>       泛型
	 */
	@SneakyThrows
	public static <T> void export(HttpServletResponse response, String fileName, String sheetName, List<T> dataList, Class<T> clazz) {
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding(Charsets.UTF_8.name());
		fileName = URLEncoder.encode(fileName, Charsets.UTF_8.name());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		EasyExcel.write(response.getOutputStream(), clazz).sheet(sheetName).doWrite(dataList);
	}

	@SneakyThrows
	public static <T> void export(HttpServletResponse response, String fileName, List<String> sheetNames, List<List<T>> dataLists, Class<T> clazz) {
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding(Charsets.UTF_8.name());
		fileName = URLEncoder.encode(fileName, Charsets.UTF_8.name());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();

		for(int i=0;i<sheetNames.size();i++){
			WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetNames.get(i)).head(clazz).build();
			excelWriter.write(dataLists.get(i), writeSheet);
		}
		excelWriter.finish();
	}

	/**
	 * 获取构建类
	 *
	 * @param excel        excel文件
	 * @param readListener excel监听类
	 * @return ExcelReaderBuilder
	 */
	public static <T> ExcelReaderBuilder getReaderBuilder(MultipartFile excel, ReadListener<T> readListener, Class<T> clazz) {
		String filename = excel.getOriginalFilename();
		if (StringUtils.isEmpty(filename)) {
			throw new ExcelException("请上传文件!");
		}
		if ((!StringUtils.endsWithIgnoreCase(filename, ".xls") && !StringUtils.endsWithIgnoreCase(filename, ".xlsx"))) {
			throw new ExcelException("请上传正确的excel文件!");
		}
		InputStream inputStream;
		try {
			inputStream = new BufferedInputStream(excel.getInputStream());
			return EasyExcel.read(inputStream, clazz, readListener);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
