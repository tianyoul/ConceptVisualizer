package edu.macalester.comp124.hw6;

import acm.graphics.GDimension;
import acm.graphics.GImage;
import acm.graphics.GObject;
import acm.program.GraphicsProgram;
import org.h2.mvstore.Page;
import org.wikapidia.core.lang.Language;
import org.wikapidia.core.model.LocalPage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

/**
 * Visualizes the most popular concepts in each language,
 * and the pages in other languages associated with the same concept.
 *
 * This class MUST be run as a Java application (ConceptVisualizer.main()).
 * This class MUST be run from the module directory.
 *
 * @author Shilad Sen
 */
public class ConceptVisualizer extends GraphicsProgram {
    private static final int PAGES_PER_LANG = 30;

    // The three languages loaded in the database
    private static final Language SIMPLE = Language.getByLangCode("simple");
    private static final Language HINDI = Language.getByLangCode("hi");
    private static final Language LATIN = Language.getByLangCode("la");

    // The Wikapidia API object
    private WikAPIdiaWrapper wp;

    private LanguageBoxes simpleBoxes;
    private LanguageBoxes latinBoxes;
    private LanguageBoxes hindiBoxes;

    // Descriptive label
    private FancyLabel label;

    /**
     * Lays out the graphic components of the widget.
     */
    public void init() {
        setSize(800, 400);
        wp = new WikAPIdiaWrapper();
        try {
            GImage bg = new GImage(ImageIO.read(getClass().getResource("/background.jpg")));
            bg.setSize(new GDimension(800, 400));
            add(bg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        label = new FancyLabel("Hover over a title to analyze it");
        label.setColor(ColorPallete.FONT_COLOR);

        add(label, 20, 20);
        simpleBoxes = makeBoxes(SIMPLE, ColorPallete.COLOR1, 150);
        hindiBoxes = makeBoxes(HINDI, ColorPallete.COLOR2, 225);
        latinBoxes = makeBoxes(LATIN, ColorPallete.COLOR3, 300);
        addMouseListeners();
        super.init();
    }

    public void run() {
        super.run();
        setSize(800, 400);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        LocalPage hoverPage = getPageAt(e.getX(), e.getY());

        if (hoverPage == null) {
            simpleBoxes.unhighlight();
            latinBoxes.unhighlight();
            hindiBoxes.unhighlight();
            label.setText("Hover over a title to analyze it");
        } else {
            List<LocalPage> pages = wp.getInOtherLanguages(hoverPage);
            String description = "";

            //System.out.println("You hovered over " + hoverPage);
            // TODO:
            // Get pages representing the same concept in other languages.
            // Build up a textual description of the pages in each language.
            // You can insert line breaks in your textual description using newlines ("\n").
            //List<LocalPage> list = wp.getInOtherLanguages(hoverPage);
            for(LocalPage page:pages){
                description += page.getLanguage()+": "+page.getTitle()+"\n";
            }

            label.setText(description);

            simpleBoxes.highlightPages(pages);
            latinBoxes.highlightPages(pages);
            hindiBoxes.highlightPages(pages);
        }
    }

    /**
     * Creates boxes for a particular language.
     * @param language
     * @param color
     * @param y
     * @return
     */
    private LanguageBoxes makeBoxes(Language language, Color color, int y) {
        PopularArticleAnalyzer analyzer = new PopularArticleAnalyzer(wp);
        List<LocalPage> popular = analyzer.getMostPopular(language, PAGES_PER_LANG);
        LanguageBoxes boxes = new LanguageBoxes(color, language, popular);
        add(boxes, 20, y);
        return boxes;
    }

    /**
     * Returns the page at an x, y location
     * @param x
     * @param y
     * @return
     */
    private LocalPage getPageAt(double x, double y) {
        GObject o = getElementAt(x, y);
        if (o instanceof LanguageBoxes) {
            LanguageBoxes boxes = (LanguageBoxes)o;
            LocalPageBox box = boxes.getLocalBoxAt(x, y);
            if (box != null) {
                return box.getPage();
            }
        }
        return null;
    }

    public static void main(String args[]) {
        ConceptVisualizer visualizer = new ConceptVisualizer();
        visualizer.start(args);
    }
}
