/*
 * Add your license here, for example LGPL
 */
package org.librecms.contenttypes.faqitem;

import org.hibernate.envers.Audited;
import org.librecms.contentsection.ContentItem;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.librecms.contenttypes.faqitem.FAQitemConstants.*;

@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class FAQitem extends ContentItem implements Serializable {

}
