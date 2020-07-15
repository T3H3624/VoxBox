import com.sudoplay.joise.module.ModuleAbs;
import com.sudoplay.joise.module.ModuleBasisFunction;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Engine
{

    // The window handle
    private long window;
    private int width, height;
    private boolean resizeViewPort;
    private boolean[] keyDown = new boolean[GLFW.GLFW_KEY_LAST];
    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private boolean leftMouseDown;
    private boolean rightMouseDown;
    private long lastTick=0;

    private final String WINDOW_TITLE = "VoxBox";

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
        window = glfwCreateWindow(500, 500, WINDOW_TITLE, NULL, NULL);
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

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        //setup view matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-5, 5, -5, 5, 5, -50);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        //enable depth testing
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);;

        // Make the window visible
        glfwShowWindow(window);


        // Set the clear color
        glClearColor(0.1f, 0.5f, 0.5f, 0.1f);

        //add entities
        entities.add(new EntityPlayer());
        entities.add(new EntityPlane());

        for(int i=0; i<entities.size(); i++)
            entities.get(i).create();
    }

    private void loop()
    {

        // Run the game loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window))
        {


            double speed = 0.01;

            if(keyDown[GLFW.GLFW_KEY_W]){
                entities.get(0).position[0]+=Math.sin(entities.get(0).rotation[2])*speed;
                entities.get(0).position[2]-=Math.cos(entities.get(0).rotation[2])*speed;
            }
            if(keyDown[GLFW.GLFW_KEY_S]){
                entities.get(0).position[0]-=Math.sin(entities.get(0).rotation[2])*speed;
                entities.get(0).position[2]+=Math.cos(entities.get(0).rotation[2])*speed;
            }
            if(keyDown[GLFW.GLFW_KEY_A]){
                entities.get(0).position[0]-=Math.cos(entities.get(0).rotation[2])*speed;
                entities.get(0).position[2]+=Math.sin(entities.get(0).rotation[2])*speed;
            }
            if(keyDown[GLFW.GLFW_KEY_D]){
                entities.get(0).position[0]+=Math.cos(entities.get(0).rotation[2])*speed;
                entities.get(0).position[2]-=Math.sin(entities.get(0).rotation[2])*speed;
            }
            if(keyDown[GLFW.GLFW_KEY_LEFT]){
                entities.get(0).rotation[1]+=0.1;
                entities.get(0).rotation[1]%=180;
            }
            if(keyDown[GLFW.GLFW_KEY_RIGHT]){
                entities.get(0).rotation[1]-=0.1;
                entities.get(0).rotation[1]%=180;
            }

            //Poll for window events. The key callback above will only be
            //invoked during this call.
            glfwPollEvents();

            //not the right way to do it
            glRotated(entities.get(0).rotation[2], 0, 0, 1);
            glRotated(entities.get(0).rotation[1], 0, 1, 0);
            glRotated(entities.get(0).rotation[0], 1, 0, 0);
            glTranslated(-entities.get(0).position[0],-entities.get(0).position[1],-entities.get(0).position[2]);

            //draw entitys
            //glBegin(GL_TRIANGLES);
            //glColor3f(1f, 0f, 0f);
            //glVertex3f(-0.6f, -0.4f, 0f);
            //glColor3f(0f, 1f, 0f);
            //glVertex3f(0.6f, -0.4f, 0f);
            //glColor3f(0f, 0f, 1f);
            //glVertex3f(0f, 0.6f, 0f);
            //glEnd();

            //glDrawElements(GL_TRIANGLES, 6 * 6, GL_UNSIGNED_INT, NULL);

            for(int i=0; i<entities.size(); i++)
            {
                Entity ent = entities.get(i);
                glBindVertexArray(ent.entityVao);
                glEnableVertexAttribArray(0);
                glEnableVertexAttribArray(1);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ent.positionVbo);
                glDrawElements(GL_TRIANGLES, ent.indices.length, GL_UNSIGNED_INT, 0);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
                glDisableVertexAttribArray(0);
                glDisableVertexAttribArray(1);
                glBindVertexArray(0);
            }

            glfwSwapBuffers(window); //swap the color buffers

            while(System.currentTimeMillis()-lastTick<17);
            lastTick=System.currentTimeMillis();

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
}