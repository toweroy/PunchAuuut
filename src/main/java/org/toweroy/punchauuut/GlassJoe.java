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

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

/**
 * Created by richardtolman on 4/30/17.
 */
public class GlassJoe implements Drawable {

    private static final Logger LOG = LoggerFactory.getLogger(GlassJoe.class);
    private static final String GLASS_JOE_IMAGE_PATH = "images/glass_joe.png";

    private Texture glassJoeTexture;

    private float glassJoeTop, glassJoeBottom, glassJoeLeft, glassJoeRight;
    private float glassJoeX = -0.4f;
    private float glassJoeY = -0.4f;

    public void bindTextures(GL2 gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        glassJoeTexture.bind(gl);
    }

    public void init(GL2 gl) {
        TextureData glassJoeTextureData = null;

        try {
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
        // 552 × 329
        TextureCoords glassJoeCoords = glassJoeTexture.getSubImageTexCoords(10, 200, 50, 329);

        glassJoeTop = glassJoeCoords.top();
        glassJoeBottom = glassJoeCoords.bottom();
        glassJoeLeft = glassJoeCoords.left();
        glassJoeRight = glassJoeCoords.right();
    }

    public void draw(GL2 gl) {
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
        final float transformX = 7.0f;
        final float transformY = 2.0f;

        gl.glVertex3f((-1.0f / transformX) + moveX, (-1.0f / transformY) + moveY, 1.0f); // bottom-left of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? glassJoeLeft : glassJoeRight, glassJoeBottom);
        gl.glVertex3f((1.0f / transformX) + moveX, (-1.0f/ transformY) + moveY, 1.0f);  // bottom-right of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? glassJoeLeft : glassJoeRight, glassJoeTop);
        gl.glVertex3f((1.0f/ transformX) + moveX, (1.0f/ transformY) + moveY, 1.0f);   // top-right of the titleScreenTexture and quad
        gl.glTexCoord2f(flip ? glassJoeRight : glassJoeLeft, glassJoeTop);
        gl.glVertex3f((-1.0f/ transformX) + moveX, (1.0f/ transformY) + moveY, 1.0f);  // top-left of the titleScreenTexture and quad
    }
}
