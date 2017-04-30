package org.toweroy.punchauuut;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static org.toweroy.punchauuut.Constants.PNG_IMAGE_FILE_TYPE;

/**
 * Created by richardtolman on 4/30/17.
 */
public class FirstScenario implements Scenario, KeyListener {
    private static final Logger LOG = LoggerFactory.getLogger(GLCanvasMain.class);
    private static final String BOXING_RINGS_IMAGE_PATH = "images/boxing_rings.png";
    // Objects
    private Texture boxingRingsTexture;
    private GLCanvasMain canvas;
    // Primitives
    private float textureTop, textureBottom, textureLeft, textureRight;

    public FirstScenario(GLCanvasMain canvas) {
        this.canvas = canvas;
        canvas.addKeyListener(this);
    }

    public void init(GL2 gl) {
        // Load boxingRingsTexture from image
        try {
            // Create a OpenGL Texture object from (URL, mipmap, file suffix)
            // Use URL so that can read from JAR and disk file.
            boxingRingsTexture = TextureIO.newTexture(
                    getClass().getClassLoader().getResource(BOXING_RINGS_IMAGE_PATH), // relative to project root
                    false, PNG_IMAGE_FILE_TYPE);
            // 775 × 454
            // 62,150 lower left
            // 90,130 top right
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            TextureCoords textureCoords = boxingRingsTexture.getImageTexCoords();
            textureTop = textureCoords.top() / 2;
            textureBottom = textureCoords.bottom();
            textureLeft = textureCoords.left();
            textureRight = textureCoords.right() / 3;
        } catch (GLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bindTextures(GL2 gl) {
        // Enables this boxingRingsTexture's target in the current GL context's state.
        boxingRingsTexture.enable(gl);  // same as gl.glEnable(boxingRingsTexture.getTarget());
        // gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        // Binds this boxingRingsTexture to the current GL context.
        boxingRingsTexture.bind(gl);  // same as gl.glBindTexture(boxingRingsTexture.getTarget(), boxingRingsTexture.getTextureObject());
    }

    public void draw(GL2 gl) {
        // Front Face
        drawBoxingRing(gl);
    }

    private void drawRectangle(GL2 gl, final float moveX, final float moveY) {
        final float transformX = 8;
        final float transformY = 11;

        gl.glVertex3f((-1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f); // bottom-left of the boxingRingsTexture and quad
        gl.glVertex3f((1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f);  // bottom-right of the boxingRingsTexture and quad
        gl.glVertex3f((1.0f / transformX) + moveX, (1.0f / transformY) + moveY, 1.0f);   // top-right of the boxingRingsTexture and quad
        gl.glVertex3f((-1.0f / transformX) + moveX, (1.0f / transformY) + moveY, 1.0f);  // top-left of the boxingRingsTexture and quad
    }

    private void drawBoxingRing(GL2 gl) {
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f); // bottom-left of the boxingRingsTexture and quad
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);  // bottom-right of the boxingRingsTexture and quad
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);   // top-right of the boxingRingsTexture and quad
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);  // top-left of the boxingRingsTexture and quad
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
}
