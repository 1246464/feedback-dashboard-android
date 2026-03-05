package com.example.visualizadorapp.utils;

import android.content.Context;
import android.os.Environment;
import com.example.visualizadorapp.model.Cardapio;
import com.example.visualizadorapp.model.Reserva;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcelExportHelper {
    private Context context;
    
    public ExcelExportHelper(Context context) {
        this.context = context;
    }
    
    public interface OnExportListener {
        void onSuccess(String filePath);
        void onFailure(String error);
    }
    
    public void exportCardapios(List<Cardapio> cardapios, OnExportListener listener) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Cardápios");
            
            // Estilo para cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Data", "Prato Principal", "Guarnição", "Acompanhamento", "Salada", "Sobremesa", "Favorito"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Dados
            int rowNum = 1;
            for (Cardapio cardapio : cardapios) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(formatDate(cardapio.getData()));
                row.createCell(1).setCellValue(cardapio.getPratoPrincipal() != null ? cardapio.getPratoPrincipal() : "---");
                row.createCell(2).setCellValue(cardapio.getGuarnicao() != null ? cardapio.getGuarnicao() : "---");
                row.createCell(3).setCellValue(cardapio.getAcompanhamento() != null ? cardapio.getAcompanhamento() : "---");
                row.createCell(4).setCellValue(cardapio.getSalada() != null ? cardapio.getSalada() : "---");
                row.createCell(5).setCellValue(cardapio.getSobremesa() != null ? cardapio.getSobremesa() : "---");
                row.createCell(6).setCellValue(cardapio.isFavorito() ? "Sim" : "Não");
            }
            
            // Auto-ajustar colunas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Salvar arquivo
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Relatorio_Cardapios_" + timestamp + ".xlsx";
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
            
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            
            if (listener != null) {
                listener.onSuccess(file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }
    }
    
    public void exportReservas(List<Reserva> reservas, OnExportListener listener) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Reservas");
            
            // Estilo para cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Usuário", "Email", "Data", "Status", "Observação", "Data/Hora da Reserva"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Dados
            int rowNum = 1;
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            for (Reserva reserva : reservas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(reserva.getId());
                row.createCell(1).setCellValue(reserva.getNomeUsuario() != null ? reserva.getNomeUsuario() : "---");
                row.createCell(2).setCellValue(reserva.getEmailUsuario() != null ? reserva.getEmailUsuario() : "---");
                row.createCell(3).setCellValue(formatDate(reserva.getData()));
                row.createCell(4).setCellValue(reserva.getStatusReserva());
                row.createCell(5).setCellValue(reserva.getObservacao() != null ? reserva.getObservacao() : "---");
                row.createCell(6).setCellValue(dateTimeFormat.format(new Date(reserva.getTimestampReserva())));
            }
            
            // Auto-ajustar colunas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Adicionar sheet de estatísticas
            Sheet statsSheet = workbook.createSheet("Estatísticas");
            int ativas = 0, canceladas = 0, utilizadas = 0;
            for (Reserva r : reservas) {
                switch (r.getStatusReserva()) {
                    case "ATIVA": ativas++; break;
                    case "CANCELADA": canceladas++; break;
                    case "UTILIZADA": utilizadas++; break;
                }
            }
            
            int statsRow = 0;
            statsSheet.createRow(statsRow++).createCell(0).setCellValue("Total de Reservas: " + reservas.size());
            statsSheet.createRow(statsRow++).createCell(0).setCellValue("Ativas: " + ativas);
            statsSheet.createRow(statsRow++).createCell(0).setCellValue("Utilizadas: " + utilizadas);
            statsSheet.createRow(statsRow++).createCell(0).setCellValue("Canceladas: " + canceladas);
            statsSheet.autoSizeColumn(0);
            
            // Salvar arquivo
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Relatorio_Reservas_" + timestamp + ".xlsx";
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
            
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            
            if (listener != null) {
                listener.onSuccess(file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }
    }
    
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
}
