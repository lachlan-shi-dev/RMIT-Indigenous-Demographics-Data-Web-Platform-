package app;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class JDBCConnection {

    // Name of database file (contained in database folder)
    public static final String DATABASE = "jdbc:sqlite:database/vtp.db";

    /**
     * This creates a JDBC Object so we can keep talking to the database
     */
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the LGAs in the database.
     * @return
     *    Returns an ArrayList of LGA objects
     */

    //Page Index
    // TODO: Add your required methods here

    //Page Index
    // Get total population for 2016&2021
    public int getTotalPopulationForYear(int year) {
        int totalPopulation = 0;

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to the JDBC database
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT SUM(count) AS total_population FROM Population WHERE lga_year = " + year;

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process the result
            if (results.next()) {
                totalPopulation = results.getInt("total_population");
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // Handle any SQL errors
            System.err.println(e.getMessage());
        } finally {
            // Safety code to clean up
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Handle any closing errors
                System.err.println(e.getMessage());
            }
        }

        return totalPopulation;
    }

    public static Map<Integer, Integer> getTotalLGAsByYear() {
        Map<Integer, Integer> totalLGAs = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT year, COUNT(*) AS total_lgas " +
                           "FROM LGA " +
                           "WHERE year IN (2016, 2021) " +
                           "GROUP BY year";

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int year = resultSet.getInt("year");
                int count = resultSet.getInt("total_lgas");
                totalLGAs.put(year, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalLGAs;
    }

    public Map<String, Map<Integer, Integer>> getTotalPopulationByStateAndYear() {
        Map<String, Map<Integer, Integer>> totalPopulationData = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            String query = "SELECT LGA.state_abbr AS state, LGA.year, SUM(Population.count) AS total_population " +
                    "FROM LGA " +
                    "JOIN Population ON LGA.code = Population.lga_code AND LGA.year = Population.lga_year " +
                    "WHERE LGA.year IN (2016, 2021) " +
                    "GROUP BY LGA.state_abbr, LGA.year " +
                    "ORDER BY LGA.state_abbr, LGA.year";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String state = resultSet.getString("state");
                        int year = resultSet.getInt("year");
                        int totalPopulation = resultSet.getInt("total_population");

                        // Add data to the result map
                        totalPopulationData
                            .computeIfAbsent(state, k -> new HashMap<>())
                            .put(year, totalPopulation);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalPopulationData;
    }

    public ArrayList<SchoolNon> getSchoolLGAs(String indig) {
        ArrayList<SchoolNon> school = new ArrayList<>();
    
        // Setup the variable for the JDBC connection
        Connection connection = null;
    
        try {
            // Connect to the JDBC database
            connection = DriverManager.getConnection(DATABASE);
    
            // Prepare a new SQL Query & Set a timeout
            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement2.setQueryTimeout(30);
    
            // Construct your SQL query based on the provided database structure
            String query1 = "SELECT h.lga_code, h.indigenous_status, h.category, SUM(h.count) AS \"Population (Raw)\" " + 
                "FROM HighestSchoolYear h" +
                " WHERE h.indigenous_status = '" + indig + "' AND h.lga_year = 2021 " +
                "GROUP BY h.lga_code, h.category;";
            
            String query2 = "SELECT h.lga_code, h.indigenous_status, h.category, SUM(h.count) AS Population " + 
                "FROM HighestSchoolYear h" +
                " WHERE h.indigenous_status = '" + indig + "' AND h.lga_year = 2021 " +
                "GROUP BY h.lga_code;";

    
            // Get the result
            ResultSet results1 = statement1.executeQuery(query1);
            ResultSet results2 = statement2.executeQuery(query2);
            
            int ct = 0;
            int totalPopulation = results2.getInt("Population");
            results2.next();

            // Process the results
            while (results1.next()) {
                double proportionalValue = 0;
                int LGAcode = results1.getInt("lga_code");
                String indigstatus = results1.getString("indigenous_status");
                String category = results1.getString("category");
                int population = results1.getInt("Population (Raw)");
    
                // Create a CompletionData object
                category = category.substring(1);

                if(totalPopulation > 0) {
                    proportionalValue = ((double)population / totalPopulation)*100;
                }
                else {
                    proportionalValue = 0.0;
                }
    
                SchoolNon schools = new SchoolNon(LGAcode, indigstatus, category, population, proportionalValue);
                school.add(schools);

                ct++;

                if (ct == 14) {
                    results2.next();
                    totalPopulation = results2.getInt("Population");
                    ct = 0;
                }

            }
    
            // Close the statement because we are done with it
            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        // Finally, return all of the data
        return school;
    }    

    public ArrayList<SchoolNon> getSchoolstateTer(String indig, String state) {
        ArrayList<SchoolNon> school = new ArrayList<>();
    
        // Setup the variable for the JDBC connection
        Connection connection = null;
    
        try {
            // Connect to the JDBC database
            connection = DriverManager.getConnection(DATABASE);
    
            // Prepare a new SQL Query & Set a timeout
            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement2.setQueryTimeout(30);
    
            // Construct your SQL query based on the provided database structure
            String query1 = "SELECT h.lga_code, h.indigenous_status, h.category, SUM(h.count) AS \"Population (Raw)\" " + 
                "FROM LGA JOIN HighestSchoolYear h ON id.lga_code" +
                " WHERE h.indigenous_status = '" + indig + "' AND h.lga_year = 2021 AND LGA.state_abbr = " + "'" + state + "' " +
                "GROUP BY h.lga_code, h.category;";
            
            String query2 = "SELECT SUM(h.count) AS Population " + 
                "FROM LGA JOIN HighestSchoolYear h ON id.lga_code" +
                " WHERE h.indigenous_status = '" + indig + "' AND h.lga_year = 2021 AND LGA.state_abbr = " + "'" + state + "' " +
                "GROUP BY h.lga_code;";

    
            // Get the result
            ResultSet results1 = statement1.executeQuery(query1);
            ResultSet results2 = statement2.executeQuery(query2);
            
            int ct = 0;
            int totalPopulation = results2.getInt("Population");
            results2.next();

            // Process the results
            while (results1.next()) {
                double proportionalValue = 0;
                int LGAcode = results1.getInt("code");
                String states = results1.getString("state_abbr");
                String indigstatus = results1.getString("indigenous_status");
                String category = results1.getString("category");
                int population = results1.getInt("Population (Raw)");
    
                // Create a CompletionData object
                category = category.substring(1);

                if(totalPopulation > 0) {
                    proportionalValue = ((double)population / totalPopulation)*100;
                }
                else {
                    proportionalValue = 0.0;
                }
    
                SchoolNon schools = new SchoolNon(LGAcode, states, indigstatus, category, population, proportionalValue);
                school.add(schools);

                ct++;

                if (ct == 14) {
                    results2.next();
                    totalPopulation = results2.getInt("Population");
                    ct = 0;
                }

            }
    
            // Close the statement because we are done with it
            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        // Finally, return all of the data
        return school;
    }    

    public ArrayList<NonSchool> getNonSchoolLGAs(String indig) {
        ArrayList<NonSchool> nonschool = new ArrayList<>();
    
        // Setup the variable for the JDBC connection
        Connection connection = null;
    
        try {
            // Connect to the JDBC database
            connection = DriverManager.getConnection(DATABASE);
    
            // Prepare a new SQL Query & Set a timeout
            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement2.setQueryTimeout(30);
    
            // Construct your SQL query based on the provided database structure
            String query1 = "SELECT h.lga_code, h.indigenous_status, h.category, SUM(h.count) AS \"Population (Raw)\" " + 
                "FROM NonSchoolCompletion h" +
                " WHERE h.indigenous_status = '" + indig + "' AND h.lga_year = 2021 " +
                "GROUP BY h.lga_code, h.category;";
            
            String query2 = "SELECT h.lga_code, h.indigenous_status, h.category, SUM(h.count) AS Population " + 
                "FROM NonSchoolCompletion h" +
                " WHERE h.indigenous_status = '" + indig + "' AND h.lga_year = 2021 " +
                "GROUP BY h.lga_code;";

    
            // Get the result
            ResultSet results1 = statement1.executeQuery(query1);
            ResultSet results2 = statement2.executeQuery(query2);
            
            int ct = 0;
            int totalPopulation = results2.getInt("Population");
            results2.next();

            // Process the results
            while (results1.next()) {
                double proportionalValue = 0;
                int LGAcode = results1.getInt("lga_code");
                String indigstatus = results1.getString("indigenous_status");
                String category = results1.getString("category");
                int population = results1.getInt("Population (Raw)");
    
                // Create a CompletionData object
                category = category.substring(1);

                if(totalPopulation > 0) {
                    proportionalValue = ((double)population / totalPopulation)*100;
                }
                else {
                    proportionalValue = 0.0;
                }
    
                NonSchool nschools = new NonSchool(LGAcode, indigstatus, category, population, proportionalValue);
                nonschool.add(nschools);

                ct++;

                if (ct == 10) {
                    results2.next();
                    totalPopulation = results2.getInt("Population");
                    ct = 0;
                }

            }
    
            // Close the statement because we are done with it
            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        // Finally, return all of the data
        return nonschool;
    }    

    public ArrayList<NonSchool> getNonSchoolStateTer(String indig, String state) {
        ArrayList<NonSchool> nonschool = new ArrayList<>();
    
        // Setup the variable for the JDBC connection
        Connection connection = null;
    
        try {
            // Connect to the JDBC database
            connection = DriverManager.getConnection(DATABASE);
    
            // Prepare a new SQL Query & Set a timeout
            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement2.setQueryTimeout(30);
    
            // Construct your SQL query based on the provided database structure
            String query1 = "SELECT h.lga_code, h.indigenous_status, h.category, SUM(h.count) AS \"Population (Raw)\" " + 
                "FROM LGA JOIN NonSchoolCompletion h ON h.lga_code" +
                " WHERE h.indigenous_status = '" + indig + "' AND h.lga_year = 2021 AND LGA.state_abbr = " + "'" + state + "' " +
                "GROUP BY h.lga_code, h.category;";
            
            String query2 = "SELECT h.lga_code, h.indigenous_status, h.category, SUM(h.count) AS Population " + 
                "FROM NonSchoolCompletion h" +
                " WHERE h.indigenous_status = '" + indig + "' AND h.lga_year = 2021 " +
                "GROUP BY h.lga_code;";

    
            // Get the result
            ResultSet results1 = statement1.executeQuery(query1);
            ResultSet results2 = statement2.executeQuery(query2);
            
            int ct = 0;
            int totalPopulation = results2.getInt("Population");
            results2.next();

            // Process the results
            while (results1.next()) {
                double proportionalValue = 0;
                int LGAcode = results1.getInt("lga_code");
                String states = results1.getString("state_abbr");
                String indigstatus = results1.getString("indigenous_status");
                String category = results1.getString("category");
                int population = results1.getInt("Population (Raw)");
    
                // Create a CompletionData object
                category = category.substring(1);

                if(totalPopulation > 0) {
                    proportionalValue = ((double)population / totalPopulation)*100;
                }
                else {
                    proportionalValue = 0.0;
                }
    
                NonSchool nschools = new NonSchool(LGAcode, states, indigstatus, category, population, proportionalValue);
                nonschool.add(nschools);

                ct++;

                if (ct == 10) {
                    results2.next();
                    totalPopulation = results2.getInt("Population");
                    ct = 0;
                }

            }
    
            // Close the statement because we are done with it
            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        // Finally, return all of the data
        return nonschool;
    }    

    public void SchoolSortPropAsc(ArrayList<SchoolNon> myArrayList, int low, int hi) {
        int tempLow, tempHi;
        double pivot;

        tempLow = low; 
        tempHi = hi;

        pivot = myArrayList.get(((low + hi) / 2)).getProportionalData();

        while (tempLow <= tempHi) { 
            while (myArrayList.get(tempLow).getProportionalData() < pivot && tempLow < hi) {
                tempLow++;
            }
            while (myArrayList.get(tempHi).getProportionalData() > pivot && tempHi > low) {
                tempHi--;
            }
            if (tempLow <= tempHi) {
                SchoolNon temp = myArrayList.get(tempLow);
                myArrayList.set(tempLow, myArrayList.get(tempHi));
                myArrayList.set(tempHi, temp);
    
                tempLow++;
                tempHi--;
            }
        }
        if (low < hi) {
            SchoolSortPropAsc(myArrayList, low, hi);
        }

        if (low < hi) {
            SchoolSortPropAsc(myArrayList, low, hi);
        }
    }

    public void SchoolSortPropDesc(ArrayList<SchoolNon> array, int low, int hi) {
        int tempLow, tempHi;
        double pivot;
    
        tempLow = low;
        tempHi = hi;

        pivot = array.get(((low + hi) / 2)).getProportionalData(); 
        while (tempLow <= tempHi) {
            while (array.get(tempLow).getProportionalData() > pivot && tempLow < hi) {
                tempLow++;
            }
            while (array.get(tempHi).getProportionalData() < pivot && tempHi > hi) {
                tempHi--;
            }
            if (tempLow <= tempHi) {
                SchoolNon temp = array.get(tempLow);
                array.set(tempLow, array.get(tempHi));
                array.set(tempHi, temp);
    
                tempLow++;
                tempHi--;
            }
        }
        if (low < tempHi) {
            SchoolSortPropDesc(array, low, tempHi);
        }
    
        if (tempLow < hi) {
            SchoolSortPropDesc(array, tempLow, hi);
        }
    }

    public void NonSchoolSortPropAsc(ArrayList<NonSchool> myArrayList, int low, int hi) {
        int tempLow, tempHi;
        double pivot;

        tempLow = low; 
        tempHi = hi;

        pivot = myArrayList.get(((low + hi) / 2)).getProportionalData();

        while (tempLow <= tempHi) { 
            while (myArrayList.get(tempLow).getProportionalData() < pivot && tempLow < hi) {
                tempLow++;
            }
            while (myArrayList.get(tempHi).getProportionalData() > pivot && tempHi > low) {
                tempHi--;
            }
            if (tempLow <= tempHi) {
                NonSchool temp = myArrayList.get(tempLow);
                myArrayList.set(tempLow, myArrayList.get(tempHi));
                myArrayList.set(tempHi, temp);
    
                tempLow++;
                tempHi--;
            }
        }
        if (low < hi) {
            NonSchoolSortPropAsc(myArrayList, low, hi);
        }

        if (low < hi) {
            NonSchoolSortPropAsc(myArrayList, low, hi);
        }
    }

    public void NonSchoolSortPropDesc(ArrayList<NonSchool> array, int low, int hi) {
        int tempLow, tempHi;
        double pivot;
    
        tempLow = low;
        tempHi = hi;
        
        pivot = array.get(((low + hi) / 2)).getProportionalData(); 
        while (tempLow <= tempHi) {
            while (array.get(tempLow).getProportionalData() > pivot && tempLow < hi) {
                tempLow++;
            }
            while (array.get(tempHi).getProportionalData() < pivot && tempHi > hi) {
                tempHi--;
            }
            if (tempLow <= tempHi) {
                NonSchool temp = array.get(tempLow);
                array.set(tempLow, array.get(tempHi));
                array.set(tempHi, temp);
    
                tempLow++;
                tempHi--;
            }
        }
        if (low < tempHi) {
            NonSchoolSortPropDesc(array, low, tempHi);
        }
    
        if (tempLow < hi) {
            NonSchoolSortPropDesc(array, tempLow, hi);
        }
    }

    public void SchoolSortRawAsc(ArrayList<SchoolNon> myArrayList, int low, int hi) {
        int tempLow, tempHi;
        double pivot;

        tempLow = low; 
        tempHi = hi;

        pivot = myArrayList.get(((low + hi) / 2)).getRawData();

        while (tempLow <= tempHi) { 
            while (myArrayList.get(tempLow).getRawData() < pivot && tempLow < hi) {
                tempLow++;
            }
            while (myArrayList.get(tempHi).getRawData() > pivot && tempHi > low) {
                tempHi--;
            }
            if (tempLow <= tempHi) {
                SchoolNon temp = myArrayList.get(tempLow);
                myArrayList.set(tempLow, myArrayList.get(tempHi));
                myArrayList.set(tempHi, temp);
    
                tempLow++;
                tempHi--;
            }
        }
        if (low < hi) {
            SchoolSortRawAsc(myArrayList, low, hi);
        }

        if (low < hi) {
            SchoolSortRawAsc(myArrayList, low, hi);
        }
    }

    public void SchoolSortRawDesc(ArrayList<SchoolNon> array, int low, int hi) {
        int tempLow, tempHi;
        double pivot;
    
        tempLow = low;
        tempHi = hi;

        pivot = array.get(((low + hi) / 2)).getRawData(); 
        while (tempLow <= tempHi) {
            while (array.get(tempLow).getRawData() > pivot && tempLow < hi) {
                tempLow++;
            }
            while (array.get(tempHi).getRawData() < pivot && tempHi > hi) {
                tempHi--;
            }
            if (tempLow <= tempHi) {
                SchoolNon temp = array.get(tempLow);
                array.set(tempLow, array.get(tempHi));
                array.set(tempHi, temp);
    
                tempLow++;
                tempHi--;
            }
        }
        if (low < tempHi) {
            SchoolSortRawDesc(array, low, tempHi);
        }
    
        if (tempLow < hi) {
            SchoolSortRawDesc(array, tempLow, hi);
        }
    }

    public void NonSchoolSortRawAsc(ArrayList<NonSchool> myArrayList, int low, int hi) {
        int tempLow, tempHi;
        double pivot;

        tempLow = low; 
        tempHi = hi;

        pivot = myArrayList.get(((low + hi) / 2)).getRawData();

        while (tempLow <= tempHi) { 
            while (myArrayList.get(tempLow).getRawData() < pivot && tempLow < hi) {
                tempLow++;
            }
            while (myArrayList.get(tempHi).getRawData() > pivot && tempHi > low) {
                tempHi--;
            }
            if (tempLow <= tempHi) {
                NonSchool temp = myArrayList.get(tempLow);
                myArrayList.set(tempLow, myArrayList.get(tempHi));
                myArrayList.set(tempHi, temp);
    
                tempLow++;
                tempHi--;
            }
        }
        if (low < hi) {
            NonSchoolSortRawAsc(myArrayList, low, hi);
        }

        if (low < hi) {
            NonSchoolSortRawAsc(myArrayList, low, hi);
        }
    }

    public void NonSchoolSortRawDesc(ArrayList<NonSchool> array, int low, int hi) {
        int tempLow, tempHi;
        double pivot;
    
        tempLow = low;
        tempHi = hi;
        
        pivot = array.get(((low + hi) / 2)).getRawData(); 
        while (tempLow <= tempHi) {
            while (array.get(tempLow).getRawData() > pivot && tempLow < hi) {
                tempLow++;
            }
            while (array.get(tempHi).getRawData() < pivot && tempHi > hi) {
                tempHi--;
            }
            if (tempLow <= tempHi) {
                NonSchool temp = array.get(tempLow);
                array.set(tempLow, array.get(tempHi));
                array.set(tempHi, temp);
    
                tempLow++;
                tempHi--;
            }
        }
        if (low < tempHi) {
            NonSchoolSortRawDesc(array, low, tempHi);
        }
    
        if (tempLow < hi) {
            NonSchoolSortRawDesc(array, tempLow, hi);
        }
    }

    // Code for 3B
    public ArrayList<LGA3B> getLGA3B(String lga, String category, String sex, String indig, String datatype, String year) {
        // Create the ArrayList of LGA objects to return
        ArrayList<LGA3B> simlga = new ArrayList<LGA3B>();

        // Setup the variable for the JDBC connection
        Connection connection = null;
        
        String rank = "";
        
        
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            
            String query2A = "SELECT lga_code, count FROM " + datatype + " where category = '" + category + "' and sex = '" + sex + "' and lga_year = '"+year+"' and indigenous_status = '" + indig + "' ORDER BY ABS(count - (SELECT count FROM "+ datatype +" where category = '"+ category +"' and sex = '"+sex+"' and indigenous_status = '"+indig+"' and lga_code = '"+ lga +"')) LIMIT 11";
           
  
            
            // Get Result
            ResultSet results1 = statement.executeQuery(query2A);
            int i = 1;
            int ct = results1.getInt("count");
            while (results1.next()) {

                
                if(i == 1){rank = "LGA SELECTED FOR COMPARISON";}
                if(i == 2){rank = "1";}
                if(i == 3){rank = "2";}
                if(i == 4){rank = "3";}
                if(i == 5){rank = "4";}
                if(i == 6){rank = "5";}
                if(i == 7){rank = "6";}
                if(i == 8){rank = "7";}
                if(i == 9){rank = "8";}
                if(i == 10){rank = "9";}
                if(i == 11){rank = "10";}

                String code = results1.getString("lga_code");
                int count = results1.getInt("count");
                
                String name = getCurrentLGA(code);


                LGA3B a1 = new LGA3B(rank, name, code, count, ct);

                simlga.add(a1);
                i++;
            }

            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return simlga;
    }

    public String getCurrentLGA(String LGA) {
        // Create the ArrayList of LGA objects to return
        String LGANAME = "";

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM LGA WHERE code='" + LGA + "'";

            // Get Result
            ResultSet results = statement.executeQuery(query);

                String name  = results.getString("name");
                LGANAME = name;

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return LGANAME;
    }

    // Gets all the state name abbreviations
    public ArrayList<String> getStateTerrNames() {
        ArrayList<String> stateTerrNames = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT DISTINCT LGA.state_abbr FROM LGA;";

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String stateTerrName = results.getString("state_abbr");
                stateTerrNames.add(stateTerrName);
            }  

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return stateTerrNames;        
    }

    // Gets all the age demographic data such as LGA Code, Indigenous Status, Age Category, Raw Data and Proportional Data
    public ArrayList<AgeDemographics> getAgeDemographicsAsLGAs(String indigStatus) {
        ArrayList<AgeDemographics> ageDemographics = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement2.setQueryTimeout(30);

            String query1 = "SELECT id.lga_code, id.indigenous_status, id.age_category, SUM(id.count) AS \"Population (Raw)\" " + 
                            "FROM IndigenousDemographics id " +
                            "WHERE id.indigenous_status = '" + indigStatus + "' AND id.lga_year = 2021 " +
                            "GROUP BY id.lga_code, id.age_category;";

            String query2 = "SELECT id.lga_code, id.indigenous_status, SUM(id.count) AS Population " +
                            "FROM IndigenousDemographics id " + 
                            "WHERE id.indigenous_status = '" + indigStatus + "' AND id.lga_year = 2021 " +
                            "GROUP BY id.lga_code;";    

            ResultSet results1 = statement1.executeQuery(query1); // Stores LGA code, indigenous status, age category and raw data for each LGA
            ResultSet results2 = statement2.executeQuery(query2); // Stores the total population of each LGA for each indigenous status

            int count = 0;
            int totalPopulation = results2.getInt("Population");
            results2.next();

            while (results1.next()) {
                double proportionalValue = 0;
                int LGACode = results1.getInt("lga_code");
                String indigenous_status = results1.getString("indigenous_status");
                String ageCategory = results1.getString("age_category");
                int population = results1.getInt("Population (Raw)");

                // Remove first underscore
                ageCategory = ageCategory.substring(1);

                if (totalPopulation > 0) {
                    proportionalValue = ((double) population / totalPopulation) * 100;
                }
                else {
                    proportionalValue = 0.0;
                }

                AgeDemographics AD = new AgeDemographics(LGACode, indigenous_status, ageCategory, population, proportionalValue);
                ageDemographics.add(AD);

                count++;

                // Use the totalPopulation value 14 times before moving onto the next
                if (count == 14) {
                        // After 14 iterations, update totalPopulation
                        results2.next();
                        totalPopulation = results2.getInt("Population");
                        count = 0;
                }
            }  

            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ageDemographics;
    }

    // Gets all the age demographic data such as State/Territroy, Indigenous Status, Age Category, Raw Data and Proportional Data
    public ArrayList<AgeDemographics> getAgeDemographicsAsStateTerr(String indigStatus, String state) {
        ArrayList<AgeDemographics> ageDemographics = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement2.setQueryTimeout(30);

            String query1 = "SELECT LGA.code, LGA.state_abbr, id.indigenous_status, id.age_category, SUM(id.count) AS \"Population (Raw)\" " + 
                            "FROM LGA JOIN IndigenousDemographics id ON LGA.code = id.lga_code " +
                            "WHERE id.indigenous_status = '" + indigStatus + "' AND id.lga_year = 2021 AND LGA.state_abbr = " + "'" + state + "' " +
                            "GROUP BY id.lga_code, id.age_category;";

            String query2 = "SELECT SUM(id.count) AS Population " +
                            "FROM LGA JOIN IndigenousDemographics id ON LGA.code = id.lga_code " + 
                            "WHERE id.indigenous_status = '" + indigStatus + "' AND id.lga_year = 2021 AND LGA.state_abbr = " + "'" + state + "' " +
                            "GROUP BY LGA.code;";    

            ResultSet results1 = statement1.executeQuery(query1); // Stores State/Territory, Indigenous Status, Age Category and Raw Data
            ResultSet results2 = statement2.executeQuery(query2); // Stores the total population of each LGA for each indigenous status for each State/Territory

            int count = 0;
            int totalPopulation = results2.getInt("Population");
            results2.next();

            while (results1.next()) {
                double proportionalValue = 0;
                int LGACode = results1.getInt("code");
                String STATE = results1.getString("state_abbr");
                String indigenous_status = results1.getString("indigenous_status");
                String ageCategory = results1.getString("age_category");
                int population = results1.getInt("Population (Raw)");

                // Remove first underscore
                ageCategory = ageCategory.substring(1);

                if (totalPopulation > 0) {
                    proportionalValue = ((double) population / totalPopulation) * 100;
                }
                else {
                    proportionalValue = 0.0;
                }

                AgeDemographics AD = new AgeDemographics(LGACode, STATE, indigenous_status, ageCategory, population, proportionalValue);
                ageDemographics.add(AD);

                count++;

                // Use the totalPopulation value 14 times before moving onto the next
                if (count == 14) {
                    // After 14 iterations, update totalPopulation
                    results2.next();
                    totalPopulation = results2.getInt("Population");
                    count = 0;
                }
            }  

            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ageDemographics;
    }

    // Gets all the LTHC data such as LGA Code, Indigenous Status, Condition, Raw Data and Proportional Data
    public ArrayList<LTHC> getLTHCAsLGAs(String indigStatus) {
        ArrayList<LTHC> LTHCs = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement2.setQueryTimeout(30);

            String query1 = "SELECT LTHC.lga_code, LTHC.indigenous_status, LTHC.condition, SUM(LTHC.count) AS \"Population (Raw)\" " + 
                            "FROM LTHC " +
                            "WHERE LTHC.indigenous_status = '" + indigStatus + "' AND LTHC.lga_year = 2021 " +
                            "GROUP BY LTHC.lga_code, LTHC.condition ;";

            String query2 = "SELECT LTHC.lga_code, LTHC.indigenous_status, SUM(LTHC.count) AS Population " +
                            "FROM LTHC " + 
                            "WHERE LTHC.indigenous_status = '" + indigStatus + "' AND LTHC.lga_year = 2021 " +
                            "GROUP BY LTHC.lga_code;";    

            ResultSet results1 = statement1.executeQuery(query1); // Stores LGA code, indigenous status, age category and raw data for each LGA
            ResultSet results2 = statement2.executeQuery(query2); // Stores the total population of each LGA for each indigenous status

            int count = 0;
            int totalPopulation = results2.getInt("Population");
            results2.next();

            while (results1.next()) {
                double proportionalValue = 0;
                int LGACode = results1.getInt("lga_code");
                String indigenous_status = results1.getString("indigenous_status");
                String condition = results1.getString("condition");
                int population = results1.getInt("Population (Raw)");

                if (totalPopulation > 0) {
                    proportionalValue = ((double) population / totalPopulation) * 100;
                }
                else {
                    proportionalValue = 0.0;
                }

                LTHC lthc = new LTHC(LGACode, indigenous_status, condition, population, proportionalValue);
                LTHCs.add(lthc);

                count++;

                // Use the totalPopulation value 10 times before moving onto the next
                if (count == 10) {
                    // After 10 iterations, update totalPopulation
                    results2.next();
                    totalPopulation = results2.getInt("Population");
                    count = 0;
                }
            }  

            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return LTHCs;
    }

    // Gets all the LTHC data such as State/Territory, Indigenous Status, Condition, Raw Data and Proportional Data
    public ArrayList<LTHC> getLTHCAsStateTerr(String indigStatus, String state) {
        ArrayList<LTHC> LTHCs = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement2.setQueryTimeout(30);

            String query1 = "SELECT LGA.code, LGA.state_abbr, LTHC.indigenous_status," + 
                                "CASE " +
                                    "WHEN LTHC.condition = 'arthritis' THEN 'arthritis' " +
                                    "WHEN LTHC.condition = 'asthma' THEN 'asthma' " +
                                    "WHEN LTHC.condition = 'cancer' THEN 'cancer' " +
                                    "WHEN LTHC.condition = 'dementia' THEN 'dementia' " +
                                    "WHEN LTHC.condition = 'diabetes' THEN 'diabetes' " +
                                    "WHEN LTHC.condition = 'heartdisease' THEN 'heartdisease' " +
                                    "WHEN LTHC.condition = 'kidneydisease' THEN 'kidneydisease' " +
                                    "WHEN LTHC.condition = 'lungcategory' THEN 'lungcategory' " +
                                    "WHEN LTHC.condition = 'mentalhealth' THEN 'mentalhealth' " +
                                    "WHEN LTHC.condition = 'stroke' THEN 'stroke' " +
                                "END AS condition, " +
                                "SUM(LTHC.count) AS \"Population (Raw)\" " +
                            "FROM LGA JOIN LTHC ON LGA.code = LTHC.lga_code " +
                            "WHERE LTHC.indigenous_status = " + "'" + indigStatus + "'" + " AND LTHC.lga_year = 2021 AND LGA.state_abbr = " + "'" + state + "' " +
                            "GROUP BY LTHC.lga_code, condition;";

            String query2 = "SELECT SUM(LTHC.count) AS Population " +
                            "FROM LGA JOIN LTHC ON LGA.code = LTHC.lga_code " + 
                            "WHERE LTHC.indigenous_status = '" + indigStatus + "' AND LTHC.lga_year = 2021 AND LGA.state_abbr = " + "'" + state + "' " +
                            "GROUP BY LGA.code;";    

            ResultSet results1 = statement1.executeQuery(query1); // Stores State/Territory, Indigenous Status, Age Category and Raw Data
            ResultSet results2 = statement2.executeQuery(query2); // Stores the total population of each LGA for each indigenous status for each State/Territory

            int count = 0;
            int totalPopulation = results2.getInt("Population");
            results2.next();

            while (results1.next()) {
                double proportionalValue = 0;
                int LGACode = results1.getInt("code");
                String STATE = results1.getString("state_abbr");
                String indigenous_status = results1.getString("indigenous_status");
                String condition = results1.getString("condition");
                int population = results1.getInt("Population (Raw)");

                if (totalPopulation > 0) {
                    proportionalValue = ((double) population / totalPopulation) * 100;
                }
                else {
                    proportionalValue = 0.0;
                }

                LTHC lthc = new LTHC(LGACode, STATE, indigenous_status, condition, population, proportionalValue);
                LTHCs.add(lthc);

                count++;

                // Use the totalPopulation value 10 times before moving onto the next
                if (count == 10) {
                    // After 10 iterations, update totalPopulation
                    results2.next();
                    totalPopulation = results2.getInt("Population");
                    count = 0;
                }
            }  

            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return LTHCs;
    }

    // Sorts Ascendingly the age demographic data with respect to the proportional values
    public void AgeDemographicSortPropAsc(ArrayList<AgeDemographics> myArrayList, int indexLow, int indexHi) {
        int tmpLow, tmpHi;
        double pivot;

        // Stores the min and max array index so that it can be used to increment or decrement through each value in the array
        tmpLow = indexLow; 
        tmpHi = indexHi;

        pivot = myArrayList.get(((indexLow + indexHi) / 2)).getProportionalData(); // Sets the pivot to the middle element in the array

        while (tmpLow <= tmpHi) {
            // Increment 
            while (myArrayList.get(tmpLow).getProportionalData() < pivot && tmpLow < indexHi) {
                tmpLow++;
            }
            // Decrement
            while (myArrayList.get(tmpHi).getProportionalData() > pivot && tmpHi > indexLow) {
                tmpHi--;
            }
            // Swap
            if (tmpLow <= tmpHi) {
                AgeDemographics temp = myArrayList.get(tmpLow);
                myArrayList.set(tmpLow, myArrayList.get(tmpHi));
                myArrayList.set(tmpHi, temp);
    
                // Move onto the next values in the ArrayList
                tmpLow++;
                tmpHi--;
            }
        }
        // Repeat quicksort for the sub array smaller than the pivot
        if (indexLow < tmpHi) {
            AgeDemographicSortPropAsc(myArrayList, indexLow, tmpHi);
        }

        // Repeat quicksort for the sub array greater than the pivot
        if (tmpLow < indexHi) {
            AgeDemographicSortPropAsc(myArrayList, tmpLow, indexHi);
        }
    }

    // Sorts Descendingly the age demographic data with respect to the proportional values
    public void AgeDemographicSortPropDesc(ArrayList<AgeDemographics> myArrayList, int indexLow, int indexHi) {
        int tmpLow, tmpHi;
        double pivot;
    
        // Stores the min and max array index so that it can be used to increment or decrement through each value in the array
        tmpLow = indexLow; 
        tmpHi = indexHi;
    
        pivot = myArrayList.get(((indexLow + indexHi) / 2)).getProportionalData(); // Sets the pivot to the middle element in the array
    
        while (tmpLow <= tmpHi) {
            // Increment 
            while (myArrayList.get(tmpLow).getProportionalData() > pivot && tmpLow < indexHi) {
                tmpLow++;
            }
            // Decrement
            while (myArrayList.get(tmpHi).getProportionalData() < pivot && tmpHi > indexLow) {
                tmpHi--;
            }
            // Swap
            if (tmpLow <= tmpHi) {
                AgeDemographics temp = myArrayList.get(tmpLow);
                myArrayList.set(tmpLow, myArrayList.get(tmpHi));
                myArrayList.set(tmpHi, temp);
    
                // Move onto the next values in the ArrayList
                tmpLow++;
                tmpHi--;
            }
        }
        // Repeat quicksort for the sub-array greater than the pivot
        if (indexLow < tmpHi) {
            AgeDemographicSortPropDesc(myArrayList, indexLow, tmpHi);
        }
    
        // Repeat quicksort for the sub-array smaller than the pivot
        if (tmpLow < indexHi) {
            AgeDemographicSortPropDesc(myArrayList, tmpLow, indexHi);
        }
    }

    // Sorts Ascendingly the LTHC data with respect to the proportional values
    public void LTHCSortPropAsc(ArrayList<LTHC> myArrayList, int indexLow, int indexHi) {
        int tmpLow, tmpHi;
        double pivot;

        // Stores the min and max array index so that it can be used to increment or decrement through each value in the array
        tmpLow = indexLow; 
        tmpHi = indexHi;

        pivot = myArrayList.get(((indexLow + indexHi) / 2)).getProportionalData(); // Sets the pivot to the middle element in the array

        while (tmpLow <= tmpHi) {
            // Increment 
            while (myArrayList.get(tmpLow).getProportionalData() < pivot && tmpLow < indexHi) {
                tmpLow++;
            }
            // Decrement
            while (myArrayList.get(tmpHi).getProportionalData() > pivot && tmpHi > indexLow) {
                tmpHi--;
            }
            // Swap
            if (tmpLow <= tmpHi) {
                LTHC temp = myArrayList.get(tmpLow);
                myArrayList.set(tmpLow, myArrayList.get(tmpHi));
                myArrayList.set(tmpHi, temp);
    
                // Move onto the next values in the ArrayList
                tmpLow++;
                tmpHi--;
            }
        }
        // Repeat quicksort for the sub array smaller than the pivot
        if (indexLow < tmpHi) {
            LTHCSortPropAsc(myArrayList, indexLow, tmpHi);
        }

        // Repeat quicksort for the sub array greater than the pivot
        if (tmpLow < indexHi) {
            LTHCSortPropAsc(myArrayList, tmpLow, indexHi);
        }
    }

    // Sorts Descendingly the LTHC data with respect to the proportional values
    public void LTHCSortPropDesc(ArrayList<LTHC> myArrayList, int indexLow, int indexHi) {
        int tmpLow, tmpHi;
        double pivot;
    
        // Stores the min and max array index so that it can be used to increment or decrement through each value in the array
        tmpLow = indexLow; 
        tmpHi = indexHi;
    
        pivot = myArrayList.get(((indexLow + indexHi) / 2)).getProportionalData(); // Sets the pivot to the middle element in the array
    
        while (tmpLow <= tmpHi) {
            // Increment 
            while (myArrayList.get(tmpLow).getProportionalData() > pivot && tmpLow < indexHi) {
                tmpLow++;
            }
            // Decrement
            while (myArrayList.get(tmpHi).getProportionalData() < pivot && tmpHi > indexLow) {
                tmpHi--;
            }
            // Swap
            if (tmpLow <= tmpHi) {
                LTHC temp = myArrayList.get(tmpLow);
                myArrayList.set(tmpLow, myArrayList.get(tmpHi));
                myArrayList.set(tmpHi, temp);
    
                // Move onto the next values in the ArrayList
                tmpLow++;
                tmpHi--;
            }
        }
        // Repeat quicksort for the sub-array greater than the pivot
        if (indexLow < tmpHi) {
            LTHCSortPropDesc(myArrayList, indexLow, tmpHi);
        }
    
        // Repeat quicksort for the sub-array smaller than the pivot
        if (tmpLow < indexHi) {
            LTHCSortPropDesc(myArrayList, tmpLow, indexHi);
        }
    }

    // Sorts Ascendingly the age demographic data with respect to the raw values
    public void AgeDemographicSortRawAsc(ArrayList<AgeDemographics> myArrayList, int indexLow, int indexHi) {
        int tmpLow, tmpHi;
        double pivot;

        // Stores the min and max array index so that it can be used to increment or decrement through each value in the array
        tmpLow = indexLow; 
        tmpHi = indexHi;

        pivot = myArrayList.get(((indexLow + indexHi) / 2)).getRawData(); // Sets the pivot to the middle element in the array

        while (tmpLow <= tmpHi) {
            // Increment 
            while (myArrayList.get(tmpLow).getRawData() < pivot && tmpLow < indexHi) {
                tmpLow++;
            }
            // Decrement
            while (myArrayList.get(tmpHi).getRawData() > pivot && tmpHi > indexLow) {
                tmpHi--;
            }
            // Swap
            if (tmpLow <= tmpHi) {
                AgeDemographics temp = myArrayList.get(tmpLow);
                myArrayList.set(tmpLow, myArrayList.get(tmpHi));
                myArrayList.set(tmpHi, temp);
    
                // Move onto the next values in the ArrayList
                tmpLow++;
                tmpHi--;
            }
        }
        // Repeat quicksort for the sub array smaller than the pivot
        if (indexLow < tmpHi) {
            AgeDemographicSortRawAsc(myArrayList, indexLow, tmpHi);
        }

        // Repeat quicksort for the sub array greater than the pivot
        if (tmpLow < indexHi) {
            AgeDemographicSortRawAsc(myArrayList, tmpLow, indexHi);
        }
    }

    // Sorts Descendingly the age demographic data with respect to the raw values
    public void AgeDemographicSortRawDesc(ArrayList<AgeDemographics> myArrayList, int indexLow, int indexHi) {
        int tmpLow, tmpHi;
        double pivot;
    
        // Stores the min and max array index so that it can be used to increment or decrement through each value in the array
        tmpLow = indexLow; 
        tmpHi = indexHi;
    
        pivot = myArrayList.get(((indexLow + indexHi) / 2)).getRawData(); // Sets the pivot to the middle element in the array
    
        while (tmpLow <= tmpHi) {
            // Increment 
            while (myArrayList.get(tmpLow).getRawData() > pivot && tmpLow < indexHi) {
                tmpLow++;
            }
            // Decrement
            while (myArrayList.get(tmpHi).getRawData() < pivot && tmpHi > indexLow) {
                tmpHi--;
            }
            // Swap
            if (tmpLow <= tmpHi) {
                AgeDemographics temp = myArrayList.get(tmpLow);
                myArrayList.set(tmpLow, myArrayList.get(tmpHi));
                myArrayList.set(tmpHi, temp);
    
                // Move onto the next values in the ArrayList
                tmpLow++;
                tmpHi--;
            }
        }
        // Repeat quicksort for the sub-array greater than the pivot
        if (indexLow < tmpHi) {
            AgeDemographicSortRawDesc(myArrayList, indexLow, tmpHi);
        }
    
        // Repeat quicksort for the sub-array smaller than the pivot
        if (tmpLow < indexHi) {
            AgeDemographicSortRawDesc(myArrayList, tmpLow, indexHi);
        }
    }

    // Sorts Ascendingly the LTHC data with respect to the raw values
    public void LTHCSortRawAsc(ArrayList<LTHC> myArrayList, int indexLow, int indexHi) {
        int tmpLow, tmpHi;
        double pivot;

        // Stores the min and max array index so that it can be used to increment or decrement through each value in the array
        tmpLow = indexLow; 
        tmpHi = indexHi;

        pivot = myArrayList.get(((indexLow + indexHi) / 2)).getRawData(); // Sets the pivot to the middle element in the array

        while (tmpLow <= tmpHi) {
            // Increment 
            while (myArrayList.get(tmpLow).getRawData() < pivot && tmpLow < indexHi) {
                tmpLow++;
            }
            // Decrement
            while (myArrayList.get(tmpHi).getRawData() > pivot && tmpHi > indexLow) {
                tmpHi--;
            }
            // Swap
            if (tmpLow <= tmpHi) {
                LTHC temp = myArrayList.get(tmpLow);
                myArrayList.set(tmpLow, myArrayList.get(tmpHi));
                myArrayList.set(tmpHi, temp);
    
                // Move onto the next values in the ArrayList
                tmpLow++;
                tmpHi--;
            }
        }
        // Repeat quicksort for the sub array smaller than the pivot
        if (indexLow < tmpHi) {
            LTHCSortRawAsc(myArrayList, indexLow, tmpHi);
        }

        // Repeat quicksort for the sub array greater than the pivot
        if (tmpLow < indexHi) {
            LTHCSortRawAsc(myArrayList, tmpLow, indexHi);
        }
    }

    // Sorts Descendingly the LTHC data with respect to the raw values
    public void LTHCSortRawDesc(ArrayList<LTHC> myArrayList, int indexLow, int indexHi) {
        int tmpLow, tmpHi;
        double pivot;
    
        // Stores the min and max array index so that it can be used to increment or decrement through each value in the array
        tmpLow = indexLow; 
        tmpHi = indexHi;
    
        pivot = myArrayList.get(((indexLow + indexHi) / 2)).getRawData(); // Sets the pivot to the middle element in the array
    
        while (tmpLow <= tmpHi) {
            // Increment 
            while (myArrayList.get(tmpLow).getRawData() > pivot && tmpLow < indexHi) {
                tmpLow++;
            }
            // Decrement
            while (myArrayList.get(tmpHi).getRawData() < pivot && tmpHi > indexLow) {
                tmpHi--;
            }
            // Swap
            if (tmpLow <= tmpHi) {
                LTHC temp = myArrayList.get(tmpLow);
                myArrayList.set(tmpLow, myArrayList.get(tmpHi));
                myArrayList.set(tmpHi, temp);
    
                // Move onto the next values in the ArrayList
                tmpLow++;
                tmpHi--;
            }
        }
        // Repeat quicksort for the sub-array greater than the pivot
        if (indexLow < tmpHi) {
            LTHCSortRawDesc(myArrayList, indexLow, tmpHi);
        }
    
        // Repeat quicksort for the sub-array smaller than the pivot
        if (tmpLow < indexHi) {
            LTHCSortRawDesc(myArrayList, tmpLow, indexHi);
        }
    }

    // Updates value of age to be used in dataset for SQL queries
    public void updateAgeCategory(ArrayList<String> myArrayList) {
        for (int i = 0; i < myArrayList.size(); i++) {
            switch (myArrayList.get(i)) {
                case "0-4":
                    myArrayList.set(i, "_0_4");
                    break;
                case "5-9":
                    myArrayList.set(i, "_5_9");
                    break;
                case "10-14":
                    myArrayList.set(i, "_10_14");
                    break;
                case "15-19":
                    myArrayList.set(i, "_15_19");
                    break;
                case "20-24":
                    myArrayList.set(i, "_20_24");
                    break;
                case "25-29":
                    myArrayList.set(i, "_25_29");
                    break;
                case "30-34":
                    myArrayList.set(i, "_30_34");
                    break;
                case "35-39":
                    myArrayList.set(i, "_35_39");
                    break;
                case "40-44":
                    myArrayList.set(i, "_40_44");
                    break;
                case "45-49":
                    myArrayList.set(i, "_45_49");
                    break;
                case "50-54":
                    myArrayList.set(i, "_50_54");
                    break;
                case "55-59":
                    myArrayList.set(i, "_55_59");
                    break;
                case "60-64":
                    myArrayList.set(i, "_60_64");
                    break;
                case "65+":
                    myArrayList.set(i, "_65_yrs_ov");
                    break;
            }
        }
    }

    // Updates value of conditions to be used in dataset for SQL queries
    public void updateHealthConditions(ArrayList<String> myArrayList) {
        for (int i = 0; i < myArrayList.size(); i++) {
            switch (myArrayList.get(i)) {
                case "Arthritis":
                    myArrayList.set(i, "arthritis");
                    break;
                case "Asthma":
                    myArrayList.set(i, "asthma");
                    break;
                case "Cancer":
                    myArrayList.set(i, "cancer");
                    break;
                case "Dementia":
                    myArrayList.set(i, "dementia");
                    break;
                case "Kidney Disease":
                    myArrayList.set(i, "kidneydisease");
                    break;
                case "Lung Category":
                    myArrayList.set(i, "lungcategory");
                    break;
                case "Mental Health":
                    myArrayList.set(i, "mentalhealth");
                    break;
                case "Stroke":
                    myArrayList.set(i, "stroke");
                    break;
                case "Other":
                    myArrayList.set(i, "other");
                    break;
            }
        }
    }

    // Updates value of school year to be used in dataset for SQL queries
    public void updateSchoolYearRange(ArrayList<String> myArrayList) {
        for (int i = 0; i < myArrayList.size(); i++) {
            switch (myArrayList.get(i)) {
                case "Didn't go to school":
                    myArrayList.set(i, "did_not_go_to_school");
                    break;
                case "Year 8 and below":
                    myArrayList.set(i, "y8_below");
                    break;
                case "Year 9":
                    myArrayList.set(i, "y9_equivalent");
                    break;
                case "Year 10":
                    myArrayList.set(i, "y10_equivalent");
                    break;
                case "Year 11":
                    myArrayList.set(i, "y11_equivalent");
                    break;
                case "Year 12":
                    myArrayList.set(i, "y12_equivalent");
                    break;
            }
        }
    } 

    // Updates value of non-school completion categories to be used in dataset for SQL queries
    public void updateNonSchool(ArrayList<String> myArrayList) {
        for (int i = 0; i < myArrayList.size(); i++) {
            switch (myArrayList.get(i)) {
                case "diploma":
                    myArrayList.set(i, "Advanced Diploma and Diploma Level");
                    break;
                case "bachelor":
                    myArrayList.set(i, "Bachelor Degree Level");
                    break;
                case "I-II-lvl":
                    myArrayList.set(i, "Certificate I & II Level");
                    break;
                case "III-IV-lvl":
                    myArrayList.set(i, "Certificate III & IV Level");
                    break;
                case "post-grad":
                    myArrayList.set(i, "Postgraduate Degree Level, Graduate Diploma and Graduate Certificate Level");
                    break;
            }
        }
    }

    // Returns 2016 age demographic data based on user input for age range, sex and indigenous status
    public ArrayList<AgeDemographics> getAgeDemographicsData2016(ArrayList<String> selectedAgeRange, String sex, String indigStatus) {
        ArrayList<AgeDemographics> ageDemographics = new ArrayList<>();
        String query = "";
        Connection connection = null;

        switch (sex) {
            case "Male":
                sex = "m";
                break;
            case "Female":
                sex = "f";
                break;
        }

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!sex.equalsIgnoreCase("both")) {
                query = "SELECT id.lga_code, id.indigenous_status, id.sex, id.age_category, id.count AS Population " + 
                        "FROM IndigenousDemographics id " + 
                        "WHERE id.lga_year = 2016 AND id.sex = " + "'" + sex + "'" + " AND id.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedAgeRange.size() > 1) {
                    query += "(";  
                    for (int i = 0; i < selectedAgeRange.size(); i++) {
                        query += "id.age_category = '" + selectedAgeRange.get(i) + "'";
                        if (i != selectedAgeRange.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";  
                } 
                else {
                    query += "id.age_category = '" + selectedAgeRange.get(0) + "'";
                }
            }
            else {
                // User chooses both male and female
                query = "SELECT id.lga_code, id.indigenous_status, id.sex, id.age_category, id.count AS Population " + 
                        "FROM IndigenousDemographics id " + 
                        "WHERE id.lga_year = 2016 AND (id.sex = 'm' OR id.sex = 'f') AND id.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedAgeRange.size() > 1) {
                    query += "(";  
                    for (int i = 0; i < selectedAgeRange.size(); i++) {
                        query += "id.age_category = '" + selectedAgeRange.get(i) + "'";
                        if (i != selectedAgeRange.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";  
                } 
                else {
                    query += "id.age_category = '" + selectedAgeRange.get(0) + "'";
                }
            }

            ResultSet results = statement.executeQuery(query); // Stores LGA code, indigenous status, age category and raw data for each LGA

            while (results.next()) {
                int LGACode = results.getInt("lga_code");
                String status = results.getString("indigenous_status");
                String SEX = results.getString("sex");
                String ageRange = results.getString("age_category");
                int population = results.getInt("Population");

                // Remove underscore at the start
                ageRange = ageRange.substring(1);

                AgeDemographics AD = new AgeDemographics(LGACode, status, SEX, ageRange, population);
                ageDemographics.add(AD);
            }  

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ageDemographics;
    }

    // Returns 2021 age demographic data based on user input for age range, sex and indigenous status
    public ArrayList<AgeDemographics> getAgeDemographicsData2021(ArrayList<String> selectedAgeRange, String sex, String indigStatus) {
        ArrayList<AgeDemographics> ageDemographics = new ArrayList<>();
        String query = "";
        Connection connection = null;

        switch (sex) {
            case "Male":
                sex = "m";
                break;
            case "Female":
                sex = "f";
                break;
        }

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!sex.equalsIgnoreCase("both")) {
                query = "SELECT id.lga_code, id.indigenous_status, id.sex, id.age_category, id.count AS Population " + 
                        "FROM IndigenousDemographics id " + 
                        "WHERE id.lga_year = 2021 AND id.sex = " + "'" + sex + "'" + " AND id.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedAgeRange.size() > 1) {
                    query += "(";  
                    for (int i = 0; i < selectedAgeRange.size(); i++) {
                        query += "id.age_category = '" + selectedAgeRange.get(i) + "'";
                        if (i != selectedAgeRange.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";  
                } 
                else {
                    query += "id.age_category = '" + selectedAgeRange.get(0) + "'";
                }
            }
            else {
                // User chooses both male and female
                query = "SELECT id.lga_code, id.indigenous_status, id.sex, id.age_category, id.count AS Population " + 
                        "FROM IndigenousDemographics id " + 
                        "WHERE id.lga_year = 2021 AND (id.sex = 'm' OR id.sex = 'f') AND id.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedAgeRange.size() > 1) {
                    query += "(";  
                    for (int i = 0; i < selectedAgeRange.size(); i++) {
                        query += "id.age_category = '" + selectedAgeRange.get(i) + "'";
                        if (i != selectedAgeRange.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";  
                } 
                else {
                    query += "id.age_category = '" + selectedAgeRange.get(0) + "'";
                }
            }

            ResultSet results = statement.executeQuery(query); // Stores LGA code, indigenous status, age category and raw data for each LGA

            while (results.next()) {
                int LGACode = results.getInt("lga_code");
                String status = results.getString("indigenous_status");
                String SEX = results.getString("sex");
                String ageRange = results.getString("age_category");
                int population = results.getInt("Population");

                // Remove underscore at the start
                ageRange = ageRange.substring(1);

                AgeDemographics AD = new AgeDemographics(LGACode, status, SEX, ageRange, population);
                ageDemographics.add(AD);
            }  

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ageDemographics;
    }

    // Returns long term health conditions data based on user input for health conditions, sex and indigenous status
    public ArrayList<LTHC> getLTHCData(ArrayList<String> selectedConditions, String sex, String indigStatus) {
        ArrayList<LTHC> LTHCs = new ArrayList<>();
        String query = "";
        Connection connection = null;

        switch (sex) {
            case "Male":
                sex = "m";
                break;
            case "Female":
                sex = "f";
                break;
        }

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!sex.equalsIgnoreCase("both")) {
                query = "SELECT l.lga_code, l.indigenous_status, l.sex, l.condition, SUM(l.count) AS Population " + 
                        "FROM LTHC l " + 
                        "WHERE l.sex = " + "'" + sex + "'" + " AND l.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedConditions.size() > 1) {
                    query += "(";  
                    for (int i = 0; i < selectedConditions.size(); i++) {
                        query += "l.condition = '" + selectedConditions.get(i) + "'";
                        if (i != selectedConditions.size() - 1) {
                        query += " OR ";
                        }
                    }
                    query += ") "; 
                } 
                else {
                    query += "l.condition = '" + selectedConditions.get(0) + "' ";
                }
                query += "GROUP BY l.lga_code, l.condition, l.sex;";
            }
            else {
                // User chooses both male and female
                query = "SELECT l.lga_code, l.indigenous_status, l.sex, l.condition, SUM(l.count) AS Population " + 
                "FROM LTHC l " + 
                "WHERE (l.sex = 'm' OR l.sex = 'f') AND l.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedConditions.size() > 1) {
                    query += "(";  
                    for (int i = 0; i < selectedConditions.size(); i++) {
                        query += "l.condition = '" + selectedConditions.get(i) + "'";
                        if (i != selectedConditions.size() - 1) {
                        query += " OR ";
                        }
                    }
                    query += ") "; 
                } 
                else {
                    query += "l.condition = '" + selectedConditions.get(0) + "' ";
                }
                query += "GROUP BY l.lga_code, l.condition, l.sex;";
            }

            ResultSet results = statement.executeQuery(query); // Stores LGA code, indigenous status, age category and raw data for each LGA

            while (results.next()) {
                int LGACode = results.getInt("lga_code");
                String status = results.getString("indigenous_status");
                String SEX = results.getString("sex");
                String condition = results.getString("condition");
                int population = results.getInt("Population");

                LTHC lthc = new LTHC(LGACode, status, SEX, condition, population);
                LTHCs.add(lthc);
            }  

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return LTHCs;
    }

    // Returns 2016 highest school year data based on user input for category, sex, and indigenous status
    public ArrayList<HighestSchoolYear> getHighestSchoolYearData2016(ArrayList<String> selectedCategory, String sex, String indigStatus) {
        ArrayList<HighestSchoolYear> HSY = new ArrayList<>();
        String query = "";
        Connection connection = null;

        switch (sex) {
            case "Male":
                sex = "m";
                break;
            case "Female":
                sex = "f";
                break;
        }

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!sex.equalsIgnoreCase("both")) {
                query = "SELECT h.lga_code, h.indigenous_status, h.sex, h.category, h.count AS Population " + 
                        "FROM HighestSchoolYear h " + 
                        "WHERE h.lga_year = 2016 AND h.sex = " + "'" + sex + "'" + " AND h.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedCategory.size() > 1) {
                    query += "(";
                    for (int i = 0; i < selectedCategory.size(); i++) {
                        query += "h.category = '" + selectedCategory.get(i) + "'";
                        if (i < selectedCategory.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";
                } 
                else {
                    query += "h.category = '" + selectedCategory.get(0) + "'";
                }
            }
            else {
                // User chooses both male and female
                query = "SELECT h.lga_code, h.indigenous_status, h.sex, h.category, h.count AS Population " + 
                        "FROM HighestSchoolYear h " + 
                        "WHERE h.lga_year = 2016 AND (h.sex = 'm' OR h.sex = 'f') AND h.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedCategory.size() > 1) {
                    query += "(";
                    for (int i = 0; i < selectedCategory.size(); i++) {
                        query += "h.category = '" + selectedCategory.get(i) + "'";
                        if (i < selectedCategory.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";
                } 
                else {
                    query += "h.category = '" + selectedCategory.get(0) + "'";
                }
            }

            ResultSet results = statement.executeQuery(query); // Stores LGA code, indigenous status, age category and raw data for each LGA

            while (results.next()) {
                int LGACode = results.getInt("lga_code");
                String status = results.getString("indigenous_status");
                String SEX = results.getString("sex");
                String category = results.getString("category");
                int population = results.getInt("Population");

                HighestSchoolYear hsy = new HighestSchoolYear(LGACode, status, SEX, category, population);
                HSY.add(hsy);
            }  

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return HSY;
    }

    // Returns 2021 highest school year data based on user input for category, sex, and indigenous status
    public ArrayList<HighestSchoolYear> getHighestSchoolYearData2021(ArrayList<String> selectedCategory, String sex, String indigStatus) {
        ArrayList<HighestSchoolYear> HSY = new ArrayList<>();
        String query = "";
        Connection connection = null;

        switch (sex) {
            case "Male":
                sex = "m";
                break;
            case "Female":
                sex = "f";
                break;
        }

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!sex.equalsIgnoreCase("both")) {
                query = "SELECT h.lga_code, h.indigenous_status, h.sex, h.category, h.count AS Population " + 
                        "FROM HighestSchoolYear h " + 
                        "WHERE h.lga_year = 2021 AND h.sex = " + "'" + sex + "'" + " AND h.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedCategory.size() > 1) {
                    query += "(";
                    for (int i = 0; i < selectedCategory.size(); i++) {
                        query += "h.category = '" + selectedCategory.get(i) + "'";
                        if (i < selectedCategory.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";
                } 
                else {
                    query += "h.category = '" + selectedCategory.get(0) + "'";
                }
            }
            else {
                // User chooses both male and female
                query = "SELECT h.lga_code, h.indigenous_status, h.sex, h.category, h.count AS Population " + 
                        "FROM HighestSchoolYear h " + 
                        "WHERE h.lga_year = 2021 AND (h.sex = 'm' OR h.sex = 'f') AND h.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedCategory.size() > 1) {
                    query += "(";
                    for (int i = 0; i < selectedCategory.size(); i++) {
                        query += "h.category = '" + selectedCategory.get(i) + "'";
                        if (i < selectedCategory.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";
                } 
                else {
                    query += "h.category = '" + selectedCategory.get(0) + "'";
                }
            }

            ResultSet results = statement.executeQuery(query); // Stores LGA code, indigenous status, age category and raw data for each LGA

            while (results.next()) {
                int LGACode = results.getInt("lga_code");
                String status = results.getString("indigenous_status");
                String SEX = results.getString("sex");
                String category = results.getString("category");
                int population = results.getInt("Population");

                HighestSchoolYear hsy = new HighestSchoolYear(LGACode, status, SEX, category, population);
                HSY.add(hsy);
            }  

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return HSY;
    }

    // Returns 2016 non-school completion data based on user input for category, sex and indigenous status
    public ArrayList<NonSchoolCompletion> getNonSchoolCategoryData2016(ArrayList<String> selectedCategory, String sex, String indigStatus) {
        ArrayList<NonSchoolCompletion> nonSchools = new ArrayList<>();
        String query = "";
        Connection connection = null;

        switch (sex) {
            case "Male":
                sex = "m";
                break;
            case "Female":
                sex = "f";
                break;
        }

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!sex.equalsIgnoreCase("both")) {
                query = "SELECT n.lga_code, n.indigenous_status, n.sex, n.category, n.count AS Population " + 
                        "FROM NonSchoolCompletion n " + 
                        "WHERE n.lga_year = 2016 AND n.sex = " + "'" + sex + "'" + " AND n.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedCategory.size() > 1) {
                    query += "(";  
                    for (int i = 0; i < selectedCategory.size(); i++) {
                        query += "n.category = '" + selectedCategory.get(i) + "'";
                        if (i != selectedCategory.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";
                } 
                else {
                    query += "n.category = '" + selectedCategory.get(0) + "'";
                }
            }
            else {
                // User chooses both male and female
                query = "SELECT n.lga_code, n.indigenous_status, n.sex, n.category, n.count AS Population " + 
                        "FROM NonSchoolCompletion n " + 
                        "WHERE n.lga_year = 2016 AND (n.sex = 'm' OR n.sex = 'f') AND n.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedCategory.size() > 1) {
                    query += "(";  
                
                    for (int i = 0; i < selectedCategory.size(); i++) {
                        query += "n.category = '" + selectedCategory.get(i) + "'";
                
                        if (i != selectedCategory.size() - 1) {
                            query += " OR ";
                        }
                    }
                
                    query += ")";  
                } 
                else {
                    query += "n.category = '" + selectedCategory.get(0) + "'";
                }
            }

            ResultSet results = statement.executeQuery(query); 

            while (results.next()) {
                int LGACode = results.getInt("lga_code");
                String status = results.getString("indigenous_status");
                String SEX = results.getString("sex");
                String category = results.getString("category");
                int population = results.getInt("Population");

                NonSchoolCompletion nonSchool = new NonSchoolCompletion(LGACode, status, SEX, category, population);
                nonSchools.add(nonSchool);
            }  

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return nonSchools;
    }

    // Returns 2021 non-school completion data based on user input for category, sex and indigenous status
    public ArrayList<NonSchoolCompletion> getNonSchoolCategoryData2021(ArrayList<String> selectedCategory, String sex, String indigStatus) {
        ArrayList<NonSchoolCompletion> nonSchools = new ArrayList<>();
        String query = "";
        Connection connection = null;

        switch (sex) {
            case "Male":
                sex = "m";
                break;
            case "Female":
                sex = "f";
                break;
        }

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (!sex.equalsIgnoreCase("both")) {
                query = "SELECT n.lga_code, n.indigenous_status, n.sex, n.category, n.count AS Population " + 
                        "FROM NonSchoolCompletion n " + 
                        "WHERE n.lga_year = 2021 AND n.sex = " + "'" + sex + "'" + " AND n.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedCategory.size() > 1) {
                    query += "(";  
                    for (int i = 0; i < selectedCategory.size(); i++) {
                        query += "n.category = '" + selectedCategory.get(i) + "'";
                        if (i != selectedCategory.size() - 1) {
                            query += " OR ";
                        }
                    }
                    query += ")";
                } 
                else {
                    query += "n.category = '" + selectedCategory.get(0) + "'";
                }
            }
            else {
                // User chooses both male and female
                query = "SELECT n.lga_code, n.indigenous_status, n.sex, n.category, n.count AS Population " + 
                        "FROM NonSchoolCompletion n " + 
                        "WHERE n.lga_year = 2021 AND (n.sex = 'm' OR n.sex = 'f') AND n.indigenous_status = " + "'" + indigStatus + "' AND ";
                if (selectedCategory.size() > 1) {
                    query += "(";  
                
                    for (int i = 0; i < selectedCategory.size(); i++) {
                        query += "n.category = '" + selectedCategory.get(i) + "'";
                
                        if (i != selectedCategory.size() - 1) {
                            query += " OR ";
                        }
                    }
                
                    query += ")";  
                } 
                else {
                    query += "n.category = '" + selectedCategory.get(0) + "'";
                }
            }

            ResultSet results = statement.executeQuery(query); 

            while (results.next()) {
                int LGACode = results.getInt("lga_code");
                String status = results.getString("indigenous_status");
                String SEX = results.getString("sex");
                String category = results.getString("category");
                int population = results.getInt("Population");

                NonSchoolCompletion nonSchool = new NonSchoolCompletion(LGACode, status, SEX, category, population);
                nonSchools.add(nonSchool);
            }  

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return nonSchools;
    }
}