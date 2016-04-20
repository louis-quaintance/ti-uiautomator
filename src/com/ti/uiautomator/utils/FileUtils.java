package com.ti.uiautomator.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> readFile(String filePath) throws Exception {

        ArrayList<String> list = new ArrayList<String>();

        FileInputStream fis = new FileInputStream(new File(filePath));

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.equals("")) {
                list.add(line);
            }
        }

        br.close();

        return list;
    }
}
