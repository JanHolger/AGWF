package eu.bebendorf.agwf.handler

import eu.bebendorf.agwf.Exchange

interface AfterRequestHandler {
    Object handleAfter(Exchange exchange, Object response);
}