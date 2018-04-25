package com.shawn.framework.codegenerator;


public class ServiceCodeCollector implements CodeInfoCollector {

  @Override
  public void makeInfo(CodeInfo info) {
    String packageName = info.getBasePackageName() + ".service";
    info.setPackageName(packageName);
    info.setTemplateName("service.ftl");
    info.setFilename(info.getClassSimpleName() + "Service.java");
    info.setOutput(CodeUtil.getJavaPath(packageName));
  }
}
