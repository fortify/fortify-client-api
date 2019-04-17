/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC, a Micro Focus company
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.client.ssc.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.fortify.client.ssc.annotation.SSCRequiredActionsPermitted;
import com.fortify.client.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.util.rest.json.JSONMap;
import com.fortify.util.spring.SpringExpressionUtil;

import lombok.extern.apachecommons.CommonsLog;

/**
 * This class is used to access SSC functionality related to up- and downloading files.
 * 
 * @author Ruud Senden
 *
 */
@CommonsLog
public class SSCFileUpDownloadAPI extends AbstractSSCAPI {
	/**
	 * Enumeration for SSC file token types, to be used for {@link #getFileToken(FileTokenType)}
	 */
	public static enum FileTokenType {
		UPLOAD, DOWNLOAD, PREVIEW_FILE, REPORT_FILE
	}
	
	public SSCFileUpDownloadAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	@SSCRequiredActionsPermitted("POST=/api/v\\d+/fileTokens")
	public final String getFileToken(FileTokenType type) {
		JSONMap entity = new JSONMap();
		entity.put("fileTokenType", type.toString());
		JSONMap data = conn().executeRequest(HttpMethod.POST, 
				conn().getBaseResource().path("/api/v1/fileTokens"),
				Entity.entity(entity, "application/json"), JSONMap.class);
		return SpringExpressionUtil.evaluateExpression(data, "data.token", String.class);
	}
	
	public final JSONMap uploadFile(WebTarget baseTarget, FileTokenType type, File file) {
		MultiPart multiPart = new FormDataMultiPart();
		multiPart.type(new MediaType("multipart", "form-data",
	    		Collections.singletonMap(Boundary.BOUNDARY_PARAMETER, Boundary.createBoundary())));
		multiPart.bodyPart(new FormDataBodyPart("Filename", file.getName()));
		multiPart.bodyPart(new FileDataBodyPart(file.getName(), file, MediaType.APPLICATION_OCTET_STREAM_TYPE));
		
		String xml = conn().executeRequest(HttpMethod.POST, 
				baseTarget
					.queryParam("mat", getFileToken(type))
					.request("application/xml"),
				Entity.entity(multiPart, multiPart.getMediaType()), String.class);
		return xml2json(new ByteArrayInputStream(xml.getBytes()));
	}
	
	public final long downloadFile(WebTarget baseTarget, FileTokenType type, Path target) {
		Response response = conn().executeRequest(HttpMethod.GET, 
				baseTarget
				.queryParam("mat", getFileToken(FileTokenType.DOWNLOAD))
				.request("*/*"), Response.class);
		try {
			return Files.copy(response.readEntity(InputStream.class), target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Error downloading file", e);
		} finally {
			response.close();
		}
	}
	
	private JSONMap xml2json(InputStream is) {
	    try {
	    	final DataCollector handler = new DataCollector();
			SAXParserFactory.newInstance().newSAXParser().parse(is, handler);
			return handler.result;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException("Error converting XML to JSONMap", e);
		}
	}

	private static class DataCollector extends DefaultHandler {
	        private final StringBuilder buffer = new StringBuilder();
	        private final JSONMap result = new JSONMap();
	
	        @Override
	        public void endElement(String uri, String localName, String qName) throws SAXException {
	            final String value = buffer.toString().trim();
	            if (value.length() > 0) {
	                result.put(qName.substring(Math.max(0,qName.indexOf(':')+1)), value);
	            }
	            buffer.setLength(0);
	        }
	
	        @Override
	        public void characters(char[] ch, int start, int length) throws SAXException {
	            buffer.append(ch, start, length);
	        }
	    }

}
