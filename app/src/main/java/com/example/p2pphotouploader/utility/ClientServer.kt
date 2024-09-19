package com.example.p2pphotouploader.utility

import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.Socket
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.SecretKey


// CONSTANTS
const val DEFAULT_ENC_MODE: String = "cbc"


/**
 * Receives exact bytes of data from an incoming 
 * socket inputStream.
 * 
 * @param [input]
 *      An inputStream object
 * 
 * @return [ByteArray]
 *      A byte array containing the received data
 */
fun receiveData(input: InputStream): ByteArray {
    val buffer = ByteArray(4096)
    val bytesRead = input.read(buffer)
    return buffer.copyOf(bytesRead)
}


/**
 * Sends data to the target socket (via. outputStream).
 *
 * @param inputData
 *      A bytearray containing data to send
 *
 * @param output
 *      The target socket's outputStream object
 *
 * @return [Nothing]
 */
fun sendData(inputData: ByteArray,
             output: DataOutputStream)
{
    output.write(inputData)
    output.flush()
    println("[+] Data has been sent successfully!")
}


/**
 * Establish security parameters with target peer such
 * as shared secret and the initialization factor (IV).
 *
 * @param peerSocket
 *      A Socket object
 *
 * @param pvtKey
 *      A PrivateKey generated under an elliptic curve
 *
 * @param pubKey
 *      A PublicKey generated under an elliptic curve
 *
 * @return [Pair]
 *      A pair containing the secret key and initialization factor (IV)
 */
fun establishSecurityParameters(peerSocket: Socket,
                                pvtKey: PrivateKey,
                                pubKey: PublicKey): Pair<SecretKey, ByteArray?>
{
    // Send encryption mode (CBC only)
    sendEncryptionMode(peerSocket)

    // Generate and send IV
    val sessionIV = generateAndSendIV(peerSocket)

    // Exchange Public Keys
    val peerPublicKey = exchangePublicKeys(ownPublicKey = pubKey, peerSocket = peerSocket)

    // Derive and load shared secret object using peer's public key
    val sharedSecret = deriveSharedSecret(pvtKey, peerPublicKey)
    val secretKey = getSecretKeyFromBytes(sharedSecret)
    println("[+] KEY EXCHANGE SUCCESS: A shared secret has been derived for the current session " +
            "(${compressSharedSecret(sharedSecret)}) | Number of Bytes = ${sharedSecret.size}")
    return Pair(secretKey, sessionIV)
}


private fun exchangePublicKeys(ownPublicKey: PublicKey, peerSocket: Socket): PublicKey
{
    sendPublicKey(ownPublicKey, peerSocket)
    val peerPublicKey = receivePublicKey(peerSocket)
    println("[+] PUBLIC KEY RECEIVED: Successfully received the peer's public key " +
            "(${compressPublicKey(peerPublicKey.encoded)})")
    return peerPublicKey
}


private fun sendEncryptionMode(peerSocket: Socket) {
    val output: OutputStream = peerSocket.getOutputStream()
    output.write(DEFAULT_ENC_MODE.toByteArray())
    output.flush()
    println("[+] The encryption mode chosen for this session is " + DEFAULT_ENC_MODE.uppercase())

}


@OptIn(ExperimentalStdlibApi::class)
private fun generateAndSendIV(peerSocket: Socket): ByteArray
{
    val output: OutputStream = peerSocket.getOutputStream()
    val sessionIV: ByteArray = generateIV()
    Thread.sleep(500)
    output.write(sessionIV)
    output.flush()
    println("[+] IV GENERATED: An initialization vector (IV) has been generated for this " +
            "session (${sessionIV.toHexString()})")
    return sessionIV
}


private fun sendPublicKey(ownPublicKey: PublicKey, peerSocket: Socket)
{
    fun publicKeyToPem(publicKey: PublicKey): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        PemWriter(OutputStreamWriter(byteArrayOutputStream)).use { pemWriter ->
            val pemObject = PemObject("PUBLIC KEY", publicKey.encoded)
            pemWriter.writeObject(pemObject)
        }
        return byteArrayOutputStream.toString("UTF-8")
    }
// ==============================================================================
    val outputStream: OutputStream = peerSocket.getOutputStream()
    outputStream.write(publicKeyToPem(ownPublicKey).toByteArray())
    outputStream.flush()
    println("[+] Your public key has been successfully sent!")

}


private fun receivePublicKey(peerSocket: Socket): PublicKey
{
    fun deserializePublicKey(publicKeyBytes: ByteArray): PublicKey {
        val keyFactory = KeyFactory.getInstance("EC", "BC")
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        return keyFactory.generatePublic(publicKeySpec)
    }
// ==============================================================================
    val bytesReceived = receiveData(input = peerSocket.getInputStream())
    val publicKeyPem = String(bytesReceived)
    val pemContent = publicKeyPem            // => Remove PEM headers and footers
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\n", "")
    val decodedBytes = Base64.getDecoder().decode(pemContent)
    return deserializePublicKey(decodedBytes)
}
