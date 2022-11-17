package de.fhg.iais.roberta.main;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import de.fhg.iais.roberta.factory.RobotFactory;
import de.fhg.iais.roberta.persistence.bo.Configuration;
import de.fhg.iais.roberta.persistence.bo.ConfigurationData;
import de.fhg.iais.roberta.persistence.bo.Program;
import de.fhg.iais.roberta.persistence.bo.Robot;
import de.fhg.iais.roberta.persistence.bo.User;
import de.fhg.iais.roberta.persistence.dao.ConfigurationDao;
import de.fhg.iais.roberta.persistence.dao.ProgramDao;
import de.fhg.iais.roberta.persistence.dao.RobotDao;
import de.fhg.iais.roberta.persistence.dao.UserDao;
import de.fhg.iais.roberta.persistence.util.DbSession;
import de.fhg.iais.roberta.persistence.util.SessionFactoryWrapper;
import de.fhg.iais.roberta.util.Util;
import de.fhg.iais.roberta.util.UtilForXmlTransformation;
import de.fhg.iais.roberta.util.XsltTransformer;
import de.fhg.iais.roberta.util.basic.Pair;
import de.fhg.iais.roberta.util.dbc.DbcException;

/**
 * <b>transform old xml to new (for mbed-plugins)</b><br>
 * <br>
 * We need access to a hsqldb target server, which -for testing- can be started by<br>
 * <code>java -Xmx8G -cp [[path-to-hsqldb-2.4.0.jar]] org.hsqldb.Server --database.0 file:[[db-directory]]/openroberta-db --dbname.0 openroberta-db &</code><br>
 * This server will later SELECT data and UPDATE rows after modifications.<br>
 * BE CAREFUL <i>NOT</i> TO SHUTDOWN A DATABASE IN USE (e.g. by the public OpenRobertaLab server!)<br>
 * Do not forget to inspect the changes in the database by running a GUI-tool (e.g. by using <code>./admin.sh ... sql-gui</code>)
 *
 * @author rbudde
 */
public class DatabaseEnhancement {
    private static final String USAGE = "usage: java -cp OpenRobertaServer/target/resources/\\* de.fhg.iais.roberta.main.DatabaseEnhancement DBCONNECTION ROBOT_ID PLUGIN_NAME";
    private static final String DB_DRIVER = "org.hsqldb.jdbcDriver";

    // select the programs to transform, add for testing e.g.: fetch first 300 rows only
    private static final String SELECT_PROGS_PRE = "select NAME, OWNER_ID, AUTHOR_ID, ROBOT_ID from PROGRAM where ROBOT_ID = ";
    private static final String SELECT_PROGS_SUF = " and not PROGRAM_TEXT like '%xmlversion=\"3.1\"%' fetch first 300 rows only";
    private static final int COMMIT_LIMIT = 100;

    private final String dbConnectionUrl;
    private final int robotId;
    private final String pluginName;

    private SessionFactoryWrapper sessionFactoryWrapper = null;
    private final XsltTransformer xsltTransformer = new XsltTransformer();
    private RobotFactory robotFactory;

    private String currentMessage = "-";
    int total = 0;

    public static void main(String[] args) throws Exception {
        System.out.println(USAGE);
        args = new String[3];
        args[0] = "jdbc:hsqldb:hsql://localhost/openroberta-db";
        args[1] = "193";
        args[2] = "calliope2017NoBlue";

        if ( args == null || args.length != 3 ) {
            System.out.println("invalid parameter. Exit 12");
            System.exit(12);
        }
        DatabaseEnhancement enhance = new DatabaseEnhancement(args[0], args[1], args[2]);
        enhance.run();
    }

    private DatabaseEnhancement(String dbConnectionUrl, String robotIdAsString, String pluginName) {
        this.dbConnectionUrl = dbConnectionUrl;
        this.robotId = Integer.parseInt(robotIdAsString);
        this.pluginName = pluginName;
    }

