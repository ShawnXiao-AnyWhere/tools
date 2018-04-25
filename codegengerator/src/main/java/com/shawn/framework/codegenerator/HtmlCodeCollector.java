package com.shawn.framework.codegenerator;


public class HtmlCodeCollector implements CodeInfoCollector {

  @Override
  public void makeInfo(CodeInfo info) {
    info.setTemplateName("html.ftl");
    info.setFilename(info.getClassSimpleName().toLowerCase() + ".html");
    info.setOutput(CodeUtil.getHtmlPath(info.getModule()));
  }
}
