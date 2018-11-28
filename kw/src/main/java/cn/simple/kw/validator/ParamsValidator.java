package cn.simple.kw.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.validate.Validator;

import net.sf.json.JSONObject;

/**
 * 存放校验条件和响应信息
 * 
 * @author may
 */
public class ParamsValidator extends Validator {

	private final Logger logger = Logger.getLogger(ParamsValidator.class);

	private String actionKey;

	private String methodName;

	@Override
	protected void validate(Controller c) {

		// 单个错误就返回
		setShortCircuit(true);
		// URI
		actionKey = getViewPath();
		// actionKey = getActionKey();
		// 方法名
		methodName = getActionMethod().getName();

		// 系统模块
		if (actionKey.contains("/sys/")) {
			sysValidate(c);
			// 用户
		} else if (actionKey.contains("/user/")) {
			userValidate(c);
		} else if (actionKey.contains("/home/")) {
			homeValidate(c);
		} else if (actionKey.contains("/bounty/")) {
			bountyValidate(c);
		}
	}

	/**
	 * 错误操作
	 */
	@Override
	protected void handleError(Controller c) {

		JSONObject result = new JSONObject();
		result.put("status", "1");
		result.put("info", c.getAttr(getViewPath()).toString());
		// result.put("info", c.getAttr(getActionKey()).toString());
		c.renderJson(result.toString());
		logger.error(result.toString());
	}

	/**
	 * 系统Validate
	 * 
	 * @param c
	 * @param actionKey
	 * @param params
	 */
	private void bountyValidate(Controller c) {

		Integer userId = null;
		Integer index = null;
		Integer count = null;
		Integer step = null;
		Integer gameId = null;
		String groupNo = "";
		switch (methodName) {
		case "index":
			userId = c.getParaToInt("userId");
			step = c.getParaToInt("step");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(step, actionKey, "step不能为空");
			break;
		case "streamList":
			userId = c.getParaToInt("userId");
			index = c.getParaToInt("index");
			count = c.getParaToInt("count");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(index, actionKey, "index不能为空");
			validateRequiredParams(count, actionKey, "count不能为空");
			break;
		case "ongoingList":
			userId = c.getParaToInt("userId");
			index = c.getParaToInt("index");
			count = c.getParaToInt("count");
			step = c.getParaToInt("step");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(step, actionKey, "step不能为空");
			validateRequiredParams(index, actionKey, "index不能为空");
			validateRequiredParams(count, actionKey, "count不能为空");
			break;
		case "enrolledList":
			userId = c.getParaToInt("userId");
			index = c.getParaToInt("index");
			count = c.getParaToInt("count");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(index, actionKey, "index不能为空");
			validateRequiredParams(count, actionKey, "count不能为空");
			break;
		case "detail":
			userId = c.getParaToInt("userId");
			gameId = c.getParaToInt("gameId");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(gameId, actionKey, "gameId不能为空");
			break;
		case "groupIndex":
			userId = c.getParaToInt("userId");
			gameId = c.getParaToInt("gameId");
			groupNo = c.getPara("groupNo");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(gameId, actionKey, "gameId不能为空");
			validateRequiredParams(groupNo, actionKey, "groupNo不能为空");
			break;
		case "groupUserList":
			userId = c.getParaToInt("userId");
			gameId = c.getParaToInt("gameId");
			groupNo = c.getPara("groupNo");
			index = c.getParaToInt("index");
			count = c.getParaToInt("count");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(gameId, actionKey, "gameId不能为空");
			validateRequiredParams(groupNo, actionKey, "groupNo不能为空");
			validateRequiredParams(index, actionKey, "index不能为空");
			validateRequiredParams(count, actionKey, "count不能为空");
			break;
		case "groupEnrollIndex":
			userId = c.getParaToInt("userId");
			gameId = c.getParaToInt("gameId");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(gameId, actionKey, "gameId不能为空");
			break;
		case "groupEnrollUserList":
			userId = c.getParaToInt("userId");
			gameId = c.getParaToInt("gameId");
			index = c.getParaToInt("index");
			count = c.getParaToInt("count");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(gameId, actionKey, "gameId不能为空");
			validateRequiredParams(index, actionKey, "index不能为空");
			validateRequiredParams(count, actionKey, "count不能为空");
			break;
		default:
			break;
		}
	}

