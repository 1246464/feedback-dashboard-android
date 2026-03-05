package com.example.visualizadorapp.utils;

import android.content.Context;
import android.os.Environment;
import com.example.visualizadorapp.model.Cardapio;
import com.example.visualizadorapp.model.Comentario;
import com.example.visualizadorapp.model.Reserva;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfExportHelper {
    private Context context;
    
    public PdfExportHelper(Context context) {
        this.context = context;
    }
    
    public interface OnExportListener {
        void onSuccess(String filePath);
        void onFailure(String error);
    }
    
    public void exportCardapios(List<Cardapio> cardapios, OnExportListener listener) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Relatorio_Cardapios_" + timestamp + ".pdf";
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
            
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Título
            Paragraph title = new Paragraph("Relatório de Cardápios")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            // Data
            Paragraph date = new Paragraph("Gerado em: " + 
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(date);
            
            document.add(new Paragraph("\n"));
            
            // Tabela
            Table table = new Table(new float[]{2, 3, 3, 3, 3, 3});
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Cabeçalhos
            String[] headers = {"Data", "Prato Principal", "Guarnição", "Acompanhamento", "Salada", "Sobremesa"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }
            
            // Dados
            for (Cardapio cardapio : cardapios) {
                table.addCell(new Cell().add(new Paragraph(formatDate(cardapio.getData()))));
                table.addCell(new Cell().add(new Paragraph(cardapio.getPratoPrincipal() != null ? cardapio.getPratoPrincipal() : "---")));
                table.addCell(new Cell().add(new Paragraph(cardapio.getGuarnicao() != null ? cardapio.getGuarnicao() : "---")));
                table.addCell(new Cell().add(new Paragraph(cardapio.getAcompanhamento() != null ? cardapio.getAcompanhamento() : "---")));
                table.addCell(new Cell().add(new Paragraph(cardapio.getSalada() != null ? cardapio.getSalada() : "---")));
                table.addCell(new Cell().add(new Paragraph(cardapio.getSobremesa() != null ? cardapio.getSobremesa() : "---")));
            }
            
            document.add(table);
            
            // Rodapé
            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("Total de registros: " + cardapios.size())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT);
            document.add(footer);
            
            document.close();
            
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
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Relatorio_Reservas_" + timestamp + ".pdf";
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
            
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Título
            Paragraph title = new Paragraph("Relatório de Reservas")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            // Data
            Paragraph date = new Paragraph("Gerado em: " + 
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(date);
            
            document.add(new Paragraph("\n"));
            
            // Tabela
            Table table = new Table(new float[]{3, 2, 2, 2});
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Cabeçalhos
            String[] headers = {"Usuário", "Data", "Status", "Observação"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }
            
            // Dados
            for (Reserva reserva : reservas) {
                table.addCell(new Cell().add(new Paragraph(reserva.getNomeUsuario() != null ? reserva.getNomeUsuario() : "---")));
                table.addCell(new Cell().add(new Paragraph(formatDate(reserva.getData()))));
                table.addCell(new Cell().add(new Paragraph(reserva.getStatusReserva())));
                table.addCell(new Cell().add(new Paragraph(reserva.getObservacao() != null ? reserva.getObservacao() : "---")));
            }
            
            document.add(table);
            
            // Estatísticas
            document.add(new Paragraph("\n"));
            int ativas = 0, canceladas = 0, utilizadas = 0;
            for (Reserva r : reservas) {
                switch (r.getStatusReserva()) {
                    case "ATIVA": ativas++; break;
                    case "CANCELADA": canceladas++; break;
                    case "UTILIZADA": utilizadas++; break;
                }
            }
            
            document.add(new Paragraph("Estatísticas:").setBold());
            document.add(new Paragraph("Total de reservas: " + reservas.size()));
            document.add(new Paragraph("Ativas: " + ativas));
            document.add(new Paragraph("Utilizadas: " + utilizadas));
            document.add(new Paragraph("Canceladas: " + canceladas));
            
            document.close();
            
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
