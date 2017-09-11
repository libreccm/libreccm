package com.arsdigita.london.terms.ui;


import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;

import org.libreccm.categorization.Category;

import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Generate part of the category tree. Used by Assign Category authoring step.
 *
 * Class is directly used by JSP page(s), eg. load-cat.jsp (currently in
 * ~/packages/content-section/www/admin, source in ccm-ldn-aplaws or
 * corresponding integration module).
 *
 * @author Alan Pevec
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorySubtree extends SimpleComponent {

    private static final Logger LOGGER = LogManager
        .getLogger(CategorySubtree.class);

    private final StringParameter selectedCatsParam = new StringParameter(
        "selectedCats");
    private final StringParameter nodeIdParam = new StringParameter("nodeID");

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addGlobalStateParam(nodeIdParam);
        page.addGlobalStateParam(selectedCatsParam);
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {

        final String node = (String) state.getValue(nodeIdParam);
        final Set<Long> ids = new HashSet<>();

        if (((String) state.getValue(selectedCatsParam)) != null) {
            StringTokenizer values = new StringTokenizer((String) state
                .getValue(selectedCatsParam), ",");
            while (values.hasMoreTokens()) {
                ids.add(Long.parseLong(values.nextToken().trim()));
            }
        }

        LOGGER.debug("selected node = {}", node);
        final String[] pathElements = StringUtils.split(node, "-");

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryRepository categoryRepo = cdiUtil
            .findBean(CategoryRepository.class);

        final Category root = categoryRepo
            .findById(Long.parseLong(pathElements[pathElements.length - 1]))
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("No Category with ID %s in the database.",
                    pathElements[pathElements.length - 1])));

        LOGGER.debug("generating subtree for cat {}...", root.getObjectId());
//        TermWidget.generateSubtree(parent, root, ids);
        generateSubtreeXml(parent, root, ids);
    }

    private void generateSubtreeXml(final Element parentElem,
                                    final Category root,
                                    final Set<Long> ids) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategorySubtreeController controller = cdiUtil
            .findBean(CategorySubtreeController.class);

        final List<Category> subCategories = controller.getSubCategories(root);

        final Element rootCategoryElem = generateCategoryXml(parentElem,
                                                             root,
                                                             ids);

        controller
            .getSubCategories(root)
            .stream()
            .sorted((category1, category2) -> {
                return category1.getName().compareTo(category2.getName());
            })
            .forEach(subCategory -> generateCategoryXml(rootCategoryElem,
                                                        root,
                                                        ids));
    }

    private Element generateCategoryXml(final Element parentElem,
                                        final Category category,
                                        final Set<Long> ids) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final GlobalizationHelper globalizationHelper = cdiUtil
            .findBean(GlobalizationHelper.class);

        final Element element = parentElem.newChildElement("cms:category",
                                                           CMS.CMS_XML_NS);
        element.addAttribute("id", Long.toString(category.getObjectId()));
        element.addAttribute("name", category.getName());
        final String desc = globalizationHelper
            .getValueFromLocalizedString(category.getDescription());
        element.addAttribute("description", desc);
        if (ids.contains(category.getObjectId())) {
            element.addAttribute("isSelected", "1");
        } else {
            element.addAttribute("isSelected", "0");
        }
        if (category.isAbstractCategory()) {
            element.addAttribute("isAbstract", "1");
        } else {
            element.addAttribute("isAbstract", "0");
        }
        if (category.isEnabled()) {
            element.addAttribute("isEnabled", "1");
        } else {
            element.addAttribute("isEnabled", "0");
        }

        final StringBuilder path = new StringBuilder(parentElem
            .getAttribute("fullname"));
        if (path.length() > 0) {
            path.append(" > ");
        }
        path.append(category.getName());
        element.addAttribute("fullname", path.toString());

        final StringBuilder nodeId = new StringBuilder(parentElem
            .getAttribute("node-id"));
        if (nodeId.length() > 0) {
            nodeId.append("-");
        }
        nodeId.append(category.getObjectId());
        element.addAttribute("node-id", nodeId.toString());
        
        return element;
    }

}
