package org.moroboshidan.entity;


import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ClientBusiness {

  private long id;
  private String corpname;
  private String apikey;
  private String ipAddress;
  private long isCallback;
  private String callbackUrl;
  private String clientLinkname;
  private String clientPhone;
  private String clientFilters;
  private java.sql.Timestamp created;
  private long createId;
  private java.sql.Timestamp updated;
  private long updateId;
  private long isDelete;
  private String extend1;
  private String extend2;
  private String extend3;
  private String extend4;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getCorpname() {
    return corpname;
  }

  public void setCorpname(String corpname) {
    this.corpname = corpname;
  }


  public String getApikey() {
    return apikey;
  }

  public void setApikey(String apikey) {
    this.apikey = apikey;
  }


  public List<String> getIpAddress() {
    String ips = ipAddress;
    if(!StringUtils.isEmpty(ips)){
      return Arrays.asList(ips.split(","));
    }
    return null;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }


  public long getIsCallback() {
    return isCallback;
  }

  public void setIsCallback(long isCallback) {
    this.isCallback = isCallback;
  }


  public String getCallbackUrl() {
    return callbackUrl;
  }

  public void setCallbackUrl(String callbackUrl) {
    this.callbackUrl = callbackUrl;
  }


  public String getClientLinkname() {
    return clientLinkname;
  }

  public void setClientLinkname(String clientLinkname) {
    this.clientLinkname = clientLinkname;
  }


  public String getClientPhone() {
    return clientPhone;
  }

  public void setClientPhone(String clientPhone) {
    this.clientPhone = clientPhone;
  }


  public String getClientFilters() {
    return clientFilters;
  }

  public void setClientFilters(String clientFilters) {
    this.clientFilters = clientFilters;
  }


  public java.sql.Timestamp getCreated() {
    return created;
  }

  public void setCreated(java.sql.Timestamp created) {
    this.created = created;
  }


  public long getCreateId() {
    return createId;
  }

  public void setCreateId(long createId) {
    this.createId = createId;
  }


  public java.sql.Timestamp getUpdated() {
    return updated;
  }

  public void setUpdated(java.sql.Timestamp updated) {
    this.updated = updated;
  }


  public long getUpdateId() {
    return updateId;
  }

  public void setUpdateId(long updateId) {
    this.updateId = updateId;
  }


  public long getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(long isDelete) {
    this.isDelete = isDelete;
  }


  public String getExtend1() {
    return extend1;
  }

  public void setExtend1(String extend1) {
    this.extend1 = extend1;
  }


  public String getExtend2() {
    return extend2;
  }

  public void setExtend2(String extend2) {
    this.extend2 = extend2;
  }


  public String getExtend3() {
    return extend3;
  }

  public void setExtend3(String extend3) {
    this.extend3 = extend3;
  }


  public String getExtend4() {
    return extend4;
  }

  public void setExtend4(String extend4) {
    this.extend4 = extend4;
  }

}
