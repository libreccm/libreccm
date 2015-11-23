/*
 * Add your license here, for example LGPL
 */
package org.librecms.contenttypes.faqitem;

import static org.librecms.contenttypes.faqitem.FAQitemConstants.*;

import org.hibernate.envers.Audited;

import org.libreccm.cms.contentsection.ContentItem;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class FAQitem extends ContentItem implements Serializable {

}
