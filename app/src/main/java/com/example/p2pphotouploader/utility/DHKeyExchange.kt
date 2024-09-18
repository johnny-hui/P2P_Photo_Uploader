package com.example.p2pphotouploader.utility

import org.bouncycastle.crypto.digests.SHA3Digest
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom

const val BRAINPOOLP256R1 = "brainpoolP256r1"


/**
 * Generates a private/public key pair under the
 * brainpoolP256r1 elliptic curve.
 *
 * @return [Pair]
 *      A pair of elliptic curve private/public keys
 */
fun generateKeyPair(): Pair<PrivateKey, PublicKey> {
    val keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC")
    val ecSpec = org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec(BRAINPOOLP256R1)
    keyPairGenerator.initialize(ecSpec, SecureRandom())
    val keyPair = keyPairGenerator.generateKeyPair()
    println("[+] Your private key: ${compressPrivateKey(keyPair.private.encoded)}")
    println("[+] Your public key: ${compressPublicKey(keyPair.public.encoded)}")
    return Pair(keyPair.private, keyPair.public)
}


/**
 * Compresses the public key into a hex string.
 *
 * @param publicKeyBytes
 *      Bytearray containing bytes of the public key
 *
 * @return [String]
 *      A hexadecimal representation of the public key
 */
fun compressPublicKey(publicKeyBytes: ByteArray): String {
    val digest = SHA3Digest(256)
    digest.update(publicKeyBytes, 0, publicKeyBytes.size)
    val hashBytes = ByteArray(digest.digestSize)
    digest.doFinal(hashBytes, 0)
    return hashBytes.joinToString("") { "%02x".format(it) }
}


/**
 * Compresses the private key into a hex string.
 *
 * @param privateKeyBytes
 *      Bytearray containing bytes of the private key
 *
 * @return [String]
 *      A hexadecimal representation of the private key
 */
fun compressPrivateKey(privateKeyBytes: ByteArray): String {
    val digest = SHA3Digest(256)
    digest.update(privateKeyBytes, 0, privateKeyBytes.size)
    val hashBytes = ByteArray(digest.digestSize)
    digest.doFinal(hashBytes, 0)
    return hashBytes.joinToString("") { "%02x".format(it) }
}