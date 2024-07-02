package org.moroboshidan.entity;


public class ClientBalance {

  private long id;
  private long clientId;
  private long balance;
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


  public long getClientId() {
    return clientId;
  }

  public void setClientId(long clientId) {
    this.clientId = clientId;
  }


  public long getBalance() {
    return balance;
  }

  public void setBalance(long balance) {
    this.balance = balance;
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
