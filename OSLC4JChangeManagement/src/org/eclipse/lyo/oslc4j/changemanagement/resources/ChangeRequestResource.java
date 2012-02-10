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
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.changemanagement.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.changemanagement.ChangeRequest;
import org.eclipse.lyo.oslc4j.changemanagement.Constants;
import org.eclipse.lyo.oslc4j.changemanagement.Persistence;
import org.eclipse.lyo.oslc4j.changemanagement.Type;
import org.eclipse.lyo.oslc4j.changemanagement.servlet.ServiceProviderSingleton;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

@OslcService(Constants.CHANGE_MANAGEMENT_DOMAIN)
@Path("changeRequests")
public class ChangeRequestResource
{
    public ChangeRequestResource()
    {
        super();
    }

    @OslcDialogs(
    {
        @OslcDialog
        (
             title = "Change Request Selection Dialog",
             label = "Change Request Selection Dialog",
             uri = "",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
             usages = {OslcConstants.OSLC_USAGE_DEFAULT}
        ),
        @OslcDialog
        (
             title = "Change Request List Dialog",
             label = "Change Request List Dialog",
             uri = "UI/changeRequests/list.jsp",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
             usages = {Constants.USAGE_LIST}
        )
    })
    @OslcQueryCapability
    (
        title = "Change Request Query Capability",
        label = "Change Request Catalog Query",
        resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_CHANGE_REQUEST,
        resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
        usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @GET
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public ChangeRequest[] getChangeRequests(@QueryParam("oslc.where") final String where)
    {

        final List<ChangeRequest> results = new ArrayList<ChangeRequest>();

        final ChangeRequest[] changeRequests = Persistence.getChangeRequests();

        for (final ChangeRequest changeRequest : changeRequests)
        {
        	changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

            results.add(changeRequest);

        }

        return results.toArray(new ChangeRequest[results.size()]);
    }

    @GET
    @Path("{changeRequestId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public ChangeRequest getChangeRequest(@Context                      final HttpServletResponse httpServletResponse,
                                          @PathParam("changeRequestId") final String              changeRequestId)
    {
        final ChangeRequest changeRequest = Persistence.getChangeRequest(changeRequestId);

        if (changeRequest != null)
        {
            changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

            setETagHeader(getETagFromChangeRequest(changeRequest), httpServletResponse);

            return changeRequest;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("{changeRequestId}")
    @Produces({OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML, OslcMediaType.APPLICATION_X_OSLC_COMPACT_JSON})
    public Compact getCompact(@Context                      final HttpServletRequest httpServletRequest,
                              @PathParam("changeRequestId") final String             changeRequestId)
           throws URISyntaxException
    {
        final ChangeRequest changeRequest = Persistence.getChangeRequest(changeRequestId);

        if (changeRequest != null)
        {
            final Compact compact = new Compact();

            compact.setAbout(changeRequest.getAbout());
            compact.setShortTitle(changeRequest.getTitle());
            compact.setTitle(changeRequest.getTitle());

            final String[] dctermsTypes = changeRequest.getDctermsTypes();

            if ((dctermsTypes != null) &&
                (dctermsTypes.length == 1))
            {
                final String dctermsType = dctermsTypes[0];

                final String file;

                if (Type.Defect.toString().equals(dctermsType))
                {
                    file = "Defect.png";
                }
                else
                {
                    // TODO - support icons for the other ChangeRequest types
                    file = null;
                }

                if (file != null)
                {
                    final URI iconURI = new URI(httpServletRequest.getScheme(),
                                                null,
                                                httpServletRequest.getServerName(),
                                                httpServletRequest.getServerPort(),
                                                httpServletRequest.getContextPath() + "UI/images/resources/" + file,
                                                null,
                                                null);

                    compact.setIcon(iconURI);
                }
            }

            return compact;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @OslcCreationFactory
    (
         title = "Change Request Creation Factory",
         label = "Change Request Creation",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_CHANGE_REQUEST},
         resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @POST
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public Response addChangeRequest(@Context final HttpServletRequest  httpServletRequest,
    		                         @Context final HttpServletResponse httpServletResponse,
                                              final ChangeRequest       changeRequest)
           throws URISyntaxException
    {
        final long identifier = Persistence.getNextIdentifier();

        final URI about = new URI(httpServletRequest.getScheme(),
                                  null,
                                  httpServletRequest.getServerName(),
                                  httpServletRequest.getServerPort(),
                                  httpServletRequest.getContextPath() + "/changeRequests/" + identifier,
                                  null,
                                  null);

        changeRequest.setAbout(about);
        changeRequest.setIdentifier(String.valueOf(identifier));
        changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

        final Date date = new Date();

        changeRequest.setCreated(date);
        changeRequest.setModified(date);

        Persistence.addChangeRequest(changeRequest);
        setETagHeader(getETagFromChangeRequest(changeRequest), httpServletResponse);

        return Response.created(about).entity(changeRequest).build();
    }

    @PUT
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Path("{changeRequestId}")
    public Response updateChangeRequest(@Context                      final HttpServletResponse httpServletResponse,
                                        @HeaderParam("If-Match")      final String              eTagHeader,
                                        @PathParam("changeRequestId") final String              changeRequestId,
    		                                                          final ChangeRequest       changeRequest)
    {
        final ChangeRequest originalChangeRequest = Persistence.getChangeRequest(changeRequestId);

        if (originalChangeRequest != null)
        {
        	final String originalETag = getETagFromChangeRequest(originalChangeRequest);

        	//no eTag or eTag unchanged
            if ((eTagHeader == null) ||
                (originalETag.equals(eTagHeader)))
            {
	            changeRequest.setModified(new Date());
	            changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());
	            Persistence.updateChangeRequest(changeRequestId, changeRequest);
	            setETagHeader(getETagFromChangeRequest(changeRequest),httpServletResponse);
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

    @DELETE
    @Path("{changeRequestId}")
    public Response deleteChangeRequest(@PathParam("changeRequestId") final String changeRequestId)
    {
        final ChangeRequest changeRequest = Persistence.deleteChangeRequest(changeRequestId);

        if (changeRequest != null)
        {
            return Response.noContent().build();
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    private static void setETagHeader(final String              eTagFromChangeRequest,
                                      final HttpServletResponse httpServletResponse)
    {
    	httpServletResponse.setHeader("ETag", eTagFromChangeRequest);
	}

    private static String getETagFromChangeRequest(final ChangeRequest changeRequest)
	{
		return Long.toString(changeRequest.getModified().getTime());
	}
}
