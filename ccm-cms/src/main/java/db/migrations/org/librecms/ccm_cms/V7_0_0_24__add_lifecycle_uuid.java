package db.migrations.org.librecms.ccm_cms;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class V7_0_0_24__add_lifecycle_uuid extends BaseJavaMigration {

    @Override
    public void migrate(final Context context) throws Exception {

        final Connection connection = context.getConnection();

        final List<Long> lifecycleIds = retrieveLifecycleIds(connection);
        final List<Long> definitionIds = retrieveDefinitionIds(
            connection
        );

        addLifecycleUuidCol(connection);
        addDefinitionLifecycleCol(connection);

        setLifecycleUuid(connection, lifecycleIds);
        setDefinitionUuid(connection, definitionIds);

        addLifecycleUniqueConstraint(connection);
        addDefinitionUniqueConstraint(connection);

    }

    private List<Long> retrieveLifecycleIds(final Connection connection)
        throws Exception {
        final List<Long> result = new ArrayList<>();
        try (PreparedStatement stmt = connection
            .prepareStatement("select LIFECYCLE_ID from LIFECYCLES");
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                result.add(resultSet.getLong("LIFECYCLE_ID"));
            }

        }

        return result;
    }

    private List<Long> retrieveDefinitionIds(final Connection connection)
        throws Exception {
        final List<Long> result = new ArrayList<>();
        try (PreparedStatement stmt = connection
            .prepareStatement(
                "select LIFECYCLE_DEFINITION_ID "
                    + "from LIFECYLE_DEFINITIONS"
            );
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                result.add(resultSet.getLong("LIFECYCLE_ID"));
            }

        }

        return result;
    }

    private void addLifecycleUuidCol(final Connection connection)
        throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement(
            "alter table LIFECYCLES add column UUID varchar(255)"
        )) {
            stmt.execute();
        }
    }

    private void addDefinitionLifecycleCol(final Connection connection)
        throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement(
            "alter table lifecyle_definitions "
                + "add column UUID varchar(255)"
        )) {
            stmt.execute();
        }
    }

    private void setLifecycleUuid(
        final Connection connection, final List<Long> lifecycleIds
    ) throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement(
            "update LIFECYCLES set UUID = ? where LIFECYCLE_ID = ?"
        )) {
            for (final Long lifecycleId : lifecycleIds) {
                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setLong(2, lifecycleId);
                stmt.executeUpdate();
                stmt.clearParameters();
            }
        }
    }

    private void setDefinitionUuid(
        final Connection connection, final List<Long> definitionIds
    ) throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement(
            "update LIFECYLE_DEFINITIONS set UUID = ? "
                + "where LIFECYCLE_DEFINITION_ID = ?"
        )) {
            for (final Long lifecycleId : definitionIds) {
                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setLong(2, lifecycleId);
                stmt.executeUpdate();
                stmt.clearParameters();
            }
        }

    }

    private void addLifecycleUniqueConstraint(
        final Connection connection
    ) throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement(
            "alter table LIFECYCLES "
                + "add constraint UK_40o4njo54m8c4xlwq6ctnsimd "
                + "unique (UUID)"
        )) {
            stmt.execute();
        }
    }

    private void addDefinitionUniqueConstraint(
        final Connection connection
    ) throws Exception {
try (PreparedStatement stmt = connection.prepareStatement(
            "alter table LIFECYLE_DEFINITIONS "
                + "add constraint UK_n6ki3s5im2k2nccpocuctqqe3 "
                + "unique (UUID)"
        )) {
            stmt.execute();
        }
    }

}
