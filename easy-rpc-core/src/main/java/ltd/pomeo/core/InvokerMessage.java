package ltd.pomeo.core;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author zhq
 * @date 2021/4/24
 */
public class InvokerMessage implements Serializable {
    private String className;
    private String methodName;
    private Class<?>[] paramType;
    private Object[] args;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamType() {
        return paramType;
    }

    public void setParamType(Class<?>[] paramType) {
        this.paramType = paramType;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "InvokerMessage{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramType=" + Arrays.toString(paramType) +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvokerMessage)) return false;

        InvokerMessage that = (InvokerMessage) o;

        if (getClassName() != null ? !getClassName().equals(that.getClassName()) : that.getClassName() != null)
            return false;
        if (getMethodName() != null ? !getMethodName().equals(that.getMethodName()) : that.getMethodName() != null)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getParamType(), that.getParamType())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getArgs(), that.getArgs());
    }

    @Override
    public int hashCode() {
        int result = getClassName() != null ? getClassName().hashCode() : 0;
        result = 31 * result + (getMethodName() != null ? getMethodName().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getParamType());
        result = 31 * result + Arrays.hashCode(getArgs());
        return result;
    }
}
