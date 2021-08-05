package com.vaadin.resource;

import com.vaadin.flow.component.Component;

public class LayoutObject {
  private Integer number = 0;
  private Component component = null;

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public Component getComponent() {
    return component;
  }

  public void setComponent(Component component) {
    this.component = component;
  }
}
