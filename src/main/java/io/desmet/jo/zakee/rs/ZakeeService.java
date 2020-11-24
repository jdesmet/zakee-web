/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.desmet.jo.zakee.rs;

import io.desmet.jo.zakee.Who;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jd3714
 */
@Path("zakee-service")
// Apparently @Context only works when we explictly mark the scope as well.
// Anyhow as per IBM it is a good practice to explicitly mark a RS service as RequestScoped.
@RequestScoped
public class ZakeeService {
  @Context HttpServletRequest httpServletContext;
  String contextPath;
  
  @Inject Who who;
  
  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("context")
  public String processContextPath() {
    return "This "+contextPath+" service is alife!";
  }
  
  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("who")
  public String processWho() {
    return "Service for "+who.getYou().getFirstName() + " (using CDI)";
  }
  
  @PostConstruct
  public void init() {
    contextPath = httpServletContext.getContextPath();
  }
}
