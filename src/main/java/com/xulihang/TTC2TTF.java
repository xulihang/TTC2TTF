package com.xulihang;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TTC2TTF {
    public static List<PDType0Font> loadTTCFontsToDoc(PDDocument doc, TrueTypeCollection ttc) throws IOException {
        List<PDType0Font> fonts = new ArrayList<>();
        // 使用processAllFonts方法处理所有字体
        ttc.processAllFonts(new TrueTypeCollection.TrueTypeFontProcessor() {
            @Override
            public void process(TrueTypeFont ttf) throws IOException {
                PDType0Font font = PDType0Font.load(doc, ttf, true);
                fonts.add(font);
            }
        });
        return fonts;
    }

    public static List<PDType0Font> loadTTCFontsToDoc2(PDDocument doc, String ttcFilePath) {
        File ttcFile = new File(ttcFilePath);
        List<PDType0Font> fonts = new ArrayList<>();
        // 使用try-with-resources确保TTC集合被正确关闭
        try (TrueTypeCollection ttc = new TrueTypeCollection(ttcFile)) {
            // 使用processAllFonts方法处理所有字体
            ttc.processAllFonts(new TrueTypeCollection.TrueTypeFontProcessor() {
                @Override
                public void process(TrueTypeFont ttf) throws IOException {
                    PDType0Font font = PDType0Font.load(doc, ttf, true);
                    fonts.add(font);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fonts;
    }

    public static List<String> extractTtfFromTtc(String ttcFilePath, String outputDir) {
        File ttcFile = new File(ttcFilePath);

        // 创建输出目录
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        List<String> outputFiles = new ArrayList<>();
        // 使用try-with-resources确保TTC集合被正确关闭
        try (TrueTypeCollection ttc = new TrueTypeCollection(ttcFile)) {
            // 使用processAllFonts方法处理所有字体
            ttc.processAllFonts(new TrueTypeCollection.TrueTypeFontProcessor() {
                @Override
                public void process(TrueTypeFont ttf) throws IOException {
                    // process方法会在每个字体上被调用
                    // 注意：这里不需要手动关闭ttf，processAllFonts会负责管理资源

                    String fontName = null;
                    try {
                        // 获取字体名称（用于文件名）
                        fontName = ttf.getName();
                        if (fontName == null || fontName.isEmpty()) {
                            // 如果没有名称，使用字体索引作为标识
                            // 但由于processAllFonts不直接提供索引，我们可以通过其他方式获取
                            fontName = "font_unknown";
                        }

                        // 处理文件名中的非法字符
                        String safeFileName = fontName.replaceAll("[\\\\/:*?\"<>|]", "_");

                        // 创建输出文件
                        File outputFile = new File(outputDirectory, safeFileName + ".ttf");

                        // 通过getOriginalData()获取输入流，然后复制到文件
                        try (InputStream fontDataStream = ttf.getOriginalData();
                             FileOutputStream fos = new FileOutputStream(outputFile)) {

                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = fontDataStream.read(buffer)) != -1) {
                                fos.write(buffer, 0, bytesRead);
                            }
                            outputFiles.add(outputFile.getName());
                        }
                    } catch (IOException e) {
                        System.err.println("处理字体 " + (fontName != null ? fontName : "未知") + " 时出错: " + e.getMessage());
                        throw e; // 重新抛出异常，让processAllFonts处理
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFiles;
    }
}
