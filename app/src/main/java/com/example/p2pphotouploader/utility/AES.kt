package com.example.p2pphotouploader.utility

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * Encrypts data using AES (operating in CBC mode).
 *
 * @param data
 *      A ByteArray containing the data to be encrypted
 *
 * @param key
 *      A SecretKey object
 *
 * @param iv
 *      A ByteArray containing the initialization factor (IV)
 *
 * @return [ByteArray]
 *      A bytearray containing encrypted data
 */
fun AESEncrypt(data: ByteArray, key: SecretKey, iv: ByteArray?): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
    return cipher.doFinal(data)
}


/**
 * Decrypts data using AES (operating in CBC mode).
 *
 * @param data
 *      A ByteArray containing the data to be decrypted
 *
 * @param key
 *      A SecretKey object
 *
 * @param iv
 *      A ByteArray containing the initialization factor (IV)
 *
 * @return [ByteArray]
 *      A bytearray containing decrypted data
 */
fun AESDecrypt(data: ByteArray, key: SecretKey, iv: ByteArray?): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
    return cipher.doFinal(data)
}