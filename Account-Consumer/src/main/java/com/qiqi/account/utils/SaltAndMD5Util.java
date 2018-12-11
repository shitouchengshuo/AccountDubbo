package com.qiqi.account.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 以userName作为盐值对密码进行MD5加密，返回加密后的密码
 */
public class SaltAndMD5Util {
    public final static Object SaltAndMD5(String userName,String password) {
        try {
            String hashAlgorithmName = "MD5";//加密方式
            ByteSource salt = ByteSource.Util.bytes(userName);//以账号作为盐值
            int hashIterations = 1024;//加密1024次
            Object result = new SimpleHash(hashAlgorithmName,password,salt,hashIterations);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 以账号作为盐值,返回盐的Hex码
     */
    public final static String getSalt(String userName) {

        ByteSource salt = ByteSource.Util.bytes(userName);
        return salt.toHex();
    }


    /**
     * 通过提供加密的强随机数生成器 生成盐
     *
     * @return salt
     * @throws NoSuchAlgorithmException

    // 盐的长度 由32位改为16位
    private static final int SALT_BYTE_SIZE = 8/2;
    public static String generateSalt(){
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);
        return StrUtil.toHex(salt);
    }
     */
}