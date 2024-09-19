package com.example.p2pphotouploader.utility

import android.graphics.Bitmap
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.ByteBuffer


const val SUCCESS: String = "1"
const val FAILURE: String = "0"
const val TIMEOUT: Long = 120000 // 2 minutes (in milliseconds)
const val TIMEOUT_MSG: String = "Timeout exceeded: "
const val DELAY: Long = 1000  // 1 second (for buffer with server)
const val CONNECT_ERROR: String = "An error has occurred: "
const val ACK: String = "ACK"
const val SIGNAL = "PHOTO"


/**
 * Securely establishes a connection to a target server to send a
 * picture (via. bitmap).
 *
 * @param [ip]
 *      A string representing the IP address
 *
 * @param [port]
 *      An integer representing the port number
 *
 * @param [bitmap]
 *      A bitmap of the image
 *
 * @return result
 *      The return status (SUCCESS/FAILURE)
 */
suspend fun uploadPhotoAsync(ip: String, port: Int, bitmap: Bitmap?): String {
    var result: String = FAILURE

    try {
        withTimeout(TIMEOUT) {
            val (privateKey, publicKey) = generateKeyPair()
            val targetSocket = Socket(ip, port)

            if(targetSocket.isConnected) {
                val input = targetSocket.getInputStream()
                val output = targetSocket.getOutputStream()
                val dataOutputStream = DataOutputStream(targetSocket.getOutputStream())

                // Perform key exchange, derive shared secret & IV
                val (secretKey, sessionIV) = establishSecurityParameters(targetSocket, privateKey, publicKey)

                // Send an encrypted signal to target
                sendData(
                    inputData = AESEncrypt(data=SIGNAL.toByteArray(), key=secretKey, iv=sessionIV),
                    output = dataOutputStream
                )
                delay(DELAY)

                // Compress photo bitmap data to bytearray
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val photoData = byteArrayOutputStream.toByteArray()

                // Encrypt the photo first
                val encryptedPhoto = AESEncrypt(data=photoData, key=secretKey, iv=sessionIV)

                // Send size of photo (encrypted)
                val photoSize = AESEncrypt(
                    data=ByteBuffer.allocate(4).putInt(encryptedPhoto.size).array(),
                    key=secretKey,
                    iv=sessionIV
                )
                sendData(inputData=photoSize, output=dataOutputStream)

                // Send encrypted photo
                sendData(inputData=encryptedPhoto, output=dataOutputStream)

                // Receive status from target
                val status = String(
                    AESDecrypt(
                        data=receiveData(input=input),
                        key=secretKey,
                        iv=sessionIV
                    )
                )

                if(status == ACK) {
                    clearFD(targetSocket, input, output, dataOutputStream)
                    result = SUCCESS
                }
            } else {
                targetSocket.close()
                result = FAILURE
            }
        }
    } catch (e: TimeoutCancellationException) {
        println(TIMEOUT_MSG.plus(e))
        result = FAILURE

    } catch (e: IOException) {
        println(CONNECT_ERROR.plus(e))
        result = FAILURE
    }
    return result
}



private fun clearFD(socket: Socket,
                    inputFD: InputStream,
                    outputFD: OutputStream,
                    outputPrint: DataOutputStream)
{
    inputFD.close()
    outputFD.close()
    outputPrint.close()
    socket.close()
}
