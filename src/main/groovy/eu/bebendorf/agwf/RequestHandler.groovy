package eu.bebendorf.agwf

interface RequestHandler {
    def handle(Exchange exchange) throws Throwable
    static class DefaultNotFoundHandler implements RequestHandler {
        def handle(Exchange exchange) {
            exchange.status(404)
            'Page not found!'
        }
    }
}