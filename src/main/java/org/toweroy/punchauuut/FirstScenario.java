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
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static org.toweroy.punchauuut.Constants.PNG_IMAGE_FILE_TYPE;

/**
 * Created by richardtolman on 4/30/17.
 */
public class FirstScenario implements Drawable, KeyListener {

    private static final Logger LOG = LoggerFactory.getLogger(FirstScenario.class);
    private static final String BOXING_RINGS_IMAGE_PATH = "images/boxing_rings.png";

    private GlassJoe glassJoe = new GlassJoe();
    private LittleMac littleMac = new LittleMac();
    private GLCanvasMain canvas;
    private Texture boxingRingsTexture;

    private float textureTop, textureBottom, textureLeft, textureRight;

    public FirstScenario(GLCanvasMain canvas) {
        this.canvas = canvas;
        canvas.addKeyListener(this);
        canvas.addKeyListener(littleMac);
        glassJoe.addAnimationListener(littleMac);
    }

    public void init(GL2 gl) {
        try {
            // 775 × 454
            boxingRingsTexture = TextureIO.newTexture(
                    getClass().getClassLoader().getResource(BOXING_RINGS_IMAGE_PATH), // relative to project root
                    false, PNG_IMAGE_FILE_TYPE);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            TextureCoords textureCoords = boxingRingsTexture.getImageTexCoords();
            textureTop = textureCoords.top() / 2;
            textureBottom = textureCoords.bottom();
            textureLeft = textureCoords.left();
            textureRight = textureCoords.right() / 3;

            bindTextures(gl);

        } catch (GLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bindTextures(GL2 gl) {
        boxingRingsTexture.enable(gl);
        boxingRingsTexture.bind(gl);
    }

    public void draw(GL2 gl) {
        gl.glBegin(GL_QUADS);
        // Front Face
        drawBoxingRing(gl);

        gl.glEnd();

        glassJoe.draw(gl);
        littleMac.draw(gl);
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
