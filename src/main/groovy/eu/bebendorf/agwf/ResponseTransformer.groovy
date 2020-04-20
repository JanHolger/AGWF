package eu.bebendorf.agwf

import java.nio.charset.StandardCharsets

interface ResponseTransformer {
    String transform(Object object)
    default byte[] transformBytes(Object object){
        String str = transform(object)
        if(str == null)
            return null
        str.getBytes(StandardCharsets.UTF_8)
    }
}
