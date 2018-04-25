package com.shawn.framework.codegenerator;

public class ControllerCodeCollector implements CodeInfoCollector {

    @Override
  public void makeInfo(CodeInfo info) {
    String packageName = info.getBasePackageName() + ".controller";
    info.setPackageName(packageName);
    info.setTemplateName("controller.ftl");
    int index = info.getBasePackageName().lastIndexOf('.') + 1;
    info.setModule(index > 0 ? info.getBasePackageName().substring(index) : info.getBasePackageName());
    info.setFilename(info.getClassSimpleName() + "Controller.java");
    info.setOutput(CodeUtil.getJavaPath(packageName));
  }
}
