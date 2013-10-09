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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.lyo.oslc4j.automation.AutomationResource;
import org.eclipse.lyo.oslc4j.automation.Persistence;
import org.eclipse.lyo.oslc4j.automation.servlet.ServiceProviderSingleton;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

public class BaseAutoResource<T extends AutomationResource>
{
	private static final String SERVICES_PATH = "services";
    private final Class<T> resourceType;

    protected BaseAutoResource(Class<T> resourceType)
    {
        super();
        this.resourceType = resourceType;
    }
    
    @SuppressWarnings("unchecked")
    public T[] getResources(final String where)
    {

        final List<T> resources = Persistence.getAutoResources(resourceType);

        for (final T resource : resources)
        {
        	resource.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

        }

        return resources.toArray((T[]) Array.newInstance(resourceType, resources.size()));
    }
    
    @GET
    @Produces({MediaType.TEXT_HTML})
    public Response getHtmlResources(@Context HttpServletRequest  httpServletRequest,
    		                         @Context HttpServletResponse httpServletResponse)
    {
    	final List<T> resources = Persistence.getAutoResources(resourceType);
    	httpServletRequest.setAttribute("results",resources);
    	httpServletRequest.setAttribute("resourceType", resourceType.getSimpleName());
    		
    	try {	
    		RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/autoresource_collection_html.jsp"); 
	    	rd.forward(httpServletRequest, httpServletResponse);
				
		} catch (Exception e) {
			throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
		}
        
    	
    	throw new WebApplicationException(Status.NOT_FOUND);
    }

    public T getResource(final HttpServletResponse httpServletResponse,
                         final String              resourceId)
    {
        final T resource = Persistence.getAutoResource(resourceId, resourceType);

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
        final T resource = Persistence.getAutoResource(resourceId, resourceType);

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
    
    @GET
    @Path("selector")
    @Produces({MediaType.TEXT_HTML, MediaType.WILDCARD})
    
    public void autoResourceSelector(@Context                 final HttpServletRequest httpServletRequest,
    		                     @Context                 final HttpServletResponse httpServletResponse,
    		                     @Context                 final UriInfo uriInfo,
    		                     @QueryParam("searchFor") final String searchFor)
    {
    	httpServletRequest.setAttribute("selectionUri",uriInfo.getAbsolutePath().toString());
    	httpServletRequest.setAttribute("resourceType", this.resourceType.getSimpleName());
    	if (searchFor == null)
    	{
    		try {	
                RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/autoresource_selector.jsp"); 
	    		rd.forward(httpServletRequest, httpServletResponse);
				
			} catch (Exception e) {
				throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
			}
    	} else
    	{
    		List<AutomationResource> matchingResources = new ArrayList<AutomationResource>();
    		
    		for (AutomationResource thisResource:Persistence.getAutoResources())
    		{
    			if (thisResource.getClass().equals(this.resourceType))
    			{
	    			String title = thisResource.getTitle();
	    			if (title != null)
	    			{
	    				if (title.toUpperCase().contains(searchFor.toUpperCase()))
	    				{
	    					matchingResources.add(thisResource);
	    				}
	    			}
    			}
    		}
    		try {
    			httpServletRequest.setAttribute("results", matchingResources);
    			RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/autoresource_filtered_json.jsp"); 
    			rd.forward(httpServletRequest, httpServletResponse);
    		} catch (Exception e) {
				throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
			}
    		
    	}
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
                                  httpServletRequest.getContextPath() + "/" + SERVICES_PATH + "/" + getPath() +"/" + identifier,
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
        final T originalResource = Persistence.getAutoResource(resourceId, resourceType);

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
