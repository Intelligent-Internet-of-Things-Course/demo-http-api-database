package it.unimore.dipi.iot.dt.api.exception;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project http-iot-api-demo
 * @created 05/10/2020 - 12:59
 */
public class EdgeNodeDataManagerConflict extends Exception {

    public EdgeNodeDataManagerConflict(String errorMessage){
        super(errorMessage);
    }

}
