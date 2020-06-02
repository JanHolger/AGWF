package eu.bebendorf.agwf

import eu.bebendorf.agwf.helper.HttpMethod
import eu.bebendorf.agwf.router.DefaultRouteParamTransformer
import eu.bebendorf.agwf.router.Route
import eu.bebendorf.agwf.router.RouteParamTransformer
import eu.bebendorf.agwf.router.RouteParamTransformerProvider
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets

class WebService implements RouteParamTransformerProvider {

    private List<Route> routes = []
    private List<RouteParamTransformer> routeParamTransformers = [new DefaultRouteParamTransformer()]
    private List<ResponseTransformer> responseTransformers = []
    private RequestHandler notFoundHandler = new RequestHandler.DefaultNotFoundHandler()
    private ExceptionHandler exceptionHandler = new ExceptionHandler.DefaultExceptionHandler()
    private List<RequestHandler> middleware = []
    private List<AfterRequestHandler> after = []
    private Server server
    private int port = 8080

    WebService get(String pattern, RequestHandler... handlers){
        routes.add(new Route(this, HttpMethod.GET, pattern, Arrays.asList(handlers)))
        this
    }

    WebService post(String pattern, RequestHandler... handlers){
        routes.add(new Route(this, HttpMethod.POST, pattern, Arrays.asList(handlers)))
        this
    }

    WebService put(String pattern, RequestHandler... handlers){
        routes.add(new Route(this, HttpMethod.PUT, pattern, Arrays.asList(handlers)))
        this
    }

    WebService delete(String pattern, RequestHandler... handlers){
        routes.add(new Route(this, HttpMethod.DELETE, pattern, Arrays.asList(handlers)))
        this
    }

    WebService notFound(RequestHandler handler){
        notFoundHandler = handler
        this
    }

    WebService middleware(RequestHandler handler){
        middleware.add(handler)
        this
    }

    WebService after(AfterRequestHandler handler){
        after.add(handler)
        this
    }

    WebService routeParamTransformer(RouteParamTransformer transformer){
        routeParamTransformers.add(transformer)
        this
    }

    WebService responseTransformer(ResponseTransformer transformer){
        responseTransformers.add(transformer)
        this
    }

    WebService exceptionHandler(ExceptionHandler handler){
        exceptionHandler = handler
        this
    }

    WebService port(int port){
        this.port = port
        this
    }

    WebService start(){
        server = new Server(port)
        server.handler = new HttpHandler()
        server.start()
        this
    }

    void join(){
        server.join()
    }

    void stop(){
        server.stop()
    }

    void execute(Exchange exchange){
        try {
            for(route in routes){
                exchange.pathVariables = route.match(exchange.method, exchange.path)
                if(exchange.pathVariables == null)
                    continue
                for(handler in middleware){
                    Object response = handler.handle(exchange)
                    if(response != null){
                        exchange.write(transformResponse(response))
                        exchange.close()
                        return
                    }
                }
                for(handler in route.handlers){
                    Object response = handler.handle(exchange)
                    if(response != null){
                        for(afterHandler in after){
                            response = afterHandler.handleAfter(exchange, response)
                        }
                        exchange.write(transformResponse(response))
                        exchange.close()
                        return
                    }
                }
                exchange.close()
                return
            }
            exchange.write(transformResponse(notFoundHandler.handle(exchange)))
        }catch(Throwable ex){
            exchange.write(exceptionHandler.handleBytes(exchange, ex))
        }
        exchange.close()
    }

    List<RouteParamTransformer> getRouteParamTransformers() {
        routeParamTransformers
    }

    byte[] transformResponse(Object object){
        for(t in responseTransformers){
            byte[] res = t.transformBytes(object)
            if(res != null)
                return res
        }
        object.toString().getBytes(StandardCharsets.UTF_8)
    }

    private class HttpHandler extends AbstractHandler {
        void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            execute(new Exchange(WebService.this, httpServletRequest, httpServletResponse))
        }
    }

}
