package org.toweroy.punchauuut;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * Created by richardtolman on 4/30/17.
 */
public class GlassJoe implements Drawable {

    private static final Logger LOG = LoggerFactory.getLogger(GlassJoe.class);
    private static final String GLASS_JOE_IMAGE_PATH = "images/glass_joe.png";
    // Sub-texture coords
    private static final Coordinates GLASS_JOE_1X1 = new Coordinates(10, 200, 50, 329);
    private static final Coordinates GLASS_JOE_1X2 = new Coordinates(50, 200, 90, 329);
    private static final Coordinates GLASS_JOE_1X3 = new Coordinates(95, 200, 135, 329);
    private static final Coordinates GLASS_JOE_1X4 = new Coordinates(135, 200, 175, 329);
    private static final Coordinates GLASS_JOE_1X11 = new Coordinates(420, 200, 460, 329);

    private List<AnimationListener> animationListeners = new ArrayList<>();
    private Texture glassJoeTexture;
    private TextureCoords glassJoeCoords;

    private float glassJoeTop, glassJoeBottom, glassJoeLeft, glassJoeRight;
    private float glassJoeX = 0.7f;
    private float glassJoeY = 0.4f;
    private boolean initialized;

    public GlassJoe() {
    }

    public void addAnimationListener(AnimationListener animationListener) {
        animationListeners.add(animationListener);
    }

    public void bindTextures(GL2 gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        glassJoeTexture.bind(gl);
    }

    public void init(GL2 gl) {
        TextureData glassJoeTextureData = null;

        try {
            // 552 × 329
            glassJoeTextureData = TextureIO.newTextureData(
                    GLProfile.getDefault(),
                    getClass().getClassLoader().getResource(GLASS_JOE_IMAGE_PATH),
                    GL.GL_RGBA,
                    GL.GL_RGBA,
                    false,
                    TextureIO.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }

        glassJoeTexture = TextureIO.newTexture(glassJoeTextureData);

        getSubImage(GLASS_JOE_1X3);
        animIntro();
    }

    private void getSubImage(Coordinates coordinates) {
        glassJoeCoords = glassJoeTexture.getSubImageTexCoords(
                coordinates.getX1(),
                coordinates.getY1(),
                coordinates.getX2(),
                coordinates.getY2());

        glassJoeTop = glassJoeCoords.top();
        glassJoeBottom = glassJoeCoords.bottom();
        glassJoeLeft = glassJoeCoords.left();
        glassJoeRight = glassJoeCoords.right();
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
        drawGlassJoe(gl, false, glassJoeX, glassJoeY);

        gl.glEnd();
        gl.glDisable(GL_BLEND);
    }

    private void drawGlassJoe(GL2 gl, boolean flip, final float moveX, final float moveY) {
        gl.glTexCoord2f(flip ? glassJoeRight : glassJoeLeft, glassJoeBottom);
        final float transformX = 6.5f;
        final float transformY = 1.8f;

        gl.glVertex3f((-1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f); // bottom-left of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? glassJoeLeft : glassJoeRight, glassJoeBottom);
        gl.glVertex3f((1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f);  // bottom-right of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? glassJoeLeft : glassJoeRight, glassJoeTop);
        gl.glVertex3f((1.0f / transformX) + moveX, (1.0f / transformY) + moveY, 1.0f);   // top-right of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? glassJoeRight : glassJoeLeft, glassJoeTop);
        gl.glVertex3f((-1.0f / transformX) + moveX, (1.0f / transformY) + moveY, 1.0f);  // top-left of the titleScreenTexture and quad
    }

    private void animIntro() {
        //our timer
        Timer alarm = new Timer();

        //our task (really a TimerTask object)
        TimerTask timerTask = new TimerTask() {
            public void run() {
                GlassJoeIntroAnimation animation = new GlassJoeIntroAnimation();
                animation.run();
            }
        };

        alarm.schedule(timerTask, 2000);
    }

    // 2 secs arm up
    // 8 secs start walking
    // 4, 2, 3 walking
    public class GlassJoeIntroAnimation implements Runnable {
        final int moveArmsSleep = 150;

        public void run() {
            getSubImage(GLASS_JOE_1X1);
            try {
                Thread.sleep(1000);

                for (int i = 0; i < 100; i++) {
                    getSubImage(GLASS_JOE_1X3);
                    Thread.sleep(moveArmsSleep);
                    getSubImage(GLASS_JOE_1X2);
                    Thread.sleep(moveArmsSleep);
                    getSubImage(GLASS_JOE_1X4);
                    Thread.sleep(moveArmsSleep);

                    if (glassJoeX > 0.0f) {
                        glassJoeX -= 0.06f;
                        glassJoeY -= 0.028f;
                        i = 96;
                    }
                }

                getSubImage(GLASS_JOE_1X11);
                animIntroDone();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void animIntroDone() {
        animationListeners.forEach(AnimationListener::end);
        animationListeners.clear();
    }
}
