package com.example.p2pphotouploader.utility

import android.graphics.Bitmap
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


const val SUCCESS: String = "1"
const val FAILURE: String = "0"
const val TIMEOUT: Long = 120000 // 2 minutes (in milliseconds)
const val TIMEOUT_MSG: String = "Timeout exceeded: "
const val CONNECT_ERROR: String = "An error has occurred: "


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

                    // TODO: Send bitmap to server (determine how to)

                    // TODO: Await message from server (this is where timeout can occur)

                    // Clear file descriptors
                    clearFD(socket, input, output)

                    return@withTimeout SUCCESS
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
                    outputFD: PrintWriter)
{
    inputFD.close()
    outputFD.close()
    socket.close()
}
