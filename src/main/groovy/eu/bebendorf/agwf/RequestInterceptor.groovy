package eu.bebendorf.agwf

interface RequestInterceptor {
    boolean intercept(Exchange exchange);
}