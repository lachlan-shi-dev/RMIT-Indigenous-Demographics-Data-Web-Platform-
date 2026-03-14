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
public class PageST3B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3B.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 3.2</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <section class = 'header3'>
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
                    LGA Similar Characteristics (Subtask 3B)
                    <h2>
                        (Subtask 3B)
                    </h2>
                </h1>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class = 'content'>";

        // Add HTML for the page content
        html = html + """
        <div class = '3BForm'>
        <form action='/page3B.html' method="post">
        <div class = '3Bsearch'>
        <div class = 'titl'>
            <label for="search">Search for LGAs (Value Range: 10050-99799):</label>
            </div>
            <input type="text" class='search2a' id="searchno1" name="searchno1" placeholder="SEARCH LGA" required>
        </div>
        <div class='whitespace-xs'>
        </div>
        <div class = 'radio3b'>
        <div class = 'titl'>
            YEAR:
            </div>
            <label>
                <input type="radio" name="years" id="year-2016" value="2016"> 2016
            </label>
            <label>
                <input type="radio" name="years" id="year-2021" value="2021"> 2021
            </label>
        </div>
        <div class='whitespace-xs'>
        </div>
        <div class = '3BForm'>
        <div class = 'radio3b'>
        <div class = 'titl'>
            DATA:
            </div>
            <label>
                <input type="radio" name="per1" value="Population"> AGE STATISTICS
            </label>
            <label>
                <input type="radio" name="per1" id="health-radio" "value="LTHC"> HEALTH STATISTICS
            </label>
            <label>
                <input type="radio" name="per1" value="HighestSchoolYear"> SCHOOLING STATISTICS
            </label>
            <label>
                <input type="radio" name="per1" value="NonSchoolCompletion"> NON-SCHOOL EDUCATION STATISTICS
            </label>
        </div>
        <div class='whitespace-xs'>
        </div>
        <div class = '3Bsearch'>
            <div class = 'key-section'>
            <div class = 'titl'>
            <p> Key for Categories:</p>
            </div>
            <p> Age Statistics: _0_4, _10_14, _15_19, _20_24, _25_29, _30_34, _35_39, _40_44, _45_49, _50_54, _55_59, _60_64, _65_yrs_ov</p>
            <p> Health Statistics: arthritis, asthma, cancer, dementia, diabetes, heartdisease, kidneydisease, lungcategory, mentalhealth, stroke</p>
            <p> Schooling Statistics: did_not_go_to_school, y8_below, y9_equivalent, y10_equivalent, y11_equivalent, y12_equivalent</p>
            <p> Non School Education Statistics: Postgrad Level (pd_gd_gc), Bachelors (bd), Advanced Diploma (adip_dip), Cert 3&4 Level (ct_iii_iv), Cert 1&2 Level (ct_i_ii)</p>
            </div>
            <label for="search">CATEGORY Search:</label>
            <input type="text" class='search2a' id="searchno2" name="searchno2" placeholder="SEARCH CATEGORY" required>
        </div>
        <div class='whitespace-xs'>
        </div>
        <div class = 'radio3b'>
        <div class = 'titl'>
            GENDER:
            </div>
            <label>
                <input type="radio" name="gender" value="m"> MALE
            </label>
            <label>
                <input type="radio" name="gender" value="f"> FEMALE
            </label>
        </div>
        <div class='whitespace-xs'>
        </div>
        <div class = 'radio3b'>
        <div class = 'titl'>
            INDIGENOUS STATUS:
            </div>
            <label>
                <input type="radio" name="indigstat" value="indig"> INDIGENOUS
            </label>
            <label>
                <input type="radio" name="indigstat" value="non_indig"> NON-INDIGENOUS
            </label>
        
        </div>
        <div class='whitespace-xs'>
        </div>
        <input type="submit" value="Submit">
    </form>

    <script>
    document.addEventListener('DOMContentLoaded', function () {
        const year2016Radio = document.getElementById('year-2016');
        const year2021Radio = document.getElementById('year-2021');
        const healthRadio = document.getElementById('health-radio');
    
        year2016Radio.addEventListener('change', function () {
            if (year2016Radio.checked) {
                healthRadio.disabled = true;
            } else {
                healthRadio.disabled = false;
            }
        });
    
        year2021Radio.addEventListener('change', function () {
            if (year2021Radio.checked) {
                healthRadio.disabled = false;
            } else {
                healthRadio.disabled = true;
            }
        });
    });
    </script>
    </div>

         """;

         String LGAQUERYCODE3B = context.formParam("searchno1");
         String CATEGORY3B = context.formParam("searchno2");
         String GENDERQUERY3B = context.formParam("gender");
         String INDIGQUERY3B = context.formParam("indigstat");    
         String DATAQUERY3B = context.formParam("per1");    
         String YEARQUERY3B = context.formParam("years");  
         
         JDBCConnection jdbc = new JDBCConnection();
         
         ArrayList<LGA3B> tablerow = jdbc.getLGA3B(LGAQUERYCODE3B, CATEGORY3B, GENDERQUERY3B, INDIGQUERY3B, DATAQUERY3B, YEARQUERY3B);

         html = html + """
            <div class = 'table3'>    
            <table id="sortable-table2" bgcolor="#f5f0f0" width="1250">
            <thead>
            <tr bgcolor="#ffcccc">
                <th id="rank-header" data-column="0" width="250">RANK</th>
                <th id="lga-name-header" data-column="1" width="250">LGA NAME</th>
                <th id="lga-code-header" data-column="2" width="250">LGA CODE</th>
                <th id="count-header" data-column="3" width="250">COUNT</th>
                <th id="similarity-header" data-column="4" width="250">SIMILARITY</th>
            </tr>
            </thead>
                """;
    
            for (LGA3B row : tablerow) {
                html = html + "<tr>"; 
                html = html +"<td>" + row.getRank() + "</td>";
                html = html +"<td>" + row.getLGANAME() + "</td> ";
                html = html + "<td>" + row.getLGA() + "</td>";
                html = html + "<td>" + row.getCount() + "</td>";
                html = html + "<td>" + row.getSimilarity() + "</td></tr>";
            }  

         
        // Close Content div
        html = html + "</table></div>";
        html = html + """
                <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const table = document.getElementById('sortable-table2');
                    const tbody = table.querySelector('tbody');
                    const headers = table.querySelectorAll('th[data-column]');
                
                    const sortingDirections = Array(headers.length).fill(1);
                
                    headers.forEach((header, index) => {
                        header.addEventListener('click', () => {
                            const column = header.getAttribute('data-column');
                            const rows = Array.from(tbody.rows);
                
                            sortingDirections[index] *= -1; // Toggle sorting direction
                
                            rows.sort((a, b) => {
                                const cellA = a.cells[column].textContent;
                                const cellB = b.cells[column].textContent;
                
                                return cellA.localeCompare(cellB) * sortingDirections[index];
                            });
                
                            // Clear the table
                            while (tbody.firstChild) {
                                tbody.removeChild(tbody.firstChild);
                            }
                
                            // Re-add the sorted rows
                            rows.forEach(row => tbody.appendChild(row));
                        });
                    });
                });
                </script>
                """;

        // Footer
        html = html + """
            <div class='footer'>
                <p>LGA Similar Characteristics (By: Aniketh Yella)</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

}
