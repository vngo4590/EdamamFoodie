package au.edu.sydney.soft3202.majorproject.model.db;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.IngredientParsedItem;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.*;

public class NutrientDaoImpl implements NutrientDao {
    private static final String dbName = "nutrient.db";
    private static final String dbURL = "jdbc:sqlite:" + dbName;
    private static final SQLiteConfig config = new SQLiteConfig();
    private static final Logger LOGGER = LogManager.getLogger(NutrientDaoImpl.class);
    private Connection conn;

    private enum LabelType {
        DIET("diet"),
        HEALTH("health"),
        CAUTION("caution");
        private final String labelName;

        LabelType(String labelName) {
            this.labelName = labelName;
        }

        public String getLabelName() {
            return labelName;
        }

        @Override
        public String toString() {
            return labelName;
        }
    }

    private enum NutrientInfoType {
        TOTAL_DAILY("total daily"),
        TOTAL_NUTRIENTS("total nutrients"),
        ;
        private final String nutrientInfoName;

        NutrientInfoType(String nutrientInfoName) {
            this.nutrientInfoName = nutrientInfoName;
        }

        public String getNutrientInfoName() {
            return nutrientInfoName;
        }

        @Override
        public String toString() {
            return nutrientInfoName;
        }
    }

    public void setUp() {
        try {
            config.enforceForeignKeys(true);
            startConnection();
            createDB();
            createDatabaseWithSchema();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }

    public void createDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            LOGGER.debug("Database already created");
            return;
        }
        // If we get here that means no exception raised from getConnection - meaning it worked
        LOGGER.debug("A new database has been created.");
    }

    public void removeDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            /*
             * Close all connections
             * */
            try {
                closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error(e);
            }
            boolean result = dbFile.delete();
            if (!result) {
                LOGGER.debug("Couldn't delete existing db file");
            } else {
                LOGGER.debug("Removed existing DB file.");
            }
        } else {
            LOGGER.debug("No existing DB file.");
        }
    }

    public void startConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(dbURL, config.toProperties());
            conn.setAutoCommit(true);
            LOGGER.debug("Connection Started");
        }
        LOGGER.debug("Connection has already Started");
    }

    /**
     * This will create the necessary tables for the database
     */
    public void createDatabaseWithSchema() throws SQLException {
        String nutrientResponseTableQuery = """
                create table if not EXISTS NUTRIENT_RESPONSE(
                	food_id VARCHAR(100) NOT NULL,
                  	measure_uri VARCHAR(200) NOT NULL,
                  	quantity INTEGER NOT NULL,
                  	nutrient_info VARCHAR(2000) NOT NULL,
                  	PRIMARY KEY (food_id, measure_uri, quantity)
                );
                """;
        Statement statement = conn.createStatement();
        statement.execute(nutrientResponseTableQuery);
        LOGGER.debug("Created NUTRIENT_RESPONSE Table");
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            LOGGER.debug("Connection Closed");
        }
    }

    /**
     * This function will break down the {@link NutrientsResponse} class and then update the db accordingly.
     * The operation used
     *
     * @param nutrientsResponse the nutrientResponse that we have collected from the DB
     */
    @Override
    public void insertNutrientResponse(NutrientsResponse nutrientsResponse) throws SQLException {
        if (nutrientsResponse.getIngredients() != null && nutrientsResponse.getIngredients().get(0) != null) {
            String nutrientResponseInsertQuery = """
                    INSERT OR REPLACE INTO NUTRIENT_RESPONSE (food_id, measure_uri, quantity, nutrient_info)
                    VALUES(?, ?, ?, ?);
                    """;
            String nutrientResponseData = new Gson().toJson(nutrientsResponse);
            IngredientParsedItem ingredient = nutrientsResponse.getIngredients().get(0);
            PreparedStatement preparedStatement = conn.prepareStatement(nutrientResponseInsertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, ingredient.getFoodId());
            preparedStatement.setString(2, ingredient.getMeasureURI());
            preparedStatement.setInt(3, ingredient.getQuantity());
            preparedStatement.setString(4, nutrientResponseData);
            boolean result = preparedStatement.executeUpdate() > 0;
            if (result) {
                LOGGER.debug(String.format("Nutrient Response has been inserted or replaced to the cache: %s",
                        nutrientResponseData));
            } else {
                LOGGER.debug(String.format("Nutrient Response has not been inserted or replaced to the cache: %s",
                        nutrientResponseData));
            }
        } else {
            LOGGER.debug("No Ingredients found for this nutrient record: " + nutrientsResponse);
            throw new IllegalArgumentException("No Ingredients found for this nutrient record");
        }
    }

    @Override
    public String getNutrientResponse(String foodId, String measureUri, int quantity) throws SQLException {
        String response = null;
        String nutrientResponseSelectStatement = """
                SELECT * FROM NUTRIENT_RESPONSE
                WHERE food_id=? AND measure_uri=? AND quantity = ?
                ORDER BY food_id DESC;
                """;
        LOGGER.debug(String.format("Fetching Nutrient Response with Food Id: %s, Measure: %s and Quantity: %d",
                foodId,
                measureUri, quantity)
        );
        PreparedStatement preparedStatement = conn.prepareStatement(nutrientResponseSelectStatement);
        preparedStatement.setString(1, foodId);
        preparedStatement.setString(2, measureUri);
        preparedStatement.setInt(3, quantity);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet != null && resultSet.next()) {
            response = resultSet.getString ("nutrient_info");
        }
        LOGGER.debug(String.format("Responses Found for Nutrient Response with Food Id: %s, Measure: %s and Quantity: %d",
                foodId, measureUri, quantity)
        );
        return response;
    }

    @Override
    public void deleteAllNutrientResponses() {
        removeDB();
        setUp();
        LOGGER.debug("All Nutrient Responses have been cleared");
    }
}
