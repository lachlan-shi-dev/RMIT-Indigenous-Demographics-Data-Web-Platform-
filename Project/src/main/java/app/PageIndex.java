package app;

import java.util.ArrayList;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageIndex implements Handler {

    JDBCConnection jdbc = new JDBCConnection();

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Header information
        html = html + "<head>" + 
               "<title>Homepage</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <section class = 'header'>
                    <div class='topnav'>
                            <a href='/'>Homepage</a>
                            <a href='mission.html'>Our Mission</a>
                            <a href='page2A.html'>Age and Health Statistics 2021 (2A)</a>
                            <a href='page2B.html'>School/Non-School Completion 2021 (2B)</a>
                            <a href='page3A.html'>Indigenous/Non-Indigenous Gap statistics (3A)</a>
                            <a href='page3B.html'>LGA Similar Characteristics (3B)</a>
                    </div>
            </section>
        """;

        // Add header content block
        html = html + """
            <div class='subtitle'>
                <h1>
                    <img src='flag.png' class='top-image' height='180'>
                    Homepage
                </h1>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class='content'>";

        // Add HTML for the page content
        html = html + """
            <div class='whitespace-small'>
            </div>
                <div class = 'image1'>
                    <img src='dance.png' class='feature-image' height='500' width='770'>
                </div>
                <div class = 'text1'>
                    <h2>
                    Information on Voice to Parliament
                    </h2>
                    <p>
                    The purpose of the website is to serve as an informative and unbiased platform for individuals voting 
                    in the Australian Referendum on "The Voice to Parliament." This web application aims to empower users 
                    with essential data and statistics drawn from the last two Australian Censuses (2016 and 2021) to facilitate 
                    a deeper understanding of the gap between Indigenous and Non-Indigenous people in key areas like health, 
                    education, employment, housing, and incarceration. By providing comprehensive and respectful information, 
                    the website caters to a diverse range of users, allowing them to access both high-level summaries and in-depth
                    data analysis. Ultimately, the goal is to equip users with the knowledge they need to make informed decisions
                    in the referendum and contribute to the ongoing discussion about Indigenous issues in Australia.
                    </p>
                </div>
            """;

        // Adding Title to Sample Data Section
        html = html + """
            <div class = 'tax'>
                <h1> Sample Data for LGAs in 2016 vs 2021 </h1>
                </div>
                """;

        // Get the total population of census data
        int totalPopulation = jdbc.getTotalPopulationForYear(2016);

        // Add HTML for the 2016 LGA list
        html = html + """
            <div class = 'container2'>
            <div class = 'hoverimage'>
            <img src = "art.png" height='600' width='600'>
                """ 
            +
            "<desc> <ul>";
        

        // Finally we can print the total population
        html = html + "<p>Total Population Surveyed:</p>" + "<p>" + totalPopulation + "</p>";

        // Finish the List HTML
        html = html + "</ul> </desc>";

        // Close first hoverimage div
        html = html + "<titl>Total Population Surveyed in 2016 and 2021</titl>  </div>";

        // Add HTML for second card
        html = html + """
            <div class = 'hoverimage'>
            <img src = "art2.png" height='600' width='600'>
                """
                +
                "<desc> <ul>"    
                ;
        // Add content for the 2nd card from database
        Map<Integer, Integer> totalLGAsByYear = jdbc.getTotalLGAsByYear();
        for (Map.Entry<Integer, Integer> entry : totalLGAsByYear.entrySet()) {
            int year = entry.getKey();
            int totalLGAsYear = entry.getValue();
            html = html + "<li>" + year + ": " + totalLGAsYear + " LGAs</li>";
        }

        // Finish with title of card and close the div for 2nd
        html = html + "</ul> </desc>" + " <titl>Total Population by State/Territory in 2016 and 2021</titl> </div>";
        
        //HTML for third card
        html = html + """
            <div class = 'hoverimage'>
            <img src = "art3.jpeg" height='600' width='600'>
                """
                +
                "<desc>"    
                ;
        // Add content for the 3rd card from database
        Map<String, Map<Integer, Integer>> totalPopulationByStateAndYear = jdbc.getTotalPopulationByStateAndYear();
        for (Map.Entry<String, Map<Integer, Integer>> entry : totalPopulationByStateAndYear.entrySet()) {
            String state = entry.getKey();
            Map<Integer, Integer> yearData = entry.getValue();
        html = html + "<h3>" + state + "</h3>";
        for (Map.Entry<Integer, Integer> yearEntry : yearData.entrySet()) {
            int year = yearEntry.getKey();
            int totalPopulation1 = yearEntry.getValue();
            html = html + "<p>" + year + ": " + totalPopulation1 + "</p>";
            }
        }
        // Finish with title of card and close the div for 3rd
        html = html + "</desc>" + " <titl>Total Population by State/Territory in 2016 and 2021</titl> </div> </div>";

        // Close Content div
        html = html + "</div>";

        // Footer
        html = html + """
            <div class='footer'>
                <p>Information to Voice on Parliament Vote Homepage (By: Aniketh Yella)</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }
}

    /**
     * Get the names of the LGAs in the database.
     */ /*
    

    public ArrayList<String> getLGAs2021() {
        // Create the ArrayList of LGA objects to return
        ArrayList<String> lgas2 = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM LGA WHERE year='2021'";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String name17  = results.getString("name");

                // Add the lga object to the array
                lgas2.add(name17);
            }

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
        return lgas2;
    } */
