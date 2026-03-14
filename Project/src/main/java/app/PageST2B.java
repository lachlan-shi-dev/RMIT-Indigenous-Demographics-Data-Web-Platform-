package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ognl.enhance.OrderedReturn;

import java.net.IDN;
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
public class PageST2B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2B.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 2.2</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <section class = 'header2'>
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
                    School/Non-School Completion 2021
                    <h2>
                        (Subtask 2B)
                    </h2>
                </h1>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class='content'>";

        // Add HTML for the page content
        // Let user choose between age or health category in a dropdown box
        html = html + """
            <h2>Filter</h2>
            <div class='guidelines'>
                <h4><em>Mandatory Fields will have '*' next to them.</em></h4>
            </div>
            <hr>
            <form action='/page2B.html' method='post'>
                <div class='lvl2-group-form'>
                    <label for='category_drop'>Select View*:</label>
                    <select id='category_drop' name='schoolNon'>
                        <option selected disabled></option>
                        <option>School Completion</option>
                        <option>Non School Completion</option>
                    </select>
                    <br>
                    <div class='whitespace-xs'>
                    </div>
            """;
        
        // Users can choose to view data by LGAs but must not have selected a State/Territory option
        html = html + """
                    <label for='LGA_drop'>Select to view by LGAs*:</label>
                    <select id='LGA_drop' name='lga'>
                        <option selected></option>
                        <option>View by LGAs</option>
                    </select>
                    <br>
            """;

            html += """
                    <h3> OR </h3>
                    """;

        // Users can choose their preferred State/Territory but must not have selected the "View by LGAs" option
        html = html + """
                    <label for='state_terr__drop'>Select a State/Territory*:</label>
                    <select id='state_terr_drop' name='stateTer'>
                        <option selected></option>
            """;

        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<String> stateTerrNames = jdbc.getStateTerrNames(); // List of state abbreviations

        // Populate state_terr_drop dropdown with a list of states abbreviations
        for (String stateTerr : stateTerrNames) {
            html = html + "<option>" + stateTerr + "</option>";
        }

        // Close select div
        html = html + """
                    </select>
                    <br>
            """;

        // Let user choose the indigenous status {indig, non-indig, indig-not-stated} through a dropdown
        html = html + """
                    <div class='whitespace-xs'>
                    </div>
                    <label for='indig_drop'>Indigenous Status*:</label>
                    <select id='indig_drop' name='indig'>
                        <option selected disabled></option>
                        <option>indig</option>
                        <option>non_indig</option>
                        <option>indig_not_stated</option>
                    </select>
                    <br>
            """;

        // Let user choose between raw or proportional data in dropdown box
        html = html + """
                    <label for='datatype_drop'>Select Granularity View*:</label>
                    <select id='datatype_drop' name='datatype'>
                        <option selected disabled></option>
                        <option>Raw Values</option>
                        <option>Proportional Values</option>
                    </select>
                    <br>
            """;
        
        // Let user choose between Ascending or Descending sort in dropdown box
        html = html + """
                    <label for='sort_drop'>Sort By*:</label>
                    <select id='sort_drop' name='sortorder'>
                        <option selected disabled></option>
                        <option>Default</option>
                        <option>Ascending (Lowest at top)</option>
                        <option>Descending (Highest at top)</option>
                    </select>
                    <br>
            """;

        // Close lvl2-group-form div
        html = html + """
                </div>
            """;

        // Submit to Search
        html = html + """
                <button type='submit' class='btn btn-primary'>Search</button>
            """;
        
        // Close the form
        html = html + """
            </form>
            """;
        
        
        // Processing form submission
        if (context.method().equals("POST")) {
        String COMPLETION2B = context.formParam("schoolNon");
        String VIEW2B = context.formParam("lga");
        String INDIG2B = context.formParam("indig");
        String STATETER2B = context.formParam("stateTer");
        String DATATYPE2B = context.formParam("datatype");
        String ORDER2B = context.formParam("sortorder");

        ArrayList<SchoolNon> Slgas = null;
        ArrayList<SchoolNon> SstateTer = null;
        ArrayList<NonSchool> NSlgas = null;
        ArrayList<NonSchool> NSstateTer = null;

        boolean error = false;

        if (COMPLETION2B == null) {
            html = html + "<ul>";
            html = html + "<li>'Select View' was left empty. Please select an option.</li>";
            html = html + "</ul>";
            error = true;
        }

        if (!error && (VIEW2B.equals("") && STATETER2B.equals(""))) {
            html = html + "<ul>";
            html = html + "<li>Both LGA and State/Territory Not Selected, please try again</li>";
            html = html + "</ul>";
            error = true;
        }

        if (!error && (!VIEW2B.equals("") && !STATETER2B.equals(""))) {
            html = html + "<ul>";
            html = html + "<li>LGA View and State/Territory View were selected. Please select an option from one field ONLY.</li>";
            html = html + "</ul>";
            error = true;
        }

        // School & LGA
        if (!error && COMPLETION2B.equals("School Completion") && !VIEW2B.equals("") && STATETER2B.equals("")) {
            if (INDIG2B == null) {
                html = html + "<ul>";
                html = html + "<li>'Indigenous Status' was left empty. Please select an option.</li>";
                html = html + "</ul>";
                error = true;
            }
            if (DATATYPE2B == null) {
                html = html + "<ul>";
                html = html + "<li>'Select Granularity View' was left empty. Please select an option.</li>";
                html = html + "</ul>";
                error = true;
            }
            // Raw Values
            if (error && DATATYPE2B.equals("Raw Values") && ORDER2B != null) {
                Slgas = jdbc.getSchoolLGAs(INDIG2B);
                if (ORDER2B.equals("Default")) {
                    html = html + "<h1>School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw Data)</th>
                            </tr>
                        """;
                    for (int i = 0; i < Slgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + Slgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getCategory() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else if (ORDER2B.equals("Ascending (Lowest at top)")) {
                    html = html + "<h1>School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw Data)</th>
                            </tr>
                        """;
                    jdbc.SchoolSortRawAsc(Slgas, 0, Slgas.size() - 1);
                    for (int i = 0; i < Slgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + Slgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getCategory() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else {
                    html = html + "<h1>School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank></th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw Data)</th>
                            </tr>
                        """;
                    jdbc.SchoolSortRawDesc(Slgas, 0, Slgas.size() - 1);
                    for (int i = 0; i < Slgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + Slgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getCategory() + "</td>";
                        html = html + "<td>" + Slgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                }
            } 

        // Proportional
        else if (ORDER2B != null && DATATYPE2B != null && INDIG2B != null) {
            Slgas = jdbc.getSchoolLGAs(INDIG2B);
                    if (ORDER2B.equals("Default")) {
                        html = html + "<h1>School Completion for " + INDIG2B + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Completion Category</th>
                            """;
                        html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                        html = html + """
                                </tr>
                            """;
                        for (int i = 0; i < Slgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + Slgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + Slgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + Slgas.get(i).getCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f",Slgas.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (ORDER2B.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Age Demographics for " + INDIG2B + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Completion Category</th>
                            """;
                        html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                        html = html + """
                                </tr>
                            """;
                        jdbc.SchoolSortPropAsc(Slgas, 0, Slgas.size() - 1);
                        for (int i = 0; i < Slgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + Slgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + Slgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + Slgas.get(i).getCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f",Slgas.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Age Demographics for " + INDIG2B + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                            """;
                        html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                        html = html + """
                                </tr>
                            """;
                        jdbc.SchoolSortPropDesc(Slgas, 0, Slgas.size() - 1);
                        for (int i = 0; i < Slgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + Slgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + Slgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + Slgas.get(i).getCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f",Slgas.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    }
                }
                    else {
                        // Error: No sort was selected
                        html = html + "<ul>";
                        html = html + "<li>No sort was selected, please try again</li>";
                        html = html + "</ul>";
                        error = true;
                    }
                }
            
        // Non School
        else if (!error && COMPLETION2B.equals("Non School Completion") && !VIEW2B.equals("") && STATETER2B.equals("")) {
            if (INDIG2B == null) {
                html = html + "<ul>";
                html = html + "<li>'Indigenous Status' was left empty. Please select an option.</li>";
                html = html + "</ul>";
                error = true;
            }
            if (DATATYPE2B == null) {
                html = html + "<ul>";
                html = html + "<li>'Select Granularity View' was left empty. Please select an option.</li>";
                html = html + "</ul>";
                error = true;
            }
        
            if (!error && DATATYPE2B.equals("Raw Values") && ORDER2B != null && !VIEW2B.equals("") && ORDER2B != null) {
                NSlgas = jdbc.getNonSchoolLGAs(INDIG2B);
                if (ORDER2B.equals("Default")) {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw Data)</th>
                            </tr>
                        """;                               
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else if (ORDER2B.equals("Ascending (Lowest at top)")) {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw Data)</th>
                            </tr>
                        """;    
                    jdbc.NonSchoolSortRawAsc(NSlgas, 0, NSlgas.size() - 1);                           
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw Data)</th>
                            </tr>
                        """;   
                    jdbc.NonSchoolSortRawDesc(NSlgas, 0, NSlgas.size() - 1);                            
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                }
            } 
            // Proportional values
            else if (ORDER2B != null && DATATYPE2B != null && INDIG2B != null) {
                NSlgas = jdbc.getNonSchoolLGAs(INDIG2B);
                if (ORDER2B.equals("Default")) {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;                            
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", NSlgas.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else if (ORDER2B.equals("Ascending (Lowest at top)")) {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;  
                    jdbc.NonSchoolSortPropAsc(NSlgas, 0 , NSlgas.size() - 1);                          
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", NSlgas.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;
                    jdbc.NonSchoolSortPropDesc(NSlgas, 0, NSlgas.size() - 1);                            
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", NSlgas.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                }
            } 
            else {
                html = html + "<ul>";
                html = html + "<li>No options were selected, please try again.</li>";
                html = html + "</ul>";
                error = true;
            }
        }
        
        // State/Territory & School
        if (!error && !STATETER2B.equals("") && COMPLETION2B.equals("School Completion") && VIEW2B.equals("")) {
            if (INDIG2B == null) {
                html = html + "<ul>";
                html = html + "<li>'Indigenous Status' was left empty. Please select an option.</li>";
                html = html + "</ul>";
                error = true;
            }
            if (DATATYPE2B == null) {
                html = html + "<ul>";
                html = html + "<li>'Select Granularity View' was left empty. Please select an option.</li>";
                html = html + "</ul>";
                error = true;
            }
        
            // Raw
            if (!error && DATATYPE2B.equals("Raw Values") && ORDER2B != null) {
                SstateTer = jdbc.getSchoolstateTer(INDIG2B, STATETER2B);
                if (ORDER2B.equals("Default")) {
                    html = html + "<h1>School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw data)</th>
                            </tr>
                        """;                            
                    for (int i = 0; i < SstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getCategory() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else if (ORDER2B.equals("Ascending (Lowest at top)")) {
                    html = html + "<h1>School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw data)</th>
                            </tr>
                        """;
                    jdbc.SchoolSortRawAsc(SstateTer, 0, SstateTer.size() - 1);                            
                    for (int i = 0; i < SstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getCategory() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else {
                    html = html + "<h1>School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw data)</th>
                            </tr>
                        """;    
                    jdbc.SchoolSortRawDesc(SstateTer, 0, SstateTer.size() - 1);                       
                    for (int i = 0; i < SstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getCategory() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                }
            } 
            // Proportional
            else if (ORDER2B != null) {
                SstateTer = jdbc.getSchoolstateTer(INDIG2B, STATETER2B);
                if (ORDER2B.equals("Default")) {
                    html = html + "<h1>School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;                         
                    for (int i = 0; i < SstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", SstateTer.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else if (ORDER2B.equals("Ascending (Lowest at top)")) {
                    html = html + "<h1>School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;     
                    jdbc.SchoolSortPropAsc(SstateTer, 0, SstateTer.size() - 1);                    
                    for (int i = 0; i < SstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", SstateTer.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else {
                    html = html + "<h1>School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;   
                    jdbc.SchoolSortPropDesc(SstateTer, 0, SstateTer.size() - 1);                      
                    for (int i = 0; i < SstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + SstateTer.get(i).getCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", SstateTer.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                }
            } 
            else {
                html = html + "<ul>";
                html = html + "<li>No sort was selected, please try again</li>";
                html = html + "</ul>";
                error = true;
            }
        }
        // Non School
        else if (!error && !STATETER2B.equals("") && COMPLETION2B.equals("Non School Completion") && VIEW2B.equals("")) {
            if (INDIG2B == null) {
                html = html + "<ul>";
                html = html + "<li>'Indigenous Status' was left empty. Please select an option.</li>";
                html = html + "</ul>";
                error = true;
            }
            if (DATATYPE2B == null) {
                html = html + "<ul>";
                html = html + "<li>'Select Granularity View' was left empty. Please select an option.</li>";
                html = html + "</ul>";
                error = true;
            }
            if (!error && DATATYPE2B.equals("Raw Values") && ORDER2B != null) {
                NSlgas = jdbc.getNonSchoolStateTer(INDIG2B, STATETER2B);
                if (ORDER2B.equals("Default")) {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw data)</th>
                            </tr>
                        """;                            
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else if (ORDER2B.equals("Ascending (Lowest at top)")) {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw data)</th>
                            </tr>
                        """; 
                    jdbc.NonSchoolSortRawAsc(NSlgas, 0, NSlgas.size() - 1);                           
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                                <th>Population (Raw data)</th>
                            </tr>
                        """;  
                    jdbc.NonSchoolSortRawDesc(NSlgas, 0, NSlgas.size() - 1);                          
                    for (int i = 0; i < NSlgas.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + NSlgas.get(i).getRawData() + "</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                }
            } 
            // Proportional values
            else if (ORDER2B != null) {
                NSstateTer = jdbc.getNonSchoolStateTer(INDIG2B, STATETER2B);
                if (ORDER2B.equals("Default")) {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;                            
                    for (int i = 0; i < NSstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", NSstateTer.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else if (ORDER2B.equals("Ascending (Lowest at top)")) {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;    
                    jdbc.NonSchoolSortPropAsc(NSstateTer, 0, NSstateTer.size() - 1);                        
                    for (int i = 0; i < NSstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", NSstateTer.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                } 
                else {
                    html = html + "<h1>Non School Completion for " + INDIG2B + " data from " + STATETER2B + "</h1>";
                    html = html + """
                        <table class='lvl2-table'>
                            <tr>
                                <th>Rank</th>
                                <th>LGA Code</th>
                                <th>State/Territory</th>
                                <th>Indigenous Status</th>
                                <th>Completion Category</th>
                        """;   
                    html = html + "<th>Population out of all the " + INDIG2B + "</th>";      
                    html = html + """
                            </tr>
                        """;  
                    jdbc.NonSchoolSortPropDesc(NSstateTer, 0, NSstateTer.size() - 1);                          
                    for (int i = 0; i < NSstateTer.size(); i++) {
                        html = html + "<tr>";
                        html = html + "<td>" + (i + 1) + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getLGACode() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getStateAbbr() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getIndigStatus() + "</td>";
                        html = html + "<td>" + NSstateTer.get(i).getComCategory() + "</td>";
                        html = html + "<td>" + String.format("%.2f", NSstateTer.get(i).getProportionalData()) + "%</td>";
                        html = html + "</tr>";
                    }
                    html = html + "</table>";
                }
            } 
            else {
                // Error: No sort was selected
                html = html + "<ul>";
                html = html + "<li>No sort was selected, please try again</li>";
                html = html + "</ul>";
                error = true;
            }
        }
    }
        // Close form div
        html += "</div>";

        // Close Content div
        html = html + "</div>";

        // Footer
        html = html + """
            <div class='footer'>
                <p>School/Non-School Completion 2021 (By: Aniketh Yella)</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

}