package com.example.bookshare.model;

import java.io.Serializable;

public class PaymentMethod implements Serializable {
    public int methodId;
    public String methodName;
    public String methodLogo;
    public Double methodCharge;
    public String methodInfo;
    public String methodInfoImage;

    public PaymentMethod() {
    }

    public PaymentMethod(int methodId, String methodName, String methodLogo) {
        this.methodId = methodId;
        this.methodName = methodName;
        this.methodLogo = methodLogo;
    }

    public int getMethodId() {
        return methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodLogo() {
        return methodLogo;
    }
}
