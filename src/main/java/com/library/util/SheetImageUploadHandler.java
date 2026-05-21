package com.library.util;

import com.library.model.Users;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;

public final class SheetImageUploadHandler {
    private SheetImageUploadHandler() {}

    public static void handle(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Users user = requireUser(request);
        if (user == null) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, null, "Please sign in to upload images.");
            return;
        }

        Part part = firstImagePart(request);
        if (part == null || part.getSize() == 0) {
            writeJson(response, HttpServletResponse.SC_BAD_REQUEST, null, "No image file provided.");
            return;
        }

        if (!isAllowedImage(part)) {
            writeJson(response, HttpServletResponse.SC_BAD_REQUEST, null, "Only image files are allowed.");
            return;
        }

        String submitted = part.getSubmittedFileName();
        String fileName = submitted == null ? "image.jpg" : Paths.get(submitted).getFileName().toString();

        try {
            ServletContext ctx = request.getServletContext();
            // 🛠️ ပြင်ဆင်ချက် - ပုံကြီးများကို စိတ်ချရသော Buffer ဖြင့် ဖတ်ရှုခြင်း
            byte[] bytes = readAllBytesSafe(part);
            String relative = SheetContentImages.saveUploadedFile(ctx, bytes, fileName);
            String url = request.getContextPath() + "/" + relative;
            writeJson(response, HttpServletResponse.SC_OK, url, null);
        } catch (IOException e) {
            e.printStackTrace();
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null,
                    "Could not save image. Check server write permissions for uploads/sheets.");
        }
    }

    private static Part firstImagePart(HttpServletRequest request) throws IOException, ServletException {
        // 🛠️ ပြင်ဆင်ချက် - Summernote က အသုံးများတဲ့ "image", "file", "files", "files[]" Key အားလုံးကို ရှာခိုင်းခြင်း
        Part part = request.getPart("image");
        if (part == null || part.getSize() == 0) {
            part = request.getPart("file");
        }
        if (part == null || part.getSize() == 0) {
            part = request.getPart("files");
        }
        if (part == null || part.getSize() == 0) {
            part = request.getPart("files[]");
        }
        return part;
    }

    // 🛠️ ပြင်ဆင်ချက် - ကြီးမားသော image stream များကို Memory မပြည့်စေဘဲ စိတ်ချရစွာ ဖတ်ပေးမည့် Helper Method
    private static byte[] readAllBytesSafe(Part part) throws IOException {
        try (InputStream in = part.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        }
    }

    private static boolean isAllowedImage(Part part) {
        String contentType = part.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        String name = part.getSubmittedFileName();
        if (name == null) {
            return contentType == null || contentType.isBlank();
        }
        String lower = name.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".gif") || lower.endsWith(".webp");
    }

    private static Users requireUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Users) session.getAttribute("user");
    }

    private static void writeJson(HttpServletResponse response, int status, String url, String error)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("{");
            if (url != null) {
                out.print("\"url\":" + JsonUtil.string(url));
            } else {
                out.print("\"url\":null");
            }
            if (error != null) {
                out.print(",\"error\":" + JsonUtil.string(error));
            }
            out.print("}");
        }
    }
}