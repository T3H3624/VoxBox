package com.t3n4p613310.mess;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Engine {

    // The window handle
    private long window;
    private boolean[] keyDown = new boolean[GLFW.GLFW_KEY_LAST];
    private boolean leftMouseDown;
    private boolean rightMouseDown;
    private int width,height;
    private boolean resizeViewPort;

    public void run() {

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() ) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure Window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_SAMPLES, 4);

        // Create the window
        window = glfwCreateWindow(500, 500, "MESS", NULL, NULL);
        if ( window == NULL ) throw new RuntimeException("Failed to create the window");

        glfwSetKeyCallback(window, (windowIn, keyIn, scancodeIn, actionIn, modsIn) -> {
            if (keyIn == GLFW_KEY_UNKNOWN) return;
            if (keyIn == GLFW_KEY_ESCAPE && actionIn == GLFW_RELEASE) glfwSetWindowShouldClose(window, true);
            keyDown[keyIn] = (actionIn == GLFW_PRESS || actionIn == GLFW_REPEAT);
        });

        glfwSetMouseButtonCallback(window, (windowIn, buttonIn, actionIn, modsIn) -> {
            System.out.println(actionIn);
            if (buttonIn == GLFW_MOUSE_BUTTON_LEFT)
                leftMouseDown = (actionIn == GLFW_PRESS);
            else if (buttonIn == GLFW_MOUSE_BUTTON_RIGHT)
                rightMouseDown = (actionIn == GLFW_PRESS);
        });

        glfwSetWindowSizeCallback(window, (windowIn, widthIn, heightIn) -> {
            width = widthIn;
            height = heightIn;
            resizeViewPort = true;
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (videoMode.width() - pWidth.get(0)) / 2,
                    (videoMode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //resize viewport
            if(resizeViewPort)
            {
                if (width > height)
                    glViewport((width - height) >>> 1, 0, height, height);
                else
                    glViewport(0, (height - width) >>> 1, width, width);
                resizeViewPort=false;
            }

            drawLoadingScreen();
            //glColor3d(Math.random()/Math.pow(Math.random(),Math.random()),Math.random()/2,Math.random()/2);

            //glRotated(Math.random()*90-45,Math.random()*2-1,Math.random()*2-1,Math.random()*2-1);

            // draw quad
            //glBegin(GL_QUADS);
            //glVertex2f( 0.75F, 0.75F);
            //glVertex2f( 0.75F,-0.75F);
            //glVertex2f(-0.75F,-0.75F);
            //glVertex2f(-0.75F, 0.75F);
            //glVertex3d(0.5,0.5,0.5);
            //glVertex3d(0.5,0.5,-.5);
            //glVertex3d(0.5,-.5,0.5);
            //glVertex3d(0.5,-.5,-.5);
            //glVertex3d(-.5,0.5,0.5);
            //glVertex3d(-.5,0.5,-.5);
            //glVertex3d(-.5,-.5,0.5);
            //glVertex3d(-.5,-.5,-.5);
            //glEnd();




            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    void drawHUD() {
        drawContextBox();
        drawHotBar();
        drawClock();
        drawHint();
    }

    //draw stubs
    private void drawHint(){}
    private void drawClock(){}
    private void drawHotBar(){}
    private void drawContextBox(){}

    private void drawLoadingScreen(){
        glColor3d(Math.random()/Math.pow(Math.random(),Math.random()),Math.random()/2,Math.random()/2);
        glRotated(Math.random()*90-45,Math.random()*2-1,Math.random()*2-1,Math.random()*2-1);

        // draw quad
        glBegin(GL_QUADS);
        glVertex2f( 0.75F, 0.75F);
        glVertex2f( 0.75F,-0.75F);
        glVertex2f(-0.75F,-0.75F);
        glVertex2f(-0.75F, 0.75F);
        glEnd();
    }
}