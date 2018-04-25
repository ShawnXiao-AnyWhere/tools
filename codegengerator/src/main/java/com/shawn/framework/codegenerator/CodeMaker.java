package com.shawn.framework.codegenerator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;


public class CodeMaker {

  private final Logger logger = LoggerFactory.getLogger(CodeMaker.class);

  public void make(CodeInfo info) throws IOException, TemplateException {
    Configuration cfg = new Configuration();
    cfg.setClassForTemplateLoading(this.getClass(), "/codetemplates");
    Template tempalate = cfg.getTemplate(info.getTemplateName());
    StringWriter writer = new StringWriter();
    tempalate.process(info.asMap(), writer);
    File codeFile = new File(info.getOutput(), info.getFilename());
    if (!codeFile.exists() || info.isRebuild()) {
      logger.info("生成代码...{}", codeFile);
      File file=codeFile.getParentFile();
      if (!file.exists()) {
        file.mkdirs();
      }
      FileWriter fileWriter = new FileWriter(codeFile);
      fileWriter.write(writer.toString());
      fileWriter.close();
    }
  }
}
