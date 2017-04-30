package org.toweroy.punchauuut.stages;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.toweroy.punchauuut.draw.Drawable;
import org.toweroy.punchauuut.GLCanvasMain;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static org.toweroy.punchauuut.util.Constants.PNG_IMAGE_FILE_TYPE;

/**
 * Created by richardtolman on 4/30/17.
 */
public class TitleScreen implements Drawable, KeyListener {
    private static final Logger LOG = LoggerFactory.getLogger(GLCanvasMain.class);
    private static final String TITLE_SCREEN_IMAGE_PATH = "images/title_screen.png";
    // Objects
    private Texture titleScreenTexture;
    private GLCanvasMain canvas;
    // Primitives
    private boolean startFocused = true;
    private float gloveTop, gloveBottom, gloveLeft, gloveRight;
    private float leftGloveY = -0.24f;
    private float rightGloveY = -0.24f;
    private float textureTop, textureBottom, textureLeft, textureRight;

    public TitleScreen(GLCanvasMain canvas) {
        this.canvas = canvas;
        canvas.addKeyListener(this);
    }

    public void init(GL2 gl) {
        // Load titleScreenTexture from image
        try {
            // Create a OpenGL Texture object from (URL, mipmap, file suffix)
            // Use URL so that can read from JAR and disk file.
            titleScreenTexture = TextureIO.newTexture(
                    getClass().getClassLoader().getResource(TITLE_SCREEN_IMAGE_PATH), // relative to project root
                    false, PNG_IMAGE_FILE_TYPE);
            // 256 × 224
            // 62,150 lower left
            // 90,130 top right
            TextureCoords gloveCoords = titleScreenTexture.getSubImageTexCoords(60, 75, 90, 95);
            // Use linear filter for titleScreenTexture if image is larger than the original titleScreenTexture
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            // Use linear filter for titleScreenTexture if image is smaller than the original titleScreenTexture
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            // Texture image flips vertically. Shall use TextureCoords class to retrieve
            // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
            TextureCoords textureCoords = titleScreenTexture.getImageTexCoords();
            textureTop = textureCoords.top();
            gloveTop = gloveCoords.top();
            textureBottom = textureCoords.bottom();
            gloveBottom = gloveCoords.bottom();
            textureLeft = textureCoords.left();
            gloveLeft = gloveCoords.left();
            textureRight = textureCoords.right();
            gloveRight = gloveCoords.right();

            bindTextures(gl);
        } catch (GLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bindTextures(GL2 gl) {
        // Enables this titleScreenTexture's target in the current GL context's state.
        titleScreenTexture.enable(gl);  // same as gl.glEnable(titleScreenTexture.getTarget());
        // gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        // Binds this titleScreenTexture to the current GL context.
        titleScreenTexture.bind(gl);  // same as gl.glBindTexture(titleScreenTexture.getTarget(), titleScreenTexture.getTextureObject());
    }

    public void draw(GL2 gl) {
        gl.glBegin(GL_QUADS);
        // Front Face
        drawMainScreen(gl);
        // "Erase" main screen gloves
        drawRectangle(gl, -0.4f, -0.24f);
        drawRectangle(gl, 0.4f, -0.24f);
        // Left love
        drawGlove(gl, false, -0.4f, leftGloveY);
        // Right glove
        drawGlove(gl, true, 0.4f, rightGloveY);

        gl.glEnd();
    }

    private void drawRectangle(GL2 gl, final float moveX, final float moveY) {
        final float transformX = 8;
        final float transformY = 11;

        gl.glVertex3f((-1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f); // bottom-left of the titleScreenTexture and quad
        gl.glVertex3f((1.0f / transformX) + moveX, (-1.0f/ transformY) + moveY, 1.0f);  // bottom-right of the titleScreenTexture and quad
        gl.glVertex3f((1.0f/ transformX) + moveX, (1.0f/ transformY) + moveY, 1.0f);   // top-right of the titleScreenTexture and quad
        gl.glVertex3f((-1.0f/ transformX) + moveX, (1.0f/ transformY) + moveY, 1.0f);  // top-left of the titleScreenTexture and quad
    }

    private void drawGlove(GL2 gl, boolean flip, final float moveX, final float moveY) {
        gl.glTexCoord2f(flip ? gloveRight : gloveLeft, gloveBottom);
        final float transformX = 8;
        final float transformY = 11;

        gl.glVertex3f((-1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f); // bottom-left of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? gloveLeft : gloveRight, gloveBottom);
        gl.glVertex3f((1.0f / transformX) + moveX, (-1.0f/ transformY) + moveY, 1.0f);  // bottom-right of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? gloveLeft : gloveRight, gloveTop);
        gl.glVertex3f((1.0f/ transformX) + moveX, (1.0f/ transformY) + moveY, 1.0f);   // top-right of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? gloveRight : gloveLeft, gloveTop);
        gl.glVertex3f((-1.0f/ transformX) + moveX, (1.0f/ transformY) + moveY, 1.0f);  // top-left of the titleScreenTexture and quad
    }

    private void drawMainScreen(GL2 gl) {
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f); // bottom-left of the titleScreenTexture and quad
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);  // bottom-right of the titleScreenTexture and quad
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);   // top-right of the titleScreenTexture and quad
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);  // top-left of the titleScreenTexture and quad
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                leftGloveY = startFocused ? -0.4f : -0.24f;
                rightGloveY = startFocused ? -0.4f : -0.24f;
                startFocused = !startFocused;
                LOG.debug("Pressed SHIFT (%s)", e.getKeyChar());
                break;
            case KeyEvent.VK_ENTER:
                canvas.setCurrentScenario(new GlassJoeStage(canvas));
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent e) {

    }
}
