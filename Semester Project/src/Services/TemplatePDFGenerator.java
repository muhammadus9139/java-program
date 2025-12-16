package Services;

import Models.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;

public class TemplatePDFGenerator {
    private static final BaseColor LIGHT_GRAY = new BaseColor(236, 240, 241);

    public static boolean generatePDF(CV cv, String filePath) {
        try {
            // Get template style and generate accordingly
            CVTemplateStyle style = cv.getTemplateStyle();

            if (style == CVTemplateStyle.PROFESSIONAL) {
                return generateProfessionalCV(cv, filePath);
            } else if (style == CVTemplateStyle.MODERN) {
                return generateModernCV(cv, filePath);
            } else {
                return generateSimpleCV(cv, filePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================ SIMPLE TEMPLATE ================
    private static boolean generateSimpleCV(CV cv, String filePath) {
        try {
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            BaseColor primaryColor = getColorFromHex(cv.getTemplate().getColorScheme());

            // Fonts
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, primaryColor);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryColor);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);

            // Header
            addSimpleHeader(document, cv, nameFont, smallFont, primaryColor);

            // Summary
            if (!cv.getSummary().isEmpty()) {
                addSimpleSection(document, "PROFESSIONAL SUMMARY", sectionFont, primaryColor);
                Paragraph summary = new Paragraph(cv.getSummary(), normalFont);
                summary.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(summary);
                document.add(new Paragraph(" "));
            }

            // Experience
            if (!cv.getExperienceList().isEmpty()) {
                addSimpleSection(document, "WORK EXPERIENCE", sectionFont, primaryColor);
                for (Experience exp : cv.getExperienceList()) {
                    document.add(new Paragraph(exp.getJobTitle(), boldFont));
                    document.add(new Paragraph(exp.getCompany() + " | " + exp.getLocation(), normalFont));
                    document.add(new Paragraph(exp.getStartDate() + " - " + exp.getEndDate(), smallFont));

                    for (String resp : exp.getResponsibilities()) {
                        Paragraph p = new Paragraph("• " + resp, normalFont);
                        p.setIndentationLeft(15);
                        document.add(p);
                    }
                    document.add(new Paragraph(" "));
                }
            }

            // Education
            if (!cv.getEducationList().isEmpty()) {
                addSimpleSection(document, "EDUCATION", sectionFont, primaryColor);
                for (Education edu : cv.getEducationList()) {
                    String degree = edu.getDegree();
                    if (!edu.getFieldOfStudy().isEmpty()) degree += " in " + edu.getFieldOfStudy();
                    document.add(new Paragraph(degree, boldFont));
                    document.add(new Paragraph(edu.getInstitution(), normalFont));

                    String details = edu.getStartDate() + " - " + edu.getEndDate();
                    if (!edu.getGpa().isEmpty()) details += " | GPA: " + edu.getGpa();
                    if (!edu.getLocation().isEmpty()) details += " | " + edu.getLocation();
                    document.add(new Paragraph(details, smallFont));

                    if (!edu.getDescription().isEmpty()) {
                        Paragraph desc = new Paragraph(edu.getDescription(), normalFont);
                        desc.setIndentationLeft(15);
                        document.add(desc);
                    }
                    document.add(new Paragraph(" "));
                }
            }

            // Projects
            if (!cv.getProjectList().isEmpty()) {
                addSimpleSection(document, "PROJECTS", sectionFont, primaryColor);
                for (Project proj : cv.getProjectList()) {
                    document.add(new Paragraph(proj.getProjectName(), boldFont));
                    if (!proj.getTechnologies().isEmpty()) {
                        document.add(new Paragraph("Technologies: " + String.join(", ", proj.getTechnologies()), smallFont));
                    }
                    if (!proj.getProjectLink().isEmpty()) {
                        document.add(new Paragraph("Link: " + proj.getProjectLink(), smallFont));
                    }
                    document.add(new Paragraph(proj.getDescription(), normalFont));
                    document.add(new Paragraph(" "));
                }
            }

            // Skills - ONE PER LINE
            if (!cv.getSkills().isEmpty()) {
                addSimpleSection(document, "SKILLS", sectionFont, primaryColor);
                for (String skill : cv.getSkills()) {
                    Paragraph skillPara = new Paragraph("• " + skill, normalFont);
                    skillPara.setIndentationLeft(15);
                    document.add(skillPara);
                }
                document.add(new Paragraph(" "));
            }

            // Certifications
            if (!cv.getCertifications().isEmpty()) {
                addSimpleSection(document, "CERTIFICATIONS", sectionFont, primaryColor);
                for (Certification cert : cv.getCertifications()) {
                    document.add(new Paragraph(cert.getCertificationName(), boldFont));
                    String details = cert.getIssuingOrganization();
                    if (!cert.getIssueDate().isEmpty()) details += " | Issued: " + cert.getIssueDate();
                    if (!cert.getExpiryDate().isEmpty()) details += " | Expires: " + cert.getExpiryDate();
                    document.add(new Paragraph(details, smallFont));
                    document.add(new Paragraph(" "));
                }
            }

            // Languages - ONE PER LINE
            if (!cv.getLanguages().isEmpty()) {
                addSimpleSection(document, "LANGUAGES", sectionFont, primaryColor);
                for (String lang : cv.getLanguages()) {
                    Paragraph langPara = new Paragraph("• " + lang, normalFont);
                    langPara.setIndentationLeft(15);
                    document.add(langPara);
                }
            }

            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================ MODERN TEMPLATE (SIDEBAR) ================
    private static boolean generateModernCV(CV cv, String filePath) {
        try {
            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            BaseColor primaryColor = getColorFromHex(cv.getTemplate().getColorScheme());
            BaseColor sidebarColor = new BaseColor(240, 240, 245);

            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.WHITE);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, primaryColor);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);

            // Create two-column layout
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{35, 65});

            // LEFT SIDEBAR
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBackgroundColor(sidebarColor);
            leftCell.setPadding(20);
            leftCell.setBorder(Rectangle.NO_BORDER);

            // Name header in sidebar
            PdfPTable nameTable = new PdfPTable(1);
            nameTable.setWidthPercentage(100);
            PdfPCell nameCell = new PdfPCell();
            nameCell.setBackgroundColor(primaryColor);
            nameCell.setPadding(15);
            nameCell.setBorder(Rectangle.NO_BORDER);
            Paragraph namePara = new Paragraph(cv.getPersonalInfo().getFullName(), nameFont);
            nameCell.addElement(namePara);
            nameTable.addCell(nameCell);

            Paragraph leftContent = new Paragraph();
            leftContent.add(nameTable);
            leftContent.add(Chunk.NEWLINE);

            // Contact
            leftContent.add(new Paragraph("CONTACT", sectionFont));
            leftContent.add(Chunk.NEWLINE);
            if (!cv.getPersonalInfo().getEmail().isEmpty()) {
                leftContent.add(new Paragraph("• " + cv.getPersonalInfo().getEmail(), new Font(Font.FontFamily.HELVETICA, 9)));
            }
            if (!cv.getPersonalInfo().getPhone().isEmpty()) {
                leftContent.add(new Paragraph("• " + cv.getPersonalInfo().getPhone(), new Font(Font.FontFamily.HELVETICA, 9)));
            }
            if (!cv.getPersonalInfo().getCity().isEmpty()) {
                leftContent.add(new Paragraph("• " + cv.getPersonalInfo().getCity(), new Font(Font.FontFamily.HELVETICA, 9)));
            }
            leftContent.add(Chunk.NEWLINE);

            // Skills in sidebar
            if (!cv.getSkills().isEmpty()) {
                leftContent.add(new Paragraph("SKILLS", sectionFont));
                leftContent.add(Chunk.NEWLINE);
                for (String skill : cv.getSkills()) {
                    leftContent.add(new Paragraph("• " + skill, new Font(Font.FontFamily.HELVETICA, 9)));
                }
                leftContent.add(Chunk.NEWLINE);
            }

            // Languages in sidebar
            if (!cv.getLanguages().isEmpty()) {
                leftContent.add(new Paragraph("LANGUAGES", sectionFont));
                leftContent.add(Chunk.NEWLINE);
                for (String lang : cv.getLanguages()) {
                    leftContent.add(new Paragraph("• " + lang, new Font(Font.FontFamily.HELVETICA, 9)));
                }
            }

            leftCell.addElement(leftContent);
            mainTable.addCell(leftCell);

            // RIGHT CONTENT
            PdfPCell rightCell = new PdfPCell();
            rightCell.setPadding(30);
            rightCell.setBorder(Rectangle.NO_BORDER);

            Paragraph rightContent = new Paragraph();

            // Summary
            if (!cv.getSummary().isEmpty()) {
                rightContent.add(new Paragraph("PROFESSIONAL SUMMARY", sectionFont));
                rightContent.add(Chunk.NEWLINE);
                rightContent.add(new Paragraph(cv.getSummary(), normalFont));
                rightContent.add(Chunk.NEWLINE);
                rightContent.add(Chunk.NEWLINE);
            }

            // Experience
            if (!cv.getExperienceList().isEmpty()) {
                rightContent.add(new Paragraph("EXPERIENCE", sectionFont));
                rightContent.add(Chunk.NEWLINE);
                for (Experience exp : cv.getExperienceList()) {
                    rightContent.add(new Paragraph(exp.getJobTitle(), boldFont));
                    rightContent.add(new Paragraph(exp.getCompany() + " | " + exp.getLocation(), normalFont));
                    rightContent.add(new Paragraph(exp.getStartDate() + " - " + exp.getEndDate(), smallFont));
                    for (String resp : exp.getResponsibilities()) {
                        Paragraph p = new Paragraph("• " + resp, normalFont);
                        p.setIndentationLeft(10);
                        rightContent.add(p);
                    }
                    rightContent.add(Chunk.NEWLINE);
                }
            }

            // Education
            if (!cv.getEducationList().isEmpty()) {
                rightContent.add(new Paragraph("EDUCATION", sectionFont));
                rightContent.add(Chunk.NEWLINE);
                for (Education edu : cv.getEducationList()) {
                    String degree = edu.getDegree();
                    if (!edu.getFieldOfStudy().isEmpty()) degree += " in " + edu.getFieldOfStudy();
                    rightContent.add(new Paragraph(degree, boldFont));
                    rightContent.add(new Paragraph(edu.getInstitution(), normalFont));
                    rightContent.add(Chunk.NEWLINE);
                }
            }

            // Projects
            if (!cv.getProjectList().isEmpty()) {
                rightContent.add(new Paragraph("PROJECTS", sectionFont));
                rightContent.add(Chunk.NEWLINE);
                for (Project proj : cv.getProjectList()) {
                    rightContent.add(new Paragraph(proj.getProjectName(), boldFont));
                    rightContent.add(new Paragraph(proj.getDescription(), normalFont));
                    rightContent.add(Chunk.NEWLINE);
                }
            }

            rightCell.addElement(rightContent);
            mainTable.addCell(rightCell);

            document.add(mainTable);
            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================ PROFESSIONAL TEMPLATE (LIKE YOUR IMAGE) ================
    private static boolean generateProfessionalCV(CV cv, String filePath) {
        try {
            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            BaseColor primaryColor = getColorFromHex(cv.getTemplate().getColorScheme());
            BaseColor lightColor = new BaseColor(
                    primaryColor.getRed() + 100 > 255 ? 255 : primaryColor.getRed() + 100,
                    primaryColor.getGreen() + 100 > 255 ? 255 : primaryColor.getGreen() + 100,
                    primaryColor.getBlue() + 100 > 255 ? 255 : primaryColor.getBlue() + 100
            );

            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, BaseColor.BLACK);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryColor);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);

