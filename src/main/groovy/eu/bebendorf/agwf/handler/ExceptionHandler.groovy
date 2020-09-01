package eu.bebendorf.agwf.handler

import eu.bebendorf.agwf.Exchange

interface ExceptionHandler {
    def handle(Exchange exchange, Throwable ex);
    default byte[] handleBytes(Exchange exchange, Throwable ex){
        exchange.service.transformResponse(handle(exchange, ex))
    }
    static class DefaultExceptionHandler implements ExceptionHandler {
        def handle(Exchange exchange, Throwable ex) {
            'An internal server error occured! Please contact the server administrator in case you think this is a problem.'
        }
    }
}