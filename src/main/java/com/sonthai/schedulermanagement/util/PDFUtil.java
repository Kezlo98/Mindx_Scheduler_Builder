package com.sonthai.schedulermanagement.util;

import com.sonthai.schedulermanagement.model.dto.TaskDto;
import com.sonthai.schedulermanagement.model.dto.UserDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PDFUtil {

    private static PDFont font = PDType1Font.HELVETICA_BOLD;

    public static String buildPDFName(UserDto userDto, Long taskId) {
        Arrays.sort(new String[]{});
        return userDto.getUsername() + userDto.getRegistrationId() + "_" + taskId + ".pdf";
    }

    public static File getPDF(String path) {
        return new File(path);
    }

    public static void removePDF(UserDto userDto, Long resumeId) {
        String fileName = buildPDFName(userDto, resumeId);
        File pdf = getPDF(fileName);
        pdf.delete();
    }

    public static void generateResumePDF(UserDto userDto, List<TaskDto> tasks) throws IOException {
        for (TaskDto taskDto : tasks) {
            try (PDDocument document = new PDDocument()) {

                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    // ContentStream
                    contentStream.beginText();
                    contentStream.setFont(font, 8);
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(200, 685);
                    writeTaskToPDF(contentStream, taskDto);
                    contentStream.endText();
                }

                //save to local
                document.save(buildPDFName(userDto, taskDto.getId()));
            }
        }
    }

    private static void writeTaskToPDF(PDPageContentStream contentStream, TaskDto taskDto) throws IOException {
        writeSingleLinePDFWithString(contentStream, "Task Name", taskDto.getName());
        writeSingleLinePDFWithString(contentStream, "Task Content", taskDto.getContent());
        writeSingleLinePDFWithString(contentStream, "Task Assignee", taskDto.getAssignee());
        writeSingleLinePDFWithString(contentStream, "Task Status", taskDto.getStatus().name());
        writeSingleLinePDFWithString(contentStream, "Task Important", String.valueOf(taskDto.isImportant()));
        writeLinkToPDF(contentStream, taskDto.getLinks());
    }

    private static void writeLinkToPDF(PDPageContentStream contentStream, Set<String> links) throws IOException {
        writeSingleLinePDFWithString(contentStream, "Reference Links", "");
        for(String link : links){
            contentStream.newLine();
            writeSingleLinePDFWithString(contentStream,"Link", link);
        }
    }

    private static void writeSingleLinePDFWithString(PDPageContentStream contentStream, String label, String value) throws IOException {
        contentStream.showText(label + ": " + value);
        contentStream.newLine();
    }
}