            // Create two-column layout
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{35, 65});

            // LEFT SIDEBAR with light background
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBackgroundColor(lightColor);
            leftCell.setPadding(25);
            leftCell.setBorder(Rectangle.NO_BORDER);

            Paragraph leftContent = new Paragraph();

            // Objective in sidebar
            leftContent.add(new Paragraph("OBJECTIVE", sectionFont));
            leftContent.add(Chunk.NEWLINE);
            if (!cv.getSummary().isEmpty()) {
                leftContent.add(new Paragraph(cv.getSummary(), new Font(Font.FontFamily.HELVETICA, 9)));
            }
            leftContent.add(Chunk.NEWLINE);
            leftContent.add(Chunk.NEWLINE);

            // Contact
            leftContent.add(new Paragraph("CONTACT", sectionFont));
            leftContent.add(Chunk.NEWLINE);
            if (!cv.getPersonalInfo().getEmail().isEmpty()) {
                leftContent.add(new Paragraph("• " + cv.getPersonalInfo().getEmail(), new Font(Font.FontFamily.HELVETICA, 8)));
            }
            if (!cv.getPersonalInfo().getPhone().isEmpty()) {
                leftContent.add(new Paragraph("• " + cv.getPersonalInfo().getPhone(), new Font(Font.FontFamily.HELVETICA, 8)));
            }
            if (!cv.getPersonalInfo().getCity().isEmpty()) {
                String location = cv.getPersonalInfo().getCity();
                if (!cv.getPersonalInfo().getCountry().isEmpty()) {
                    location += ", " + cv.getPersonalInfo().getCountry();
                }
                leftContent.add(new Paragraph("• " + location, new Font(Font.FontFamily.HELVETICA, 8)));
            }
            leftContent.add(Chunk.NEWLINE);
            leftContent.add(Chunk.NEWLINE);

            // Education in sidebar
            if (!cv.getEducationList().isEmpty()) {
                leftContent.add(new Paragraph("EDUCATION", sectionFont));
                leftContent.add(Chunk.NEWLINE);
                for (Education edu : cv.getEducationList()) {
                    String degree = edu.getDegree();
                    if (!edu.getFieldOfStudy().isEmpty()) degree += " in " + edu.getFieldOfStudy();
                    leftContent.add(new Paragraph(degree, new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
                    leftContent.add(new Paragraph(edu.getInstitution(), new Font(Font.FontFamily.HELVETICA, 8)));
                    leftContent.add(Chunk.NEWLINE);
                }
                leftContent.add(Chunk.NEWLINE);
            }

            // Skills in sidebar
            if (!cv.getSkills().isEmpty()) {
                leftContent.add(new Paragraph("SKILLS", sectionFont));
                leftContent.add(Chunk.NEWLINE);
                for (String skill : cv.getSkills()) {
                    leftContent.add(new Paragraph("• " + skill, new Font(Font.FontFamily.HELVETICA, 9)));
                }
            }

            leftCell.addElement(leftContent);
            mainTable.addCell(leftCell);

            // RIGHT CONTENT
            PdfPCell rightCell = new PdfPCell();
            rightCell.setPadding(25);
            rightCell.setBorder(Rectangle.NO_BORDER);

            Paragraph rightContent = new Paragraph();

            // Name and title at top right
            rightContent.add(new Paragraph(cv.getPersonalInfo().getFullName().toUpperCase(), nameFont));
            rightContent.add(new Paragraph("FRONTEND DEVELOPER", titleFont));
            rightContent.add(Chunk.NEWLINE);

            // Professional summary if not in left
            if (!cv.getSummary().isEmpty()) {
                rightContent.add(new Paragraph(cv.getSummary(), normalFont));
                rightContent.add(Chunk.NEWLINE);
                rightContent.add(Chunk.NEWLINE);
            }

            // Experience
            if (!cv.getExperienceList().isEmpty()) {
                rightContent.add(new Paragraph("EXPERIENCE", sectionFont));
                LineSeparator line = new LineSeparator();
                line.setLineColor(primaryColor);
                rightContent.add(new Chunk(line));
                rightContent.add(Chunk.NEWLINE);

                for (Experience exp : cv.getExperienceList()) {
                    rightContent.add(new Paragraph(exp.getJobTitle() + " - " + exp.getCompany(), boldFont));
                    rightContent.add(new Paragraph("(" + exp.getStartDate() + " - " + exp.getEndDate() + ")", smallFont));
                    rightContent.add(Chunk.NEWLINE);

                    for (String resp : exp.getResponsibilities()) {
                        Paragraph p = new Paragraph(resp, new Font(Font.FontFamily.HELVETICA, 9));
                        p.setIndentationLeft(10);
                        rightContent.add(p);
                    }
                    rightContent.add(Chunk.NEWLINE);
                }
            }

            // Projects
            if (!cv.getProjectList().isEmpty()) {
                rightContent.add(Chunk.NEWLINE);
                rightContent.add(new Paragraph("PROJECTS", sectionFont));
                LineSeparator line2 = new LineSeparator();
                line2.setLineColor(primaryColor);
                rightContent.add(new Chunk(line2));
                rightContent.add(Chunk.NEWLINE);

                for (Project proj : cv.getProjectList()) {
                    rightContent.add(new Paragraph(proj.getProjectName(), boldFont));
                    if (!proj.getTechnologies().isEmpty()) {
                        rightContent.add(new Paragraph("Technologies: " + String.join(", ", proj.getTechnologies()), smallFont));
                    }
                    rightContent.add(new Paragraph(proj.getDescription(), normalFont));
                    rightContent.add(Chunk.NEWLINE);
                }
            }

            // Certifications
            if (!cv.getCertifications().isEmpty()) {
                rightContent.add(Chunk.NEWLINE);
                rightContent.add(new Paragraph("CERTIFICATIONS", sectionFont));
                LineSeparator line3 = new LineSeparator();
                line3.setLineColor(primaryColor);
                rightContent.add(new Chunk(line3));
                rightContent.add(Chunk.NEWLINE);

                for (Certification cert : cv.getCertifications()) {
                    rightContent.add(new Paragraph(cert.getCertificationName(), boldFont));
                    rightContent.add(new Paragraph(cert.getIssuingOrganization(), normalFont));
                    rightContent.add(Chunk.NEWLINE);
                }
            }

            // Languages
            if (!cv.getLanguages().isEmpty()) {
                rightContent.add(Chunk.NEWLINE);
                rightContent.add(new Paragraph("LANGUAGES", sectionFont));
                LineSeparator line4 = new LineSeparator();
                line4.setLineColor(primaryColor);
                rightContent.add(new Chunk(line4));
                rightContent.add(Chunk.NEWLINE);

                for (String lang : cv.getLanguages()) {
                    rightContent.add(new Paragraph("• " + lang, normalFont));
                }
            }

            rightCell.addElement(rightContent);
            mainTable.addCell(rightCell);

            document.add(mainTable);
            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper methods
    private static void addSimpleHeader(Document doc, CV cv, Font nameFont, Font smallFont, BaseColor color) throws DocumentException {
        Paragraph name = new Paragraph(cv.getPersonalInfo().getFullName(), nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        doc.add(name);
        doc.add(new Paragraph(" "));

        String contact = "";
        if (!cv.getPersonalInfo().getEmail().isEmpty()) contact += cv.getPersonalInfo().getEmail();
        if (!cv.getPersonalInfo().getPhone().isEmpty()) contact += " | " + cv.getPersonalInfo().getPhone();
        if (!cv.getPersonalInfo().getCity().isEmpty()) {
            contact += " | " + cv.getPersonalInfo().getCity();
            if (!cv.getPersonalInfo().getCountry().isEmpty()) {
                contact += ", " + cv.getPersonalInfo().getCountry();
            }
        }

        Paragraph contactPara = new Paragraph(contact, smallFont);
        contactPara.setAlignment(Element.ALIGN_CENTER);
        doc.add(contactPara);

        LineSeparator line = new LineSeparator();
        line.setLineColor(color);
        line.setLineWidth(2);
        doc.add(new Chunk(line));
        doc.add(new Paragraph(" "));
    }

    private static void addSimpleSection(Document doc, String title, Font font, BaseColor color) throws DocumentException {
        doc.add(new Paragraph(title, font));
        LineSeparator line = new LineSeparator();
        line.setLineColor(LIGHT_GRAY);
        doc.add(new Chunk(line));
        doc.add(new Paragraph(" "));
    }

    private static BaseColor getColorFromHex(String hex) {
        try {
            hex = hex.replace("#", "");
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new BaseColor(r, g, b);
        } catch (Exception e) {
            return new BaseColor(44, 62, 80);
        }
    }
}