package eu.bebendorf.agwf

interface RequestHandler {
    def handle(Exchange exchange)
    static class DefaultNotFoundHandler implements RequestHandler {
        def handle(Exchange exchange) {
            throw new RequestException(404, "RESOURCE_NOT_FOUND")
        }
    }
}