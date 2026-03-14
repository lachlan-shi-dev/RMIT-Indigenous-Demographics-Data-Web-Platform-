package app;

import java.util.ArrayList;

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
public class PageMission implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/mission.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // To ensure no character mismatches
        html = html + "<meta charset=\"UTF-8\">";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Our Mission</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <section class='header4'>
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
                    <a href='/'><img src='flag.png' class='top-image' height='180'></a>
                    Our Mission
                </h1>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class='content'>";

        // Mission statement 
        html = html + """
            <div class="mission-container">
                <div class="mission-text">
                    <h2>Our Purpose</h2>
                    <p>
                        <em>Our purpose is to empower the public with factual insights into the disparities between the Indigenous and Non-Indigenous communities 
                        in Australia, using data from the 2016 and 2021 Censuses. We are dedicated to providing unbiased statistics and informed perspectives
                        on key areas such as health and education, fostering informed decision-making in the Voice to Parliament Referendum.</em>
                    </p>
                </div>
                <div class="mission-image">
                    <img src="happy_indigenous_children.avif">
                </div>
            </div>
            """;
        
        // Personas
        html = html + """
            <br>
            <br>
            <br>
            <hr>
            <h1 class='header-mission'>Who this site is built for</h1>
            <hr>
            """;

        ArrayList<Persona> personas = getPersonas();
        
        // Displays image of personas to the screen
        html = html + """
            <div class="persona-container">
                <div class="persona">
                    <img src="persona1.jpg">
                </div>
                <div class="persona">
                    <img src="persona2.jpg">
                </div>
                <div class="persona">
                    <img src="persona3.jpg">
                </div>
            </div>
            """;

        // Create persona tables
        html = html + """
            <div class="table-container">
            <table class="persona-table">
            <tr>
            """;

        // Table for persona 1
        html = html +       "<td>Name</td>";
        html = html +       "<td>" + personas.get(0).getName() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Attributes</td>";
        html = html +       "<td>" + personas.get(0).getAttributes() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Needs</td>";
        html = html +       "<td>" + personas.get(0).getNeeds() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Goals</td>";
        html = html +       "<td>" + personas.get(0).getGoals() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Skills/Experience</td>";
        html = html +       "<td>" + personas.get(0).getSkills() + "</td>";
        html = html +  "</tr>";
        html = html + "</table>";

        // table for persona 2
        html = html + "<table class='persona-table'>";
        html = html +   "<tr>";
        html = html +       "<td>Name</td>";
        html = html +       "<td>" + personas.get(1).getName() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Attributes</td>";
        html = html +       "<td>" + personas.get(1).getAttributes() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Needs</td>";
        html = html +       "<td>" + personas.get(1).getNeeds() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Goals</td>";
        html = html +       "<td>" + personas.get(1).getGoals() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Skills/Experience</td>";
        html = html +       "<td>" + personas.get(1).getSkills() + "</td>";
        html = html +  "</tr>";
        html = html + "</table>";

        // table for persona 3
        html = html + "<table class='persona-table'>";
        html = html +   "<tr>";
        html = html +       "<td>Name</td>";
        html = html +       "<td>" + personas.get(2).getName() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Attributes</td>";
        html = html +       "<td>" + personas.get(2).getAttributes() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Needs</td>";
        html = html +       "<td>" + personas.get(2).getNeeds() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Goals</td>";
        html = html +       "<td>" + personas.get(2).getGoals() + "</td>";
        html = html +  "</tr>";
        html = html +   "<tr>";
        html = html +       "<td>Skills/Experience</td>";
        html = html +       "<td>" + personas.get(2).getSkills() + "</td>";
        html = html +  "</tr>";
        html = html + "</table>";

        // Close table-container div
        html = html + """
            </div>
            """;

        // How the site is used
        html = html + """
            <br>
            <hr>
            <h1 class='header-mission'>How to use our site</h1>
            <hr>
            <p>
                <em>Our site is controlled by one simple navigation menu positioned at the top of the page. The navigation menu allows you to go back to the homepage,
                    explore the mission behind our website, find out more information about age and health staistics or school and non school completion, explore the
                    gap between indigenous and non-indigenous data, and compare the similarity between LGAs.</em>
            </p>
            <br>
            """;

        // Contributors of the website (AKA Students)
        html = html + """
            <hr>
            <h1 class='header-mission'>Contributors</h1>
            <hr>
            <ol>
            """;
        
        ArrayList<String> studentNames = getStudentName();
        ArrayList<String> studentIDs = getStudentID();

        // Lists the student names and IDs to the page
        html = html + "<li><em>" + studentNames.get(0) + " (" + studentIDs.get(1) + ")</em></li>";
        html = html + "<li><em>" + studentNames.get(1) + " (" + studentIDs.get(0) + ")</em></li>";
        html = html + "</ol>";

        // Close Content div
        html = html + "</div>";

        // Footer
        html = html + """
            <div class='footer'>
                <p>Information to Voice on Parliament Vote Mission page (By: Lachlan Shi)</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

    // Gets the 3 personas from the database
    public ArrayList<Persona> getPersonas() {
        ArrayList<Persona> personas = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT p.name, pa.description, pa.goals, pa.needs, pa.skills, p.image_file_path " + 
                    "FROM Persona p " + 
                    "JOIN PersonaAttributes pa ON p.persona_id = pa.persona_id;";

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String name = results.getString("name");
                String description = results.getString("description");
                String needs = results.getString("needs");
                String goals = results.getString("goals");
                String skills = results.getString("skills");
                String imageFilePath = results.getString("image_file_path");

                Persona persona = new Persona(name, description, needs, goals, skills, imageFilePath);
                personas.add(persona);
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
        return personas;
    }

    // Gets student names from database
    public ArrayList<String> getStudentName() {
        ArrayList<String> studentNames = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT s.student_name FROM Student s;";

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String studentName = results.getString("student_name");
                studentNames.add(studentName);
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
        return studentNames;
    }

    // Gets student IDs from database
    public ArrayList<String> getStudentID() {
        ArrayList<String> studentIDs = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT s.student_id FROM Student s;";

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String studentID = results.getString("student_id");
                studentIDs.add(studentID);
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
        return studentIDs;
    }
}
