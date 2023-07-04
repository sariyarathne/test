package com.sysco.perso.analytics.client.crm.schema;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CRMGenericResponse {

  private String id;
  private boolean success;
  private String[] errors;

}
