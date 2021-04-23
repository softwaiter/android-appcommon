package com.puerlink.common;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class NetRSAUtils 
{
	/**
	 * 将C#公钥转换为Java公钥
	 * @param exponent
	 * @param modulus
	 * @return
	 */
	public static PublicKey generatePublicKey(String exponent, String modulus)
	{
		try
		{
			BigInteger m = new BigInteger(1, hexString2Bytes(modulus));
			BigInteger e = new BigInteger(1, hexString2Bytes(exponent));
			RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(m, e);
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(rsaPubKey);
		}
		catch (Exception exp)
		{
			;
		}
		return null;
	}

	public static byte[] hexString2Bytes(String str)    
	{    
		 int len = str.length();
         byte[] data = new byte[len / 2];

         for(int i = 0; i < len; i+=2)
         {
             data[i/2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + 
            		 Character.digit(str.charAt(i+1), 16));
         }

         return data; 
	}
	
	final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	public static String bytes2HexString(byte[] bytes)
	{
		char[] hexChars = new char[bytes.length*2];
        int v;

        for(int j=0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v>>>4];
            hexChars[j*2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

	public static String encrypt(PublicKey pubKey, String plainText)
	{
		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			
			byte[] encryptBytes = cipher.doFinal(plainText.getBytes("gb2312"));
			return bytes2HexString(encryptBytes);
		}
		catch (Exception exp)
		{
			;
		}
		return null;
	}
	
	public static String encrypt(String exponent, String modulus, String plainText)
	{
		PublicKey pubKey = generatePublicKey(exponent, modulus);
		if (pubKey != null)
		{
			return encrypt(pubKey, plainText);
		}
		return null;
	}
	
}
