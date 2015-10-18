/*
 * Add your license here, for example LGPL
 */
package ${package};

import static ${package}.${typeName}Constants.*;

import org.hibernate.envers.Audited;

import org.libreccm.cms.contentsection.ContentItem;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class ${typeName} extends ContentItem implements Serializable {

}
