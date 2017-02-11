package org.toweroy.punchauuut;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.*;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_NICEST;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

/**
 * JOGL 2.0 Program Template (GLCanvas)
 * This is a "Component" which can be added into a top-level "Container".
 * It also handles the OpenGL events to render graphics.
 *
 * Example from: http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 */
@SuppressWarnings("serial")
public class GLCanvasExample extends GLCanvas implements GLEventListener {
    // Define constants for the top-level container
    private static String TITLE = "JOGL 2.0 Setup (GLCanvas)";  // window's title
    private static final int CANVAS_WIDTH = 640;  // width of the drawable
    private static final int CANVAS_HEIGHT = 480; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second

    private Texture texture;
    private String textureFileName = "images/title_screen.png";
    private String textureFileType = ".png";
    // Texture image flips vertically. Shall use TextureCoords class to retrieve the
    // top, bottom, left and right coordinates.
    private float textureTop, textureBottom, textureLeft, textureRight;

    /**
     * The entry main() method to setup the top-level container and animator
     */
    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create the OpenGL rendering canvas
                GLCanvas canvas = new GLCanvasExample();
                canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

                // Create a animator that drives canvas' display() at the specified FPS.
                final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

                // Create the top-level container
                final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
                frame.getContentPane().add(canvas);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Use a dedicate thread to run the stop() to ensure that the
                        // animator stops before program exits.
                        new Thread() {
                            @Override
                            public void run() {
                                if (animator.isStarted()) animator.stop();
                                System.exit(0);
                            }
                        }.start();
                    }
                });
                frame.setTitle(TITLE);
                frame.pack();
                frame.setVisible(true);
                animator.start(); // start the animation loop
            }
        });
    }

    // Setup OpenGL Graphics Renderer

    private GLU glu;  // for the GL Utility

    /**
     * Constructor to setup the GUI for this Component
     */
    public GLCanvasExample() {
        this.addGLEventListener(this);
    }

    // ------ Implement methods declared in GLEventListener ------

    /**
     * Called back immediately after the OpenGL context is initialized. Can be used
     * to perform one-time initialization. Run only once.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting

        // ----- Your OpenGL initialization code here -----

        // Load texture from image
        try {
            // Create a OpenGL Texture object from (URL, mipmap, file suffix)
            // Use URL so that can read from JAR and disk file.
            texture = TextureIO.newTexture(
                    getClass().getClassLoader().getResource(textureFileName), // relative to project root
                    false, textureFileType);
            texture.getSubImageTexCoords()

            // Use linear filter for texture if image is larger than the original texture
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            // Use linear filter for texture if image is smaller than the original texture
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

            // Texture image flips vertically. Shall use TextureCoords class to retrieve
            // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
            TextureCoords textureCoords = texture.getImageTexCoords();
            textureTop = textureCoords.top();
            textureBottom = textureCoords.bottom();
            textureLeft = textureCoords.left();
            textureRight = textureCoords.right();
        } catch (GLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call-back handler for window re-size event. Also called when the drawable is
     * first set to visible.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) height = 1;   // prevent divide by zero
        float aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear, zFar

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset
    }

    /**
     * Called back by the animator to perform rendering.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers

        gl.glLoadIdentity();  // reset the model-view matrix

        // ----- Your OpenGL rendering code here (Render a white triangle for testing) -----
        gl.glTranslatef(0.0f, 0.0f, -6.0f); // translate into the screen
//        gl.glBegin(GL_TRIANGLES); // draw using triangles
//        gl.glVertex3f(0.0f, 1.0f, 0.0f);
//        gl.glVertex3f(-1.0f, -1.0f, 0.0f);
//        gl.glVertex3f(1.0f, -1.0f, 0.0f);
//        gl.glEnd();
        // Enables this texture's target in the current GL context's state.
        texture.enable(gl);  // same as gl.glEnable(texture.getTarget());
        // gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        // Binds this texture to the current GL context.
        texture.bind(gl);  // same as gl.glBindTexture(texture.getTarget(), texture.getTextureObject());

        gl.glBegin(GL_QUADS);
        // Front Face
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f); // bottom-left of the texture and quad
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);  // bottom-right of the texture and quad
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);   // top-right of the texture and quad
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);  // top-left of the texture and quad

        gl.glEnd();
    }

    /**
     * Called back before the OpenGL context is destroyed. Release resource such as buffers.
     */
    public void dispose(GLAutoDrawable drawable) {
    }
}
