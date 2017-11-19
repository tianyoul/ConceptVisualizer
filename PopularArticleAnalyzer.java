package edu.macalester.comp124.hw6;

import org.wikapidia.conf.ConfigurationException;
import org.wikapidia.core.dao.DaoException;
import org.wikapidia.core.lang.Language;
import org.wikapidia.core.model.LocalPage;

import java.util.*;

/**
 * Analyzes the overlap in popular concepts.
 * Experimental code for Shilad's intro Java course.
 * Note that you MUST correct WikAPIdiaWrapper.DATA_DIRECTORY before this works.
 *
 * @author Shilad Sen
 */
public class PopularArticleAnalyzer {
    private final WikAPIdiaWrapper wpApi;

    /**
     * Constructs a new analyzer.
     * @param wpApi
     */
    public PopularArticleAnalyzer(WikAPIdiaWrapper wpApi) {
        this.wpApi = wpApi;
    }

    /**
     * Returns the n most popular articles in the specified language.
     * @param language
     * @param n
     * @return
     */
    public List<LocalPage> getMostPopular(Language language, int n) {
        List<LocalPagePopularity> poplist = new ArrayList<>();
        List<LocalPage> pagelist = this.wpApi.getLocalPages(language);
        List<LocalPage> newlist = new ArrayList<>();
        for( LocalPage page : pagelist){
            poplist.add(new LocalPagePopularity(page,this.wpApi.getNumInLinks(page)));
        }
        Collections.sort(poplist);
        for(int i = 0; i<n;i++){
            newlist.add(poplist.get(i).getPage());
        }
        return newlist;    // TODO: implement me for part 1
    }

    public static void main(String args[]) {
        Language simple = Language.getByLangCode("simple");

        // Change the path below to point to the parent directory on the lab computer
        // or laptop that holds the BIG "db" directory.
        WikAPIdiaWrapper wrapper = new WikAPIdiaWrapper();


        // TODO: Complete me for part 1.
        // construct a PopularArticleAnalyzer
        // Print out the 20 most popular articles in the language.
        // United states should be #1
        PopularArticleAnalyzer analyzer = new PopularArticleAnalyzer(wrapper);
        for(LocalPage page:analyzer.getMostPopular(simple,20)){
            System.out.println(page.getTitle());
        }

    }
}
