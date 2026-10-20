package xiaobaisnack.crawler.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ⭐ 哈希工具类
 *
 * 功能：
 * - MD5/SHA-256哈希计算
 * - URL去重指纹
 * - 内容相似度检测
 */
public class HashUtils {

    /**
     * 计算MD5哈希
     */
    public static String md5(String input) {
        return hash(input, "MD5");
    }

    /**
     * 计算SHA-256哈希
     */
    public static String sha256(String input) {
        return hash(input, "SHA-256");
    }

    /**
     * 通用哈希计算
     */
    private static String hash(String input, String algorithm) {
        if (input == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found: " + algorithm, e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 计算URL指纹（用于去重）
     */
    public static String urlFingerprint(String url) {
        if (url == null) return null;
        // 规范化后计算MD5
        String normalized = url.toLowerCase().trim();
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return md5(normalized);
    }

    /**
     * 计算内容指纹（用于相似度检测）
     */
    public static String contentFingerprint(String content) {
        if (content == null) return null;
        // 取前1000字符计算哈希
        String sample = content.length() > 1000 ? content.substring(0, 1000) : content;
        return sha256(sample);
    }

    /**
     * 计算SimHash（用于近似去重）
     * 简化版实现
     */
    public static long simHash(String content) {
        if (content == null || content.isEmpty()) return 0;

        int[] bits = new int[64];
        String[] words = content.split("\\s+");

        for (String word : words) {
            long hash = word.hashCode();
            for (int i = 0; i < 64; i++) {
                if ((hash & (1L << i)) != 0) {
                    bits[i]++;
                } else {
                    bits[i]--;
                }
            }
        }

        long simHash = 0;
        for (int i = 0; i < 64; i++) {
            if (bits[i] > 0) {
                simHash |= (1L << i);
            }
        }

        return simHash;
    }

    /**
     * 计算两个SimHash的海明距离
     */
    public static int hammingDistance(long hash1, long hash2) {
        long xor = hash1 ^ hash2;
        int distance = 0;
        while (xor != 0) {
            distance++;
            xor &= (xor - 1);
        }
        return distance;
    }

    /**
     * 判断两个内容是否相似（海明距离<=3视为相似）
     */
    public static boolean isSimilar(String content1, String content2) {
        long hash1 = simHash(content1);
        long hash2 = simHash(content2);
        return hammingDistance(hash1, hash2) <= 3;
    }
}