	/**
	 * 系统Validate
	 * 
	 * @param c
	 * @param actionKey
	 * @param params
	 */
	private void sysValidate(Controller c) {

		Integer userId = null;
		switch (methodName) {
		// 获取验证码
		case "login":
			String code = c.getPara("code");
			validateRequiredParams(code, actionKey, "code不能为空");
			break;
		case "getUserRunInfo":
			userId = c.getParaToInt("userId");
			String encryptedData = c.getPara("encryptedData");
			String iv = c.getPara("iv");
			if (encryptedData.indexOf(" ") != -1) {
				addError(actionKey, "encryptedData存在非法字符!");
			}
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(encryptedData, actionKey, "encryptedData不能为空");
			validateRequiredParams(iv, actionKey, "iv不能为空");
			break;
		case "upUserInfo":
			userId = c.getParaToInt("userId");
			String nickName = c.getPara("nickName");
			String iconUrl = c.getPara("iconUrl");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(nickName, actionKey, "nickName不能为空");
			validateRequiredParams(iconUrl, actionKey, "iconUrl不能为空");
			break;
		case "createGame":
			userId = c.getParaToInt("userId");
			String title = c.getPara("title");
			Integer entryFee = c.getParaToInt("entryFee");
			Integer reachStep = c.getParaToInt("reachStep");
			String isOpen = c.getPara("isOpen");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(title, actionKey, "title不能为空");
			validateRequiredParams(reachStep, actionKey, "reachStep不能为空");
			validateRequiredParams(entryFee, actionKey, "entryFee不能为空");
			validateRequiredParams(isOpen, actionKey, "isOpen不能为空");
			break;
		case "enroll":
			userId = c.getParaToInt("userId");
			Integer gameId = c.getParaToInt("gameId");
			String payType = c.getPara("payType");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(gameId, actionKey, "gameId不能为空");
			validateRequiredParams(payType, actionKey, "payType不能为空");
		case "addBaseEnrollCount":
			userId = c.getParaToInt("userId");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			break;
		case "transfers":
			userId = c.getParaToInt("userId");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			String amount = c.getPara("amount");
			validateRequiredParams(amount, actionKey, "请输入提现金额");
			if (amount.contains(".")) {
				validateRequiredParams("", actionKey, "每天可整数提现一次");
			} 
			if (Integer.valueOf(amount).compareTo(2) < 0) {
				validateRequiredParams("", actionKey, "最小提现金额2元");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 用户Validate
	 * 
	 * @param c
	 * @param actionKey
	 * @param params
	 */
	private void userValidate(Controller c) {

		Integer userId = null;
		switch (methodName) {
		// 获取验证码
		case "index":
			userId = c.getParaToInt("userId");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			break;
		case "enrollInfo":
			userId = c.getParaToInt("userId");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			break;
		case "orderIndex":
			userId = c.getParaToInt("userId");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			break;
		case "gameOrderList":
			userId = c.getParaToInt("userId");
			Integer index = c.getParaToInt("index");
			Integer count = c.getParaToInt("count");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(index, actionKey, "index不能为空");
			validateRequiredParams(count, actionKey, "count不能为空");
			break;
		case "gameOrder":
			userId = c.getParaToInt("userId");
			Integer id = c.getParaToInt("id");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(id, actionKey, "id不能为空");
		default:
			break;
		}
	}

	/**
	 * 首页Validate
	 * 
	 * @param c
	 * @param actionKey
	 * @param params
	 */
	private void homeValidate(Controller c) {

		Integer userId = null;
		switch (methodName) {
		// 获取验证码
		case "index":
//			userId = c.getParaToInt("userId");
//			validateRequiredParams(userId, actionKey, "userId不能为空");
			break;
		case "gameList":
			String gameStatus = c.getPara("gameStatus");
			Integer index = c.getParaToInt("index");
			Integer count = c.getParaToInt("count");
			validateRequiredParams(gameStatus, actionKey, "gameStatus不能为空");
			validateRequiredParams(index, actionKey, "index不能为空");
			validateRequiredParams(count, actionKey, "count不能为空");
			break;
		case "gameInfo":
			userId = c.getParaToInt("userId");
			Integer gameId = c.getParaToInt("gameId");
			validateRequiredParams(userId, actionKey, "userId不能为空");
			validateRequiredParams(gameId, actionKey, "gameId不能为空");
			break;
		default:
			break;
		}
	}

	/**
	 * Validate Required. Allow space characters.
	 */
	protected void validateRequiredParams(Object value, String errorKey, String errorMessage) {
		if (value == null || "".equals(value)) // 经测试,form表单域无输入时值为"",跳格键值为"\t",输入空格则为空格"
												// "
			addError(errorKey, errorMessage);
	}

	/**
	 * Validate mobile. Allow space characters.
	 */
	protected void validateMobile(String value, String errorKey, String errorMessage) {
		Pattern p = Pattern.compile(PropKit.get("check.mobile"));
		Matcher m = p.matcher(value);
		if (!m.matches())
			addError(errorKey, errorMessage);
	}

	/**
	 * Validate mobile. Allow space characters.
	 */
	protected void validateCarNo(String value, String errorKey, String errorMessage) {
		Pattern p = Pattern.compile(PropKit.get("check.carNo"));
		Matcher m = p.matcher(value);
		if (!m.matches())
			addError(errorKey, errorMessage);
	}
}