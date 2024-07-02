package org.moroboshidan.entity;


public class ClientTemplate {

  private long id;
  private long signId;
  private String templateText;
  private long templateType;
  private long templateState;
  private long useId;
  private String useWeb;
  private java.sql.Timestamp created;
  private long createId;
  private java.sql.Timestamp updated;
  private long updateId;
  private long isDelete;
  private String extend1;
  private String extend2;
  private String extend3;
  private String extend4;

  @Override
  public String toString() {
    return "ClientTemplate{" +
            "id=" + id +
            ", signId=" + signId +
            ", templateText='" + templateText + '\'' +
            ", templateType=" + templateType +
            ", templateState=" + templateState +
            ", useId=" + useId +
            ", useWeb='" + useWeb + '\'' +
            ", created=" + created +
            ", createId=" + createId +
            ", updated=" + updated +
            ", updateId=" + updateId +
            ", isDelete=" + isDelete +
            ", extend1='" + extend1 + '\'' +
            ", extend2='" + extend2 + '\'' +
            ", extend3='" + extend3 + '\'' +
            ", extend4='" + extend4 + '\'' +
            '}';
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getSignId() {
    return signId;
  }

  public void setSignId(long signId) {
    this.signId = signId;
  }


  public String getTemplateText() {
    return templateText;
  }

  public void setTemplateText(String templateText) {
    this.templateText = templateText;
  }


  public long getTemplateType() {
    return templateType;
  }

  public void setTemplateType(long templateType) {
    this.templateType = templateType;
  }


  public long getTemplateState() {
    return templateState;
  }

  public void setTemplateState(long templateState) {
    this.templateState = templateState;
  }


  public long getUseId() {
    return useId;
  }

  public void setUseId(long useId) {
    this.useId = useId;
  }


  public String getUseWeb() {
    return useWeb;
  }

  public void setUseWeb(String useWeb) {
    this.useWeb = useWeb;
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
