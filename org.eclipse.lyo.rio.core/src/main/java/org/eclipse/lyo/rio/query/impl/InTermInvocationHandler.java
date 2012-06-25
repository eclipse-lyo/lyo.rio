/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
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
 *    Steve Pitschke - initial API and implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.query.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.eclipse.lyo.rio.query.InTerm;
import org.eclipse.lyo.rio.query.OslcWhereParser;
import org.eclipse.lyo.rio.query.SimpleTerm.Type;
import org.eclipse.lyo.rio.query.StringValue;
import org.eclipse.lyo.rio.query.UriRefValue;
import org.eclipse.lyo.rio.query.Value;

/**
 * Proxy implementation of {@link InTerm} interface
 */
class InTermInvocationHandler extends SimpleTermInvocationHandler
{
    public
    InTermInvocationHandler(CommonTree tree)
    {
        super(tree, Type.IN_TERM);
    }
    
    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object
    invoke(
        Object proxy,
        Method method,
        Object[] args
    ) throws Throwable
    {
        if (! method.getName().equals("values")) {
            return super.invoke(proxy, method, args);
        }
        
        if (values == null) {
            
            @SuppressWarnings("unchecked")
            List<CommonTree> treeValues = tree.getChildren();
            
            values = new ArrayList<Value>(treeValues.size() - 1);
            
            boolean skip = true;
            
            for (CommonTree treeValue : treeValues) {
                
                if (skip) {
                    skip = false;
                    continue;
                }
            
                Value value;
                
                switch (treeValue.getToken().getType()) {
                case OslcWhereParser.IRI_REF:
                    value =
                        (Value)Proxy.newProxyInstance(
                                UriRefValue.class.getClassLoader(), 
                                new Class<?>[] { UriRefValue.class },
                                new UriRefValueInvocationHandler(
                                        treeValue));
                    break;
                case OslcWhereParser.STRING_LITERAL2:
                    value =
                        (Value)Proxy.newProxyInstance(
                                StringValue.class.getClassLoader(), 
                                new Class<?>[] { StringValue.class },
                                new StringValueInvocationHandler(
                                        treeValue));
                    break;
                case OslcWhereParser.BOOLEAN:
                case OslcWhereParser.DECIMAL:
                case OslcWhereParser.TYPED_VALUE:
                case OslcWhereParser.LANGED_VALUE:
                default:
                    throw new IllegalStateException("unspported literal value type");
                }
                
                values.add(value);
            }
            
            values = Collections.unmodifiableList(values);
        }
        
        return values;
    }
    
    private List<Value> values = null;
}
