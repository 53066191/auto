package com.auto.base

public class ActiveProperties {

    public Properties props ;


    private  ActiveProperties(){
        props  = new Properties();
        loadEnv(props);
    }

    private  static ActiveProperties instance = new ActiveProperties();


    public static ActiveProperties getInstance(){
        return instance
    }

    private static String getEnvFile() {
        InputStream inputStream = null;
        Properties props = new Properties();

        try {
            inputStream = getClass().getResourceAsStream("/active.properties");
            props.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String env = props.getProperty("env.active");
        return env;
    }

    private  void loadEnv(Properties properties) {
        InputStream inputStream = null;
        try {
            String env = getEnvFile();
            String envFile = "/application." + env + ".properties";
            inputStream = getClass().getResourceAsStream(envFile);
            properties.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
