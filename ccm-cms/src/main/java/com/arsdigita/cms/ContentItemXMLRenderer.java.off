/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms;

import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectTraversalAdapter;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

/**
 * This is a special ContentItemXMLRenderer for CMS to get a more transparent
 * way to handle ContentBundles during XML output.
 *
 *  The problem was to change RelatedLinks and therefore Link to always link to
 *  the corresponding ContentBundle instead of the content item. To get the
 *  corresponding content item during XML generation, I have to test for
 *  ContentBundle and negotiate the language version.
 *  This is not possible in com.arsdigita.ccm
 *
 * @author quasi
 */
public class ContentItemXMLRenderer extends DomainObjectXMLRenderer {

    private static final Logger logger =
                                Logger.getLogger(ContentItemXMLRenderer.class);
    private String m_propertyName = "";
    private String m_keyName = "";
    private String m_relationAttribute = "";

    public ContentItemXMLRenderer(final Element root) {
        super(root);
    }

    // This method will be called by DomainObjectTraversal.walk()
    // It's purpose is to test for ContentBundle objects and if found, replace
    // that object with the negotiated version of the content item.
    // Otherwise this methd will do nothing.
    @Override
    protected void walk(final DomainObjectTraversalAdapter adapter,
                        final DomainObject obj,
                        final String path,
                        final String context,
                        final DomainObject linkObject) {
        //final long start = System.nanoTime();

        DomainObject nObj = obj;

        if (nObj instanceof ContentBundle) {

            nObj = ((ContentBundle) obj).getInstance(GlobalizationHelper.getNegotiatedLocale(), true);
        }

        super.walk(adapter, nObj, path, context, linkObject);

        //System.out.printf("Walked object in %d ms\n", (System.nanoTime() - start) / 1000000);
    }

    @Override
    protected void handleAttribute(final DomainObject obj, final String path, final Property property) {
        final String propertyName = property.getName();

        // Special handling for the isoCountryCode field in GenericAddress
        if (obj instanceof GenericAddress && "isoCountryCode".equals(propertyName)) {
            //if (propertyName.equals("isoCountryCode")) {            
            super.handleAttribute(obj, path, property);

            final Element element = newElement(m_element, "country");
            element.setText(GenericAddress.getCountryNameFromIsoCode(((GenericAddress) obj).getIsoCountryCode()));
            return;

        }

        // Special handling for the relation attribute keys
        if (!m_relationAttribute.isEmpty()) {
            String key = "";

            // The RelationAttribute is part of this domain object as field
            if (obj instanceof RelationAttributeInterface
                && ((RelationAttributeInterface) obj).
                    hasRelationAttributeProperty(propertyName)) {

                final RelationAttributeInterface relationAttributeObject = (RelationAttributeInterface) obj;
                key = relationAttributeObject.getRelationAttributeKey(
                        propertyName);

            }

            // This RelationAttribute is part of an n:m association as link attribute
            if (obj instanceof LinkDomainObject
                && propertyName.equals(m_keyName)) {
                key = (String) ((LinkDomainObject) obj).get(m_keyName);
            }

            // Replace value of the property defined in RELATION_ATTRIBUTES string
            // of the primary domain object with the localized String.
            if (!key.isEmpty()) {
//                logger.debug(String.format(
//                        "Getting relation attribute value for key '%s' of relation attribute '%s'",
//                        key, m_relationAttribute));
                final RelationAttributeCollection relationAttributeCollection = new RelationAttributeCollection(
                        m_relationAttribute, key);
                relationAttributeCollection.addLanguageFilter(GlobalizationHelper.
                        getNegotiatedLocale().getLanguage());
                if (!relationAttributeCollection.isEmpty()) {
                    relationAttributeCollection.next();
                    final Element element = newElement(m_element, m_keyName);
                    element.setText(relationAttributeCollection.getName());
                    final Element elementId = newElement(m_element, m_keyName + "Id");
                    elementId.setText(relationAttributeCollection.getKey());
                    relationAttributeCollection.close();
                }
                return;
            }
        }

        super.handleAttribute(obj, path, property);
    }

    @Override
    protected void beginAssociation(final DomainObject obj, final String path, final Property property) {
        super.beginAssociation(obj, path, property);

        final String propertyName = property.getName();

        if (obj instanceof RelationAttributeInterface
            && ((RelationAttributeInterface) obj).hasRelationAttributeProperty(
                propertyName)) {

            final RelationAttributeInterface relationAttributeObject = (RelationAttributeInterface) obj;

            m_propertyName = propertyName;
            m_keyName = relationAttributeObject.getRelationAttributeKeyName(propertyName);
            m_relationAttribute = relationAttributeObject.getRelationAttributeName(propertyName);

        }
    }

    @Override
    protected void endAssociation(final DomainObject obj, final String path, final Property property) {

        m_propertyName = "";
        m_keyName = "";
        m_relationAttribute = "";

        super.endAssociation(obj, path, property);
    }

}
