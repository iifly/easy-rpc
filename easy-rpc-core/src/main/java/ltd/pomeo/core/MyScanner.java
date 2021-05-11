package ltd.pomeo.core;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author pomeos
 */
public class MyScanner {
    public static Set<String> fileScan(String packageName){
        Set<String> set = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(packageName.replaceAll("\\.", "/"));
        assert url != null;
        File baseDir = new File(url.getFile());
        File[] files = baseDir.listFiles();
        assert files != null;
        for (File file : files) {
            if(file.isDirectory()){
                set.addAll(fileScan(packageName + "." + file.getName()));
            }else if(file.getName().contains(".class")){
                String className = packageName + "." + file.getName().replace(".class", "").trim();
                set.add(className);
            }
        }
        return set;
    }
    public static Set<String> jarScan(String packageName) {
        Set<String> set = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            URL url = classLoader.getResource(packageName.replaceAll("\\.", "/"));
            assert url != null;
            //转换为JarURLConnection
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            assert connection != null;

            JarFile jarFile = connection.getJarFile();
            assert jarFile != null;
            //得到该jar文件下面的类实体
            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
            while (jarEntryEnumeration.hasMoreElements()) {
                JarEntry entry = jarEntryEnumeration.nextElement();
                String jarEntryName = entry.getName();
                String clazz = jarEntryName.replaceAll("/", ".");
                if (clazz.contains(".class") && clazz.startsWith(packageName)) {
                    int index = clazz.indexOf(packageName);
                    String className = clazz.substring(index).replace(".class", "").trim();
                    set.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return set;
    }

    public static Set<String> scanClassName(String packageName){
        Set<String> set = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(packageName.replaceAll("\\.", "/"));
        assert url != null;
        String protocol = url.getProtocol();
        if ("file".equalsIgnoreCase(protocol)) {
            set.addAll(fileScan(packageName));
        }else if("jar".equalsIgnoreCase(protocol)){
            set.addAll(jarScan(packageName));
        }
        return set;
    }
}
