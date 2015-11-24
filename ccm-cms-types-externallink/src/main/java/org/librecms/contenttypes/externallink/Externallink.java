/*
 * Add your license here, for example LGPL
 */
package org.librecms.contenttypes.externallink;

import static org.librecms.contenttypes.externallink.ExternallinkConstants.*;

import org.hibernate.envers.Audited;

import org.librecms.contentsection.ContentItem;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class Externallink extends ContentItem implements Serializable {

}
