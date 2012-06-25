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
import org.eclipse.lyo.rio.query.ComparisonTerm;
import org.eclipse.lyo.rio.query.CompoundTerm;
import org.eclipse.lyo.rio.query.InTerm;
import org.eclipse.lyo.rio.query.OslcWhereParser;
import org.eclipse.lyo.rio.query.SimpleTerm;
import org.eclipse.lyo.rio.query.SimpleTerm.Type;

/**
 * Proxy implementation of {@link CompoundTerm} interface
 */
public class CompoundTermInvocationHandler extends SimpleTermInvocationHandler
{
    public
    CompoundTermInvocationHandler(
        CommonTree tree,
        boolean isTopLevel
    )
    {
        super(isTopLevel ? null : (CommonTree)tree.getChild(0),
              isTopLevel ? Type.TOP_LEVEL : Type.NESTED);
        
        this.tree = tree;
        this.isTopLevel = isTopLevel;
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
        if (! method.getName().equals("children")) {
            return super.invoke(proxy, method, args);
        }
        
        if (children != null) {
            return children;
        }
        
        @SuppressWarnings("unchecked")
        List<CommonTree> treeChildren = tree.getChildren();
        
        children =
            new ArrayList<SimpleTerm>(
                    treeChildren.size() - (isTopLevel ? 0 : 1));
        
        boolean skip = ! isTopLevel;
        
        for (CommonTree child : treeChildren) {
            
            if (skip) {
                skip = false;
                continue;
            }
            
            Object simpleTerm;
            
            switch(child.getToken().getType()) {
            case OslcWhereParser.SIMPLE_TERM:
                simpleTerm = 
                    Proxy.newProxyInstance(ComparisonTerm.class.getClassLoader(), 
                            new Class<?>[] { ComparisonTerm.class },
                            new ComparisonTermInvocationHandler(
                                    child));
                break;
            case OslcWhereParser.IN_TERM:
                simpleTerm = 
                    Proxy.newProxyInstance(InTerm.class.getClassLoader(), 
                            new Class<?>[] { InTerm.class },
                            new InTermInvocationHandler(
                                    child));
            case OslcWhereParser.COMPOUND_TERM:
                simpleTerm = 
                    Proxy.newProxyInstance(CompoundTerm.class.getClassLoader(), 
                            new Class<?>[] { CompoundTerm.class },
                            new CompoundTermInvocationHandler(
                                    child, false));
            default:
                throw new IllegalStateException("unimplemented type of simple term: " + child.getToken().getText());
            }
            
            children.add((SimpleTerm)simpleTerm);
        }
        
        children = Collections.unmodifiableList(children);
        
        return children;
    }
    
    private final CommonTree tree;
    private final boolean isTopLevel;
    List<SimpleTerm> children = null;
}
