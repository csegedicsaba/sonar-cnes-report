package fr.cnes.sonar.report.exporters.xlsx;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.model.Report;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exports the report in .docx format
 */
public class XlsXExporterAggregated {

    /**
     * Logger for XlsXExporter.
     */
    private static final Logger LOGGER = Logger.getLogger(XlsXExporterAggregated.class.getCanonicalName());

    private static final String AGGREGATED_SHEET_NAME = "SonarQubeAggregate";

    /**
     *
     */

    public void export(List<Report> reports, String path, String filename)
            throws BadExportationDataTypeException, IOException {

        // set output filename
        final String outputFilePath = path;

        // open excel file from the path given in the parameters
        final File file = new File(filename);

        // Check if template file exists
        if (!file.exists() && !filename.isEmpty()) {
            LOGGER.log(Level.WARNING, "Unable to find provided XLSX template file (using default one instead) : " + file.getAbsolutePath());
        }

        // open the template
        try (
                InputStream excelFile = file.exists() ?
                        new FileInputStream(file) : getClass().getResourceAsStream("/template/aggregated-template.xlsx");
                Workbook workbook = new XSSFWorkbook(excelFile);
                FileOutputStream fileOut = new FileOutputStream(outputFilePath)
        ) {

            // retrieve the sheet aiming to contain selected resources
            final XSSFSheet aggergatedSheet = (XSSFSheet) workbook.getSheet(AGGREGATED_SHEET_NAME);
            final Map<Integer, String> measuresInTemplate = getMeasuresFromTemplate(aggergatedSheet);

            // write selected resources in the file
            AtomicInteger i = new AtomicInteger(1);
            reports.forEach(report -> {
                XlsXToolsAggregated.addReport(aggergatedSheet, report, i.getAndIncrement(), measuresInTemplate);
            });


            // write output as file
            workbook.write(fileOut);
        }


    }

    private Map<Integer, String> getMeasuresFromTemplate(XSSFSheet aggergatedSheet) {
        Map<Integer, String> ret = new HashMap<>();
        int c = 6;
        while (aggergatedSheet.getRow(0).getCell(c) != null) {
            ret.put(c, aggergatedSheet.getRow(0).getCell(c).getStringCellValue());
            c++;
        }
        return ret;
    }

}
