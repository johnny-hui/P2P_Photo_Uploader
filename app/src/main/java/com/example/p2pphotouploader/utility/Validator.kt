package com.example.p2pphotouploader.utility

import java.net.InetAddress

private const val MIN_PORT = 0
private const val MAX_PORT = 65535

/**
 *  Validates an IP address
 *
 * @param [inputIP]
 *      A string representing the IP address to validate
 *
 */
fun validateIP(inputIP: String) : Boolean {
    return try {
        InetAddress.getByName(inputIP)
        true
    } catch (e: Exception) {
        false
    }
}


/**
 * Validates a port number (from 0-65535)
 *
 * @param [inputPort]
 *      - An integer representing the port number to validate
 *
 * @return True/False
 *      - A boolean (T/F)
 */
fun validatePort(inputPort: Int) : Boolean {
    return inputPort in MIN_PORT..MAX_PORT
}