package app;

import java.util.ArrayList;
import java.util.List;

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
public class PageST3A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 3.1</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <section class='header6'>
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
                    Indigenous/Non-Indigenous Gap statistics (3A)
                </h1>
            </div>
            """;

        // Add HTML for the page content
        html = html + """
            <center>
                <h2>The Gap</h2>
            </center>
            <h3>Filter</h3>
            <hr>
            """;

        // Create form and let users choose between each dataset they want to view
        html = html + """
            <form action='/page3A.html' method='post'>
                <div class='lvl2-group-form'>
                    <label for='dataset_drop'>Select Datasets (1-4)</label>
                    <select id='dataset_drop' name='dataset_drop'>
                        <option selected disabled></option>
                        <option>Age Demographics</option>
                        <option>Long Term Health Conditions</option>
                        <option>Highest School Year</option>
                        <option>Non-School Completion</option>
                    </select>
                    <br>
            """;

        // Only allow certain filters accessable for a particular dataset selected
        html = html + """
            <script>
                document.getElementById('dataset_drop').addEventListener('change', function() {
                    var ageRangeCheckboxes = document.querySelectorAll("input[name='age_range']");
                    var healthCheckboxes = document.querySelectorAll("input[name='health_condition']");
                    var schoolYearRangeCheckboxes = document.querySelectorAll("input[name='school_year_range']");
                    var nonSchoolCheckboxes = document.querySelectorAll("input[name='non_school']");
                    if (this.value === 'Age Demographics') {
                        ageRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = false;
                        });
                        healthCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        schoolYearRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        nonSchoolCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                    } 
                    else if (this.value === 'Long Term Health Conditions') {
                        ageRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        healthCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = false;
                        });
                        schoolYearRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        nonSchoolCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                    }
                    else if (this.value === 'Highest School Year') {
                        ageRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        healthCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        schoolYearRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = false;
                        });
                        nonSchoolCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                    }
                    else if (this.value === 'Non-School Completion') {
                        ageRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        healthCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        schoolYearRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        nonSchoolCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = false;
                        });
                    }
                    else {
                        ageRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        healthCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        schoolYearRangeCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                        nonSchoolCheckboxes.forEach(function(checkbox) {
                            checkbox.disabled = true;
                        });
                    }
                });
            </script>
        """;
        
        // User selects indig status
        html =  html + """
                <label for='status_drop'>Indigenous Status</label>
                <select id='status_drop' name='status_drop'>
                    <option selected disabled></option>
                    <option>indig</option>
                    <option>non_indig</option>
                    <option>indig_not_stated</option>
                </select>
                <br>
            """;
        
        // User selects sex
        html = html + """
                <label for='sex_drop'>Sex</label>
                <select id='sex_drop' name='sex_drop'>
                    <option selected disabled></option>
                    <option>Male</option>
                    <option>Female</option>
                    <option>Both</option>
                </select>
                <br>
            """;

        // User selects age range
        html = html + """
        <fieldset>
            <legend>Select Age Range(s)</legend>
            <input type="checkbox" id="age_0-4" name="age_range" value="0-4" disabled>
            <label for="age_0-4">0-4</label><br>

            <input type="checkbox" id="age_5-9" name="age_range" value="5-9" disabled>
            <label for="age_5-9">5-9</label><br>

            <input type="checkbox" id="age_10-14" name="age_range" value="10-14" disabled>
            <label for="age_10-14">10-14</label><br>

            <input type="checkbox" id="age_15-19" name="age_range" value="15-19" disabled>
            <label for="age_15-19">15-19</label><br>

            <input type="checkbox" id="age_20-24" name="age_range" value="20-24" disabled>
            <label for="age_20-24">20-24</label><br>

            <input type="checkbox" id="age_25-29" name="age_range" value="25-29" disabled>
            <label for="age_25-29">25-29</label><br>

            <input type="checkbox" id="age_30-34" name="age_range" value="30-34" disabled>
            <label for="age_30-34">30-34</label><br>

            <input type="checkbox" id="age_35-39" name="age_range" value="35-39" disabled>
            <label for="age_35-39">35-39</label><br>

            <input type="checkbox" id="age_40-44" name="age_range" value="40-44" disabled>
            <label for="age_40-44">40-44</label><br>

            <input type="checkbox" id="age_45-49" name="age_range" value="45-49" disabled>
            <label for="age_45-49">45-49</label><br>

            <input type="checkbox" id="age_50-54" name="age_range" value="50-54" disabled>
            <label for="age_50-54">50-54</label><br>

            <input type="checkbox" id="age_55-59" name="age_range" value="55-59" disabled>
            <label for="age_55-59">55-59</label><br>

            <input type="checkbox" id="age_60-64" name="age_range" value="60-64" disabled>
            <label for="age_60-64">60-64</label><br>

            <input type="checkbox" id="age_65+" name="age_range" value="65+" disabled>
            <label for="age_65+">65+</label><br>
        </fieldset>
        <br>
            """;

        // User selects health condition
        html = html + """
            <fieldset>
                <legend>Select Health Condition(s)</legend>
                <input type="checkbox" id="arthritis" name="health_condition" value="arthritis" disabled>
                <label for="arthritis">Arthritis</label><br>

                <input type="checkbox" id="asthma" name="health_condition" value="asthma" disabled>
                <label for="asthma">Asthma</label><br>

                <input type="checkbox" id="cancer" name="health_condition" value="cancer" disabled>
                <label for="cancer">Cancer</label><br>

                <input type="checkbox" id="dementia" name="health_condition" value="dementia" disabled>
                <label for="dementia">Dementia</label><br>

                <input type="checkbox" id="diabetes" name="health_condition" value="diabetes" disabled>
                <label for="diabetes">Diabetes</label><br>

                <input type="checkbox" id="kidneydisease" name="health_condition" value="kidneydisease" disabled>
                <label for="kidneydisease">Kidney Disease</label><br>

                <input type="checkbox" id="lungcategory" name="health_condition" value="lungcategory" disabled>
                <label for="lungcategory">Lung Category</label><br>

                <input type="checkbox" id="mentalhealth" name="health_condition" value="mentalhealth" disabled>
                <label for="mentalhealth">Mental Health</label><br>

                <input type="checkbox" id="stroke" name="health_condition" value="stroke" disabled>
                <label for="stroke">Stroke</label><br>

                <input type="checkbox" id="other" name="health_condition" value="other" disabled>
                <label for="other">Other</label><br>
            </fieldset>
            <br>
            """;

        // User selects school year range
        html = html + """
            <fieldset>
                <legend>Select School Year Range(s)</legend>
                <input type="checkbox" id="did_not_go_to_school" name="school_year_range" value="did_not_go_to_school" disabled>
                <label for="did_not_go_to_school">Didn't go to school</label><br>

                <input type="checkbox" id="y8_below" name="school_year_range" value="y8_below" disabled>
                <label for="y8_below">Year 8 and below</label><br>

                <input type="checkbox" id="y9_equivalent" name="school_year_range" value="y9_equivalent" disabled>
                <label for="y9_equivalent">Year 9</label><br>

                <input type="checkbox" id="y10_equivalent" name="school_year_range" value="y10_equivalent" disabled>
                <label for="y10_equivalent">Year 10</label><br>

                <input type="checkbox" id="y11_equivalent" name="school_year_range" value="y11_equivalent" disabled>
                <label for="y11_equivalent">Year 11</label><br>
                
                <input type="checkbox" id="y12_equivalent" name="school_year_range" value="y12_equivalent" disabled>
                <label for="y12_equivalent">Year 12</label><br>
            </fieldset>
            <br>
            """;
        
        // User selects Non-School completion categories
        html = html + """
            <fieldset>
                <legend>Select Non-School Completion Category(s)</legend>
                <input type="checkbox" id="diploma" name="non_school" value="diploma" disabled>
                <label for="diploma">Advanced Diploma and Diploma Level</label><br>

                <input type="checkbox" id="bachelor" name="non_school" value="bachelor" disabled>
                <label for="bachelor">Bachelor Degree Level</label><br>

                <input type="checkbox" id="I-II-lvl" name="non_school" value="I-II-lvl" disabled>
                <label for="I-II-lvl">Certificate I & II Level</label><br>

                <input type="checkbox" id="III-IV-lvl" name="non_school" value="III-IV-lvl" disabled>
                <label for="III-IV-lvl">Certificate III & IV Level</label><br>
                
                <input type="checkbox" id="post-grad" name="non_school" value="post-grad" disabled>
                <label for="post-grad">Postgraduate Degree Level, Graduate Diploma and Graduate Certificate Level</label><br>
            </fieldset>
            <br>
            """;


        // Submit dataset form
        html = html + """
                    <button type='submit' name='dataset_button' class='btn btn-primary'>Apply</button>
                </div>
            </form>
            """;

        JDBCConnection jdbc = new JDBCConnection();

        // Collect user input after form submission
        if (context.method().equals("POST")) {
            String selectedDataset = context.formParam("dataset_drop");                                 // Selected dataset to view (Age, healh, education)
            String selectedIndigStatus = context.formParam("status_drop");                              // Selected indig status
            String selectedSex = context.formParam("sex_drop");                                         // Selected sex
            List<String> selectedAgeRangesList = context.formParams("age_range");                       // Selected age range
            List<String> selectedHealthConditionsList = context.formParams("health_condition");         // Selected health conditions
            List<String> selectedSchoolYearRangeList = context.formParams("school_year_range");         // Selected school year ranges (yr8-yr12)
            List<String> selectedNonSchoolCategoryList = context.formParams("non_school");              // Selected non school categories (bachelor, diploma, etc)

            ArrayList<String> selectedAgeRanges = new ArrayList<>(selectedAgeRangesList);
            ArrayList<String> selectedHealthConditions = new ArrayList<>(selectedHealthConditionsList);
            ArrayList<String> selectedSchoolYearRange = new ArrayList<>(selectedSchoolYearRangeList);
            ArrayList<String> selectedNonSchoolCategory = new ArrayList<>(selectedNonSchoolCategoryList);

            ArrayList<AgeDemographics> ageDemoDataset2016 = null;
            ArrayList<AgeDemographics> ageDemoDataset2021 = null;
            ArrayList<LTHC> LTHCDataset = null;
            ArrayList<HighestSchoolYear> highSchoolYearDataset2016 = null;
            ArrayList<HighestSchoolYear> highSchoolYearDataset2021 = null;
            ArrayList<NonSchoolCompletion> nonSchoolCompletionDataset2016 = null;
            ArrayList<NonSchoolCompletion> nonSchoolCompletionDataset2021 = null;

            // Initialize to false
            boolean hasError = false;

            if (selectedDataset == null) {
                // User didn't select a category
                html = html + "<ul>";
                html = html + "<li>No dataset was selected, please try again</li>";
                html = html + "</ul>";
                hasError = true;
            }

            if (!hasError && selectedIndigStatus == null) {
                // User didn't select a category
                html = html + "<ul>";
                html = html + "<li>No indigenous status was selected, please try again</li>";
                html = html + "</ul>";
                hasError = true;
            }

            if (!hasError && selectedSex == null) {
                html = html + "<ul>";
                html = html + "<li>No sex was selected, please try again</li>";
                html = html + "</ul>";
                hasError = true;
            }
            
            if (!hasError) {
                switch (selectedDataset) {
                    case "Age Demographics":
                        if (!selectedAgeRangesList.isEmpty()) {
                            jdbc.updateAgeCategory(selectedAgeRanges);
                            // 2016 Data
                            ageDemoDataset2016 = jdbc.getAgeDemographicsData2016(selectedAgeRanges, selectedSex, selectedIndigStatus);
                            html = html + """
                                <h1>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2016 Data &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    2021 Data &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Gap
                                </h1>
                                <div class='table-container-lvl3'>
                                        <table class='table-lvl3'>
                                            <tr>
                                                <th>LGA Code</th>
                                                <th>Indigenous Status</th>
                                                <th>Sex</th>
                                                <th>Age Category</th>
                                                <th>Population</th>
                                            </tr>
                                """;
                            for (int i = 0; i < ageDemoDataset2016.size(); i++) {
                                html = html + "<tr>";
                                html = html + "<td>" + ageDemoDataset2016.get(i).getLGACode() + "</td>";
                                html = html + "<td>" + ageDemoDataset2016.get(i).getIndigStatus() + "</td>";
                                html = html + "<td>" + ageDemoDataset2016.get(i).getSex() + "</td>";
                                html = html + "<td>" + ageDemoDataset2016.get(i).getAgeCategory() + "</td>";
                                html = html + "<td>" + ageDemoDataset2016.get(i).getRawData() + "</td>";
                                html = html + "</tr>";
                            }
                            html = html + "</table>";
                            // 2021 Data
                            ageDemoDataset2021 = jdbc.getAgeDemographicsData2021(selectedAgeRanges, selectedSex, selectedIndigStatus);
                            html = html + """
                                    <table class='table-lvl3'>
                                        <tr>
                                            <th>LGA Code</th>
                                            <th>Indigenous Status</th>
                                            <th>Sex</th>
                                            <th>Age Category</th>
                                            <th>Population</th>
                                        </tr>
                                """;
                            for (int i = 0; i < ageDemoDataset2021.size(); i++) {
                                html = html + "<tr>";
                                html = html + "<td>" + ageDemoDataset2021.get(i).getLGACode() + "</td>";
                                html = html + "<td>" + ageDemoDataset2021.get(i).getIndigStatus() + "</td>";
                                html = html + "<td>" + ageDemoDataset2021.get(i).getSex() + "</td>";
                                html = html + "<td>" + ageDemoDataset2021.get(i).getAgeCategory() + "</td>";
                                html = html + "<td>" + ageDemoDataset2021.get(i).getRawData() + "</td>";
                                html = html + "</tr>";
                            }
                            html = html + "</table>";
                            // Comparison table
                            html = html + """
                                    <table class='table-lvl3'>
                                        <tr>
                                            <th>LGA Code</th>
                                            <th>Difference</th>
                                            <th>Incline/Decline</th>
                                        </tr>
                                """;
                            for (int i = 0; i < ageDemoDataset2016.size(); i++) {
                                double proportionalVal = 0;
                                int populationDifference = 0;
                                if (ageDemoDataset2016.get(i).getRawData() > ageDemoDataset2021.get(i).getRawData()) {
                                    populationDifference = Math.abs(ageDemoDataset2016.get(i).getRawData() - ageDemoDataset2021.get(i).getRawData());
                                    proportionalVal = (((double) populationDifference) / ageDemoDataset2016.get(i).getRawData()) * 100;
                                }
                                else {
                                    populationDifference = Math.abs(ageDemoDataset2021.get(i).getRawData() - ageDemoDataset2016.get(i).getRawData());
                                    proportionalVal = (((double) populationDifference) / ageDemoDataset2021.get(i).getRawData()) * 100;
                                }
                                html = html + "<tr>";
                                html = html + "<td>" + ageDemoDataset2016.get(i).getLGACode() + "</td>";
                                html = html + "<td>" + populationDifference + "</td>";
                                html = html + "<td>" + String.format("%.2f", proportionalVal) + "%</td>";
                                html = html + "</tr>";
                            }
                            html = html + """
                                    </table>
                                </div>
                                """;
                        }
                        else {
                            // User didn't select an age
                            html = html + "<ul>";
                            html = html + "<li>No age was selected, please try again</li>";
                            html = html + "</ul>";
                            hasError = true;
                        }
                        break;
                    case "Long Term Health Conditions":
                        if (!selectedHealthConditionsList.isEmpty()) {
                            jdbc.updateHealthConditions(selectedHealthConditions);
                            html = html + "<h1>Long Term Health Conditions Data</h1>";
                            html = html + """
                                <table class='lvl2-table'>
                                <tr>
                                    <th>LGA Code</th>
                                    <th>Indigenous Status</th>
                                    <th>Sex</th>
                                    <th>Condition</th>
                                    <th>Population</th>
                                </tr>
                                """;
                            LTHCDataset = jdbc.getLTHCData(selectedHealthConditions, selectedSex, selectedIndigStatus);
                            for (int i = 0; i < LTHCDataset.size(); i++) {
                                html = html + "<tr>";
                                html = html + "<td>" + LTHCDataset.get(i).getLGACode() + "</td>";
                                html = html + "<td>" + LTHCDataset.get(i).getIndigStatus() + "</td>";
                                html = html + "<td>" + LTHCDataset.get(i).getSex() + "</td>";
                                html = html + "<td>" + LTHCDataset.get(i).getCondition() + "</td>";
                                html = html + "<td>" + LTHCDataset.get(i).getRawData() + "</td>";
                                html = html + "</tr>";
                            }
                            html = html + "</table>";
                        }
                        else {
                            // No health condition was selected
                            html = html + "<ul>";
                            html = html + "<li>No health condition was selected, please try again</li>";
                            html = html + "</ul>";
                            hasError = true;
                        }
                        break;
                    case "Highest School Year":
                        if (!selectedSchoolYearRangeList.isEmpty()) {
                            jdbc.updateSchoolYearRange(selectedSchoolYearRange);
                            // 2016 Data
                            highSchoolYearDataset2016 = jdbc.getHighestSchoolYearData2016(selectedSchoolYearRange, selectedSex, selectedIndigStatus);
                            html = html + """
                                <h1>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2016 Data &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    2021 Data &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Gap
                                </h1>
                                <div class='table-container-lvl3'>
                                        <table class='table-lvl3'>
                                            <tr>
                                                <th>LGA Code</th>
                                                <th>Indigenous Status</th>
                                                <th>Sex</th>
                                                <th>Category</th>
                                                <th>Population</th>
                                            </tr>
                                """;
                            for (int i = 0; i < highSchoolYearDataset2016.size(); i++) {
                                html = html + "<tr>";
                                html = html + "<td>" + highSchoolYearDataset2016.get(i).getLGACode() + "</td>";
                                html = html + "<td>" + highSchoolYearDataset2016.get(i).getIndigStatus() + "</td>";
                                html = html + "<td>" + highSchoolYearDataset2016.get(i).getSex() + "</td>";
                                html = html + "<td>" + highSchoolYearDataset2016.get(i).getCategory() + "</td>";
                                html = html + "<td>" + highSchoolYearDataset2016.get(i).getRawData() + "</td>";
                                html = html + "</tr>";
                            }
                            html = html + "</table>";
                            // 2021 Data
                            highSchoolYearDataset2021 = jdbc.getHighestSchoolYearData2021(selectedSchoolYearRange, selectedSex, selectedIndigStatus);
                            html = html + """
                                        <table class='table-lvl3'>
                                            <tr>
                                                <th>LGA Code</th>
                                                <th>Indigenous Status</th>
                                                <th>Sex</th>
                                                <th>Category</th>
                                                <th>Population</th>
                                            </tr>
                                """;
                            for (int i = 0; i < highSchoolYearDataset2021.size(); i++) {
                                html = html + "<tr>";
                                html = html + "<td>" + highSchoolYearDataset2021.get(i).getLGACode() + "</td>";
                                html = html + "<td>" + highSchoolYearDataset2021.get(i).getIndigStatus() + "</td>";
                                html = html + "<td>" + highSchoolYearDataset2021.get(i).getSex() + "</td>";
                                html = html + "<td>" + highSchoolYearDataset2021.get(i).getCategory() + "</td>";
                                html = html + "<td>" + highSchoolYearDataset2021.get(i).getRawData() + "</td>";
                                html = html + "</tr>";
                            }
                            html = html + "</table>";
                            // Comparison table
                            html = html + """
                                    <table class='table-lvl3'>
                                        <tr>
                                            <th>LGA Code</th>
                                            <th>Difference</th>
                                            <th>Incline/Decline</th>
                                        </tr>
                                """;
                            for (int i = 0; i < highSchoolYearDataset2016.size(); i++) {
                                double proportionalVal = 0;
                                int populationDifference = 0;
                                if (highSchoolYearDataset2016.get(i).getRawData() > highSchoolYearDataset2021.get(i).getRawData()) {
                                    populationDifference = Math.abs(highSchoolYearDataset2016.get(i).getRawData() - highSchoolYearDataset2021.get(i).getRawData());
                                    proportionalVal = (((double) populationDifference) / highSchoolYearDataset2016.get(i).getRawData()) * 100;
                                }
                                else {
                                    populationDifference = Math.abs(highSchoolYearDataset2021.get(i).getRawData() - highSchoolYearDataset2016.get(i).getRawData());
                                    proportionalVal = (((double) populationDifference) / highSchoolYearDataset2021.get(i).getRawData()) * 100;
                                }
                                html = html + "<tr>";
                                html = html + "<td>" + highSchoolYearDataset2016.get(i).getLGACode() + "</td>";
                                html = html + "<td>" + populationDifference + "</td>";
                                html = html + "<td>" + String.format("%.2f", proportionalVal) + "%</td>";
                                html = html + "</tr>";
                            }
                            html = html + """
                                    </table>
                                </div>
                                """;
                        }
                        else {
                            // No school year was selected
                            html = html + "<ul>";
                            html = html + "<li>No school year was selected, please try again</li>";
                            html = html + "</ul>";
                            hasError = true;
                        }
                        break;
                    case "Non-School Completion":
                        if (!selectedNonSchoolCategoryList.isEmpty()) {
                            jdbc.updateNonSchool(selectedNonSchoolCategory);
                            // 2016 Data
                            nonSchoolCompletionDataset2016 = jdbc.getNonSchoolCategoryData2016(selectedNonSchoolCategory, selectedSex, selectedIndigStatus);
                            html = html + """
                                <h1>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2016 Data &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    2021 Data &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Gap
                                </h1>
                                <div class='table-container-lvl3'>
                                        <table class='table-lvl3'>
                                            <tr>
                                                <th>LGA Code</th>
                                                <th>Indigenous Status</th>
                                                <th>Sex</th>
                                                <th>Category</th>
                                                <th>Population</th>
                                            </tr>
                                """;
                                for (int i = 0; i < nonSchoolCompletionDataset2016.size(); i++) {
                                    html = html + "<tr>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2016.get(i).getLGACode() + "</td>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2016.get(i).getIndigStatus() + "</td>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2016.get(i).getSex() + "</td>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2016.get(i).getCategory() + "</td>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2016.get(i).getRawData() + "</td>";
                                    html = html + "</tr>";
                                }
                                html = html + "</table>";
                                // 2021 data 
                                nonSchoolCompletionDataset2021 = jdbc.getNonSchoolCategoryData2021(selectedNonSchoolCategory, selectedSex, selectedIndigStatus);
                                html = html + """
                                            <table class='table-lvl3'>
                                                <tr>
                                                    <th>LGA Code</th>
                                                    <th>Indigenous Status</th>
                                                    <th>Sex</th>
                                                    <th>Category</th>
                                                    <th>Population</th>
                                                </tr>
                                """;
                                for (int i = 0; i < nonSchoolCompletionDataset2021.size(); i++) {
                                    html = html + "<tr>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2021.get(i).getLGACode() + "</td>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2021.get(i).getIndigStatus() + "</td>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2021.get(i).getSex() + "</td>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2021.get(i).getCategory() + "</td>";
                                    html = html + "<td>" + nonSchoolCompletionDataset2021.get(i).getRawData() + "</td>";
                                    html = html + "</tr>";
                                }
                                html = html + "</table>";
                                // Comparison table
                                html = html + """
                                    <table class='table-lvl3'>
                                        <tr>
                                            <th>LGA Code</th>
                                            <th>Difference</th>
                                            <th>Incline/Decline</th>
                                        </tr>
                                """;
                            for (int i = 0; i < nonSchoolCompletionDataset2016.size(); i++) {
                                double proportionalVal = 0;
                                int populationDifference = 0;
                                if (nonSchoolCompletionDataset2016.get(i).getRawData() > nonSchoolCompletionDataset2021.get(i).getRawData()) {
                                    populationDifference = Math.abs(nonSchoolCompletionDataset2016.get(i).getRawData() - nonSchoolCompletionDataset2021.get(i).getRawData());
                                    proportionalVal = (((double) populationDifference) / nonSchoolCompletionDataset2016.get(i).getRawData()) * 100;
                                }
                                else {
                                    populationDifference = Math.abs(nonSchoolCompletionDataset2016.get(i).getRawData() - nonSchoolCompletionDataset2021.get(i).getRawData());
                                    proportionalVal = (((double) populationDifference) / nonSchoolCompletionDataset2021.get(i).getRawData()) * 100;
                                }
                                html = html + "<tr>";
                                html = html + "<td>" + nonSchoolCompletionDataset2016.get(i).getLGACode() + "</td>";
                                html = html + "<td>" + populationDifference + "</td>";
                                html = html + "<td>" + String.format("%.2f", proportionalVal) + "%</td>";
                                html = html + "</tr>";
                            }
                            html = html + """
                                    </table>
                                </div>
                                """;
                        }
                        else {
                            // No non-school completion category was selected
                            html = html + "<ul>";
                            html = html + "<li>No non-school completion category was selected, please try again</li>";
                            html = html + "</ul>";
                            hasError = true;
                        }
                        break;
                }
            }

        }

        // Footer
        html = html + """
            <div class='footer'>
                <p>The gap statistics page (By: Lachlan Shi)</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }
}
