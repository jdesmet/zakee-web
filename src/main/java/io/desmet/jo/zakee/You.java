/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.desmet.jo.zakee;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author jdesmet
 */
@Named
@SessionScoped
public class You implements Serializable {
  private String firstName;
  private String lastName;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public boolean isIntroduced() {
    return Objects.nonNull(firstName);
  }
  
  @PostConstruct
  public void init() {
    firstName = "Guest";
  }
}
