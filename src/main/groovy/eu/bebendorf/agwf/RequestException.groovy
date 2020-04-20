package eu.bebendorf.agwf

class RequestException extends Exception {
    private int responseCode
    RequestException(int responseCode, String code){
        super(code)
        this.responseCode = responseCode
    }
    int getResponseCode(){
        responseCode
    }
}
