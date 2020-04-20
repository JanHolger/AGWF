package eu.bebendorf.agwf.router

interface RouteParamTransformerProvider {
    List<RouteParamTransformer> getRouteParamTransformers()
    default RouteParamTransformer getRouteParamTransformer(String type){
        for(t in routeParamTransformers){
            if(t.canTransform(type)){
                return t
            }
        }
        null
    }
}