package com.game.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.game.utils.LogUtils;

import sun.misc.BASE64Encoder;

public class RSAUtil {
	private static final Logger LOGGER = LogManager.getLogger(RSAUtil.class.getName());

	/**
	 * * 生成密钥对 *
	 * 
	 * @return KeyPair *
	 * @throws EncryptException
	 */
	public static KeyPair generateKeyPair() throws Exception {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
			// KEY_SIZE关系到块加密的大小

			final int KEY_SIZE = 1024;
			keyPairGen.initialize(KEY_SIZE, new SecureRandom());
			KeyPair keyPair = keyPairGen.generateKeyPair();
			return keyPair;
		} catch (Exception e) {
			LogUtils.error("generateKeyPair", e);
		}
		return null;
	}

	// 生成公钥
	public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) throws Exception {
		KeyFactory keyFac = null;
		try {
			keyFac = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(StackMessagePrint.printErrorTrace(ex));
		}

		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
		try {
			return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
		} catch (InvalidKeySpecException ex) {
			throw new Exception(StackMessagePrint.printErrorTrace(ex));
		}
	}

	// 生成私钥
	public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent) throws Exception {
		KeyFactory keyFac = null;
		try {
			keyFac = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(StackMessagePrint.printErrorTrace(ex));
		}

		RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
		try {
			return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
		} catch (InvalidKeySpecException ex) {
			throw new Exception(StackMessagePrint.printErrorTrace(ex));
		}
	}

	/**
	 * Encrypt String.
	 * 
	 * @return byte[]
	 */
	public static byte[] encrypt(RSAPublicKey publicKey, byte[] obj) {
		if (publicKey != null) {
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				// ENCRYPT_MODE : 用于将 cipher 初始化为加密模式的常量。
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);

				ByteBuffer buffer = ByteBuffer.wrap(obj);
				ByteBuffer result = ByteBuffer.allocate(2048);

				byte[] data = new byte[117];

				// 分段解密
				while (buffer.position() < buffer.limit()) {
					if (buffer.remaining() >= 117)
						buffer.get(data);
					else {
						data = new byte[buffer.remaining()];
						buffer.get(data, 0, buffer.remaining());
					}

					result.put(cipher.doFinal(data));
				}

				result.flip();
				byte[] cipherBytes = new byte[result.limit()];
				result.get(cipherBytes);

				return cipherBytes;
			} catch (Exception e) {
				LOGGER.error("RSAUtil:encrypt", e);
			}
		}
		return null;
	}

	public static String encrypt(String pubModString, String pubPubExpString, String str) {
		try {
			byte[] pubModBytesNew = Base64.decodeBase64(pubModString);
			byte[] pubPubExpBytesNew = Base64.decodeBase64(pubPubExpString);
			RSAPublicKey recoveryPubKey;
			recoveryPubKey = RSAUtil.generateRSAPublicKey(pubModBytesNew, pubPubExpBytesNew);
			byte[] raw = encrypt(recoveryPubKey, str.getBytes());
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(raw);
		} catch (Exception e) {
			LOGGER.error("RSAUtil:encrypt", e);
		}

		return null;
	}

	/**
	 * Basic decrypt method
	 * 
	 * @return byte[]
	 */
	public static byte[] decrypt(RSAPrivateKey privateKey, byte[] obj) {
		if (privateKey != null) {
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				ByteBuffer buffer = ByteBuffer.wrap(obj);
				ByteBuffer result = ByteBuffer.allocate(2048);

				// DECRYPT_MODE : 用于将 cipher 初始化为解密模式的常量。
				cipher.init(Cipher.DECRYPT_MODE, privateKey);

				byte[] data = new byte[128];

				// 分段解密
				while (buffer.position() < buffer.limit()) {
					if (buffer.remaining() >= 128) {
						buffer.get(data);
					} else {
						data = new byte[buffer.remaining()];
						buffer.get(data, 0, buffer.remaining());
					}

					result.put(cipher.doFinal(data));
				}

				result.flip();
				byte[] cipherBytes = new byte[result.limit()];
				result.get(cipherBytes);

				return cipherBytes;
			} catch (Exception e) {
				LOGGER.error("RSAUtil:decrypt", e);
			}
		}

		return null;
	}

	public static byte[] decrypt(String priModBytesNew, String priPriExpBytesNew, byte[] clipherString)
			throws Exception {
		byte[] byteModBytesNew = Base64.decodeBase64(priModBytesNew);
		byte[] bytePriExpBytesNew = Base64.decodeBase64(priPriExpBytesNew);
		RSAPrivateKey recoveryPriKey = generateRSAPrivateKey(byteModBytesNew, bytePriExpBytesNew);
		return RSAUtil.decrypt(recoveryPriKey, clipherString);
	}

	public static String decrypt(String priModBytesNew, String priPriExpBytesNew, String str) {
		try {
			byte[] enRaw = Base64.decodeBase64(str.getBytes());
			byte[] data = RSAUtil.decrypt(priModBytesNew, priPriExpBytesNew, enRaw);
			return new String(data);
		} catch (Exception e) {
			LOGGER.error("RSAUtil:decrypt", e);
		}
		;
		return null;
	}

	public static void main(String[] args) {
		KeyPair keyPair;
		try {
			keyPair = generateKeyPair();
			PrivateKey privateKey = keyPair.getPrivate();
			System.out.print(privateKey);
			// System.out.print(keyPair.getPublic());
		} catch (Exception e) {
			LOGGER.error("RSAUtil:main", e);
		}

	}
}
