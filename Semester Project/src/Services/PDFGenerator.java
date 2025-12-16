package Services;

import Models.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;


import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;


public class PDFGenerator {

    // Colors
    private static final BaseColor PRIMARY_COLOR = new BaseColor(41, 128, 185);
    private static final BaseColor SECONDARY_COLOR = new BaseColor(52, 73, 94);
    private static final BaseColor LIGHT_GRAY = new BaseColor(236, 240, 241);


    public static boolean generatePDF(CV cv, String filePath) {
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Fonts
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, PRIMARY_COLOR);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, PRIMARY_COLOR);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
            Font italicFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.DARK_GRAY);

            // ===== HEADER =====
            PersonalInfo info = cv.getPersonalInfo();
            Paragraph name = new Paragraph(info.getFullName().toUpperCase(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);
            document.add(new Paragraph(" "));

            // Contact info
            String contactInfo = "";
            if (!info.getEmail().isEmpty()) contactInfo += "✉ " + info.getEmail();
            if (!info.getPhone().isEmpty()) contactInfo += " | 📞 " + info.getPhone();
            if (!info.getCity().isEmpty()) {
                contactInfo += " | 📍 " + info.getCity();
                if (!info.getCountry().isEmpty()) contactInfo += ", " + info.getCountry();
            }
            Paragraph contact = new Paragraph(contactInfo, smallFont);
            contact.setAlignment(Element.ALIGN_CENTER);
            document.add(contact);

            // Line
            LineSeparator line = new LineSeparator();
            line.setLineColor(PRIMARY_COLOR);
            line.setLineWidth(2);
            document.add(new Chunk(line));
            document.add(new Paragraph(" "));

            // ===== PROFESSIONAL SUMMARY =====
            if (!cv.getSummary().isEmpty()) {
                addSectionHeader(document, "PROFESSIONAL SUMMARY", sectionFont);
                Paragraph summary = new Paragraph(cv.getSummary(), normalFont);
                summary.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(summary);
                document.add(new Paragraph(" "));
            }

            // ===== WORK EXPERIENCE =====
            if (!cv.getExperienceList().isEmpty()) {
                addSectionHeader(document, "WORK EXPERIENCE", sectionFont); // boldFont must be iText 5 Font
                for (Experience exp : cv.getExperienceList()) {
                    Paragraph jobPara = new Paragraph(exp.getJobTitle() + " at " + exp.getCompany(), boldFont);
                    document.add(jobPara);

                    Paragraph locPara = new Paragraph(exp.getLocation() + " | " + exp.getStartDate() + " - " + exp.getEndDate(), italicFont);
                    document.add(locPara);

                    java.util.List<String> respList = exp.getResponsibilities();
                    if (!respList.isEmpty()) {
                        com.itextpdf.text.List list = new com.itextpdf.text.List(); // no parameter
                        list.setListSymbol("•"); // set bullet
                        list.setIndentationLeft(15f);

                        for (String r : respList) {
                            ListItem item = new ListItem(r, normalFont); // normalFont must be iText 5 Font
                            list.add(item);
                        }
                        document.add(list);
                    }
                    document.add(new Paragraph(" ")); // spacing
                }
            }


            // ===== EDUCATION =====
            if (!cv.getEducationList().isEmpty()) {
                addSectionHeader(document, "EDUCATION", sectionFont);
                for (Education edu : cv.getEducationList()) {
                    String degree = edu.getDegree();
                    if (!edu.getFieldOfStudy().isEmpty()) degree += " in " + edu.getFieldOfStudy();
                    document.add(new Paragraph(degree, boldFont));
                    document.add(new Paragraph(edu.getInstitution(), normalFont));

                    String eduDetails = edu.getStartDate() + " - " + edu.getEndDate();
                    if (!edu.getLocation().isEmpty()) eduDetails += " | " + edu.getLocation();
                    if (!edu.getGpa().isEmpty()) eduDetails += " | GPA: " + edu.getGpa();
                    document.add(new Paragraph(eduDetails, italicFont));

                    if (!edu.getDescription().isEmpty()) {
                        Paragraph desc = new Paragraph(edu.getDescription(), normalFont);
                        desc.setIndentationLeft(15);
                        document.add(desc);
                    }
                    document.add(new Paragraph(" "));
                }
            }

            // ===== SKILLS =====
            if (!cv.getSkills().isEmpty()) {
                addSectionHeader(document, "SKILLS", sectionFont);

                com.itextpdf.text.List skillList = new com.itextpdf.text.List();
                skillList.setListSymbol("• "); // bullet
                skillList.setIndentationLeft(15f);

                for (String skill : cv.getSkills()) {
                    skillList.add(new ListItem(skill, normalFont));
                }

                document.add(skillList);
                document.add(new Paragraph(" "));

            }

            // ===== CERTIFICATIONS =====
            if (!cv.getCertifications().isEmpty()) {
                addSectionHeader(document, "CERTIFICATIONS", sectionFont);
                for (Certification cert : cv.getCertifications()) {
                    Paragraph certName = new Paragraph(cert.getCertificationName(), boldFont);
                    document.add(certName);

                    String certDetails = cert.getIssuingOrganization();
                    if (!cert.getIssueDate().isEmpty()) certDetails += " | Issued: " + cert.getIssueDate();
                    if (!cert.getExpiryDate().isEmpty()) certDetails += " | Expires: " + cert.getExpiryDate();
                    if (!cert.getCredentialId().isEmpty()) certDetails += " | ID: " + cert.getCredentialId();

                    Paragraph detailsPara = new Paragraph(certDetails, smallFont);
                    document.add(detailsPara);
                    document.add(new Paragraph(" "));
                }
            }

// ===== LANGUAGES =====
            if (!cv.getLanguages().isEmpty()) {
                addSectionHeader(document, "LANGUAGES", sectionFont);

                com.itextpdf.text.List langList = new com.itextpdf.text.List();
                langList.setListSymbol("• "); // bullet
                langList.setIndentationLeft(15f);

                for (String lang : cv.getLanguages()) {
                    langList.add(new ListItem(lang, normalFont));
                }

                document.add(langList);
            }

            document.close();

            // ===== Auto-open PDF =====
            openPDF(filePath);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (document.isOpen()) document.close();
            return false;
        }
    }

    // Section Header
    private static void addSectionHeader(Document doc, String title, Font font) {
        try {
            doc.add(new Paragraph(title, font));
            LineSeparator line = new LineSeparator();
            line.setLineColor(LIGHT_GRAY);
            doc.add(new Chunk(line));
            doc.add(new Paragraph(" "));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    // Auto-open PDF
    private static void openPDF(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
