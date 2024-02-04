package com.example.p2pphotouploader.utility

import android.graphics.Bitmap
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
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
fun uploadPhoto(ip: String,
                port: Int,
                bitmap: Bitmap?) : String
{
    runBlocking {
        try {
            withTimeout(TIMEOUT) {
                // Connect to the server
                val socket = Socket(ip, port)

                if(socket.isConnected) {
                    // Create input and output streams for communication
                    val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                    val output = PrintWriter(socket.getOutputStream(), true)
                    val outputBitmap = socket.getOutputStream()

                    // Send initial signal to send message
                    output.write(SIGNAL)

                    delay(DELAY)

                    // Convert Bitmap to byte array + Send to server
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    outputBitmap.write(byteArrayOutputStream.toByteArray())

                    // Await message from server (stalls here)
                    if(input.readLine() == ACK) {
                        clearFD(socket, input, outputBitmap, output)
                        return@withTimeout SUCCESS
                    } else {
                        clearFD(socket, input, outputBitmap, output)
                        return@withTimeout FAILURE
                    }
                } else {
                    socket.close()
                }
            }

        } catch (e: TimeoutCancellationException) {
            println(TIMEOUT_MSG.plus(e))
            return@runBlocking FAILURE

        } catch (e: IOException) {
            println(CONNECT_ERROR.plus(e))
            return@runBlocking FAILURE
        }
    }
    return FAILURE
}

private fun clearFD(socket: Socket,
                    inputFD: BufferedReader,
                    outputFD: OutputStream,
                    outputPrint: PrintWriter)
{
    inputFD.close()
    outputFD.close()
    outputPrint.close()
    socket.close()
}
