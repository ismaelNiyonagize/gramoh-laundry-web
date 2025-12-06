package com.gramoh.laundry.gramoh_laundry_web.controller;

import com.gramoh.laundry.gramoh_laundry_web.model.*;
import com.gramoh.laundry.gramoh_laundry_web.model.Package;
import com.gramoh.laundry.gramoh_laundry_web.service.ClothesService;
import com.gramoh.laundry.gramoh_laundry_web.service.GarmentTypeService;
import com.gramoh.laundry.gramoh_laundry_web.service.OrderService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/clothes")
public class ClothesController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ClothesService clothesService;

    @Autowired
    private GarmentTypeService garmentTypeService;

    /**
     * ✅ Add multiple clothes items to an order
     */
    @PostMapping("/{orderId}/clothes")
    public String saveClothes(
            @PathVariable Long orderId,
            @RequestParam("garmentId") Long[] garmentIds,
            @RequestParam("quantity") Integer[] quantities,
            @RequestParam("notes") String[] notes,
            @RequestParam("imageFile") MultipartFile[] imageFiles
    ) throws IOException {

        if (garmentIds.length != quantities.length || garmentIds.length != notes.length
                || garmentIds.length != imageFiles.length) {
            throw new IllegalArgumentException("Mismatch in form data arrays");
        }

        for (int i = 0; i < garmentIds.length; i++) {
            Long garmentId = garmentIds[i];
            Integer qty = quantities[i];
            String note = notes[i];
            MultipartFile image = imageFiles[i];
            clothesService.addClothesToOrder(orderId, garmentId, qty, note, image);
        }

        return "redirect:/admin/order/" + orderId + "/clothes";
    }

    /**
     * ✅ Generate Clothes PDF Report
     */
    @GetMapping("/pdf/{orderId}")
    public void generateClothesPdf(@PathVariable Long orderId, HttpServletResponse response)
            throws IOException, DocumentException {

        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        if (!(order.getStatus() == OrderStatus.AWAITING_APPROVAL
                || order.getStatus() == OrderStatus.WASHING
                || order.getStatus() == OrderStatus.DELIVERED)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "PDF available only for orders awaiting approval, washing or delivered");
            return;
        }

        List<Clothes> clothesList = order.getClothes();
        Client client = order.getClient();
        Package pkg = client.getSubscribedPackage();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=order_" + orderId + "_clothes.pdf");

        Document document = new Document(PageSize.A4, 25, 25, 25, 25);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        // ---- HEADER ----
        document.add(new Paragraph("Gramoh Laundry - Clothes Report", titleFont));
        document.add(new Paragraph("Client: " + client.getFullName(), smallFont));
        document.add(new Paragraph("Order ID: " + orderId + " | Status: " + order.getStatus(), smallFont));
        document.add(new Paragraph("Notes: " + order.getServiceType(), smallFont));
        document.add(Chunk.NEWLINE);

        // ---- PACKAGE + WEIGHT INFO ----
        try {
            YearMonth currentMonth = YearMonth.now();

            // ✅ Fetch client orders via OrderService
            List<Order> clientOrders = orderService.getOrdersForClient(client);

            double usedWeight = clientOrders.stream()
                    .filter(o -> o.getOrderDate().getYear() == currentMonth.getYear()
                            && o.getOrderDate().getMonthValue() == currentMonth.getMonthValue())
                    .flatMap(o -> o.getClothes().stream())
                    .mapToDouble(Clothes::getWeight)
                    .sum();

            double currentOrderWeight = clothesList.stream()
                    .mapToDouble(Clothes::getWeight)
                    .sum();

            double limit = pkg != null ? pkg.getWeightLimit() : 0.0;
            double remaining = limit > 0 ? Math.max(0, limit - usedWeight) : 0.0;

            document.add(new Paragraph("Subscription Package: " + (pkg != null ? pkg.getName() : "N/A"), normalFont));
            document.add(new Paragraph(String.format("Package Limit / Ibiro Byukwezi: %.1f kg / month", limit), normalFont));
            document.add(new Paragraph(String.format("Used This Month / Ibiro bimaze Gukoreshwa: %.1f kg", usedWeight), normalFont));
            document.add(new Paragraph(String.format("Remaining Weight / Ibiro Bisigaye: %.1f kg", remaining), normalFont));
            document.add(new Paragraph(String.format("This Order Weight / Ibiro byiyi Oda : %.1f kg", currentOrderWeight), normalFont));
            document.add(Chunk.NEWLINE);

        } catch (Exception e) {
            document.add(new Paragraph("⚠️ Unable to load package/weight details.", smallFont));
            e.printStackTrace();
        }

        // ---- CLOTHES GRID (2 per row) ----
        PdfPTable grid = new PdfPTable(2);
        grid.setWidthPercentage(100);
        grid.setSpacingBefore(5f);
        grid.setSpacingAfter(5f);
        grid.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        grid.getDefaultCell().setPadding(5f);

        String baseUploadDir = "D:/Gramoh/System to deploy/gramoh-laundry-web/uploads/";

        for (int i = 0; i < clothesList.size(); i++) {
            Clothes c = clothesList.get(i);

            PdfPTable block = new PdfPTable(1);
            block.setWidthPercentage(100);
            block.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            block.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            block.getDefaultCell().setPadding(3f);

            Image img = null;
            try {
                String path = null;
                if (c.getImageUrl() != null && !c.getImageUrl().isEmpty()) {
                    String cleanUrl = c.getImageUrl().replace("\\", "/");
                    if (cleanUrl.contains("uploads/order_")) {
                        path = "D:/Gramoh/System to deploy/gramoh-laundry-web/" + cleanUrl;
                    } else {
                        path = baseUploadDir + "order_" + order.getId() + "/" + cleanUrl;
                    }
                }
                if (path != null) {
                    File f = new File(path);
                    if (f.exists()) {
                        img = Image.getInstance(f.toURI().toURL());
                        img.scaleToFit(80, 80);
                    }
                }
            } catch (Exception ignored) {}

            if (img != null) {
                PdfPCell imgCell = new PdfPCell(img, true);
                imgCell.setBorder(Rectangle.BOX);
                imgCell.setFixedHeight(90f);
                imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                imgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                block.addCell(imgCell);
            } else {
                PdfPCell placeholder = new PdfPCell(new Phrase("No Image", smallFont));
                placeholder.setFixedHeight(90f);
                placeholder.setHorizontalAlignment(Element.ALIGN_CENTER);
                placeholder.setVerticalAlignment(Element.ALIGN_MIDDLE);
                placeholder.setBorder(Rectangle.BOX);
                block.addCell(placeholder);
            }

            // ✅ Show garment name, quantity, and weight
            String garmentName = c.getGarmentType() != null ? c.getGarmentType().getName() : "Unknown";
            String label = garmentName +
                    " ×" + c.getQuantity() +
                    (c.getNotes() != null ? " - " + c.getNotes() : "") +
                    String.format(" (%.1f kg)", c.getWeight());

            PdfPCell noteCell = new PdfPCell(new Phrase(label, smallFont));
            noteCell.setBorder(Rectangle.NO_BORDER);
            noteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            noteCell.setPaddingTop(3f);
            block.addCell(noteCell);

            PdfPCell gridCell = new PdfPCell(block);
            gridCell.setPadding(5f);
            gridCell.setBorder(Rectangle.NO_BORDER);
            grid.addCell(gridCell);

            // Fill empty cell if odd count
            if (i == clothesList.size() - 1 && clothesList.size() % 2 != 0) {
                PdfPCell empty = new PdfPCell(new Phrase(""));
                empty.setBorder(Rectangle.NO_BORDER);
                grid.addCell(empty);
            }
        }

        document.add(grid);

        Paragraph total = new Paragraph("Total Clothes: " + clothesList.size(), smallFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();
    }
}
