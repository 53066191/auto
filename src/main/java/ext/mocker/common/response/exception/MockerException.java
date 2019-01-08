package ext.mocker.common.response.exception;

public class MockerException
  extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public MockerException(String message)
  {
    super(message);
  }

  public MockerException(String message, Throwable e)
  {
    super(message, e);
  }

  public MockerException(Throwable e)
  {
    super(e);
  }
}


