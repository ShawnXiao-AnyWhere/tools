package com.shawn.framework.codegenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CodeInfo {

  private String author;
  private String date;
  //
  private String basePackageName;
  private String packageName;
  private String module;
  private String className;
  private String classSimpleName;
  private String templateName;
  private List<Map> fields = new ArrayList<>();
  //
  private String output;
  private String filename;
  private boolean rebuild;

  public CodeInfo() {
    this.author = "Auto Generator";
    this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
  }

  public void setFields(List<Map> fields) {
    this.fields = fields;
  }

  public List<Map> getFields() {
    return fields;
  }

  public void addField(Map map) {
    this.fields.add(map);
  }

  public boolean isRebuild() {
    return rebuild;
  }

  public void setRebuild(boolean rebuild) {
    this.rebuild = rebuild;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getModule() {
    return module;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getDate() {
    return date;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getAuthor() {
    return author;
  }

  public void setBasePackageName(String basePackageName) {
    this.basePackageName = basePackageName;
  }

  public String getBasePackageName() {
    return basePackageName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getClassSimpleName() {
    return classSimpleName;
  }

  public void setClassSimpleName(String classSimpleName) {
    this.classSimpleName = classSimpleName;
  }

  public Map asMap() {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(this, HashMap.class);
  }
}
