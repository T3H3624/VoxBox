package com.t3n4p613310.mess;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

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

        setIcon();

        glfwSetKeyCallback(window, (windowIn, keyIn, scancodeIn, actionIn, modsIn) -> {
            if (keyIn == GLFW_KEY_UNKNOWN) return;
            if (keyIn == GLFW_KEY_ESCAPE && actionIn == GLFW_RELEASE) glfwSetWindowShouldClose(window, true);
            keyDown[keyIn] = (actionIn == GLFW_PRESS || actionIn == GLFW_REPEAT);
        });

        glfwSetMouseButtonCallback(window, (windowIn, buttonIn, actionIn, modsIn) -> {
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

        //texture stuff
        //float pixels[] = {
        //        0.0f, 0.0f, 0.0f,   1.0f, 0.0f, 0.0f,
        //        1.0f, 0.0f, 0.0f,   0.0f, 0.0f, 0.0f
        //};
        //int texture = glGenTextures();
        //glBindTexture(GL_TEXTURE_2D, texture);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 2, 2, 0, GL_RGB, GL_FLOAT, pixels);

        // Set the clear color
        glClearColor(0.1f, 0.1f, 0.1f, 0.1f);

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


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    //icon code

    private void setIcon()
    {
        GLFWImage image = GLFWImage.malloc();
        int size = 1<<((int)(Math.random()*16));
        image.set(size, size, genIcon(size));
        GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);
        glfwSetWindowIcon(window, images);
        images.free();
        image.free();
    }

    private static ByteBuffer genIcon(int sizeIn)
    {
        ByteBuffer buffer = BufferUtils.createByteBuffer(sizeIn*sizeIn*4);
        int counter = 0;
        for (int i = 0; i < sizeIn; i++)
            for (int j = 0; j < sizeIn; j++)
            {
                buffer.put(counter + 0,(byte)Math.floor(Math.random()*255));
                buffer.put(counter + 1,(byte)Math.floor(Math.random()*255));
                buffer.put(counter + 2,(byte)Math.floor(Math.random()*255));
                buffer.put(counter + 3,(byte)Math.floor(Math.random()*255));
                counter += 4;
            }
        return buffer;
    }

    private void createEntity(Entity entityIn) throws IOException
    {
        entityIn.positionVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, entityIn.positionVbo);
        glBufferData(GL_ARRAY_BUFFER, entityIn.positions, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        entityIn.normalVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, entityIn.normalVbo);
        glBufferData(GL_ARRAY_BUFFER, entityIn.normals, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void drawEntity(Entity entityIn) {
        glBindBuffer(GL_ARRAY_BUFFER, entityIn.positionVbo);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, entityIn.normalVbo);
        glNormalPointer(GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDrawArrays(GL_TRIANGLES, 0, entityIn.numVertices);
        glDisableClientState(GL_NORMAL_ARRAY);
    }

}