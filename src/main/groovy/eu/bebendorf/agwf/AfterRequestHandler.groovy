package eu.bebendorf.agwf

interface AfterRequestHandler {
    Object handleAfter(Exchange exchange, Object response);
}