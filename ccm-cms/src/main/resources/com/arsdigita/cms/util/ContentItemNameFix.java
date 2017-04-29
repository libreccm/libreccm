/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.util;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.contenttypes.GenericArticle;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.cmd.Program;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jens Pelzetter
 */
@SuppressWarnings("PMD.SystemPrintln")
public class ContentItemNameFix extends Program {

    private boolean pretend = false;

    public ContentItemNameFix() {
        super("ContentItemNameFix", "1.0.0", "");

        getOptions().addOption(
            OptionBuilder
            .hasArg(false)
            .withLongOpt("pretend")
            .withDescription("Only show what would be done")
            .create("p"));
    }

    public static final void main(final String[] args) {
        new ContentItemNameFix().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {

        System.out.printf("Running ContentItemNameFix...\n");

        pretend = cmdLine.hasOption("p");

        if (pretend) {
            System.out.printf("Pretend option is on, only showing what would be done...\n\n");
        } else {
            System.out.print("\n");
        }

        new KernelExcursion() {

            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                final Set<LinkToCheck> linksToCheck = new HashSet<LinkToCheck>();

                final Session session = SessionManager.getSession();
                final TransactionContext transactionContext = session.getTransactionContext();

                transactionContext.beginTxn();

                final DataCollection draftFolders = session.retrieve(Folder.BASE_DATA_OBJECT_TYPE);
                draftFolders.addEqualsFilter(ContentItem.VERSION, "draft");

                while (draftFolders.next()) {
                    checkFolder(draftFolders.getDataObject(), linksToCheck);
                }

                final DataCollection draftBundles = session.retrieve(
                    ContentBundle.BASE_DATA_OBJECT_TYPE);
                draftBundles.addEqualsFilter(ContentItem.VERSION, "draft");

                while (draftBundles.next()) {
                    checkBundle(draftBundles.getDataObject(), linksToCheck);

                }

                transactionContext.commitTxn();

                System.out.println("-------------------------------------------------------------");

                System.out.println("Checking for potentially brocken links...");
                System.out.println("GenericArticle (ccm-cms-types-article, ccm-cms-types-news, ...");
                System.out.println("");

                final DataCollection articles = session.retrieve(
                    GenericArticle.BASE_DATA_OBJECT_TYPE);
                articles.addEqualsFilter(ContentItem.VERSION, "draft");
                while (articles.next()) {
                    checkArticle(articles.getDataObject(), linksToCheck);
                }

                System.out.println("");
                System.out.println("MultiPartArticles...");
                System.out.println("");

                final DataCollection mpArticles = session.retrieve(
                    "com.arsdigita.cms.contenttypes.MultiPartArticle");
                mpArticles.addEqualsFilter(ContentItem.VERSION, "draft");
                while (mpArticles.next()) {
                    checkMpArticle(mpArticles.getDataObject(), linksToCheck);
                }

            }

        }.run();

    }

    private void checkFolder(final DataObject folderObj, final Set<LinkToCheck> linksToCheck) {

        final Folder draftFolder = new Folder(folderObj);
        final Folder liveFolder = (Folder) draftFolder.getLiveVersion();

        if (liveFolder != null && !draftFolder.getName().equals(liveFolder.getName())) {
            System.out.printf("Problems with folder %s:/%s (id: %s):\n",
                              draftFolder.getContentSection().getName(),
                              draftFolder.getPath(),
                              draftFolder.getID().toString());
            System.out.printf("\t Live Folder has wrong name: Is '%s' but should be '%s'.",
                              liveFolder.getName(),
                              draftFolder.getName());

            linksToCheck.add(new LinkToCheck(liveFolder.getName(),
                                             draftFolder.getName(),
                                             String.format("%s:/%s",
                                                           liveFolder.getContentSection().getName(),
                                                           liveFolder.getPath()),
                                             String.format("%s:/%s",
                                                           draftFolder.getContentSection().getName(),
                                                           draftFolder.getPath())));

            if (pretend) {
                System.out.print("\n\n");
            } else {

                liveFolder.setName(draftFolder.getName());
                System.out.print(" Corrected.\n\n");
            }

        }
    }

