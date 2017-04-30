package org.toweroy.punchauuut.characters;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.toweroy.punchauuut.animation.AnimationListener;
import org.toweroy.punchauuut.draw.Drawable;
import org.toweroy.punchauuut.util.Coordinates;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * Created by richardtolman on 4/30/17.
 */
public class LittleMac implements Drawable, KeyListener, AnimationListener {

    private static final Logger LOG = LoggerFactory.getLogger(LittleMac.class);
    private static final String LITTLE_MAC_IMAGE_PATH = "images/little_mac.png";
    // Sub-texture coords (30,  103)
    private static final Coordinates LITTLE_MAC_2X2 = new Coordinates(86, 1828, 116, 1920);
    private static final Coordinates LITTLE_MAC_3X2 = new Coordinates(86, 1739, 116, 1840);
    private static final Coordinates LITTLE_MAC_3X3 = new Coordinates(122, 1739, 152, 1840);
    private static final Coordinates LITTLE_MAC_3X4 = new Coordinates(160, 1739, 190, 1840);
    private static final Coordinates LITTLE_MAC_3X5 = new Coordinates(205, 1739, 235, 1840);

    private Texture littleMacTexture;
    private TextureCoords littleMacCoords;

    private float littleMacTop, littleMacBottom, littleMacLeft, littleMacRight;
    private float littleMacX = 0.0f;
    private float littleMacY = -0.8f;
    private boolean initialized;
    private boolean readyToPunch;

    public LittleMac() {

    }

    public void bindTextures(GL2 gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        littleMacTexture.bind(gl);
    }

    public void init(GL2 gl) {
        TextureData glassJoeTextureData = null;

        try {
            // 552 × 329
            glassJoeTextureData = TextureIO.newTextureData(
                    GLProfile.getDefault(),
                    getClass().getClassLoader().getResource(LITTLE_MAC_IMAGE_PATH),
                    GL.GL_RGBA,
                    GL.GL_RGBA,
                    false,
                    TextureIO.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }

        littleMacTexture = TextureIO.newTexture(glassJoeTextureData);

        getSubImage(LITTLE_MAC_2X2);
    }

    private void getSubImage(Coordinates coordinates) {
        littleMacCoords = littleMacTexture.getSubImageTexCoords(
                coordinates.getX1(),
                coordinates.getY1(),
                coordinates.getX2(),
                coordinates.getY2());

        littleMacTop = littleMacCoords.top();
        littleMacBottom = littleMacCoords.bottom();
        littleMacLeft = littleMacCoords.left();
        littleMacRight = littleMacCoords.right();
    }

    public void draw(GL2 gl) {
        if (!initialized) {
            init(gl);
            initialized = true;
        }
        bindTextures(gl);
        gl.glEnable(GL_BLEND);
        gl.glBegin(GL_QUADS);
        // Draw Glass Joe
        drawLittleMac(gl, false, littleMacX, littleMacY);

        gl.glEnd();
        gl.glDisable(GL_BLEND);
    }

    private void drawLittleMac(GL2 gl, boolean flip, final float moveX, final float moveY) {
        gl.glTexCoord2f(flip ? littleMacRight : littleMacLeft, littleMacBottom);
        final float transformX = 8.7f;
        final float transformY = 2.4f;

        gl.glVertex3f((-1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f); // bottom-left of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? littleMacLeft : littleMacRight, littleMacBottom);
        gl.glVertex3f((1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f);  // bottom-right of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? littleMacLeft : littleMacRight, littleMacTop);
        gl.glVertex3f((1.0f / transformX) + moveX, (1.0f / transformY) + moveY, 1.0f);   // top-right of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? littleMacRight : littleMacLeft, littleMacTop);
        gl.glVertex3f((-1.0f / transformX) + moveX, (1.0f / transformY) + moveY, 1.0f);  // top-left of the titleScreenTexture and quad
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (readyToPunch) {
                    LOG.debug("Punching LEFT");
                    leftPunch();
                } else {
                    LOG.debug("Not ready to punch LEFT");
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (readyToPunch) {
                    LOG.debug("Punching RIGHT");
                    rightPunch();
                } else {
                    LOG.debug("Not ready to punch RIGHT");
                }
                break;
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    private void leftPunch() {
        PunchAnimation animation = new PunchAnimation(LITTLE_MAC_3X2, LITTLE_MAC_3X4);
        Thread punch = new Thread(animation);
        punch.start();
    }

    private void rightPunch() {
        PunchAnimation animation = new PunchAnimation(LITTLE_MAC_3X3, LITTLE_MAC_3X5);
        Thread punch = new Thread(animation);
        punch.start();
    }

    @Override
    public void end() {
        readyToPunch = true;
    }

    public class PunchAnimation implements Runnable {
        final int punchDelay = 100;
        final Coordinates[] coordinates;

        public PunchAnimation(Coordinates... coordinates) {
            this.coordinates = coordinates;
        }

        public void run() {
            try {
                getSubImage(coordinates[0]);
                Thread.sleep(punchDelay);
                getSubImage(coordinates[1]);
                Thread.sleep(punchDelay);
                getSubImage(LITTLE_MAC_2X2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
