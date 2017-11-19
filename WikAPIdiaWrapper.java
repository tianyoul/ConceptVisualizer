package edu.macalester.comp124.hw6;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.wikapidia.conf.ConfigurationException;
import org.wikapidia.core.cmd.Env;
import org.wikapidia.core.cmd.EnvBuilder;
import org.wikapidia.core.dao.*;
import org.wikapidia.core.lang.Language;
import org.wikapidia.core.lang.LanguageSet;
import org.wikapidia.core.lang.LocalId;
import org.wikapidia.core.model.*;
import org.wikapidia.dao.load.PipelineLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Shilad wrote this wrapper around the WikAPIdia API for COMP 124.
 *
 * The design strives to be understandable to intro students, so parts of it may seem
 * awkward to experienced Java programmers.
 *
 * @author Shilad Sen
 */
public class WikAPIdiaWrapper {

    // The data directory for WikAPIdia. Change this so it is correct for your laptop
    // It should be the full path to either "wp-db-large" or "wp-db-small"
    private static final String DATA_DIRECTORY = "/Users/user/Desktop/wp/wp-db-large";

    private static final int CONCEPT_ALGORITHM_ID = 1;

    private final Env env;
    private RawPageDao rpDao;
    private LocalPageDao lpDao;
    private LocalLinkDao llDao;
    private UniversalPageDao upDao;

    public WikAPIdiaWrapper() {
        this(DATA_DIRECTORY);
    }

    /**
     * Creates a new wrapper object with default configuration settings.
     *
     * baseDir should be the parent "wikAPIdia" directory containing the "db" directory.
     * You must have read / write permissions in this directory.
     */
    public WikAPIdiaWrapper(String baseDir) {
        try {
            File dbDir = new File(baseDir);
            System.err.println("Checking to see if " + dbDir.getAbsolutePath() + " exists...");
            if (!FilenameUtils.getBaseName(dbDir.getAbsolutePath()).equals("db")) {
                dbDir = new File(dbDir, "db");
            }
            if (!dbDir.isDirectory()) {
                System.err.println(
                        "\n\n!!!!!!!!!!!!!!ERROR. READ THIS MESSAGE!!!!!!!!!!!!!!!!!\n" +
                        "Database directory " + dbDir.getAbsolutePath() + " does not exist.\n" +
                                "Have you downloaded and extracted the database?\n" +
                                "Did you specify the correct DATA_DIRECTORY in WikAPIdiaWrapper?"
                );
                System.exit(1);
            }
            env = new EnvBuilder()
                    .setBaseDir(dbDir.getParent())
                    .build();
            this.rpDao = env.getConfigurator().get(RawPageDao.class);
            this.lpDao = env.getConfigurator().get(LocalPageDao.class);
            this.llDao = env.getConfigurator().get(LocalLinkDao.class);
            this.upDao = env.getConfigurator().get(UniversalPageDao.class);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The list of installed languages.
     */
    public List<Language> getLanguages() {
        LanguageSet lset = env.getLanguages();
        return new ArrayList<Language>(lset.getLanguages());
    }

    /**
     * Returns a local page with a particular title.
     */
    public LocalPage getLocalPageByTitle(Language language, String title) {
        try {
            return lpDao.getByTitle(new Title(title, language), NameSpace.ARTICLE);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the number of WikiLinks to a particular page.
     * @param page
     * @return
     */
    public int getNumInLinks(LocalPage page) {
        DaoFilter filter = new DaoFilter()
                .setLanguages(page.getLanguage())
                .setDestIds(page.getLocalId());
        try {
            return llDao.getCount(filter);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of all the local pages in the same language that are linked to from a particular page.
     * @param page
     * @return
     */
    public List<LocalPage> getLocalPageLinks(LocalPage page){
        List<LocalPage> links = new ArrayList<LocalPage>();
        try{
            for(LocalLink link : llDao.getLinks(page.getLanguage(), page.getLocalId(), true)){
                if(link.isParseable()){
                    links.add(lpDao.getById(page.getLanguage(), link.getDestId()));
                }
            }
        } catch (DaoException e){
            throw new RuntimeException(e);
        }
        return links;
    }

    /**
     * Returns a list of ALL the local pages in a particular language.
     * @param language
     * @return
     */
    public List<LocalPage> getLocalPages(Language language) {
        DaoFilter df = new DaoFilter()
                .setLanguages(language)
                .setRedirect(false)
                .setDisambig(false)
                .setNameSpaces(NameSpace.ARTICLE);
        try {
            return IteratorUtils.toList(lpDao.get(df).iterator());
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of the pages that represent the same concept in other languages.
     *
     * @param page
     * @return All pages that represent the same concept, INCLUDING the page passed as an argument.
     */
    public List<LocalPage> getInOtherLanguages(LocalPage page) {
        try {
            int conceptId = upDao.getUnivPageId(page, CONCEPT_ALGORITHM_ID);
            List<LocalPage> results = new ArrayList<LocalPage>();
            UniversalPage up = upDao.getById(conceptId, CONCEPT_ALGORITHM_ID);
            if (up == null) {
                return results;
            }
            for (LocalId lid : up.getLocalEntities()) {
                if (!lid.equals(page.toLocalId())) {
                    LocalPage lp = lpDao.getById(lid.getLanguage(), lid.getId());
                    if (lp != null) {
                        results.add(lp);
                    }
                }
            }
            results.add(page);
            return results;
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the wiki markup of a page with some text.
     *
     * @param page
     * @return
     */
    public String getPageText(LocalPage page) {
        try {
            return rpDao.getById(page.getLanguage(), page.getLocalId()).getBody();
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Load a set of languages into the h2 database.
     * THIS MUST BE CALLED BEFORE AN INSTANCE OF WIKIPADIA WRAPPER IS CREATED!
     * @param langCodes comma separated list of langcodes - ie "simple,la"
     */
    public static void loadLanguages(String langCodes) throws IOException, InterruptedException, ClassNotFoundException, ConfigurationException {
        PipelineLoader.main(new String[]{"-l", langCodes});
    }
}
