/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;

/**
 * A fieldset for form.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class Fieldset extends SimpleContainer {

    GlobalizedMessage m_title;

    public Fieldset(GlobalizedMessage title) {
        super("bebop:fieldset", BEBOP_XML_NS);
        m_title = title;
    }

    @Override
    public void generateXML(PageState state, Element p) {
        if (isVisible(state)) {
            Element parent = generateParent(p);
            parent.addAttribute("legend", (String) m_title.localize());
            generateChildrenXML(state, parent);
        }
    }

}
