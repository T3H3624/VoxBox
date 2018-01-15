package com.t3n4p613310.mess;

import com.sudoplay.joise.module.ModuleAbs;
import com.sudoplay.joise.module.ModuleBasisFunction;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Engine
{

    // The window handle
    private long window;
    private int width, height;
    private boolean resizeViewPort;
    private boolean[] keyDown = new boolean[GLFW.GLFW_KEY_LAST];
    private ArrayList<Entity> Entity = new ArrayList<Entity>();
    private boolean leftMouseDown;
    private boolean rightMouseDown;



    public void run()
    {

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure Window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_SAMPLES, 4);

        // Create the window
        window = glfwCreateWindow(500, 500, "MESS", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the window");

        setIcon();

        glfwSetKeyCallback(window, (windowIn, keyIn, scancodeIn, actionIn, modsIn) ->
        {
            if (keyIn == GLFW_KEY_UNKNOWN) return;
            if (keyIn == GLFW_KEY_ESCAPE && actionIn == GLFW_RELEASE) glfwSetWindowShouldClose(window, true);
            keyDown[keyIn] = (actionIn == GLFW_PRESS || actionIn == GLFW_REPEAT);
        });

        glfwSetMouseButtonCallback(window, (windowIn, buttonIn, actionIn, modsIn) ->
        {
            if (buttonIn == GLFW_MOUSE_BUTTON_LEFT)
                leftMouseDown = (actionIn == GLFW_PRESS);
            else if (buttonIn == GLFW_MOUSE_BUTTON_RIGHT)
                rightMouseDown = (actionIn == GLFW_PRESS);
        });

        glfwSetWindowSizeCallback(window, (windowIn, widthIn, heightIn) ->
        {
            width = widthIn;
            height = heightIn;
            resizeViewPort = true;
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush())
        {
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

    private void loop()
    {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        //setup view matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-5, 5, -5, 5, -5, 5);
        glMatrixMode(GL_MODELVIEW);

        //enable depth testing
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        // Set the clear color
        glClearColor(0.1f, 0.1f, 0.1f, 0.1f);

        //Entity block = new Block();
        //Entity player = new Player();
        //createEntity(block);
        //createEntity(player);

        buildCube();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window))
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //resize viewport
            if (resizeViewPort)
            {
                if (width > height)
                    glViewport((width - height) >>> 1, 0, height, height);
                else
                    glViewport(0, (height - width) >>> 1, width, width);
                resizeViewPort = false;
            }

            if(keyDown[GLFW.GLFW_KEY_W]) glTranslated(0,0,-0.1);
            if(keyDown[GLFW.GLFW_KEY_S]) glTranslated(0,0,0.1);
            if(keyDown[GLFW.GLFW_KEY_A]) glTranslated(-0.1,0,0);
            if(keyDown[GLFW.GLFW_KEY_D]) glTranslated(0.1,0,0);

            //glRotated(-player.position[2],0,1,0);

            //System.out.println(player.position[0]+" "+player.position[2]);

            //draw entitys
            //drawEntity(block);
            //drawEntity(player);

            glDrawElements(GL_TRIANGLES, 6 * 6, GL_UNSIGNED_INT, 0L);

            glfwSwapBuffers(window); //swap the color buffers

            //Poll for window events. The key callback above will only be
            //invoked during this call.
            glfwPollEvents();
        }
    }

    //icon code

    private void setIcon()
    {
        int numberOfImages=10;
        GLFWImage[] image = new GLFWImage[numberOfImages];
        GLFWImage.Buffer images = GLFWImage.malloc(numberOfImages);
        for(int i=0;i<numberOfImages;i++)
        {
            int sizeOfImage = 1<<i;
            image[i] = GLFWImage.malloc();
            image[i].set(sizeOfImage,sizeOfImage, genIcon(sizeOfImage));
            images.put(i, image[i]);
        }
        glfwSetWindowIcon(window, images);
        images.free();
        for(int i=0;i<numberOfImages;i++) image[i].free();
    }

    private static ByteBuffer genIcon(int sizeIn)
    {
        ByteBuffer buffer = BufferUtils.createByteBuffer(sizeIn * sizeIn * 4);
        int counter = 0;

        ModuleBasisFunction func = new ModuleBasisFunction();
        func.setType(ModuleBasisFunction.BasisType.GRADVAL);
        func.setInterpolation(ModuleBasisFunction.InterpolationType.QUINTIC);
        func.setSeed(genSeed());
        ModuleAbs mod = new ModuleAbs();
        mod.setSource(func);
        for (int i = 0; i < sizeIn; i++)
        {
            for (int j = 0; j < sizeIn; j++)
            {
                buffer.put(counter + 0, (byte) (255 * mod.get(i, j)));
                buffer.put(counter + 1, (byte) (255 * mod.get(i, j)));
                buffer.put(counter + 2, (byte) (255 * mod.get(i, j)));
                buffer.put(counter + 3, (byte) 0);
                counter += 4;
            }
        }
        return buffer;
    }

    private static long genSeed()
    {
        return (System.nanoTime() - System.currentTimeMillis()) * Thread.activeCount();
    }

    private void createEntity(Entity entityIn)
    {

    }

    private void drawEntity(Entity entityIn)
    {

    }

    private void buildCube() {
        // create buffer to hold the vertex colors
        FloatBuffer cb = BufferUtils.createFloatBuffer(3 * 4 * 6);
        // create buffer to hold the vertex positions
        FloatBuffer pb = BufferUtils.createFloatBuffer(3 * 4 * 6);
        // define all faces of a cube as quads.
        // this has redundant vertices in it, but it's easy to create
        // an element buffer for it.
        for (int i = 0; i < 4; i++)
            cb.put(0.0f).put(0.0f).put(0.2f);
        pb.put( 0.5f).put(-0.5f).put(-0.5f);
        pb.put(-0.5f).put(-0.5f).put(-0.5f);
        pb.put(-0.5f).put( 0.5f).put(-0.5f);
        pb.put( 0.5f).put( 0.5f).put(-0.5f);
        for (int i = 0; i < 4; i++)
            cb.put(0.0f).put(0.0f).put(1.0f);
        pb.put( 0.5f).put(-0.5f).put( 0.5f);
        pb.put( 0.5f).put( 0.5f).put( 0.5f);
        pb.put(-0.5f).put( 0.5f).put( 0.5f);
        pb.put(-0.5f).put(-0.5f).put( 0.5f);
        for (int i = 0; i < 4; i++)
            cb.put(1.0f).put(0.0f).put(0.0f);
        pb.put( 0.5f).put(-0.5f).put(-0.5f);
        pb.put( 0.5f).put( 0.5f).put(-0.5f);
        pb.put( 0.5f).put( 0.5f).put( 0.5f);
        pb.put( 0.5f).put(-0.5f).put( 0.5f);
        for (int i = 0; i < 4; i++)
            cb.put(0.2f).put(0.0f).put(0.0f);
        pb.put(-0.5f).put(-0.5f).put( 0.5f);
        pb.put(-0.5f).put( 0.5f).put( 0.5f);
        pb.put(-0.5f).put( 0.5f).put(-0.5f);
        pb.put(-0.5f).put(-0.5f).put(-0.5f);
        for (int i = 0; i < 4; i++)
            cb.put(0.0f).put(1.0f).put(0.0f);
        pb.put( 0.5f).put( 0.5f).put( 0.5f);
        pb.put( 0.5f).put( 0.5f).put(-0.5f);
        pb.put(-0.5f).put( 0.5f).put(-0.5f);
        pb.put(-0.5f).put( 0.5f).put( 0.5f);
        for (int i = 0; i < 4; i++)
            cb.put(0.0f).put(0.2f).put(0.0f);
        pb.put( 0.5f).put(-0.5f).put(-0.5f);
        pb.put( 0.5f).put(-0.5f).put( 0.5f);
        pb.put(-0.5f).put(-0.5f).put( 0.5f);
        pb.put(-0.5f).put(-0.5f).put(-0.5f);
        pb.flip();
        cb.flip();
        // build element buffer
        IntBuffer eb = BufferUtils.createIntBuffer(6 * 6);
        for (int i = 0; i < 4 * 6; i += 4)
            eb.put(i).put(i+1).put(i+2).put(i+2).put(i+3).put(i+0);
        eb.flip();
        // setup vertex positions buffer
        int cubeVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, cubeVbo);
        glBufferData(GL_ARRAY_BUFFER, pb, GL_STATIC_DRAW);
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        // setup vertex color buffer
        int cubeCb = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, cubeCb);
        glBufferData(GL_ARRAY_BUFFER, cb, GL_STATIC_DRAW);
        glEnableClientState(GL_COLOR_ARRAY);
        glColorPointer(3, GL_FLOAT, 0, 0);
        // setup element buffer
        int cubeEbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, cubeEbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, eb, GL_STATIC_DRAW);
    }
}