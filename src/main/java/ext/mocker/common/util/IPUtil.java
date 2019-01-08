  package ext.mocker.common.util;

  import org.apache.commons.lang3.StringUtils;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;

  import java.io.IOException;
  import java.net.*;
  import java.util.Enumeration;
  import java.util.concurrent.ConcurrentHashMap;
  import java.util.concurrent.ConcurrentMap;

  public class IPUtil
  {
    private static Logger logger = LoggerFactory.getLogger(IPUtil.class);
    private static final String LOCALHOST = "127.0.0.1";
    private static InetAddress localInetAddress;
    private static String localHostAddress = "127.0.0.1";
    private static String localHostAddressPrefix = "127.0.";
    private static ConcurrentMap<Integer, String> integer2IPV4Map = new ConcurrentHashMap();
    private static ConcurrentMap<String, Integer> IPV42IntegerMap = new ConcurrentHashMap();
    private static ConcurrentMap<String, InetAddress> inetAddressCache = new ConcurrentHashMap();

    static
    {
      try
      {
        localInetAddress = InetAddress.getLocalHost();
        if ((localInetAddress.getHostAddress() == null) || ("127.0.0.1".equals(localInetAddress.getHostAddress())))
        {
          NetworkInterface ni = NetworkInterface.getByName("bond0");
          if (ni == null) {
            ni = NetworkInterface.getByName("eth0");
          }
          if (ni == null) {
            throw new RuntimeException("wrong with get ip cause by could not read any info from local host,bond0 and eth0");
          }
          Enumeration<InetAddress> ips = ni.getInetAddresses();
          while (ips.hasMoreElements())
          {
            InetAddress nextElement = (InetAddress)ips.nextElement();
            if ((!"127.0.0.1".equals(nextElement.getHostAddress())) && (!(nextElement instanceof Inet6Address)) &&
              (!nextElement.getHostAddress().contains(":"))) {
              localInetAddress = nextElement;
            }
          }
        }
        setHostAddress(localInetAddress.getHostAddress());
      }
      catch (SocketException e)
      {
        logger.error("InetAddress.getLocalHost error.", e);
      }
      catch (Throwable e)
      {
        logger.error("[init IpUtils error][please configure hostname or bond0 or eth0]", e);
      }
    }

    public static InetAddress getAddresses(String ip)
    {
      InetAddress result = (InetAddress)inetAddressCache.get(ip);
      if (result == null)
      {
        try
        {
          result = InetAddress.getByName(ip);
        }
        catch (UnknownHostException e)
        {
          logger.error("InetAddress.getByName(+ip+) error.", e);
          result = localInetAddress;
        }
        inetAddressCache.put(ip, result);
      }
      return result;
    }

    private static void setHostAddress(String address)
    {
      localHostAddress = address;
      String[] hostAddressIpDigitals = StringUtils.split(address, ".", 4);
      localHostAddressPrefix = hostAddressIpDigitals[0] + '.' + hostAddressIpDigitals[1] + '.';
    }

    public static String integer2IPV4(Integer iIPV4)
    {
      if ((iIPV4 == null) || (iIPV4.intValue() == 0)) {
        return null;
      }
      String result = (String)integer2IPV4Map.get(iIPV4);
      if (result != null) {
        return result;
      }
      StringBuilder sb = new StringBuilder();
      sb.append(0xFF & iIPV4.intValue() >> 24).append('.').append(0xFF & iIPV4.intValue() >> 16).append('.')
        .append(0xFF & iIPV4.intValue() >> 8).append('.').append(0xFF & iIPV4.intValue());
      result = sb.toString();

      integer2IPV4Map.put(iIPV4, result);
      return result;
    }

    public static Integer IPV42Integer(String strIPV4)
    {
      if (strIPV4 == null) {
        return null;
      }
      Integer result = (Integer)IPV42IntegerMap.get(strIPV4);
      if (result != null) {
        return result;
      }
      String[] it = StringUtils.split(strIPV4, ".", 4);

      byte[] byteAddress = new byte[4];
      for (int i = 0; i < 4; i++)
      {
        int tempInt = Integer.parseInt(it[i]);
        byteAddress[i] = ((byte)tempInt);
      }
      result = Integer.valueOf((byteAddress[0] & 0xFF) << 24 | (byteAddress[1] & 0xFF) << 16 | (byteAddress[2] & 0xFF) << 8 | byteAddress[3] & 0xFF);

      IPV42IntegerMap.put(strIPV4, result);
      return result;
    }

    public static String localIp4Str()
    {
      return localHostAddress;
    }

    public static String localIp4Prefix()
    {
      return localHostAddressPrefix;
    }

    public static InetAddress localIp()
    {
      return localInetAddress;
    }

    public static InetAddress getInetAddress(String hostName)
    {
      InetAddress ipAddr;
      try
      {
        ipAddr = hostName != null ? getAddresses(hostName) : localIp();
      }
      catch (Exception e)
      {
         logger.warn("hostName format is worng：" + hostName, e);
        ipAddr = localIp();
      }
      return ipAddr;
    }

    public static String getHostAddress(SocketAddress socketAddress)
    {
      InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
      return inetSocketAddress.getAddress().getHostAddress() + ':' + inetSocketAddress.getPort();
    }

    public static InetAddress getInetAddress(SocketAddress socketAddress)
    {
      return ((InetSocketAddress)socketAddress).getAddress();
    }

    public static HostAndPort getHostAddressAndPort(SocketAddress socketAddress)
    {
      InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
      HostAndPort hostAndPort = new HostAndPort();
      hostAndPort.port = inetSocketAddress.getPort();
      hostAndPort.hostAddress = inetSocketAddress.getAddress().getHostAddress();
      return hostAndPort;
    }

    @Deprecated
    public static String getHostName(SocketAddress socketAddress)
    {
      if (socketAddress != null) {
        return getHostName(socketAddress.toString());
      }
      return null;
    }

    public static String getHostName(String hostString)
    {
      String hostName = null;
      if ((hostString != null) && (hostString.length() > 0))
      {
        int index = hostString.indexOf(':');
        if (index != -1) {
          hostName = hostString.substring(0, index);
        } else {
          hostName = hostString;
        }
        if ("127.0.0.1".equals(hostName)) {
          return localIp4Str();
        }
      }
      return hostName;
    }

    public static HostAndPort getHostAndPort(String hostString)
    {
      HostAndPort hostAndPort = new HostAndPort();
      if ((hostString == null) || (hostString.length() == 0)) {
        return hostAndPort;
      }
      int index = hostString.indexOf(':');
      if (index != -1)
      {
        hostAndPort.hostAddress = hostString.substring(0, index);
        hostAndPort.port = Integer.parseInt(hostString.substring(index + 1));
      }
      else
      {
        hostAndPort.hostAddress = hostString;
        hostAndPort.port = 80;
      }
      index = hostAndPort.hostAddress.indexOf('/');
      if (index != -1) {
        hostAndPort.hostAddress = hostAndPort.hostAddress.substring(index + 1, hostAndPort.hostAddress.length());
      }
      if ("127.0.0.1".equals(hostAndPort.hostAddress)) {
        hostAndPort.hostAddress = localIp4Str();
      }
      return hostAndPort;
    }

    public static boolean isLocalPortInUse(int port)
    {
      try
      {
        return isPortInUse("127.0.0.1", port);
      }
      catch (Exception e) {}
      return false;
    }

    public static boolean isPortInUse(String host, int port)
      throws UnknownHostException
    {
      InetAddress theAddress = InetAddress.getByName(host);
      try
      {
        Socket socket = new Socket(theAddress, port);
        closeQuietly(socket);
        return true;
      }
      catch (Exception localException) {}
      return false;
    }

    public static class HostAndPort
    {
      public String hostAddress;
      public int port = -1;

      public String toString()
      {
        return this.hostAddress + ":" + this.port;
      }
    }

    public static void closeQuietly(Socket closeable)
    {
      if (closeable != null) {
        try
        {
          closeable.close();
        }
        catch (IOException e)
        {
          logger.error("error when close the closeable", e);
        }
      }
    }

    public static int getRandomPort()
    {
      ServerSocket server = null;
      try
      {
        server = new ServerSocket(0);
        return server.getLocalPort();
      }
      catch (IOException e)
      {
        throw new Error(e);
      }
      finally
      {
        if (server != null) {
          try
          {
            server.close();
          }
          catch (IOException localIOException2) {}
        }
      }
    }
  }


