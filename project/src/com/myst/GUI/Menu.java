package com.myst.GUI;


import com.myst.helper.Timer;
import com.myst.input.Input;
import com.myst.rendering.Model;
import com.myst.rendering.Shader;
import com.myst.rendering.Window;
import com.myst.rendering.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import sun.nio.ch.IOUtil;


import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;






public class Menu {
    private final float[] baseVertices = new float[] {
            -1f, 0.5f, 0f, /*0*/  1f, 0.5f, 0f, /*1*/    1f, -0.5f, 0f, /*2*/
            -1f, -0.5f, 0f/*3*/
    };
    private final float[] textureDocks = new float[] {
            0f, 0f,   1, 0f,  1f, 1f,
            0f, 1f
    };
    private final int[] indices = new int[] {
            0,1,2,
            2,3,0
    };
    private HashMap<Rectangle2D.Float, String> menuButtons = new HashMap<>();
    private HashMap<Rectangle2D.Float, String> multiplayerButtons = new HashMap<>();
    private HashMap<Rectangle2D.Float, String> joinGameButtons = new HashMap<>();
    private Window window;
    private Shader shader;
    private Input input;
    private MenuStates currentWindow;
    private Boolean multiplayerAccessed;
    private Boolean joinGameAccessed;
    private Boolean typing;
    private String ipAddress;
    private Shader shaderInvis;
    private String port;
    private int change;






    public Menu(Window window, Input input)   {
        this.window = window;
        this.shader = new Shader ("assets/shader");
        this.shaderInvis = new Shader("assets/shader2");
        shaderInvis.setUniform("opacity", 1f);
        this.input = input;
        this.currentWindow = MenuStates.MAIN_MENU;
        this.multiplayerAccessed = false;
        this.joinGameAccessed = false;
        this.ipAddress = "";
        this.port = "";
        //this.font = new Font("Times New Roman", Font.BOLD, 24);
        this.change = 0;
        this.typing = true;



    }

    public static void main(String[] args) {
        Window.setCallbacks();
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialise GLFW");
        }

            Window window = new Window();
            window.setFullscreen(false);
            window.createWindow("My game");

            GL.createCapabilities();
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glClearColor(0f, 0f, 0f, 0f);
            Boolean renderFrame;
            double frame_cap = 1.0 / 60.0;

            double time = Timer.getTime();
            double unprocessed = 0;

            double frame_time = 0;
            int frames = 0;

