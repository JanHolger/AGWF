package eu.bebendorf.agwf

import java.nio.charset.StandardCharsets

interface ExceptionHandler {
    def handle(Exchange exchange, Throwable ex);
    static class DefaultExceptionHandler implements ExceptionHandler {
        String handle(Exchange exchange, Throwable ex) {
            'An internal server error occured! Please contact the server administrator in case you think this is a problem.'
        }
        default byte[] handleBytes(Exchange exchange, Throwable ex){
            handle(exchange, ex).getBytes(StandardCharsets.UTF_8)
        }
    }
}