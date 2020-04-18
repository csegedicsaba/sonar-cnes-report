package fr.cnes.sonar.report.exporters.xlsx;

import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.Report;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class XlsXToolsAggregated {
    public static void addReport(XSSFSheet sheet, Report report, int rowNumber, Map<Integer, String> measuresInTemplate) {

        // create a new row from the context, it will be returned
        final XSSFRow row = sheet.createRow(rowNumber);
        row.createCell(0).setCellValue(report.getProject().getKey());
        row.createCell(1).setCellValue(report.getProject().getName());
        row.createCell(2).setCellValue(report.getProject().getBranch());
        row.createCell(3).setCellValue(report.getProject().getVersion());
        row.createCell(4).setCellValue(getQualityProfiles(report));
        row.createCell(5).setCellValue(report.getQualityGate().getName());

        measuresInTemplate.keySet().forEach(i -> {
            row.createCell(i).setCellValue(getMeasure(measuresInTemplate.get(i), report));
        });
    }

    private static String getMeasure(String s, Report report) {
        AtomicReference<String> ret = new AtomicReference<>("n/a");
        report.getMeasures().forEach(measure -> {
            if (measure.getMetric().endsWith(s)) {
                if (measure.getValue() != null) {
                    ret.set(measure.getValue());
                }
            }
        });

        return ret.get();
    }

    private static String getQualityProfiles(Report report) {

        StringBuilder ret = new StringBuilder();

        List<ProfileMetaData> qpList = Arrays.asList(report.getProject().getQualityProfiles());

        if (qpList != null) {
            qpList.sort((o1, o2) -> {
                return o1.getName().compareTo(o2.getName());
            });
        }

        qpList.forEach(qualityProfile -> {
            ret.append(qualityProfile.getLanguage() + ":" + qualityProfile.getName() + "; ");
        });
        return ret.toString();
    }
}
