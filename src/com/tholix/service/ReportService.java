package com.tholix.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import org.joda.time.DateTime;

import com.tholix.domain.ReceiptEntity;
import com.tholix.web.rest.Header;
import com.tholix.web.rest.LandingView;

/**
 * User: hitender
 * Date: 9/4/13 1:19 PM
 */
@Service
public final class ReportService {
    private static final Logger log = Logger.getLogger(ReportService.class.getName());

    @Autowired LandingService landingService;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired private FreeMarkerConfigurationFactoryBean freemarkerConfiguration;

    @Value("${http}")
    private String http;

    @Value("${host}")
    private String host;

    @Value("${port}")
    private String port;

    @Value("${app.name}")
    private String appName;

    public String monthlyReport(DateTime month, String profileId, String emailId, Header header) {
        List<ReceiptEntity> receipts = landingService.getAllReceiptsForThisMonth(profileId, month);

        LandingView landingView = LandingView.newInstance(profileId, emailId, header);
        landingView.setPendingCount(0);
        landingView.setReceipts(receipts);
        landingView.setHeader(header);

        return monthlyReport(landingView);
    }

    private String monthlyReport(LandingView landingView) {
        File file = null;
        try {
            file = File.createTempFile("XML-Report", ".xml");
        } catch (IOException e) {
            log.error("Error creating time file to save XML output from JAXB: " + e.getLocalizedMessage());
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(LandingView.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

            jaxbMarshaller.marshal(landingView, file);
            jaxbMarshaller.marshal(landingView, System.out);

            Map rootMap = new HashMap();
            rootMap.put("doc", freemarker.ext.dom.NodeModel.parse(file));

            rootMap.put("http", http);
            rootMap.put("host", host);
            rootMap.put("port", port);
            rootMap.put("appname", appName);

            return freemarkerDo(rootMap);
        } catch (JAXBException | SAXException | ParserConfigurationException | IOException | TemplateException e) {
            log.error("Error while processing reporting template: " + e.getLocalizedMessage());
        }
        return null;
    }


    private String freemarkerDo(Map rootMap) throws IOException, TemplateException {
        Configuration cfg = freemarkerConfiguration.createConfiguration();
        Template template = cfg.getTemplate("monthly-report.ftl");
        final String text = processTemplateIntoString(template, rootMap);
        log.debug(text);
        return text;
    }

    /**
     * Stream to console
     *
     * @param template
     * @param rootMap
     * @throws IOException
     * @throws TemplateException
     */
    private void processTemplateToSystemOut(Template template, Map rootMap) throws IOException, TemplateException {
        OutputStreamWriter output = new OutputStreamWriter(System.out);
        template.process(rootMap, output);
    }
}
