package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ui.ContentItemPage;

/**
 * <p>
 * This interface is implemented by "meta" content items which need to 
 * customise their preview link. An example is a content item which serves
 * as an access point to an application. 
 * </p>
 * <p>
 * An implementation may return {@code null}. In this case 
 * {@link ContentItemPage} will use the default preview URL.
 * </p>
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface CustomizedPreviewLink {
    
    String getPreviewUrl(PageState state);
    
}