    private void run() {
        try {
            printMsgAndSaveForDiagnostic("connecting to the database");
            this.sessionFactoryWrapper = new SessionFactoryWrapper("/hibernate-cfg.xml", this.dbConnectionUrl);

            printMsgAndSaveForDiagnostic("configuring the robot factory");
            robotFactory = Util.configureRobotPlugin(pluginName, ".", ".", Collections.emptyList());

            printMsgAndSaveForDiagnostic("selecting the programs to transform");
            Session hibernateSession = this.sessionFactoryWrapper.getHibernateSession();
            NativeQuery select = hibernateSession.createSQLQuery(SELECT_PROGS_PRE + robotId + SELECT_PROGS_SUF); // no injection possible
            List<Object[]> progDescrArrayList = select.list();
            hibernateSession.close();

            printMsgAndSaveForDiagnostic("processing the programs");
            DbSession dbSession = sessionFactoryWrapper.getSession();
            for ( Object[] progDescrArray : progDescrArrayList ) {
                String name = (String) progDescrArray[0];
                int ownerId = (int) progDescrArray[1];
                int authorId = (int) progDescrArray[2];
                int robotId = (int) progDescrArray[3];
                UserDao userDao = new UserDao(dbSession);
                RobotDao robotDao = new RobotDao(dbSession);
                ProgramDao programDao = new ProgramDao(dbSession);
                User owner = userDao.get(ownerId);
                User author = userDao.get(authorId);
                Robot robot = robotDao.get(robotId);
                Program program = programDao.load(name, owner, robot, author);
                String programText = program.getProgramText();
                String configurationText = getConfigurationFromProgram(dbSession, program);
                String transformedProgramText = xsltTransformer.transform(programText);
                Pair<String, String> progConfPair = UtilForXmlTransformation.transformBetweenVersions(robotFactory, transformedProgramText, configurationText);
                if ( total++ % COMMIT_LIMIT == 0 ) {
                    dbSession.close();
                    dbSession = sessionFactoryWrapper.getSession();
                    printMsgAndSaveForDiagnostic("processed " + total + " programs");
                }
            }
            dbSession.close();
            System.out.println("successful termination after " + total + " successful db updates");
        } catch ( Exception e ) {
            System.out.println("after " + total + " processed ids exception:  " + currentMessage);
            e.printStackTrace(System.out);
        }
    }

    private String getConfigurationFromProgram(DbSession dbSession, Program program) {
        ConfigurationDao configDao = new ConfigurationDao(dbSession);
        String configName = program.getConfigName();
        String configHash = program.getConfigHash();
        if ( configName != null ) {
            Configuration config = configDao.load(configName, program.getOwner(), program.getRobot());
            if ( config == null ) {
                throw new DbcException("config name invalid");
            } else {
                ConfigurationData configData = configDao.load(config.getConfigurationHash());
                if ( configData == null ) {
                    throw new DbcException("config data invalid");
                } else {
                    return configData.getConfigurationText();
                }
            }
        } else if ( configHash != null ) {
            ConfigurationData configData = configDao.load(configHash);
            if ( configData == null ) {
                throw new DbcException("config hash invalid");
            } else {
                return configData.getConfigurationText();
            }
        } else {
            return null; // null to indicate, that the default configuration has to be used.
        }
    }

    private static String transform(String xmlOldVersion) {
        return ":-) " + xmlOldVersion;
    }

    private static void closeQuietly(AutoCloseable statement) {
        if ( statement != null ) {
            try {
                statement.close();
            } catch ( Exception e ) {
                // OK
            }
        }
    }

    private void printMsgAndSaveForDiagnostic(String msg) {
        System.out.println(msg);
        currentMessage = msg;
    }

    private static class ProgDescr {
        public final int id;
        public final String name;
        public final String owner;

        public ProgDescr(int id, String name, String owner) {
            this.id = id;
            this.name = name;
            this.owner = owner;
        }
    }
}
