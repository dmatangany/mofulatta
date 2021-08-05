package com.vaadin.resource;

public class ConceptOrder {
  private String thetype = "";
  private String name = "";
  private String iinterface = "None";
  private String function = "None";
  private String concept = "";
  private String target = "Self";
  private boolean processed = false;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFunction() {
    return function;
  }

  public void setFunction(String function) {
    this.function = function;
  }

  public String getIinterface() {
    return iinterface;
  }

  public void setIinterface(String iinterface) {
    this.iinterface = iinterface;
  }

  public String getConcept() {
    return concept;
  }

  public void setConcept(String concept) {
    this.concept = concept;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getThetype() {
    return thetype;
  }

  public void setThetype(String thetype) {
    this.thetype = thetype;
  }

  public boolean getProcessed() {
    return processed;
  }

  public void setProcessed(boolean processed) {
    this.processed = processed;
  }
}
