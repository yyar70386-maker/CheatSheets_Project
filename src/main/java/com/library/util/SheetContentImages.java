package com.library.util;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Stores cheat sheet inline images on disk and rewrites HTML for reliable display. */
public final class SheetContentImages {
    // 🛠️ ပြင်ဆင်ချက် - Max Image Size ကို Servlet အတိုင်း 50MB အထိ တိုးမြှင့်ပေးလိုက်ပါတယ်
    private static final int MAX_INLINE_BYTES = 50 * 1024 * 1024;
    
    // Base64 Regex ကို ပိုမိုကျယ်ပြန့်စွာ ဖမ်းယူနိုင်အောင် ပြင်ဆင်ထားပါတယ်
    private static final Pattern DATA_URI_SRC = Pattern.compile(
            "src\\s*=\\s*([\"'])data:image/(png|jpe?g|gif|webp);base64,([A-Za-z0-9+/=\\s\\-_]+)\\1",
            Pattern.CASE_INSENSITIVE);

    private SheetContentImages() {}

    public static String persistInlineImages(String html, ServletContext ctx) {
        if (html == null || html.isBlank() || !html.contains("data:image")) {
            return html;
        }
        Matcher matcher = DATA_URI_SRC.matcher(html);
        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            String quote = matcher.group(1);
            String subtype = matcher.group(2).toLowerCase();
            String base64 = matcher.group(3).replaceAll("\\s+", ""); // spacing တွေကို ဖယ်ရှားခြင်း
            
            String relative = saveDecodedImage(ctx, subtype, base64);
            
            // 🛠️ ပြင်ဆင်ချက် - အကယ်၍ ပုံသိမ်းရတာ အဆင်မပြေရင် ဒေတာဘေ့စ်မပိအောင် ပုံပျက်နေတဲ့ img tag နေရာမှာ အစားထိုးပေးခြင်း
            String replacement = relative == null
                    ? "src=" + quote + "data:image/png;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" + quote + " data-broken-img='true'" // 1x1 transparent spacer ဖြင့် အစားထိုးခြင်း
                    : "src=" + quote + relative + quote;
            
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    public static String resolveForDisplay(String html, String contextPath) {
        if (html == null || html.isBlank()) {
            return html == null ? "" : html;
        }
        String ctx = contextPath == null ? "" : contextPath;
        if (ctx.endsWith("/")) {
            ctx = ctx.substring(0, ctx.length() - 1);
        }
        if (ctx.isEmpty()) {
            return html;
        }
        Pattern relative = Pattern.compile(
                "src=(\"|')(uploads/(?:sheets|avatars)/[^\"']+)\\1",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = relative.matcher(html);
        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            String quote = matcher.group(1);
            String path = matcher.group(2);
            matcher.appendReplacement(out, Matcher.quoteReplacement("src=" + quote + ctx + "/" + path + quote));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    public static String saveUploadedFile(ServletContext ctx, byte[] bytes, String originalName) throws IOException {
        String ext = extensionFromName(originalName);
        if (ext == null) {
            ext = ".jpg";
        }
        Path dir = sheetUploadDir(ctx);
        Files.createDirectories(dir);
        String fileOnly = UUID.randomUUID() + ext;
        Path target = dir.resolve(fileOnly);
        Files.write(target, bytes);
        mirrorToWebappIfNeeded(ctx, target);
        return "uploads/sheets/" + fileOnly;
    }

    public static Path sheetUploadDir(ServletContext ctx) throws IOException {
        String realPath = ctx.getRealPath("/uploads/sheets");
        if (realPath != null) {
            return Paths.get(realPath);
        }
        URL sheetsUrl = ctx.getResource("/uploads/sheets");
        if (sheetsUrl != null && "file".equals(sheetsUrl.getProtocol())) {
            return pathFromFileUrl(sheetsUrl);
        }
        URL rootUrl = ctx.getResource("/");
        if (rootUrl != null && "file".equals(rootUrl.getProtocol())) {
            Path dir = pathFromFileUrl(rootUrl).resolve("uploads").resolve("sheets");
            Files.createDirectories(dir);
            return dir;
        }
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null) {
            Path dir = Paths.get(catalinaBase, "cheatsheets-data", "uploads", "sheets");
            Files.createDirectories(dir);
            return dir;
        }
        Path dir = Paths.get(System.getProperty("user.home"), "cheatsheets-data", "uploads", "sheets");
        Files.createDirectories(dir);
        return dir;
    }

    private static void mirrorToWebappIfNeeded(ServletContext ctx, Path savedFile) {
        try {
            String webRoot = ctx.getRealPath("/uploads/sheets");
            if (webRoot == null) {
                URL rootUrl = ctx.getResource("/");
                if (rootUrl == null || !"file".equals(rootUrl.getProtocol())) {
                    return;
                }
                Path webDir = pathFromFileUrl(rootUrl).resolve("uploads").resolve("sheets");
                if (!savedFile.startsWith(webDir)) {
                    Files.createDirectories(webDir);
                    Files.copy(savedFile, webDir.resolve(savedFile.getFileName().toString()),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                return;
            }
            Path webDir = Paths.get(webRoot);
            if (!savedFile.startsWith(webDir)) {
                Files.createDirectories(webDir);
                Files.copy(savedFile, webDir.resolve(savedFile.getFileName().toString()),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
        }
    }

    private static Path pathFromFileUrl(URL url) throws IOException {
        try {
            return Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new IOException("Invalid upload path in web application", e);
        }
    }

    private static String saveDecodedImage(ServletContext ctx, String subtype, String base64) {
        try {
            // Base64 စာသားတွေကို သန့်စင်ပြီးမှ Decode လုပ်ခြင်း
            byte[] bytes = Base64.getDecoder().decode(base64);
            if (bytes.length == 0 || bytes.length > MAX_INLINE_BYTES) {
                System.out.println("❌ Image rejected: Size out of bounds (" + bytes.length + " bytes)");
                return null;
            }
            String ext = switch (subtype) {
                case "png" -> ".png";
                case "gif" -> ".gif";
                case "webp" -> ".webp";
                default -> ".jpg";
            };
            Path dir = sheetUploadDir(ctx);
            Files.createDirectories(dir);
            String fileOnly = UUID.randomUUID() + ext;
            Path target = dir.resolve(fileOnly);
            Files.write(target, bytes);
            mirrorToWebappIfNeeded(ctx, target);
            return "uploads/sheets/" + fileOnly;
        } catch (IllegalArgumentException | IOException e) {
            System.out.println("❌ Exception decoding/saving image: " + e.getMessage());
            return null;
        }
    }

    private static String extensionFromName(String name) {
        if (name == null || !name.contains(".")) {
            return null;
        }
        String ext = name.substring(name.lastIndexOf('.')).toLowerCase();
        return switch (ext) {
            case ".png", ".jpg", ".jpeg", ".gif", ".webp" -> ext.equals(".jpeg") ? ".jpg" : ext;
            default -> null;
        };
    }
}