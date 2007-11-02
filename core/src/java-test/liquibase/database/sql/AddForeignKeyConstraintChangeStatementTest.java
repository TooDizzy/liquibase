package liquibase.database.sql;

import liquibase.database.Database;
import liquibase.database.structure.DatabaseSnapshot;
import liquibase.database.structure.ForeignKey;
import liquibase.test.DatabaseTestTemplate;
import liquibase.test.SqlStatementDatabaseTest;
import liquibase.test.TestContext;
import liquibase.exception.JDBCException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AddForeignKeyConstraintChangeStatementTest extends AbstractSqlStatementTest {

    private static final String FK_NAME = "FK_ADDTEST";

    private static final String BASE_TABLE_NAME = "AddFKTest";
    private static final String REF_TABLE_NAME = "AddFKTestRef";
    private static final String BASE_COLUMN_NAME = "NewCol";
    private static final String REF_COL_NAME = "id";

    protected void setupDatabase(Database database) throws Exception {
            dropAndCreateTable(new CreateTableStatement(BASE_TABLE_NAME)
                    .addPrimaryKeyColumn("id", "int")
                    .addColumn(BASE_COLUMN_NAME, "int"), database);

            dropAndCreateTable(new CreateTableStatement(REF_TABLE_NAME)
                    .addPrimaryKeyColumn(REF_COL_NAME, "int")
                    .addColumn("existingCol", "int"), database);

            dropAndCreateTable(new CreateTableStatement(TestContext.ALT_SCHEMA, BASE_TABLE_NAME)
                    .addPrimaryKeyColumn("id", "int")
                    .addColumn(BASE_COLUMN_NAME, "int"), database);

            dropAndCreateTable(new CreateTableStatement(TestContext.ALT_SCHEMA, REF_TABLE_NAME)
                    .addPrimaryKeyColumn(REF_COL_NAME, "int")
                    .addColumn("existingCol", "int"), database);
    }

    protected AddForeignKeyConstraintChangeStatement generateTestStatement() {
        return new AddForeignKeyConstraintChangeStatement(null, null, null, null, null, null, null);
    }

    @Test
    public void execute() throws Exception {
        new DatabaseTestTemplate().testOnAvailableDatabases(
                new SqlStatementDatabaseTest(null, new AddForeignKeyConstraintChangeStatement(FK_NAME,
                        null, BASE_TABLE_NAME, BASE_COLUMN_NAME,
                        null, REF_TABLE_NAME, REF_COL_NAME)) {
                    protected void preExecuteAssert(DatabaseSnapshot snapshot) {
                        assertNull(snapshot.getForeignKey(FK_NAME));
                    }

                    protected void postExecuteAssert(DatabaseSnapshot snapshot) {
                        ForeignKey fkSnapshot = snapshot.getForeignKey(FK_NAME);
                        assertNotNull(fkSnapshot);
                        assertEquals(BASE_TABLE_NAME.toUpperCase(), fkSnapshot.getForeignKeyTable().getName().toUpperCase());
                        assertEquals(BASE_COLUMN_NAME.toUpperCase(), fkSnapshot.getForeignKeyColumn().toUpperCase());
                        assertEquals(REF_TABLE_NAME.toUpperCase(), fkSnapshot.getPrimaryKeyTable().getName().toUpperCase());
                        assertEquals(REF_COL_NAME.toUpperCase(), fkSnapshot.getPrimaryKeyColumn().toUpperCase());
                        assertFalse(fkSnapshot.isDeferrable());
                        assertFalse(fkSnapshot.isInitiallyDeferred());
                    }

                });
    }

    @Test
    public void execute_deferrable() throws Exception {
        new DatabaseTestTemplate().testOnAvailableDatabases(
                new SqlStatementDatabaseTest(null, new AddForeignKeyConstraintChangeStatement(FK_NAME,
                        null, BASE_TABLE_NAME, BASE_COLUMN_NAME,
                        null, REF_TABLE_NAME, REF_COL_NAME)
                        .setDeferrable(true)
                        .setInitiallyDeferred(true)) {
                    protected boolean expectedException(Database database, JDBCException exception) {
                        return !database.supportsInitiallyDeferrableColumns();
                    }

                    protected void preExecuteAssert(DatabaseSnapshot snapshot) {
                        assertNull(snapshot.getForeignKey(FK_NAME));
                    }

                    protected void postExecuteAssert(DatabaseSnapshot snapshot) {
                        ForeignKey fkSnapshot = snapshot.getForeignKey(FK_NAME);
                        assertNotNull(fkSnapshot);
                        assertEquals(BASE_TABLE_NAME.toUpperCase(), fkSnapshot.getForeignKeyTable().getName().toUpperCase());
                        assertEquals(BASE_COLUMN_NAME.toUpperCase(), fkSnapshot.getForeignKeyColumn().toUpperCase());
                        assertEquals(REF_TABLE_NAME.toUpperCase(), fkSnapshot.getPrimaryKeyTable().getName().toUpperCase());
                        assertEquals(REF_COL_NAME.toUpperCase(), fkSnapshot.getPrimaryKeyColumn().toUpperCase());
                        assertTrue(fkSnapshot.isDeferrable());
                        assertTrue(fkSnapshot.isInitiallyDeferred());
                    }
                });
    }

    @Test
    public void execute_deleteCascade() throws Exception {
        new DatabaseTestTemplate().testOnAvailableDatabases(new SqlStatementDatabaseTest(null, new AddForeignKeyConstraintChangeStatement(FK_NAME,
                null, BASE_TABLE_NAME, BASE_COLUMN_NAME,
                null, REF_TABLE_NAME, REF_COL_NAME).setDeleteCascade(true)) {
            protected void preExecuteAssert(DatabaseSnapshot snapshot) {
                assertNull(snapshot.getForeignKey(FK_NAME));
            }

            protected void postExecuteAssert(DatabaseSnapshot snapshot) {
                ForeignKey fkSnapshot = snapshot.getForeignKey(FK_NAME);
                assertNotNull(fkSnapshot);
                assertEquals(BASE_TABLE_NAME.toUpperCase(), fkSnapshot.getForeignKeyTable().getName().toUpperCase());
                assertEquals(BASE_COLUMN_NAME.toUpperCase(), fkSnapshot.getForeignKeyColumn().toUpperCase());
                assertEquals(REF_TABLE_NAME.toUpperCase(), fkSnapshot.getPrimaryKeyTable().getName().toUpperCase());
                assertEquals(REF_COL_NAME.toUpperCase(), fkSnapshot.getPrimaryKeyColumn().toUpperCase());
                assertFalse(fkSnapshot.isDeferrable());
                assertFalse(fkSnapshot.isInitiallyDeferred());
            }

        });
    }

    @Test
    public void execute_altSchema() throws Exception {
        new DatabaseTestTemplate().testOnAvailableDatabases(
                new SqlStatementDatabaseTest(TestContext.ALT_SCHEMA, new AddForeignKeyConstraintChangeStatement(FK_NAME,
                        TestContext.ALT_SCHEMA, BASE_TABLE_NAME, BASE_COLUMN_NAME,
                        TestContext.ALT_SCHEMA, REF_TABLE_NAME, REF_COL_NAME)) {
                    protected void preExecuteAssert(DatabaseSnapshot snapshot) {
                        assertNull(snapshot.getForeignKey(FK_NAME));
                    }

                    protected void postExecuteAssert(DatabaseSnapshot snapshot) {
                        ForeignKey fkSnapshot = snapshot.getForeignKey(FK_NAME);
                        assertNotNull(fkSnapshot);
                        assertEquals(BASE_TABLE_NAME.toUpperCase(), fkSnapshot.getForeignKeyTable().getName().toUpperCase());
                        assertEquals(BASE_COLUMN_NAME.toUpperCase(), fkSnapshot.getForeignKeyColumn().toUpperCase());
                        assertEquals(REF_TABLE_NAME.toUpperCase(), fkSnapshot.getPrimaryKeyTable().getName().toUpperCase());
                        assertEquals(REF_COL_NAME.toUpperCase(), fkSnapshot.getPrimaryKeyColumn().toUpperCase());
                        assertFalse(fkSnapshot.isDeferrable());
                        assertFalse(fkSnapshot.isInitiallyDeferred());
                    }

                });
    }
}
