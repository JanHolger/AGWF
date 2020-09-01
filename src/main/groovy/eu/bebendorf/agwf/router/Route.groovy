package eu.bebendorf.agwf.router


import eu.bebendorf.agwf.helper.HttpMethod

import java.util.regex.Matcher
import java.util.regex.Pattern

class Route {
    private RouteParamTransformerProvider routeParamTransformerProvider
    private HttpMethod method
    private Pattern pattern
    private Map<String, String> variables = [:]
    private List<Closure> handlers
    Route(RouteParamTransformerProvider routeParamTransformerProvider, HttpMethod method, String pattern, List<Closure> handlers){
        this(routeParamTransformerProvider, method, pattern, ':', handlers)
    }
    Route(RouteParamTransformerProvider routeParamTransformerProvider, HttpMethod method, String pattern, String variableDelimiter, List<Closure> handlers){
        this.handlers = handlers
        this.method = method
        this.routeParamTransformerProvider = routeParamTransformerProvider
        pattern = pattern.toLowerCase(Locale.ENGLISH)
        if(pattern.endsWith("/"))
            pattern = pattern.substring(0, pattern.length()-1)
        if(!pattern.startsWith("/"))
            pattern = "/" + pattern
        int pos = 0
        StringBuilder sb = new StringBuilder()
        StringBuilder text = new StringBuilder()
        boolean inVar = false
        while (pos < pattern.length()){
            if(pattern[pos] == '{'){
                if(inVar){
                    throw new RuntimeException("Unexpected character '{' in route at position "+pos)
                }
                if(text.length() > 0){
                    sb.append("("+regexEscape(text.toString())+")")
                    text = new StringBuilder()
                }
                inVar = true
                pos++
                continue
            }
            if(pattern[pos] == '}'){
                if(!inVar){
                    throw new RuntimeException("Unexpected character '}' in route at position "+pos)
                }
                if(text.length() > 0){
                    String variableName = text.toString()
                    String type = "string"
                    int loc = variableName.indexOf(variableDelimiter)
                    if(loc != -1){
                        String t = variableName.substring(0, loc).toLowerCase(Locale.ENGLISH)
                        if(routeParamTransformerProvider.getRouteParamTransformer(t)){
                            type = t
                            variableName = variableName.substring(loc+1)
                        }
                    }
                    sb.append("(?<"+regexEscape(variableName)+">"+routeParamTransformerProvider.getRouteParamTransformer(type).regex(type)+")")
                    variables.put(variableName, type)
                    text = new StringBuilder()
                }
                inVar = false
                pos++
                continue
            }
            text.append(pattern[pos])
            pos++
        }
        if(inVar){
            throw new RuntimeException("Unexpected end in route")
        }
        if(text.length() > 0){
            sb.append("("+regexEscape(text.toString())+")")
        }
        this.pattern = Pattern.compile(sb.toString())
    }
    Map<String, Object> match(HttpMethod method, String path){
        if(this.method != method)
            return null
        Matcher matcher = pattern.matcher(path)
        if(matcher.matches()){
            Map<String, Object> params = new HashMap<>()
            for(name in variables.keySet()){
                params[name] = routeParamTransformerProvider.getRouteParamTransformer(variables[name]).transform(variables[name], matcher.group(name))
            }
            return params
        }
        return null
    }
    List<Closure> getHandlers(){
        handlers
    }
    private static regexEscape(String s){
        s = s.replace("\\", "\\\\")
        for(c in "<([{^-=\$!|]})?*+.>"){
            s = s.replace(c, "\\"+c)
        }
        return s
    }
}
