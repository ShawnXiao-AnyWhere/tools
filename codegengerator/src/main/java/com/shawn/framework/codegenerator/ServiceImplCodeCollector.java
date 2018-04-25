package com.shawn.framework.codegenerator;


public class ServiceImplCodeCollector implements CodeInfoCollector {

  @Override
  public void makeInfo(CodeInfo info) {
    String packageName = info.getBasePackageName() + ".impl";
    info.setPackageName(packageName);
    info.setTemplateName("serviceImpl.ftl");
    info.setFilename(info.getClassSimpleName() + "ServiceImpl.java");
    info.setOutput(CodeUtil.getJavaPath(packageName));
  }
}