            double debugCurrentTime = Timer.getTime();
            double debugLastTime = Timer.getTime();
            Menu menu = new Menu(window, window.getInput());
            while (!window.shouldClose()) {

                renderFrame = false;
                double time2 = Timer.getTime();
                double deltaTime = time2 - time;
                time = time2;
                unprocessed += deltaTime;
                frame_time += deltaTime;
//            in the case you want to render a frame as you have gone over the frame_cap
//            a while is used instead of an if incase the performance is less than 30 FPS
                while (unprocessed >= frame_cap) {
//                look into effects of containing a thread.sleep();
//                take away the frame cap so that you account for the time you've taken of the next frame
                    unprocessed -= frame_cap;
                    renderFrame = true;
                    debugCurrentTime = Timer.getTime();
                    double timeSinceLastUpdate = (debugCurrentTime - debugLastTime);
                    debugLastTime = debugCurrentTime;
                    window.update();
                    menu.update();

                }
                if (frame_time >= 1) {
                    System.out.println(frames);
                    frame_time = 0;
                    frames = 0;
                }
                if (renderFrame) {
                    glClear(GL_COLOR_BUFFER_BIT);
                    menu.render();
                    window.swapBuffers();
                    frames += 1;
                }
            }
            //        clears everything we have used from memory
            glfwTerminate();
//        sloppy and needs tidying
            System.exit(1);
        }

    public void renderImage(Shader shader, Texture texture, float x, float y, Matrix4f scale, Model model){
        shader.bind();
        texture.bind(0);
        Matrix4f target = new Matrix4f();

        Matrix4f tile_pos = new Matrix4f().translate(new Vector3f(x,y,0));
        scale.mul(tile_pos, target);


        shader.setUniform("sampler",0);
        shader.setUniform("projection", target);
        model.render();
    }

    public void render(){
        switch(currentWindow){
            case MAIN_MENU:
                this.renderMenu();
                break;
            case MULTIPLAYER:
                this.renderMultiplayer();
                break;
            case HIDDEN:
                break;
            case JOIN_GAME:
                this.renderJoinGame();
                break;
        }
    }

    public void update(){
        switch(currentWindow){
            case MAIN_MENU:
                this.mainMenuInput();
                break;
            case MULTIPLAYER:
                multiplayerAccessed = true;
                this.multiplayerInput();
                break;
            case JOIN_GAME:
                joinGameAccessed = true;
                this.joinGameInput();
                break;
            case HIDDEN:
                break;
        }
    }


    public void renderMenu()    {
        glClear(GL_COLOR_BUFFER_BIT);
        this.renderBackground();
        Texture[] menuTextures = new Texture[]{new Texture("assets/main_menu/singleplayer_button.png"),
                new Texture("assets/main_menu/multiplayer_button.png"), new Texture("assets/main_menu/quit_button.png")};
        setupImages(menuTextures, 0f, 0.5f, true);
    }

    public void renderMultiplayer() {
        glClear(GL_COLOR_BUFFER_BIT);
        this.renderBackground();
        Texture[] multiplayerTextures = new Texture[]{new Texture("assets/main_menu/host_game_button.png"),
        new Texture("assets/main_menu/join_game_button.png")};
        setupImages(multiplayerTextures, 0f, 0.33f, true);
    }

    public void renderJoinGame()    {
        glClear(GL_COLOR_BUFFER_BIT);
        this.renderBackground();
        Texture[] joinGameTextures = new Texture[]
                {new Texture("assets/main_menu/IP.png"), new Texture("assets/main_menu/port.png"), new Texture("assets/main_menu/text_box_1.png"), new Texture("assets/main_menu/text_box_2.png")};
        setupImages(Arrays.copyOfRange(joinGameTextures, 0, 2), -0.5f, 0.25f, false);
        setupImages(Arrays.copyOfRange(joinGameTextures, 2, 4), 0.5f, 0.25f, true);
    }

    public void addButton(float x, float y, float width, float height, String filepath) {
        Rectangle2D.Float bounds = new Rectangle2D.Float(x, y, width , height);
        if (multiplayerAccessed)    {
            multiplayerButtons.put(bounds, filepath);
        }
        else if(joinGameAccessed)   {
            joinGameButtons.put(bounds, filepath);
        }
        else    {
            menuButtons.put(bounds, filepath);
        }
    }

    public void mainMenuInput()   {
        for (Rectangle2D.Float b : menuButtons.keySet())   {

            double mouseX = ((this.input.getMouseCoordinates()[0])/(window.getWidth()/2))-1;
            double mouseY = -(((this.input.getMouseCoordinates()[1])/(window.getHeight()/2))-1);

            if (mouseX >= b.getX() && mouseX <= (b.getX()+b.getWidth()) && mouseY <= b.getY() && mouseY >= (b.getY()-b.getHeight()))  {
                if (window.getInput().isMouseButtonDown(GLFW_MOUSE_BUTTON_1)){
                    String buttonName = menuButtons.get(b);
                    switch(buttonName) {
                        case "singleplayer_button.png":
                            break;
                        case "multiplayer_button.png":
                            this.currentWindow = MenuStates.MULTIPLAYER;
                            try {
                                Thread.sleep(100);

                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "quit_button.png":
                            System.exit(1);
                            break;
                    }
                }
            }
        }
    }

    public void multiplayerInput()   {
        for (Rectangle2D.Float b : multiplayerButtons.keySet())   {

            double mouseX = ((this.input.getMouseCoordinates()[0])/(window.getWidth()/2))-1;
            double mouseY = -(((this.input.getMouseCoordinates()[1])/(window.getHeight()/2))-1);

            if (mouseX >= b.getX() && mouseX <= (b.getX()+b.getWidth()) && mouseY <= b.getY() && mouseY >= (b.getY()-b.getHeight()))  {
                if (window.getInput().isMouseButtonDown(GLFW_MOUSE_BUTTON_1)){
                    String buttonName = multiplayerButtons.get(b);
                    switch(buttonName) {
                        case "host_game_button.png":
                            System.exit(1);
                            break;
                        case "join_game_button.png":
                            multiplayerAccessed = false;
                            this.currentWindow = MenuStates.JOIN_GAME;
                            try {
                                Thread.sleep(80);

                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
        }
        if (window.getInput().isKeyPressed(GLFW_KEY_ESCAPE)) {
            multiplayerAccessed = false;
            currentWindow = MenuStates.MAIN_MENU;
        }
    }

    public void joinGameInput()   {
        for (Rectangle2D.Float b : joinGameButtons.keySet())   {

            double mouseX = ((this.input.getMouseCoordinates()[0])/(window.getWidth()/2))-1;
            double mouseY = -(((this.input.getMouseCoordinates()[1])/(window.getHeight()/2))-1);

            if (mouseX >= b.getX() && mouseX <= (b.getX()+b.getWidth()) && mouseY <= b.getY() && mouseY >= (b.getY()-b.getHeight()))  {
                if (window.getInput().isMouseButtonDown(GLFW_MOUSE_BUTTON_1)){
                    String buttonName = joinGameButtons.get(b);
                    switch(buttonName) {
                        case "text_box_1.png":
                            this.takeInput(new String());
                            break;
                        case "text_box_2.png":
                            System.exit(2);
                            break;
                    }
                }
            }
        }
        if (window.getInput().isKeyPressed(GLFW_KEY_ESCAPE)) {
            joinGameAccessed = false;
            currentWindow = MenuStates.MULTIPLAYER;
        }
    }

    public float[] alterVertices(float[] vertices, int height, int width, double widthScale, double heightScale) {
        vertices[0] *= width * widthScale;
        vertices[3] *= width * widthScale;
        vertices[6] *= width * widthScale;
        vertices[9] *= width * widthScale;

        vertices[1] *= height * heightScale;
        vertices[4] *= height * heightScale;
        vertices[7] *= height * heightScale;
        vertices[10] *= height * heightScale;
        return vertices;
    }

    public void setupImages(Texture[] textureArray, Float x, Float y, Boolean isButton)  {
        Float xPos = x;
        Float yPos = y;
        for (Texture t: textureArray)   {
            float[] vertices = Arrays.copyOf(baseVertices, baseVertices.length);
            vertices = this.alterVertices(vertices, t.getHeight(), t.getWidth(), 0.002, 0.005);
            Model model = new Model(vertices, textureDocks, indices);
            renderImage(shaderInvis, t, xPos, yPos, new Matrix4f(), model);
            if(isButton) {
                this.addButton(0 + vertices[0], yPos + vertices[1], vertices[3] - vertices[0], vertices[1] - vertices[7], t.getPath());
            }
            yPos += (-0.35f);
        }
    }

    public void takeInput(String chosenInput) {
        glfwPollEvents();
        if (!window.getInput().isKeyDown(GLFW_KEY_0))   {
            chosenInput += Integer.toString(2);

        }
        /*    for (int i = 48; i <= 57; i++) {
                if (window.getInput().isKeyPressed(i)) {
                    chosenInput += glfwGetKeyName(i, 0);
                }
            }*/
            renderText(chosenInput);
            if(window.getInput().isMousePressed(GLFW_MOUSE_BUTTON_1))  {
                typing = false;
            }
    }

    public void renderText(String input)    {
        char[] charInput = input.toCharArray();
        float x = 0.5f;
        for(char c : charInput) {
            Texture texture = new Texture("assets/main_menu/IP.png");
            float[] vertices = Arrays.copyOf(baseVertices, baseVertices.length);
            vertices = this.alterVertices(vertices, texture.getHeight(), texture.getWidth(), 0.001, 0.006);
            Model model = new Model(vertices, textureDocks, indices);
            this.renderImage(shader, texture, x, 0.25f, new Matrix4f(), model);
            System.out.println("rendering");
            x = x + 0.05f;
        }
    }

    public void renderBackground()  {

        Texture background = new Texture("assets/main_menu/NighBg0.jpg");
        float[] vertices = Arrays.copyOf(baseVertices, baseVertices.length);
        vertices = this.alterVertices(vertices, background.getHeight(), background.getWidth(), 0.001, 0.003);
        Model model = new Model(vertices, textureDocks, indices);

        this.renderImage(shader, background, 0, 0, new Matrix4f(), model);
        change += 1;

        /*if(change == 100) {
            Random rand = new Random();
            int n = rand.nextInt(3);

            Texture changeBackground = new Texture("assets/main_menu/NighBg" + Integer.toString(n) + ".jpg");
            float[] newVertices = Arrays.copyOf(baseVertices, baseVertices.length);
            newVertices = this.alterVertices(newVertices, changeBackground.getHeight(), changeBackground.getWidth(), 0.001, 0.003);
            Model newModel = new Model(newVertices, textureDocks, indices);
            this.renderImage(shader, changeBackground, 0, 0, new Matrix4f(), newModel);
            background = changeBackground;
            change = 0;
        }   else    {

            this.renderImage(shader, background, 0, 0, new Matrix4f(), model);
            change += 1;


        }*/

    }
}
