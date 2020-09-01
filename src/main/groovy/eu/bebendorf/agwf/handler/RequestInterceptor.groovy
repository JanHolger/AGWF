package eu.bebendorf.agwf.handler

import eu.bebendorf.agwf.Exchange

interface RequestInterceptor {
    boolean intercept(Exchange exchange);
}