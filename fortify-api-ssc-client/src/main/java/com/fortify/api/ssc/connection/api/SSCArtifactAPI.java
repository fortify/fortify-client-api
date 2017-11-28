/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC
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
package com.fortify.api.ssc.connection.api;

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
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection;
import com.fortify.api.util.rest.json.JSONMap;

public class SSCArtifactAPI extends AbstractSSCAPI {
	private static final Log LOG = LogFactory.getLog(SSCArtifactAPI.class);

	public SSCArtifactAPI(SSCAuthenticatingRestConnection conn) {
		super(conn);
	}
	
	public final long downloadApplicationFile(String applicationVersionId, Path target) {
		InputStream is = conn().executeRequest(HttpMethod.POST, 
				conn().getBaseResource()
				.path("/download/currentStateFprDownload.html")
				.queryParam("id", ""+applicationVersionId)
				.queryParam("mat", getFileToken(FileTokenType.DOWNLOAD))
				.request("*/*"), InputStream.class);
		try {
			return Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Error downloading application file", e);
		} finally {
			try {
				is.close();
			} catch ( IOException ioe ) {
				LOG.warn("Error closing response stream, subsequent requests may fail", ioe);
			}
		}
	}
	
	public final JSONMap uploadArtifact(String applicationVersionId, File fprFile) {
		MultiPart multiPart = new FormDataMultiPart();
		multiPart.type(new MediaType("multipart", "form-data",
	    		Collections.singletonMap(Boundary.BOUNDARY_PARAMETER, Boundary.createBoundary())));
		multiPart.bodyPart(new FormDataBodyPart("Filename", fprFile.getName()));
		multiPart.bodyPart(new FileDataBodyPart(fprFile.getName(), fprFile, MediaType.APPLICATION_OCTET_STREAM_TYPE));
		
		String xml = conn().executeRequest(HttpMethod.POST, 
				conn().getBaseResource()
					.path("/upload/resultFileUpload.html")
					.queryParam("entityId", ""+applicationVersionId)
					.queryParam("mat", getFileToken(FileTokenType.UPLOAD))
					.request("application/xml"),
				Entity.entity(multiPart, multiPart.getMediaType()), String.class);
		return xml2json(new ByteArrayInputStream(xml.getBytes()));
		// With 'id' field we can query http://localhost:1710/ssc/api/v1/jobs/JOB_ARTIFACTUPLOAD%2479cc0be0-1f39-4e95-ada9-36b96496dff9
		// to get job state and artifact id; artifact id can be used to check artifact processing status (complete, approval req, error, ...)
	}

	public static void main(String[] args) {
		SSCAuthenticatingRestConnection conn = new SSCAuthenticatingRestConnection("http://localhost:1710/ssc", "ssc",  "Admin123!", null);
		JSONMap upload = conn.api().artifact().uploadArtifact("6", new File("c:/work/Programs/HP/SCA/17.20/samples/basic/sampleOutput/WebGoat5.0.fpr"));
		System.out.println(upload);
		JSONMap job = conn.api().job().query().id(upload.get("id", String.class)).getUnique();
		System.out.println(job);
	}
}