    private void checkBundle(final DataObject bundleObj, final Set<LinkToCheck> linksToCheck) {

        final ContentBundle draftBundle = new ContentBundle(bundleObj);
        final ContentItem primaryDraftItem = draftBundle.getPrimaryInstance();

        final String itemId = primaryDraftItem.getID().toString();
        final String itemPath = String.format("%s:/%s",
                                              primaryDraftItem.getContentSection().getName(),
                                              primaryDraftItem.getPath());

        final HeaderStatus headerStatus = new HeaderStatus();

        //This is our reference, all bundles, instances etc belonging to the item sould have this 
        //name
        final String itemName = primaryDraftItem.getName();

        if (!draftBundle.getName().equals(itemName)) {
            printItemHeaderLine(itemId, itemPath, headerStatus);

            System.out.printf(
                "\t Draft ContentBundle has wrong name: Is '%s' but should be '%s'.",
                itemName,
                draftBundle.getName());

            linksToCheck.add(new LinkToCheck(draftBundle.getName(),
                                             itemName,
                                             String.format("%s:/%s",
                                                           draftBundle.getContentSection().getName(),
                                                           draftBundle.getPath()),
                                             itemPath));

            if (pretend) {
                System.out.print("\n");
            } else {
                draftBundle.setName(itemName);
                System.out.printf(" Corrected.\n");
            }
        }

        checkInstances(draftBundle, itemName, itemId, itemPath, headerStatus, linksToCheck);

        final ContentBundle liveBundle = (ContentBundle) draftBundle.getLiveVersion();
        if (liveBundle != null) {
            if (!liveBundle.getName().equals(itemName)) {
                printItemHeaderLine(itemId, itemPath, headerStatus);

                System.out.printf(
                    "\tLive ContentBundle has wrong name. Should be '%s' but is '%s'",
                    itemName,
                    liveBundle.getName());

                linksToCheck.add(new LinkToCheck(liveBundle.getName(),
                                                 itemName,
                                                 String.format("%s:/%s",
                                                               liveBundle.getContentSection()
                                                               .getName(),
                                                               liveBundle.getPath()),
                                                 itemPath));

                if (pretend) {
                    System.out.print("\n");
                } else {
                    liveBundle.setName(itemName);
                    System.out.printf(" Corrected.\n");
                }
            }

            checkInstances(liveBundle, itemName, itemId, itemPath, headerStatus, linksToCheck);
        }

        if (headerStatus.isHeaderPrinted()) {
            System.out.print("\n");
        }

    }

    private void checkInstances(final ContentBundle draftBundle,
                                final String itemName,
                                final String itemId,
                                final String itemPath,
                                final HeaderStatus headerStatus,
                                final Set<LinkToCheck> linksToCheck) {
        final ItemCollection instances = draftBundle.getInstances();
        ContentItem current;
        while (instances.next()) {
            current = instances.getContentItem();

            if (!itemName.equals(current.getName())) {
                printItemHeaderLine(itemId, itemPath, headerStatus);
                System.out.printf(
                    "\t%s instance %s (language: %s has wrong name. Should be '%s', but is '%s'.",
                    current.getVersion(),
                    current.getID().toString(),
                    current.getLanguage(),
                    itemName,
                    current.getName());

                linksToCheck.add(new LinkToCheck(current.getName(),
                                                 itemName,
                                                 String.format("%s:/%s",
                                                               current.getContentSection().getName(),
                                                               current.getPath()),
                                                 itemPath));

                if (pretend) {
                    System.out.print("\n");
                } else {
                    current.setName(itemName);
                    System.out.printf(" Corrected.\n");
                }
            }
        }

    }

    private class HeaderStatus {

        private boolean headerPrinted = false;

        public HeaderStatus() {
            //Nothing
        }

        public boolean isHeaderPrinted() {
            return headerPrinted;
        }

        public void setHeaderPrinted(final boolean headerPrinted) {
            this.headerPrinted = headerPrinted;
        }

    }

    private void printItemHeaderLine(final String itemId,
                                     final String itemPath,
                                     final HeaderStatus headerStatus) {
        if (!headerStatus.isHeaderPrinted()) {
            System.out.printf("Problems with item %s (id: %s):\n", itemPath, itemId);
            headerStatus.setHeaderPrinted(true);
        }
    }

    private class LinkToCheck {

        private String wrongName;
        private String correctName;
        private String wrongPath;
        private String correctPath;

        public LinkToCheck() {
            //Nothing
        }

        public LinkToCheck(final String wrongName,
                           final String correctName,
                           final String wrongPath,
                           final String correctPath) {
            this.wrongName = wrongName;
            this.correctName = correctName;
            this.wrongPath = wrongPath;
            this.correctPath = correctPath;
        }

        public String getWrongName() {
            return wrongName;
        }

        public void setWrongName(final String wrongName) {
            this.wrongName = wrongName;
        }

