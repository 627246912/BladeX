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
package org.springblade.energy.diagram.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.entity.DiagramCamera;
import org.springblade.energy.diagram.service.IDiagramCameraService;
import org.springblade.energy.diagram.utils.NewVideoProcessUtils;
import org.springblade.energy.diagram.vo.DiagramCameraVO;
import org.springblade.energy.diagram.wrapper.DiagramCameraWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-05-18
 */
@RestController
@AllArgsConstructor
@RequestMapping("/diagramcamera")
@Api(value = "系统图摄像头", tags = "系统图摄像头接口")
public class DiagramCameraController extends BladeController {

	private IDiagramCameraService diagramCameraService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入diagramCamera")
	public R<DiagramCameraVO> detail(DiagramCamera diagramCamera) {
		DiagramCamera detail = diagramCameraService.getOne(Condition.getQueryWrapper(diagramCamera));
		return R.data(DiagramCameraWrapper.build().entityVO(detail));
	}

//	/**
//	 * 分页
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入diagramCamera")
//	public R<IPage<DiagramCameraVO>> list(DiagramCamera diagramCamera, Query query) {
//		IPage<DiagramCamera> pages = diagramCameraService.page(Condition.getPage(query), Condition.getQueryWrapper(diagramCamera));
//		return R.data(DiagramCameraWrapper.build().pageVO(pages));
//	}


	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入diagramCamera")
	public R<IPage<DiagramCameraVO>> page(DiagramCameraVO diagramCamera, Query query) {
		IPage<DiagramCameraVO> pages = diagramCameraService.selectDiagramCameraPage(Condition.getPage(query), diagramCamera);
		return R.data(pages);
	}

	/**
	 * 新增
	 */
	@ApiLog("新增 系统图摄像头")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入diagramCamera")
	public R save(@Valid @RequestBody DiagramCamera diagramCamera) {
		return R.status(diagramCameraService.save(diagramCamera));
	}

	/**
	 * 修改
	 */
	@ApiLog("修改 系统图摄像头")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入diagramCamera")
	public R update(@Valid @RequestBody DiagramCamera diagramCamera) {
		return R.status(diagramCameraService.updateById(diagramCamera));
	}

	/**
	 * 新增或修改
	 */
	@ApiLog("新增或修改 系统图摄像头")
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入diagramCamera")
	public R submit(@Valid @RequestBody DiagramCamera diagramCamera) {
		return R.status(diagramCameraService.saveOrUpdate(diagramCamera));
	}


	/**
	 * 删除
	 */
	@ApiLog("删除 系统图摄像头")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(diagramCameraService.deleteLogic(Func.toLongList(ids)));
	}


	/**
	 * 启动-停止    1:启动   0:停止
	 */
	@ApiLog("启动停止摄像头")
	@PostMapping("/startOrStop")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "启动停止摄像头", notes = "传入diagramCamera")
	public R startOrStop(@Valid @RequestBody DiagramCamera diagramCamera) {
		//先通过id查询
		String status = diagramCamera.getVideoStatus();
		diagramCamera.setVideoStatus(status.equals("1")?"0":"1");//查询的时候需要查相反的状态
		DiagramCamera detail = diagramCameraService.getOne(Condition.getQueryWrapper(diagramCamera));
		System.out.println("========查询视频信息========"+detail);
		//更新状态
		if(detail==null){
			return R.status(false);
		}
		detail.setVideoStatus(status);
		System.out.println("=========更新视频状态=========="+detail);
		diagramCameraService.updateById(detail);
		if(status.equals("1")){//启动服务
			new Thread(new Runnable() {
				public void run() {
					try {
						String start = NewVideoProcessUtils.startLinuxProcess(detail.getVideoAccount(),detail.getVideoPwd(),detail.getVideoIp(),detail.getVideoName(),detail.getMediaServerIp());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}else{//停止服务
			new Thread(new Runnable() {
				public void run() {
					try {
						String pid = NewVideoProcessUtils.getAllPID(detail.getVideoAccount(),detail.getVideoPwd(),detail.getVideoIp(),detail.getVideoName(),detail.getMediaServerIp());
						System.out.println("=========获取到的PID信息:::"+pid);
						if(null != pid){
							String[] pids = pid.split(",");
							for(String p: pids){
								NewVideoProcessUtils.closeLinuxProcess(p);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

		}
		return R.status(true);
	}


}
