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

import org.antlr.runtime.tree.CommonTree;
import org.eclipse.lyo.rio.query.ComparisonTerm;
import org.eclipse.lyo.rio.query.OslcWhereParser;
import org.eclipse.lyo.rio.query.SimpleTerm.Type;
import org.eclipse.lyo.rio.query.StringValue;
import org.eclipse.lyo.rio.query.UriRefValue;
import org.eclipse.lyo.rio.query.Value;

/**
 * Proxy implmentation of {@link ComparisonTerm} interface
 */
class ComparisonTermInvocationHandler extends SimpleTermInvocationHandler
{
    public
    ComparisonTermInvocationHandler(CommonTree tree)
    {
        super(tree, Type.COMPARISON);
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
        String methodName = method.getName();
        
        if (methodName.equals("operator")) {
            
            if (operator == null) {
                operator =
                    ((CommonTree)tree.getChild(1)).getToken().getType();
            }
            
            return operator;
        }
        
        if (! methodName.equals("operand")) {
            return super.invoke(proxy, method, args);
        }
        
        if (operand == null) {
            
            CommonTree treeOperand = (CommonTree)tree.getChild(2);
            
            switch (treeOperand.getToken().getType()) {
            case OslcWhereParser.IRI_REF:
                operand =
                    (Value)Proxy.newProxyInstance(
                            UriRefValue.class.getClassLoader(), 
                            new Class<?>[] { UriRefValue.class },
                            new UriRefValueInvocationHandler(
                                   treeOperand));
                break;
            case OslcWhereParser.STRING_LITERAL2:
                operand =
                    (Value)Proxy.newProxyInstance(
                            StringValue.class.getClassLoader(), 
                            new Class<?>[] { StringValue.class },
                            new StringValueInvocationHandler(
                                   treeOperand));
                break;
            case OslcWhereParser.DECIMAL:
            case OslcWhereParser.TYPED_VALUE:
            case OslcWhereParser.LANGED_VALUE:
            default:
                throw new IllegalStateException(
                        "unspported literal value type: " +
                            treeOperand.getToken().getText());
            }
        }
        
        return operand;
    }
    
    private Integer operator = null;
    private Value operand = null;
}
