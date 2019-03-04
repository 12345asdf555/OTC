

/**
 * WeldService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

    package org.tempuri;

    /*
     *  WeldService java interface
     */

    public interface WeldService {
          

        /**
          * Auto generated method signature
          * 
                    * @param getDataUsingDataContract0
                
         */

         
                     public service.weld.jn.GetDataUsingDataContractResponse getDataUsingDataContract(

                        service.weld.jn.GetDataUsingDataContract getDataUsingDataContract0)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getDataUsingDataContract0
            
          */
        public void startgetDataUsingDataContract(

            service.weld.jn.GetDataUsingDataContract getDataUsingDataContract0,

            final org.tempuri.WeldServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param serviceCall2
                
         */

         
                     public service.weld.jn.ServiceCallResponse serviceCall(

                        service.weld.jn.ServiceCall serviceCall2)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param serviceCall2
            
          */
        public void startserviceCall(

            service.weld.jn.ServiceCall serviceCall2,

            final org.tempuri.WeldServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getData4
                
         */

         
                     public service.weld.jn.GetDataResponse getData(

                        service.weld.jn.GetData getData4)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getData4
            
          */
        public void startgetData(

            service.weld.jn.GetData getData4,

            final org.tempuri.WeldServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    