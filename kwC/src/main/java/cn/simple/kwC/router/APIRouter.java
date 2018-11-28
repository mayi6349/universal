package cn.simple.kwC.router;

import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;

import cn.simple.kwC.controller.GameController;
import cn.simple.kwC.controller.GroupGameOrderController;
import cn.simple.kwC.controller.UserController;
import cn.simple.kwC.controller.UserGameOrderController;

/**
 * @author may
 */
public class APIRouter extends Routes {
	@Override
	public void config() {

		// 用户模块
		add(PropKit.get("user"),UserController.class);
		add(PropKit.get("game"),GameController.class);
		add(PropKit.get("game.group"),GroupGameOrderController.class);
		add(PropKit.get("game.user"),UserGameOrderController.class);
	}
}
