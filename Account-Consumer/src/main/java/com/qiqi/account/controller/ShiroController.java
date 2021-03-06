package com.qiqi.account.controller;

import com.qiqi.account.exception.ParameterErrorException;
import com.qiqi.account.service.ShiroService;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/ui/")
public class ShiroController{

	@Autowired
	private ShiroService shiroService;

	private static final Logger logger = Logger.getLogger(ShiroController.class);

	/**
	 * 登录入口,跳转到login.jsp
	 */
	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		return "login";
	}

	/**
	 * 注册
	 */
	@RequestMapping("/registerPage")
	public String registerPage(HttpServletRequest request, Model model) throws Exception {
		return "register";
	}

	/**
	 * 发送手机验证码
	 * @param phonenumber  手机
	 * @param mailaddress  邮箱
	 * @param mailtype     邮箱类型
	 * @return
	 */
	@RequestMapping(value = "verificationCode", method = RequestMethod.GET)
	@ResponseBody
	public String verificationCode(String phonenumber, String mailaddress, String mailtype) throws Exception {

		String sendStatus = null;

		if (phonenumber != null && mailaddress == null) {
			sendStatus = shiroService.sendPhoneVerificationCode(phonenumber,null);
		} else if (mailaddress != null && phonenumber == null) {
			//sendStatus = accountService.sendMailVerificationCode(mailaddress, notsend);
		} else {
			throw new ParameterErrorException("Parameter error!");
		}
		return sendStatus;

	}

	/**
	 * 注册
	 */
	@RequestMapping(value = "register", method = RequestMethod.POST)
	@ResponseBody
	public String registeredAccount(HttpServletRequest request, HttpServletResponse response,
								  @RequestParam(value = "authorizationcode", required = false) String authorizationcode,
								  @RequestParam(value = "username", required = true) String username,
								  @RequestParam(value = "password", required = true) String password,
								  @RequestParam(value = "phonenumber", required = false) String phonenumber,
								  @RequestParam(value = "mailaddress", required = false) String mailaddress,
								  @RequestParam(value = "verificationcode", required = true) String verificationcode,
								  @RequestParam(value = "data", required = false) String data)
			throws Exception {
		logger.info("authorizationcode:" + authorizationcode + ", username:" + username+ ", password:" + password
				+ ", phonenumber:" + phonenumber + ", mailaddress:" + mailaddress + ", verificationcode:" + verificationcode  + ", data:" + data);

		String result = "{\"error\":\"0\"}";
		if (phonenumber != null && mailaddress == null) {
			result = shiroService.registeredPhoneAccount(username, password, phonenumber, verificationcode, data);
		} else if (mailaddress != null && phonenumber == null) {
			//shiroService.registeredMailAccount(username, mailaddress, verificationcode, password, appId, data);
		} else {
			throw new ParameterErrorException("phonenumber:" + phonenumber + ", mailaddress:" + mailaddress + " is error!");
		}
		logger.info("username:" + username + ",phonenumber" + phonenumber + " 注册成功");
		return result;

		//Map<Object, Object> retValue = new HashMap<Object, Object>();
		//retValue.put("uid", shiroService.getUidByPhoneOrMail(phonenumber, mailaddress, username));
		//使用全局的响应状态
		//retValue.put("error", ReturnStatusCode.Success.getErrorCode());
		//String result = JSON.toJSONString(retValue);
	}

	/**
	 * 登录成功跳转到success.jsp
	 */
	@RequestMapping("success")
	public String success(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		System.out.println("++++++++++登录成功++++++++++");
		return "success";
	}

	/**
	 * 	没有权限时跳转的页面
	 */
	@RequestMapping("unauthorized")
	public String unauthorized(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		return "unauthorized";
	}

	/**
	 * 登录认证：验证用户名和密码
	 */
	@RequestMapping(value = "checkLogin", method = RequestMethod.POST)
	@ResponseBody
	public String login(String username, String password){
		//获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		//测试当前用户是否已经被认证(即是否已经登录)
		if (!currentUser.isAuthenticated()){
			//将用户名与密码封装为UsernamePasswordToken对象
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			//设置rememberMe记录用户
			token.setRememberMe(true);
			try {
				//调用Subject的login方法执行登录验证
				currentUser.login(token);
			} catch (UnknownAccountException uae) {
				logger.warn("不存在用户 " + token.getPrincipal());
				return "{\"error\":\"7\"}";
			} catch (IncorrectCredentialsException ice) {
				logger.warn("用户 " + token.getPrincipal() + " 密码错误!");
				return "{\"error\":\"8\"}";
			} catch (LockedAccountException lae) {
				logger.warn("用户 " + token.getPrincipal() + " 被锁定!");
				return "{\"error\":\"15\"}";
			} catch (AuthenticationException ae) {
				logger.warn("用户:" + token.getPrincipal() + " 登录失败!");
			}
		}
		//登录成功
		return "{\"error\":\"0\"}";
	}

	/**
	 *  退出登录
	 */
	@RequestMapping(value = "logout", method = RequestMethod.POST)
	@ResponseBody
	public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
		return "{\"error\":\"0\"}";
	}



	/**
	 * 用于测试

	 @RequestMapping("/index")
	 public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
	 return "login";
	 }
	 */
	/**
	 * 用于测试权限
	 @RequestMapping("admin")
	 public String admin() throws Exception {
	 return "admin";
	 }
	 */
	/**
	 * 用于测试权限
	 @RequestMapping("user")
	 public String user(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
	 return "user";
	 }
	 */

	/**
	 *测试Shiro Service层的权限注解@RequiresRoles({"admin"})
	 @RequestMapping("testShiroAnnocation")
	 public String testShiroAnnocation(HttpSession session) throws Exception {
	 session.setAttribute("test", "Hello1234");
	 shiroService.testMethod();
	 return "success";
	 }
	 */

}
