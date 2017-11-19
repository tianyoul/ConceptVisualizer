package edu.macalester.comp124.hw6;

import acm.graphics.GCompound;
import acm.graphics.GLabel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A multiline label.
 * The string can include newlines ("\n" characters) to indicate line breaks.
 * @author Shilad Sen
 */
public class FancyLabel extends GCompound {
    private Color color = Color.BLACK;
    private List<GLabel> children = new ArrayList<GLabel>();

    public FancyLabel(String text) {
        this.setText(text);
    }

    public void setText(String text) {
        this.removeAll();
        children.clear();
        int y = 0;
        for (String line : text.split("\n")) {
            GLabel label = new GLabel(line);
            label.setColor(color);
            add(label, 0.0, label.getHeight() + y);
            y += label.getHeight() + 10;
            children.add(label);
        }
    }

    public void setColor(Color color) {
        for (GLabel child : children) {
            child.setColor(color);
        }
        this.color = color;
    }
}
