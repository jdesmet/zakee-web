/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.desmet.jo.zakee;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author jdesmet
 */
@RequestScoped
@Named
public class Who implements Serializable {
  @Inject
  private You you;

  public You getYou() {
    return you;
  }

  public void setYou(You you) {
    this.you = you;
  }
}
