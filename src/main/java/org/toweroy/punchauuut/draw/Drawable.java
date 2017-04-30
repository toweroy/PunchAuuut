package org.toweroy.punchauuut.draw;

import com.jogamp.opengl.GL2;

import java.security.Key;

/**
 * Created by richardtolman on 4/30/17.
 */
public interface Drawable {

    void bindTextures(GL2 gl);

    void init(GL2 gl);

    void draw(GL2 gl);
}
