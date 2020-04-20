package eu.bebendorf.agwf

import eu.bebendorf.agwf.helper.HttpMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets

class Exchange {
    final HttpMethod method
    final String path
    Map<String, Object> pathVariables
    Map<String, String> parameters = [:]
    private HttpServletRequest request
    private HttpServletResponse response
    private Map<String, Object> attributes = [:]
    Exchange(HttpServletRequest request, HttpServletResponse response){
        this.request = request
        this.response = response
        this.path = request.getPathInfo()
        method = HttpMethod.valueOf(request.getMethod())
    }
    void write(String data){
        write(data.getBytes(StandardCharsets.UTF_8))
    }
    void write(byte[] bytes){
        response.getOutputStream().write(bytes)
    }
    void write(byte[] bytes, int offset, int length){
        response.getOutputStream().write(bytes, offset, length)
    }
    void close(){
        response.getOutputStream().close()
    }
    void header(String header, String value){
        if(header.equalsIgnoreCase('content-type')){
            response.setContentType(value)
            return
        }
        response.setHeader(header, value)
    }
    String header(String header){
        request.getHeader(header)
    }
    void redirect(String url){
        response.setStatus(302)
        response.sendRedirect(url)
    }
    HttpServletRequest rawRequest(){
        request
    }
    HttpServletResponse rawResponse(){
        response
    }
    def <T> T attrib(String key){
        if(attributes[key] == null)
            return null
        (T) attributes[key]
    }
    void attrib(String key, Object value){
        attributes[key] = value
    }
}