package com.qiqi.account.shiro.core;

import com.qiqi.account.dao.AccountMapper;
import com.qiqi.account.model.TbAccount;
import com.qiqi.account.shiro.model.User;
import com.qiqi.account.utils.MD5Util;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 权限检查类
 */
public class Realm extends AuthorizingRealm {

	private static final Logger logger = Logger.getLogger(Realm.class);

	@Autowired
	private AccountMapper accountMapper;

	/*
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		//1.从principals中获取登录用户的信息
		Object principal = principals.getPrimaryPrincipal();

		//2.利用登录用户的信息获取当前用户的角色（有数据库的话，从数据库中查询）
		Set<String> roles = new HashSet<String>();//放置用户角色的set集合(不重复)
		roles.add("user");//为所有用户添加user角色
		if("admin".equals(principal)){
			roles.add("admin");//为用户名为admin的用户添加admin角色
		}

		//3.创建SimpleAuthorizationInfo，并设置其roles属性
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);

		//4.返回SimpleAuthorizationInfo对象
		return info;

	}

	/*
	 * 登录验证  只有调用自己调用Subject subject.login(token)才会调用该方法
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {

		//1.把AuthenticationToken转换为UsernamePasswordToken
		UsernamePasswordToken userToken = (UsernamePasswordToken) authcToken;

		//2.从UsernamePasswordToken中获取username
		String username = userToken.getUsername();

		//3.调用数据库的方法，从数据库中查询Username对应的用户记录
		TbAccount account = accountMapper.getByAccountName(username);
		//4.若用户不行存在，可以抛出UnknownAccountException
		if(account == null){
			System.out.println("用户："+username+"不存在");
			throw new UnknownAccountException("用户："+username+"不存在");
		}

		//5.若用户被锁定，可以抛出LockedAccountException
//		if(account.isLocked()){
//			System.out.println("用户："+username+"不存在");
//			System.out.println("用户："+username+"被锁定");
//			throw new LockedAccountException("用户："+username+"被锁定");
//		}

		//6.根据用户的情况，来构建AuthenticationInfo对象,通常使用的实现类为SimpleAuthenticationInfo

		//1)principal ：认证的实体信息，这里使用Accountname，也可以是数据库表对应的用户的实体对象
		Object principal  = account.getAccountname();
		//从数据库获取的密码是已经加密的密码
		Object credentials = account.getPassword();
		//realmName：当前realm对象的name，调用父类的getName()方法即可
		String realmName = getName();
        //构建SimpleAuthenticationInfo对象
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal ,credentials,realmName);
		return info;
	}

}
