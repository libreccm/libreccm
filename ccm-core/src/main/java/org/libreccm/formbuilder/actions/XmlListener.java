/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.formbuilder.actions;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.libreccm.formbuilder.ProcessListener;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_xml_listeners")
public class XmlListener extends ProcessListener implements Serializable {

    private static final long serialVersionUID = -8674849210363260180L;
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof XmlListener;
    }

}
