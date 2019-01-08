package ext.mocker.common.httpserver;

public abstract interface ResponseClosure<T>
{
  public abstract Object call(T paramT);
}


