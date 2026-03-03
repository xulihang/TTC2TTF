package com.xulihang;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String ttcFilePath = "msyh.ttc";
        String outputDir = "./";
        //List<String> outputFiles = TTC2TTF.extractTtfFromTtc(ttcFilePath, outputDir);
        //for (var name : outputFiles) {
        //    System.out.println(name);
        //}
        PDDocument document = new PDDocument();
        List<PDType0Font> fonts = TTC2TTF.loadTTCFontsToDoc(document,ttcFilePath);
        for (var font:fonts) {
            System.out.println(font.getName());
        }
    }
}