package com.shawn.framework.codegenerator;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;


public class Generator {

    private Generator() {
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     *
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, TemplateException {
        CodeMaker maker = new CodeMaker();
        List<CodeInfoCollector> codes = new ArrayList();
        codes.add(new ServiceCodeCollector());
        codes.add(new ServiceImplCodeCollector());
        codes.add(new ControllerCodeCollector());
        //codes.add(new HtmlCodeCollector());
        //

        Set<Class> set = ClassScaner.scan("com.gnnet", AutoGenerateCode.class);
        for (Class<?> cls : set) {
            if (cls.isAnnotationPresent(AutoGenerateCode.class)) {
                CodeInfo info = new CodeInfo();
                info.setRebuild(cls.getAnnotation(AutoGenerateCode.class).allwaysGenerate());
                String packageName = cls.getPackage().getName();
                String basePackage = StringUtils.remove(packageName, ".domain");
                info.setBasePackageName(basePackage);
                info.setClassName(cls.getName());
                info.setClassSimpleName(cls.getSimpleName());
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Map map = new HashMap();
                    map.put("code", fields[i].getName());
                    map.put("name", fields[i].getName());
                    map.put("comma", i != fields.length - 1);
                    map.put("checkbox", fields[i].getType().equals(Boolean.class)
                            || fields[i].getType().equals(boolean.class));
                    map.put("datetime", fields[i].getType().equals(Date.class));
                    info.addField(map);
                }

                for (CodeInfoCollector code : codes) {
                    code.makeInfo(info);
                    maker.make(info);
                }
            }
        }
    }
}
