
/**
 * WeldServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

    package org.tempuri;

    /**
     *  WeldServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class WeldServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public WeldServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public WeldServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getDataUsingDataContract method
            * override this method for handling normal response from getDataUsingDataContract operation
            */
           public void receiveResultgetDataUsingDataContract(
                    service.weld.jn.GetDataUsingDataContractResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDataUsingDataContract operation
           */
            public void receiveErrorgetDataUsingDataContract(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for serviceCall method
            * override this method for handling normal response from serviceCall operation
            */
           public void receiveResultserviceCall(
                    service.weld.jn.ServiceCallResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from serviceCall operation
           */
            public void receiveErrorserviceCall(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getData method
            * override this method for handling normal response from getData operation
            */
           public void receiveResultgetData(
                    service.weld.jn.GetDataResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getData operation
           */
            public void receiveErrorgetData(java.lang.Exception e) {
            }
                


    }
    