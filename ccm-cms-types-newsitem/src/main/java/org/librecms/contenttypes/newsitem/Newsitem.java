/*
 * Add your license here, for example LGPL
 */
package org.librecms.contenttypes.newsitem;

import static org.librecms.contenttypes.newsitem.NewsitemConstants.*;

import org.hibernate.envers.Audited;

import org.librecms.contentsection.ContentItem;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class Newsitem extends ContentItem implements Serializable {

}
