package ext.httpmocker.service.model;

import java.io.Serializable;

public class ResponseTemplateEntry
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String fileName;
  private long lastModified;
  private long fileSize;
  private String profile;
  private String fileContent;
  private SyncType syncType;
  private Boolean overwrite;
  private SyncStatus status;

  public static enum SyncType
  {
    PULL(1),  PUSH(2);

    private final int value;

    private SyncType(int value)
    {
      this.value = value;
    }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum SyncStatus
  {
    DELETED(1),  NEW(2),  OBSOLETED(3),  UPDATED(4);

    private final int value;

    private SyncStatus(int value)
    {
      this.value = value;
    }

    public int getValue()
    {
      return this.value;
    }
  }

  public String getFileName()
  {
    return this.fileName;
  }

  public void setFileName(String value)
  {
    this.fileName = value;
  }

  public long getLastModified()
  {
    return this.lastModified;
  }

  public void setLastModified(long value)
  {
    this.lastModified = value;
  }

  public long getFileSize()
  {
    return this.fileSize;
  }

  public void setFileSize(long value)
  {
    this.fileSize = value;
  }

  public String getProfile()
  {
    return this.profile;
  }

  public void setProfile(String value)
  {
    this.profile = value;
  }

  public String getFileContent()
  {
    return this.fileContent;
  }

  public void setFileContent(String value)
  {
    this.fileContent = value;
  }

  public SyncType getSyncType()
  {
    return this.syncType;
  }

  public void setSyncType(SyncType value)
  {
    this.syncType = value;
  }

  public Boolean getOverwrite()
  {
    return this.overwrite;
  }

  public void setOverwrite(Boolean value)
  {
    this.overwrite = value;
  }

  public SyncStatus getStatus()
  {
    return this.status;
  }

  public void setStatus(SyncStatus value)
  {
    this.status = value;
  }

  public String toString()
  {
    return "fileName:" + this.fileName + "," + "lastModified:" + this.lastModified + "," + "fileSize:" + this.fileSize + "," + "profile:" + this.profile + "," + "fileContent:" + this.fileContent + "," + "syncType:" + this.syncType + "," + "overwrite:" + this.overwrite + "," + "status:" + this.status;
  }
}