        public String getCorrectName() {
            return correctName;
        }

        public void setCorrectName(final String correctName) {
            this.correctName = correctName;
        }

        public String getWrongPath() {
            return wrongPath;
        }

        public void setWrongPath(final String wrongPath) {
            this.wrongPath = wrongPath;
        }

        public String getCorrectPath() {
            return correctPath;
        }

        public void setCorrectPath(final String correctPath) {
            this.correctPath = correctPath;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            if (wrongName == null) {
                hash = 47 * hash;
            } else {
                hash = 47 * hash + wrongName.hashCode();
            }

            if (correctName == null) {
                hash = 47 * hash;
            } else {
                hash = 47 * hash + correctName.hashCode();
            }

            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final LinkToCheck other = (LinkToCheck) obj;
            if (wrongName == null && other.getWrongName() != null) {
                return false;
            }

            if (wrongName != null && other.getWrongName() == null) {
                return false;
            }

            if (correctName == null && other.getCorrectName() != null) {
                return false;
            }

            if (correctName != null && other.getCorrectName() == null) {
                return false;
            }

            return ((correctName.equals(other.getCorrectName()))
                    && (wrongName.equals(other.getWrongName())));
        }

    }

    private void checkArticle(final DataObject articleObj,
                              final Set<LinkToCheck> linksToCheck) {
        final GenericArticle article = new GenericArticle(articleObj);

        final TextAsset textAsset = article.getTextAsset();

        if (textAsset == null) {
            return;
        }

        final String text = textAsset.getText();

        if (text == null) {
            return;
        }

        for (LinkToCheck linkToCheck : linksToCheck) {
            //if (text.contains(linkToCheck.getWrongName())) {
            
            /*if (text.matches(String.format("^(.*)href=\"(.*)%s(.*)\"(.*)$"
                                           linkToCheck.getWrongName()))) {*/
            if (checkForPotentialBrockenLink(text, linkToCheck.getWrongName())) {
                System.out.printf("Found a potenially brocken link in article item %s:/%s:\n",
                                  article.getContentSection().getName(),
                                  article.getPath());
                System.out.printf("\tLook for a link containing to path '%s' and replace it with "
                                      + "the stable link to the target item %s.\n\n",
                                  linkToCheck.getWrongPath(),
                                  linkToCheck.getCorrectPath());
            }
        }
    }

    private void checkMpArticle(final DataObject mpArticleObj,
                                final Set<LinkToCheck> linksToCheck) {
        final ContentItem mpItem = new ContentItem(mpArticleObj);
        final DataCollection sections = (DataCollection) mpArticleObj.get("sections");

        while (sections.next()) {
            checkMpSection(mpItem, sections.getDataObject(), linksToCheck);
        }
    }

    private void checkMpSection(final ContentItem mpItem,
                                final DataObject sectionObj,
                                final Set<LinkToCheck> linksToCheck) {
        final DataObject textAssetObj = (DataObject) sectionObj.get("text");

        if (textAssetObj == null) {
            return;
        }

        final String text = (String) textAssetObj.get(TextAsset.CONTENT);

        if (text == null) {
            return;
        }

        for (LinkToCheck linkToCheck : linksToCheck) {
            //if (text.contains(linkToCheck.getWrongName())) {
            /*if (text.matches(String.format("^(.*)href=\"(.*)%s(.*)\"(.*)$",
                                           linkToCheck.getWrongName()))) {*/
            if(checkForPotentialBrockenLink(text, linkToCheck.getWrongName())) {
                System.out.printf("Found a potenially brocken link in section '%s' of "
                                      + "MultiPartArticle %s:/%s.\n",
                                  (String) sectionObj.get("title"),
                                  mpItem.getContentSection().getName(),
                                  mpItem.getPath());
                System.out.printf("\tLook for a link containing to path '%s' and replace it with "
                                      + "the stable link to the target item %s.\n\n",
                                  linkToCheck.getWrongPath(),
                                  linkToCheck.getCorrectPath());
            }
        }
    }

    /**
     * Returns {@code true} if a match for {@code checkFor} is found in the links of {@code text}.
     * @param text
     * @param checkFor
     * @return 
     */
    private boolean checkForPotentialBrockenLink(final String text, final String checkFor) {
        final Document document = Jsoup.parseBodyFragment(text);
        
        final Elements links = document.select("a");
        boolean result = false;
        for(Element link : links) {
             result = (link.attr("href").contains(checkFor));
        }
        
        return result;
        
    }
}
