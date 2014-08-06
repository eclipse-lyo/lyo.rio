/*******************************************************************************
 * Copyright (c) 2011, 2014 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution. 
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *    Jim Conallen   - initial API and implementation
 *    Samuel Padgett - use org.eclipse.lyo package name
 *******************************************************************************/

package org.eclipse.lyo.rio.core.test;

import java.util.Date;
import javax.xml.datatype.DatatypeConfigurationException;
import junit.framework.TestCase;

import org.eclipse.lyo.rio.store.JsonFormatter;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioValue;
import org.eclipse.lyo.rio.store.UnrecognizedValueTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestJson extends TestCase {
    Logger log = LoggerFactory.getLogger(TestJson.class);
    
    public void testJson() {
        try {
            String rurl = "http://example.com/resource";
            RioResource r = new RioResource(rurl);

            
            RioStatement s1 = new RioStatement(
                rurl, "http://example.com/test#stringProp", 
                RioValue.createStringValue("valueS"), null);
            r.addStatement(s1);
            
            
            
            RioStatement sa1 = new RioStatement(
                rurl, "http://example.com/test#stringArrayProp", 
                RioValue.createStringValue("value1"), null);
            r.addStatement(sa1);
            
            RioStatement sa2 = new RioStatement(
                rurl, "http://example.com/test#stringArrayProp", 
                RioValue.createStringValue("value2"), null);
            r.addStatement(sa2);
            
            RioStatement sa3 = new RioStatement(
                rurl, "http://example.com/test#stringArrayProp", 
                RioValue.createStringValue("value3"), null);
            r.addStatement(sa3);
            
            
            
            RioStatement s2 = new RioStatement(
                rurl, "http://example.com/test#booleanProp", 
                RioValue.createBooleanValue(Boolean.TRUE), null);
            r.addStatement(s2);
            
            RioStatement s3 = new RioStatement(
                rurl, "http://example.com/test#decimalProp", 
                RioValue.createDecimalValue(100.5), null);
            r.addStatement(s3);
            
            RioStatement s4 = new RioStatement(
                rurl, "http://example.com/test#integerProp", 
                RioValue.createIntegerValue(5), null);
            r.addStatement(s4);
            
            RioStatement s5 = new RioStatement(
                rurl, "http://example.com/test#dateProp", 
                RioValue.createCalendarValue(new Date()), null);
            r.addStatement(s5);
            
            RioStatement s6 = new RioStatement(
                rurl, "http://example.com/test#resourceProp", 
                RioValue.createUriValue("http://example.com/linkedThing1"), null);
            r.addStatement(s6);
            

            RioStatement su1 = new RioStatement(
                rurl, "http://example.com/test#resourceArrayProp",
                RioValue.createUriValue("http://example.com/linkedThingA"), null);
            r.addStatement(su1);

            RioStatement su2 = new RioStatement(
                rurl, "http://example.com/test#resourceArrayProp",
                RioValue.createUriValue("http://example.com/linkedThingB"), null);
            r.addStatement(su2);

            RioStatement su3 = new RioStatement(
                rurl, "http://example.com/test#resourceArrayProp",
                RioValue.createUriValue("http://example.com/linkedThingC"), null);
            r.addStatement(su3);


            System.out.println(JsonFormatter.formatResource(r));
            
        } catch (UnrecognizedValueTypeException ex) {
            log.error("ERROR in testJson", ex);
            
        } catch (DatatypeConfigurationException ex) {
            log.error("ERROR in testJson", ex);
            
        } catch (RioServerException ex) {
            log.error("ERROR in testJson", ex);
        }
        
    }
}
