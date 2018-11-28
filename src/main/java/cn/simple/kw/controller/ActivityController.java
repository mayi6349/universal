package cn.simple.kw.controller;

import cn.simple.kw.BaseAPIController;
import cn.simple.kwA.ioc.Autowired;
import cn.simple.kwA.service.GameService;
import cn.simple.kwA.service.SysService;
import cn.simple.kwA.service.UserService;

/**
 * 活动模块
 * 
 * @author may
 *
 */
public class ActivityController extends BaseAPIController {

	@Autowired
	private UserService userService;

	@Autowired
	private GameService gameService;
	
	@Autowired
	private SysService sysService;
}
