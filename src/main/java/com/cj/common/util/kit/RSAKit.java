package com.cj.common.util.kit;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * RSA加密算法
 *
 * @author THINKPAD
 */
@SuppressWarnings("all")
public class RSAKit {

    private static String filename = "RSAKey.txt";

    /**
     * 密文解密
     *
     * @param password 明文
     * @return result 密文
     * @throws Exception
     */
    public static String decryptionToString(String password) throws Exception {
        String result = "";

        byte[] enResultUserCode = hexStringToBytes(password);
        byte[] deResultUserCode = decrypt(getKeyPair().getPrivate(), enResultUserCode);

        StringBuffer sbUserCode = new StringBuffer();
        sbUserCode.append(new String(deResultUserCode));
        result = sbUserCode.reverse().toString();
        // 转译特殊字符
        result = URLDecoder.decode(result, "UTF-8");

        return result.equals("") ? password : result;
    }

    /**
     * 字符到字节
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 解密
     *
     * @param pk  解密的密钥 *
     * @param raw 已经加密的数据 *
     * @return 解密后的明文 *
     * @throws Exception
     */
    public static byte[] decrypt(PrivateKey pk, byte[] raw) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, pk);
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;

            while ((raw.length - (j * blockSize)) > 0) {
                bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 加密
     *
     * @param pk   加密的密钥 *
     * @param data 待加密的明文数据 *
     * @return 加密后的数据 *
     * @throws Exception
     */
    public static byte[] encrypt(PublicKey pk, byte[] data) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            int blockSize = cipher.getBlockSize();// 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
            // 加密块大小为127
            // byte,加密后为128个byte;因此共有2个加密块，第一个127
            // byte第二个为1个byte
            int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大小
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? (data.length / blockSize) + 1
                    : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while ((data.length - (i * blockSize)) > 0) {
                if ((data.length - (i * blockSize)) > blockSize) {
                    cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
                } else {
                    cipher.doFinal(data, i * blockSize, data.length - (i * blockSize),
                            raw, i * outputSize);
                }
                // 这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到
                // ByteArrayOutputStream中，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了
                // OutputSize所以只好用dofinal方法。

                i++;
            }
            return raw;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 生成密钥对
     *
     * @return KeyPair *
     * @throws Exception
     */
    public static KeyPair generateKeyPair() throws Exception {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            final int keySize = 1024;// 这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
            keyPairGen.initialize(keySize, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();

            System.out.println(keyPair.getPrivate());
            System.out.println(keyPair.getPublic());

            saveKeyPair(keyPair);
            return keyPair;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 生成私钥
     *
     * @param modulus         *
     * @param privateExponent *
     * @return RSAPrivateKey *
     */
    public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus,
                                                      byte[] privateExponent) throws Exception {
        KeyFactory keyFac = null;
        try {
            keyFac = KeyFactory.getInstance("RSA",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        }

        RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus),
                new BigInteger(privateExponent));
        try {
            return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * 生成公钥
     *
     * @param modulus        *
     * @param publicExponent *
     * @return RSAPublicKey *
     */
    public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent)
            throws Exception {
        KeyFactory keyFac = null;
        try {
            keyFac = KeyFactory.getInstance("RSA",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        }

        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(modulus),
                new BigInteger(publicExponent));
        try {
            return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * 获取密钥对
     *
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair() throws Exception {
        // String path=ClassLoader.getSystemResource(filename).toString();
        // path=path.replace("file:/","");
        // FileInputStream fis=new FileInputStream(path);
        InputStream is = RSAKit.class.getClassLoader().getResourceAsStream(filename);
        ObjectInputStream oos = new ObjectInputStream(is);
        KeyPair kp = (KeyPair) oos.readObject();
        oos.close();
        is.close();
        return kp;
    }

    /**
     * 16进制 To byte[]
     *
     * @param hexString
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if ((hexString == null) || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) ((charToByte(hexChars[pos]) << 4)
                    | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 执行main生成密钥
     *
     * @param args *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//		RSAPublicKey rsap = (RSAPublicKey) RSAUtil.generateKeyPair().getPublic();
//		String test = "qinhailin";
//		byte[] enTest = encrypt(getKeyPair().getPublic(), test.getBytes());
//		byte[] deTest = decrypt(getKeyPair().getPrivate(), enTest);
//		System.out.println(new String(deTest));
    }

    /**
     * 保存密钥对
     *
     * @param kp
     * @throws Exception
     */
    public static void saveKeyPair(KeyPair kp) throws Exception {
        String path = ClassLoader.getSystemResource(filename).toString();
        path = path.replace("file:/", "");
        FileOutputStream fos = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        // 生成密钥对
        oos.writeObject(kp);
        oos.close();
        fos.close();
    }
}
