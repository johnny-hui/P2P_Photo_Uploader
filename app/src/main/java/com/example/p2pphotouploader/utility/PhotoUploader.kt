package com.example.p2pphotouploader.utility

import android.graphics.Bitmap
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.Socket


const val SUCCESS: String = "1"
const val FAILURE: String = "0"
const val TIMEOUT: Long = 120000 // 2 minutes (in milliseconds)
const val TIMEOUT_MSG: String = "Timeout exceeded: "
const val DELAY: Long = 1000  // 1 second (for buffer with server)
const val CONNECT_ERROR: String = "An error has occurred: "
const val ACK: String = "ACK"
const val SIGNAL = "RECEIVE PHOTO"


/**
 * Establishes a client socket and connecting to a target server to send a
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
 * @return status
 *      The return status (SUCCESS/FAILURE)
 */
suspend fun uploadPhotoAsync(ip: String, port: Int, bitmap: Bitmap?): String {
    var result: String = FAILURE

    try {
        withTimeout(TIMEOUT) {
            val socket = Socket(ip, port)

            if(socket.isConnected) {
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                val output = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                output.write(SIGNAL)
                output.flush()
                delay(DELAY)

                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                val dataOutputStream = DataOutputStream(socket.getOutputStream())
                dataOutputStream.writeInt(byteArray.size)
                dataOutputStream.write(byteArray)

                if(input.readLine() == ACK) {
                    clearFD(socket, input, dataOutputStream, output)
                    result = SUCCESS
                }
            } else {
                socket.close()
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
                    inputFD: BufferedReader,
                    outputFD: OutputStream,
                    outputPrint: BufferedWriter)
{
    inputFD.close()
    outputFD.close()
    outputPrint.close()
    socket.close()
}
