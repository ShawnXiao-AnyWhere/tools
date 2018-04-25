package com.shawn.framework.codegenerator;

import java.io.File;

public class CodeUtil {

    private CodeUtil() {
    }

    public static String getJavaPath(String packageName) {
        String path = CodeUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File projectBaseDir = new File(path).getParentFile().getParentFile();
        File javaBaseDir = new File(projectBaseDir, "src/main/java");
        File javaPath = new File(javaBaseDir, packageName.replace(".", "/"));
        return javaPath.getPath();
    }

    public static String getHtmlPath(String module) {
        String path = CodeUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File projectBaseDir = new File(path).getParentFile().getParentFile();
        File javaBaseDir = new File(projectBaseDir, "src/main/webapp/WEB-INF");
        File javaPath = new File(javaBaseDir, module);
        return javaPath.getPath();
    }
}
