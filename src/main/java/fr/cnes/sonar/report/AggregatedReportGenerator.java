package fr.cnes.sonar.report;

import fr.cnes.sonar.report.exporters.xlsx.XlsXExporterAggregated;
import fr.cnes.sonar.report.factory.ProviderFactory;
import fr.cnes.sonar.report.factory.ReportModelFactory;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.providers.AllProjectProvider;
import fr.cnes.sonar.report.utils.ReportConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AggregatedReportGenerator {

    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ReportCommandLine.class.getName());

    public static void generate(ReportConfiguration conf, SonarQubeServer server) {
        try {
            LOGGER.info("AggregatedReportGenerator.generate start...");
            final ProviderFactory providerFactory = new ProviderFactory(server, conf.getToken(), null, null);
            final AllProjectProvider allProjectProvider = providerFactory.create(AllProjectProvider.class);

            List<String> projects = allProjectProvider.getProjects();
            List<Report> reports = new ArrayList<>();
            projects.forEach(project -> {
                LOGGER.info("Getting project model for project: " + project);
                try {
                    final Report model = new ReportModelFactory(server, conf, project).create();
                    reports.add(model);
                } catch (Exception e) {
                    LOGGER.warning("Error in getting model for project: " + project);
                    e.printStackTrace();
                }
            });

            XlsXExporterAggregated xlsXExporterAggregated = new XlsXExporterAggregated();
            xlsXExporterAggregated.export(reports, "aggregated_report_" + System.currentTimeMillis() + ".xlsx", "dummy");

            LOGGER.info("AggregatedReportGenerator.generate end...");
        } catch (Exception e) {
            LOGGER.warning("Error in AggregatedReportGenerator: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
