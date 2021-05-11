package ltd.pomeo.core;

/**
 * @author zhq
 * @date 2021/5/8
 */
public class RpcServiceDefinition {
    private String beanName;
    private Class<?> clazz;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public RpcServiceDefinition() {
    }

    public RpcServiceDefinition(String beanName, Class<?> clazz) {
        this.beanName = beanName;
        this.clazz = clazz;
    }
}
