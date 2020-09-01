package eu.bebendorf.agwf.handler

import eu.bebendorf.agwf.Exchange

class DefaultNotFoundHandler {
    static def handle(Exchange exchange) {
        exchange.status(404)
        'Page not found!'
    }
}
