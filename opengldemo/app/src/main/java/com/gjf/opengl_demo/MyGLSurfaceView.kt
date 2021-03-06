package com.gjf.opengl_demo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import java.io.*
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread


class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f
    private val renderer: MyGLRenderer
//    private lateinit var socket: Socket
    private lateinit var taskQueue: TaskQueue
    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        renderer = MyGLRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

//        thread {
//            socket = Socket("10.195.204.219", 11000)
//        }

        taskQueue = TaskQueue()
        taskQueue.start()
    }



    private var previousX: Float = 0f
    private var previousY: Float = 0f

    private fun handleMessage(input: InputStream, output: OutputStream, message: String) {
        val writer = BufferedWriter(OutputStreamWriter(output, StandardCharsets.UTF_8))
        val reader = BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8))
        writer.write(message)
        writer.newLine()
        writer.flush()
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx: Float = x - previousX
                var dy: Float = y - previousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }

                renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                Log.e("1", renderer.angle.toString())
//                thread {
//                    var input = socket.getInputStream()
//                    var output = socket.getOutputStream()
//                    handleMessage(input, output, renderer.angle.toString())
//                }
                taskQueue.addTask(renderer.angle)
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }




}