package com.example.p2pphotouploader.tests

import com.example.p2pphotouploader.utility.AESDecrypt
import com.example.p2pphotouploader.utility.establishSecurityParameters
import com.example.p2pphotouploader.utility.generateKeyPair
import com.example.p2pphotouploader.utility.receiveData
import com.example.p2pphotouploader.utility.sendData
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.DataOutputStream
import java.net.Socket
import java.security.Security


fun main() {
    Security.addProvider(BouncyCastleProvider())
    val (privateKey, publicKey) = generateKeyPair()
    val peerSocket = Socket("10.0.0.75", 69)

    // Exchange keys and derive shared secret + IV    
    val (secretKey, sessionIV) = establishSecurityParameters(peerSocket, privateKey, publicKey)

    // Encrypt the data
    sendData(
        inputData = "Hello Dude!".toByteArray(),
        output = DataOutputStream(peerSocket.getOutputStream()),
        secretKey = secretKey,
        iv = sessionIV
    )

    // Receive encrypted data
    val bytesReceived = receiveData(input = peerSocket.getInputStream())
    val decryptedData = AESDecrypt(data=bytesReceived, key=secretKey, iv=sessionIV)
    println("[+] Decrypted Data: ${String(decryptedData)}")

    // Close connection
    peerSocket.close()
}