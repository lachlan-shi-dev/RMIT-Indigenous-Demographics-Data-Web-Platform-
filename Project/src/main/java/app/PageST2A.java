package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageST2A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2A.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 2.1</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <section class='header5'>
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
                    Age and Health Statistics 2021 (2A)
                </h1>
            </div>
        """;

        // Let user choose between age or health category in a dropdown box
        html = html + """
            <h3>Filter</h3>
            <div class='guidelines'>
                <h5><em>*If you select to view by LGAs, you CANNOT also select a State/Territory</em></h5>
                <h5><em>*If you select a State/Territory, you CANNOT also select to view by LGAs</em></h5>
            </div>
            <hr>
            <form action='/page2A.html' method='post'>
                <div class='lvl2-group-form'>
                    <label for='category_drop'>Select information to view</label>
                    <select id='category_drop' name='category_drop'>
                        <option selected disabled></option>
                        <option>Age Demographics</option>
                        <option>Long Term Health Conditions</option>
                    </select>
                    <br>
            """;
        
        // Users can choose to view data by LGAs but must not have selected a State/Territory option
        html = html + """
                    <label for='LGA_drop'>Select to view by LGAs</label>
                    <select id='LGA_drop' name='LGA_drop'>
                        <option selected></option>
                        <option>View by LGAs</option>
                    </select>
                    <br>
            """;

        // Users can choose their preferred State/Territory but must not have selected the "View by LGAs" option
        html = html + """
                    <label for='state_terr__drop'>Select a State/Territory</label>
                    <select id='state_terr_drop' name='state_terr_drop'>
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
                    <label for='indig_drop'>Indigenous Status</label>
                    <select id='indig_drop' name='indig_drop'>
                        <option selected disabled></option>
                        <option>indig</option>
                        <option>non_indig</option>
                        <option>indig_not_stated</option>
                    </select>
                    <br>
            """;

        // Let user choose between raw or proportional data in dropdown box
        html = html + """
                    <label for='datatype_drop'>Select to view as Raw or Proportional Values</label>
                    <select id='datatype_drop' name='datatype_drop'>
                        <option selected disabled></option>
                        <option>Raw Values</option>
                        <option>Proportional Values</option>
                    </select>
                    <br>
            """;
        
        // Let user choose between Ascending or Descending sort in dropdown box
        html = html + """
                    <label for='sort_drop'>Sort Ascending or Descending Order (Sorted by Proportional Values or Raw Values)</label>
                    <select id='sort_drop' name='sort_drop'>
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

        // Submit the form
        html = html + """
                <button type='submit' class='btn btn-primary'>Apply</button>
            """;
        
        // Close the form
        html = html + """
            </form>
            """;

        // Only uses the values after the form submission
        if (context.method().equals("POST")) {
            String selectedInfoToView = context.formParam("category_drop");     // Selected information to view (Age or Health)
            String selectedViewByLGA = context.formParam("LGA_drop");           // Selected to view by LGA
            String selectedStateTerr = context.formParam("state_terr_drop");    // Selected State/Territory
            String selectedIndigStatus = context.formParam("indig_drop");       // Selected Indigenous Status
            String selectedRawPropValue = context.formParam("datatype_drop");   // Selected Raw or Proportional Values
            String selectedSort = context.formParam("sort_drop");               // Selected sort option (Desc or Asc or default)
            
            ArrayList<AgeDemographics> ADlgas = null;
            ArrayList<AgeDemographics> ADstateTerr = null;
            ArrayList<LTHC> LTHClgas = null;
            ArrayList<LTHC> LTHCstateTerr = null;

            // Initialize to false
            boolean hasError = false; 

            if (selectedInfoToView == null) {
                // User didn't select a category
                html = html + "<ul>";
                html = html + "<li>No information to view was selected, please try again</li>";
                html = html + "</ul>";
                hasError = true;
            }
            
            // Error: Both LGA and State/Territory not selected
            if (!hasError && (selectedViewByLGA.equals("") && selectedStateTerr.equals(""))) {
                html = html + "<ul>";
                html = html + "<li>Both LGA and State/Territory Not Selected, please try again</li>";
                html = html + "</ul>";
                hasError = true;
            }

            // Error: Both LGA and State/Territory not selected
            if (!hasError && (!selectedViewByLGA.equals("") && !selectedStateTerr.equals(""))) {
                html = html + "<ul>";
                html = html + "<li>Both LGA and State/Territory Selected, please try again</li>";
                html = html + "</ul>";
                hasError = true;
            }

            // Error: Indigenous status not selected
            if (!hasError && selectedIndigStatus == null) {
                html = html + "<ul>";
                html = html + "<li>Indigenous status not selected, please try again</li>";
                html = html + "</ul>";
                hasError = true;
            }

            // Error: No raw or proportional values selected
            if (selectedRawPropValue == null) {
                html = html + "<ul>";
                html = html + "<li>No raw or proportional values selected, please try again</li>";
                html = html + "</ul>";
                hasError = true;
            }

            // if LGA option has been selected and Age Demographic data to view
            if (!hasError && selectedInfoToView.equals("Age Demographics") && !selectedViewByLGA.equals("") && selectedStateTerr.equals("")) {
                // Checks each sorting option for raw values
                if (!hasError && selectedRawPropValue.equals("Raw Values") && selectedSort != null) {
                    ADlgas = jdbc.getAgeDemographicsAsLGAs(selectedIndigStatus); 
                    if (selectedSort.equals("Default")) {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                                    <th>Population (Raw Data)</th>
                                </tr>
                            """;
                        for (int i = 0; i < ADlgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (selectedSort.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                                    <th>Population (Raw Data)</th>
                                </tr>
                            """;
                        jdbc.AgeDemographicSortRawAsc(ADlgas, 0, ADlgas.size() - 1);
                        for (int i = 0; i < ADlgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank></th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                                    <th>Population (Raw Data)</th>
                                </tr>
                            """;
                        jdbc.AgeDemographicSortRawDesc(ADlgas, 0, ADlgas.size() - 1);
                        for (int i = 0; i < ADlgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    }
                } 
                // Proportional values
                else if (selectedSort != null && selectedRawPropValue != null && selectedIndigStatus != null) {
                    ADlgas = jdbc.getAgeDemographicsAsLGAs(selectedIndigStatus);
                    if (selectedSort.equals("Default")) {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                            """;
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;
                        for (int i = 0; i < ADlgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f",ADlgas.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (selectedSort.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                            """;
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;
                        jdbc.AgeDemographicSortPropAsc(ADlgas, 0, ADlgas.size() - 1);
                        for (int i = 0; i < ADlgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f",ADlgas.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                            """;
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;
                        jdbc.AgeDemographicSortPropDesc(ADlgas, 0, ADlgas.size() - 1);
                        for (int i = 0; i < ADlgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADlgas.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f",ADlgas.get(i).getProportionalData()) + "%</td>";
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
                    hasError = true;
                }
            }
            // Long term Health issues category
            else if (!hasError && selectedInfoToView.equals("Long Term Health Conditions") && !selectedViewByLGA.equals("") && selectedStateTerr.equals("")) {
                if (!hasError && selectedRawPropValue.equals("Raw Values") && selectedSort != null && !selectedViewByLGA.equals("") && selectedSort != null) {
                    LTHClgas = jdbc.getLTHCAsLGAs(selectedIndigStatus);
                    if (selectedSort.equals("Default")) {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                                    <th>Population (Raw Data)</th>
                                </tr>
                            """;                               
                        for (int i = 0; i < LTHClgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getCondition() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (selectedSort.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                                    <th>Population (Raw Data)</th>
                                </tr>
                            """;    
                        jdbc.LTHCSortRawAsc(LTHClgas, 0, LTHClgas.size() - 1);                           
                        for (int i = 0; i < LTHClgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getCondition() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                                    <th>Population (Raw Data)</th>
                                </tr>
                            """;   
                        jdbc.LTHCSortRawDesc(LTHClgas, 0, LTHClgas.size() - 1);                            
                        for (int i = 0; i < LTHClgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getCondition() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    }
                } 
                // Proportional values
                else if (selectedSort != null && selectedRawPropValue != null && selectedIndigStatus != null) {
                    LTHClgas = jdbc.getLTHCAsLGAs(selectedIndigStatus);
                    if (selectedSort.equals("Default")) {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;                            
                        for (int i = 0; i < LTHClgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getCondition() + "</td>";
                            html = html + "<td>" + String.format("%.2f", LTHClgas.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (selectedSort.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;  
                        jdbc.LTHCSortPropAsc(LTHClgas, 0 , LTHClgas.size() - 1);                          
                        for (int i = 0; i < LTHClgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getCondition() + "</td>";
                            html = html + "<td>" + String.format("%.2f", LTHClgas.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;
                        jdbc.LTHCSortPropDesc(LTHClgas, 0, LTHClgas.size() - 1);                            
                        for (int i = 0; i < LTHClgas.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHClgas.get(i).getCondition() + "</td>";
                            html = html + "<td>" + String.format("%.2f", LTHClgas.get(i).getProportionalData()) + "%</td>";
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
                    hasError = true;
                }
            }

            // if state/territory has been selected for Age Demographics
            if (!hasError && !selectedStateTerr.equals("") && selectedInfoToView.equals("Age Demographics") && selectedViewByLGA.equals("")) {
                if (selectedIndigStatus == null) {
                    // Error: No indigenous status selected
                    html = html + "<ul>";
                    html = html + "<li>No indig status selected, please try again</li>";
                    html = html + "</ul>";
                    hasError = true;
                }
                if (selectedRawPropValue == null) {
                    // Error: No raw or proportional values selected
                    html = html + "<ul>";
                    html = html + "<li>No raw or proportional values selected, please try again</li>";
                    html = html + "</ul>";
                    hasError = true;
                }
            
                // Raw values
                if (!hasError && selectedRawPropValue.equals("Raw Values") && selectedSort != null) {
                    ADstateTerr = jdbc.getAgeDemographicsAsStateTerr(selectedIndigStatus, selectedStateTerr);
                    if (selectedSort.equals("Default")) {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                                    <th>Population (Raw data)</th>
                                </tr>
                            """;                            
                        for (int i = 0; i < ADstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (selectedSort.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                                    <th>Population (Raw data)</th>
                                </tr>
                            """;
                        jdbc.AgeDemographicSortRawAsc(ADstateTerr, 0, ADstateTerr.size() - 1);                            
                        for (int i = 0; i < ADstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                                    <th>Population (Raw data)</th>
                                </tr>
                            """;    
                        jdbc.AgeDemographicSortRawDesc(ADstateTerr, 0, ADstateTerr.size() - 1);                       
                        for (int i = 0; i < ADstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    }
                } 
                // Proportional values
                else if (selectedSort != null) {
                    ADstateTerr = jdbc.getAgeDemographicsAsStateTerr(selectedIndigStatus, selectedStateTerr);
                    if (selectedSort.equals("Default")) {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;                         
                        for (int i = 0; i < ADstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f", ADstateTerr.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (selectedSort.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;     
                        jdbc.AgeDemographicSortPropAsc(ADstateTerr, 0, ADstateTerr.size() - 1);                    
                        for (int i = 0; i < ADstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f", ADstateTerr.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Age Demographics for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Age Category</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;   
                        jdbc.AgeDemographicSortPropDesc(ADstateTerr, 0, ADstateTerr.size() - 1);                      
                        for (int i = 0; i < ADstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + ADstateTerr.get(i).getAgeCategory() + "</td>";
                            html = html + "<td>" + String.format("%.2f", ADstateTerr.get(i).getProportionalData()) + "%</td>";
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
                    hasError = true;
                }
            }
            // Long term Health conditions category
            else if (!hasError && !selectedStateTerr.equals("") && selectedInfoToView.equals("Long Term Health Conditions") && selectedViewByLGA.equals("")) {
                if (selectedIndigStatus == null) {
                    // Error: No indigenous status selected
                    html = html + "<ul>";
                    html = html + "<li>No indig status selected, please try again</li>";
                    html = html + "</ul>";
                    hasError = true;
                }
                if (selectedRawPropValue == null) {
                    // Error: No raw or proportional values selected
                    html = html + "<ul>";
                    html = html + "<li>No raw or proportional values selected, please try again</li>";
                    html = html + "</ul>";
                    hasError = true;
                }
            
                if (!hasError && selectedRawPropValue.equals("Raw Values") && selectedSort != null) {
                    LTHCstateTerr = jdbc.getLTHCAsStateTerr(selectedIndigStatus, selectedStateTerr);
                    if (selectedSort.equals("Default")) {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                                    <th>Population (Raw data)</th>
                                </tr>
                            """;                            
                        for (int i = 0; i < LTHCstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getCondition() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (selectedSort.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                                    <th>Population (Raw data)</th>
                                </tr>
                            """; 
                        jdbc.LTHCSortRawAsc(LTHCstateTerr, 0, LTHCstateTerr.size() - 1);                           
                        for (int i = 0; i < LTHCstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getCondition() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                                    <th>Population (Raw data)</th>
                                </tr>
                            """;  
                        jdbc.LTHCSortRawDesc(LTHCstateTerr, 0, LTHCstateTerr.size() - 1);                          
                        for (int i = 0; i < LTHCstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getCondition() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getRawData() + "</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    }
                } 
                // Proportional values
                else if (selectedSort != null) {
                    LTHCstateTerr = jdbc.getLTHCAsStateTerr(selectedIndigStatus, selectedStateTerr);
                    if (selectedSort.equals("Default")) {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;                            
                        for (int i = 0; i < LTHCstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getCondition() + "</td>";
                            html = html + "<td>" + String.format("%.2f", LTHCstateTerr.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else if (selectedSort.equals("Ascending (Lowest at top)")) {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;    
                        jdbc.LTHCSortPropAsc(LTHCstateTerr, 0, LTHCstateTerr.size() - 1);                        
                        for (int i = 0; i < LTHCstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getCondition() + "</td>";
                            html = html + "<td>" + String.format("%.2f", LTHCstateTerr.get(i).getProportionalData()) + "%</td>";
                            html = html + "</tr>";
                        }
                        html = html + "</table>";
                    } 
                    else {
                        html = html + "<h1>Long Term Health Conditions for " + selectedIndigStatus + " data from " + selectedStateTerr + "</h1>";
                        html = html + """
                            <table class='lvl2-table'>
                                <tr>
                                    <th>Rank</th>
                                    <th>LGA Code</th>
                                    <th>State/Territory</th>
                                    <th>Indigenous Status</th>
                                    <th>Condition</th>
                            """;   
                        html = html + "<th>Population out of all the " + selectedIndigStatus + "</th>";      
                        html = html + """
                                </tr>
                            """;  
                        jdbc.LTHCSortPropDesc(LTHCstateTerr, 0, LTHCstateTerr.size() - 1);                          
                        for (int i = 0; i < LTHCstateTerr.size(); i++) {
                            html = html + "<tr>";
                            html = html + "<td>" + (i + 1) + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getLGACode() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getStateAbbr() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getIndigStatus() + "</td>";
                            html = html + "<td>" + LTHCstateTerr.get(i).getCondition() + "</td>";
                            html = html + "<td>" + String.format("%.2f", LTHCstateTerr.get(i).getProportionalData()) + "%</td>";
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
                    hasError = true;
                }
            }

        }

        // Footer
        html = html + """
            <div class='footer'>
                <p>Age and Health statistics page (By: Lachlan Shi)</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }
}