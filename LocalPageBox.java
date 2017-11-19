package edu.macalester.comp124.hw6;

import acm.graphics.GCompound;
import acm.graphics.GRect;
import org.wikapidia.core.model.LocalPage;

import java.awt.*;

/**
 * A box that represents a local page.
 *
 * @author Shilad Sen
 */
public class LocalPageBox extends GCompound {
    private static final int WIDTH = 15;
    private static final int HEIGHT = 20;

    private final LocalPage page;
    private final Color color;
    private final GRect grect;

    public LocalPageBox(Color color, LocalPage page) {
        this.color = color;
        this.page = page;
        grect = new GRect(WIDTH, HEIGHT);
        grect.setFilled(true);
        grect.setFillColor(color);
        add(grect);
    }
    /**
     * Returns the normal color of this box.
     * @return
     */

    public Color getColor(){return color;}

    /**
     * Returns the page associated with this box.
     * @return
     */
    public LocalPage getPage() {
        return page;
    }

    /**
     * Sets the color of the rectangle.
     * @param color
     */
    public void setFillColor(Color color) {
        grect.setFillColor(color);
    }
}
