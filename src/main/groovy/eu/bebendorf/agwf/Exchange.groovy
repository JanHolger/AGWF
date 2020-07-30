package eu.bebendorf.agwf

import eu.bebendorf.agwf.helper.HttpMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets

class Exchange {
    final WebService service
    final HttpMethod method
    final String path
    private byte[] body
    Map<String, Object> pathVariables
    Map<String, String> parameters = [:]
    private HttpServletRequest request
    private HttpServletResponse response
    private Map<String, Object> attributes = [:]
    Exchange(WebService service, HttpServletRequest request, HttpServletResponse response){
        this.service = service
        this.request = request
        this.response = response
        this.path = request.getPathInfo()
        method = HttpMethod.valueOf(request.getMethod())
    }
    def <T> T getBody(Class<T> clazz){
        if(body == null)
            body = read()
        if(clazz == byte[].class)
            return body
        String body = new String(body, StandardCharsets.UTF_8)
        if(clazz == String.class)
            return body
        service.gson.fromJson(body, clazz)
    }
    String getContentType(){
        request.contentType
    }
    byte[] read(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        try {
            InputStream is = request.getInputStream()
            byte [] data = new byte[1024]
            int r
            while (is.available() > 0){
                r = is.read(data)
                baos.write(data, 0, r)
            }
        }catch(IOException ex){}
        baos.toByteArray()
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
    void status(int code){
        response.setStatus(code)
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
    String bearerAuth(){
        String auth = header('Authorization')
        if(auth == null)
            return null
        if(!auth.startsWith('Bearer '))
            return null
        auth.substring(7)
    }
}