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
 *      Michael Fiedler        - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation.resources;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.automation.Persistence;
import org.eclipse.lyo.oslc4j.automation.AutomationResource;
import org.eclipse.lyo.oslc4j.automation.servlet.ServiceProviderSingleton;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

public class BaseAutoResource<T extends AutomationResource>
{
    private final Class<T> resourceType;

    protected BaseAutoResource(Class<T> resourceType)
    {
        super();
        this.resourceType = resourceType;
    }
    
    @SuppressWarnings("unchecked")
    public T[] getResources(final String where)
    {

        final List<T> resources = Persistence.getQmResources(resourceType);

        for (final T resource : resources)
        {
        	resource.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

        }

        return resources.toArray((T[]) Array.newInstance(resourceType, resources.size()));
    }

    public T getResource(final HttpServletResponse httpServletResponse,
                         final String              resourceId)
    {
        final T resource = Persistence.getQmResource(resourceId, resourceType);

        if (resource != null)
        {
            resource.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

            setETagHeader(getETagFromResource(resource), httpServletResponse);

            return resource;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("{resourceId}")
    @Produces({OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML, OslcMediaType.APPLICATION_X_OSLC_COMPACT_JSON})
    public Compact getCompact(@Context                      final HttpServletRequest httpServletRequest,
                              @PathParam("resourceId") final String             resourceId)
           throws URISyntaxException
    {
        final T resource = Persistence.getQmResource(resourceId, resourceType);

        if (resource != null)
        {
            final Compact compact = new Compact();

            compact.setAbout(resource.getAbout());
            compact.setShortTitle(resource.getTitle());
            compact.setTitle(resource.getTitle());

            final URI iconURI = new URI(httpServletRequest.getScheme(),
                                        null,
                                        httpServletRequest.getServerName(),
                                        httpServletRequest.getServerPort(),
                                        httpServletRequest.getContextPath() + "UI/images/resources/" + getPath() + "/icon.png",
                                        null,
                                        null);

            compact.setIcon(iconURI);
            
            return compact;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    public Response addResource(final HttpServletRequest  httpServletRequest,
    		                    final HttpServletResponse httpServletResponse,
                                final T                   resource)
           throws URISyntaxException
    {
        final long identifier = Persistence.getNextIdentifier();

        
        final URI about = new URI(httpServletRequest.getScheme(),
                                  null,
                                  httpServletRequest.getServerName(),
                                  httpServletRequest.getServerPort(),
                                  httpServletRequest.getContextPath() + "/" + getPath() +"/" + identifier,
                                  null,
                                  null);

        resource.setAbout(about);
        resource.setIdentifier(String.valueOf(identifier));
        resource.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

        final Date date = new Date();

        resource.setCreated(date);
        resource.setModified(date);

        Persistence.addResource(resource);
        setETagHeader(getETagFromResource(resource), httpServletResponse);

        return Response.created(about).entity(resource).build();
    }

    public Response updateResource(final HttpServletResponse httpServletResponse,
                                   final String              eTagHeader,
                                   final String              resourceId,
    		                       final T                   resource)
    {
        final T originalResource = Persistence.getQmResource(resourceId, resourceType);

        if (originalResource != null)
        {
        	final String originalETag = getETagFromResource(originalResource);

        	//no eTag or eTag unchanged
            if ((eTagHeader == null) ||
                (originalETag.equals(eTagHeader)))
            {
            	resource.setModified(new Date());
            	resource.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());
	            Persistence.updateResource(resourceId, resource);
	            setETagHeader(getETagFromResource(resource),httpServletResponse);
            }
            else
            {
            	throw new WebApplicationException(Status.PRECONDITION_FAILED);
            }
        }
        else
        {
        	throw new WebApplicationException(Status.NOT_FOUND);
        }

        return Response.ok().build();
    }

    public Response deleteResource(final String identifier)
    {
        final T resource = Persistence.deleteResource(identifier);

        if (resource != null)
        {
            return Response.noContent().build();
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    private void setETagHeader(final String              eTagFromResource,
                                      final HttpServletResponse httpServletResponse)
    {
    	httpServletResponse.setHeader("ETag", eTagFromResource);
	}

    private String getETagFromResource(final T resource)
	{
		return Long.toString(resource.getModified().getTime());
	}
    
    protected String getPath() {
        return this.getClass().getAnnotation(Path.class).value();
    }
    
}
