package edu.macalester.comp124.hw6;

import org.wikapidia.core.lang.Language;
import org.wikapidia.core.model.LocalPage;

/**
 * Note that you MUST correct WikAPIdiaWrapper.DATA_DIRECTORY before this works.
 *
 * @author Shilad Sen
 */
public class WikAPIdiaExample {
    public static void main(String args[]) {
        Language simple = Language.getByLangCode("simple");

        //
        WikAPIdiaWrapper wrapper = new WikAPIdiaWrapper();

        // A simple test of the WikAPIdia wrapper.
        LocalPage page = wrapper.getLocalPageByTitle(simple, "Apple");
        if (page == null) {
            System.err.println("COULDN'T FIND Apple! Something's wrong...");
            System.exit(1);
        }
        System.out.println("page is " + page);
        for (LocalPage lp : wrapper.getInOtherLanguages(page)) {
            int inlinks = wrapper.getNumInLinks(lp);
            System.err.println("page in " + lp.getLanguage() + " is " + lp.getTitle() + " with " + inlinks + " inlinks");
        }
    }
}
