package com.example.p2pphotouploader.utility
import org.bouncycastle.crypto.digests.SHA3Digest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


// CONSTANTS
const val IV_SIZE = 16
const val SHARED_SECRET_SIZE = 16


/**
 * Derives shared secret key using another peer's public key.
 *
 * @param privateKey
 *      A PrivateKey generated under an elliptic curve
 *
 * @param publicKey
 *      A PublicKey generated under an elliptic curve
 *
 * @return [ByteArray]
 *      A ByteArray containing bytes of the shared secret
 */
fun deriveSharedSecret(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
    // Generate shared secret using KeyAgreement and Peer's public key
    val keyAgreement = KeyAgreement.getInstance("ECDH", "BC")
    keyAgreement.init(privateKey)
    keyAgreement.doPhase(publicKey, true)
    val sharedKeyBytes = keyAgreement.generateSecret()

    // Compute SHA3-256 hash of the shared key
    val digest = SHA3Digest(256)
    digest.update(sharedKeyBytes, 0, sharedKeyBytes.size)
    val sharedKeyHash = ByteArray(digest.digestSize)
    digest.doFinal(sharedKeyHash, 0)

    // Return only the first 16 bytes of the hash
    return sharedKeyHash.copyOf(SHARED_SECRET_SIZE)
}


/**
 * Generates a 16-byte initialization factor (IV) used
 * for encryption/decryption purposes.
 *
 * @return [ByteArray]
 *      A byteArray containing bytes of the IV
 */
fun generateIV(): ByteArray {
    val secureRandom = SecureRandom()
    val iv = ByteArray(IV_SIZE)
    secureRandom.nextBytes(iv)
    return iv
}


/**
 * Instantiates a SecretKey object from bytes of the
 * shared secret key.
 *
 * @param keyBytes
 *      A bytearray containing bytes of the secret key
 *
 * @return [SecretKey]
 *      A SecretKey object
 */
fun getSecretKeyFromBytes(keyBytes: ByteArray): SecretKey {
    return SecretKeySpec(keyBytes, "AES")
}


/**
 * Compresses the shared secret into a hex string.
 *
 * @param sharedSecretBytes
 *      Bytearray containing bytes of the shared secret
 *
 * @return [String]
 *      A hexadecimal representation of the shared key
 */
fun compressSharedSecret(sharedSecretBytes: ByteArray): String {
    return sharedSecretBytes.joinToString("") { "%02x".format(it) }
}
