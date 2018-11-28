package cn.simple.kwC;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.nmtx.doc.core.api.jfinal.JFinalApiDocConfig;

import cn.simple.kwC.controller.GameController;
import cn.simple.kwC.controller.GroupGameOrderController;
import cn.simple.kwC.controller.UserController;
import cn.simple.kwC.controller.UserGameOrderController;

/**
 * @author may
 *
 */
public class JDocMain {
	public static void main(String[] args) {

		JFinalApiDocConfig jFinalApiDocConfig = new JFinalApiDocConfig("jdoc.properties");
		Prop p = PropKit.use("resources.properties");
		String user = p.get("user");
		String game = p.get("game");
		String gameGroup = p.get("game.group");
		String gameUser = p.get("game.user");
		jFinalApiDocConfig.add(user.substring(0, user.length() - 1), UserController.class);
		jFinalApiDocConfig.add(game.substring(0, game.length() - 1), GameController.class);
		jFinalApiDocConfig.add(gameGroup.substring(0, gameGroup.length() - 1), GroupGameOrderController.class);
		jFinalApiDocConfig.add(gameUser.substring(0, gameUser.length() - 1), UserGameOrderController.class);
		jFinalApiDocConfig.setUseClearSuffix(false);
		jFinalApiDocConfig.start();
	}
}
