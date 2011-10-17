/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation.
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
 *    Jim Conallen - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.rio.store;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.RioValue.RioValueType;
import org.eclipse.lyo.rio.util.StringUtils;

/**
 * @author hz0wyg-e
 *
 */
public class OslcResource extends RioResource {

	/**
	 * @param uri
	 * @throws RioServerException
	 */
	public OslcResource(String uri) throws RioServerException {
		super(uri);
	}

	/**
	 * @param uri
	 * @param statements
	 */
	public OslcResource(String uri, List<RioStatement> statements) {
		super(uri,statements);
	}

	/**
	 * @return
	 */
	public Date getCreated() {
		RioValue prop = this.getFirstPropertyValue(IConstants.DCTERMS_CREATED);
		if (prop != null) {
			try{
				return prop.dateValue();
			}catch( IncompatibleValueException e) {
				// log it?
			}
		} else {
			// we need to create it
			try {
				Date now = new Date();
				this.setDateProperty(IConstants.DCTERMS_CREATED, now );
				return now;
			} catch (RioServerException e) {
				// log it?
			}
		}
		return null;
	}

	/**
	 * @return user
	 */
	public String getCreator() {
		String user = null;
		RioValue prop = this.getFirstPropertyValue(IConstants.DCTERMS_CREATOR);
		if (prop != null) {
			user = prop.stringValue();
		} else {
			try {
				// need to set it.
				user = RioStore.getStore().getDefaultUserUri();
			} catch (RioServerException e) {
				// log it?
			}
		}
		return user;
	}

	/**
	 * @return modified date or created date
	 */
	public Date getModified()  {
		RioValue prop = this.getFirstPropertyValue(IConstants.DCTERMS_MODIFIED);
		if( prop != null ) {
			try{
				return prop.dateValue();
			} catch( IncompatibleValueException e ) {
				// log it?
			}
		} 
		return getCreated();
	}

	/**
	 * @return contributor
	 */
	public String getContributor() {
		RioValue prop = this.getFirstPropertyValue(IConstants.DCTERMS_CONTRIBUTOR);
		if( prop != null ) {
			return prop.stringValue();
		}
		return null;
	}

	/**
	 * @return ETag
	 * @throws RioServerException
	 */
	public String getETag() throws RioServerException {
		// just MD5 hash of last modified date
		Date modified = getModified();
		String str = StringUtils.rfc2822(modified);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
			md.update(str.getBytes());
			byte buf[] = md.digest();
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < buf.length; i++) {
				String hex = Integer.toHexString(0xff & buf[i]);
				if (hex.length() == 1)
					strBuf.append('0'); // prepend 0 to make sure all values are two chars
				strBuf.append(hex);
			}
			return strBuf.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RioServerException(e);
		}
	}

	/**
	 * @return title
	 * @default [URI]
	 */
	public String getTitle() {
		RioValue prop = this.getFirstPropertyValue(IConstants.DCTERMS_TITLE);
		if( prop != null ) {
			return prop.stringValue();
		} else {
			return '[' + this.getUri() + ']'; // default to uri
		}
	}

	/**
	 * @param title
	 * @throws RioServerException
	 */
	public void setTitle(String title) throws RioServerException {
		setStringProperty(IConstants.DCTERMS_TITLE, title);
	}

	/**
	 * @return identifier
	 */
	public String getIdentifier() {
		RioValue prop = this.getFirstPropertyValue(IConstants.DCTERMS_IDENTIFIER);
		if( prop != null ){
			return prop.stringValue();
		}

		// we can derive this from the uri
		int pos = uri.lastIndexOf('/');
		if (pos > 0) {
			return uri.substring(pos + 1);
		}
		return null;

	}

	/**
	 * @param identifier
	 * @throws RioServerException
	 */
	public void setIdentifier(String identifier) throws RioServerException {
		this.setStringProperty(IConstants.DCTERMS_IDENTIFIER, identifier);
	}

	/**
	 * @param created
	 * @throws RioServerException
	 */
	public void setCreated(Date created) throws RioServerException {
		this.setDateProperty(IConstants.DCTERMS_CREATED, created);
	}

	/**
	 * @param userUri
	 * @throws RioServerException
	 */
	public void setCreator(String userUri) throws RioServerException {
		addProperty(IConstants.DCTERMS_CREATOR, RioValueType.URI, userUri, true);
	}

	/**
	 * @param modified
	 * @throws RioServerException
	 */
	public void setModified(Date modified) throws RioServerException {
		this.setDateProperty(IConstants.DCTERMS_MODIFIED, modified);
	}

	/**
	 * @param userUri
	 * @throws RioServerException
	 */
	public void setContributor(String userUri) throws RioServerException {
		this.setUriProperty(IConstants.DCTERMS_CONTRIBUTOR, userUri);
	}
	

}
