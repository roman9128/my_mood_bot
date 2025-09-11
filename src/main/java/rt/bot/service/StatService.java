package rt.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import rt.bot.entity.BotUser;
import rt.bot.repo.BotUserRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatService {

    private final BotUserRepository botUserRepository;

    public InputFile getReport() {
        try {
            InputStream is = exportAllDataToInputStream();
            return new InputFile(is, "report.xlsx");
        } catch (IOException e) {
            log.error("Не удалось подготовить отчёт в XLSX");
            return null;
        }
    }

    private InputStream exportAllDataToInputStream() throws IOException {
        List<BotUser> users = botUserRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Пользователи");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle summaryStyle = createSummaryStyle(workbook);
        createHeaders(sheet, headerStyle);
        fillData(sheet, users, dateStyle);
        addSummaryRow(sheet, users.size(), summaryStyle);
        autoSizeColumns(sheet);
        return convertToInputStream(workbook);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy hh:mm"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createSummaryStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void createHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);

        String[] headers = {
                "ID пользователя",
                "Username",
                "Имя",
                "Фамилия",
                "Статус",
                "Дата включения в БД"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillData(Sheet sheet, List<BotUser> botUsers, CellStyle dateStyle) {
        int rowNum = 1;

        for (BotUser user : botUsers) {
            Row row = sheet.createRow(rowNum++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(user.getTelegramUserId());

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(user.getTelegramUsername() != null ? user.getTelegramUsername() : "");

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(user.getTelegramFirstName() != null ? user.getTelegramFirstName() : "");

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(user.getTelegramLastName() != null ? user.getTelegramLastName() : "");

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(user.getStatus().toString());

            Cell cell5 = row.createCell(5);
            if (user.getCreatedAt() != null) {
                cell5.setCellValue(user.getCreatedAt());
                cell5.setCellStyle(dateStyle);
            }
        }
    }

    private void addSummaryRow(Sheet sheet, int userCount, CellStyle summaryStyle) {
        int lastRowNum = sheet.getLastRowNum() + 1;
        Row summaryRow = sheet.createRow(lastRowNum);

        sheet.addMergedRegion(new CellRangeAddress(lastRowNum, lastRowNum, 0, 4));

        Cell summaryHeaderCell = summaryRow.createCell(0);
        summaryHeaderCell.setCellValue("Всего пользователей: " + userCount);
        summaryHeaderCell.setCellStyle(summaryStyle);

        Cell emptyCell = summaryRow.createCell(5);
        emptyCell.setCellStyle(summaryStyle);
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private InputStream convertToInputStream(Workbook workbook) throws IOException {
        try (workbook; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}