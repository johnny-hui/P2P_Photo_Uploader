package com.example.p2pphotouploader.tests

import com.example.p2pphotouploader.utility.AESDecrypt
import com.example.p2pphotouploader.utility.AESEncrypt
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
    val test = AESEncrypt(data="Hello Dude!".toByteArray(), key=secretKey, iv=sessionIV)
    sendData(
        inputData = test,
        output = DataOutputStream(peerSocket.getOutputStream()),
    )

    // Receive encrypted data
    val bytesReceived = receiveData(input = peerSocket.getInputStream())
    val decryptedData = AESDecrypt(data=bytesReceived, key=secretKey, iv=sessionIV)
    println("[+] Decrypted Data: ${String(decryptedData)}")

    // Close connection
    peerSocket.close()
}