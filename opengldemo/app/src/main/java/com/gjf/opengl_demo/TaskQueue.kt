package com.gjf.opengl_demo

import java.io.*
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class TaskQueue : Thread() {

    private val queue : Queue<Float> = LinkedList()
    private val lock : Lock = ReentrantLock()
    private val condition : Condition = lock.newCondition()
    private lateinit var socket: Socket
    public override fun run() {
//        try {
//            socket = Socket("172.19.64.1", 6666)
//            Log.e("12345", "hello")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        socket = Socket("192.168.43.51", 7777)

        while (!interrupted()) {
            val angle = getTask()
            handleMessage(socket.getInputStream(), socket.getOutputStream(), angle.toString())
        }
    }


    public fun addTask(angle : Float) {
        lock.lock()
        try {
            queue.add(angle)
            condition.signalAll()
        } finally {
            lock.unlock()
        }
    }

    private fun getTask() : Float {
        lock.lock()
        try {
            while (queue.isEmpty())
                condition.await()
            return queue.remove()
        } finally {
            lock.unlock()
        }
    }

    private fun handleMessage(input: InputStream, output: OutputStream, message: String) {
        val writer = BufferedWriter(OutputStreamWriter(output, StandardCharsets.UTF_8))
        val reader = BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8))
        writer.write(message)
        writer.newLine()
        writer.flush()
    }

}