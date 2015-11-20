/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contenttypes.decisiontree;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.libreccm.l10n.LocalizedString;

/**
 *
 * @author koalamann
 */
@Embeddable
public class DecisiontreeOptionTarget implements Serializable {

    private static final long serialVersionUID = 1L;
      
    @Column(name="TARGETURL")
    private LocalizedString targetURL;


}
