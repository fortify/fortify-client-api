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
/**
 * <p>This package provides the main functionality for accessing the SSC REST API.
 *    The main entry point is {@link com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection},
 *    which handles SSC authentication (username/password-based or token-based) and other low-level details.</p>
 *    
 * <p>Instances of this class can be constructed through the {@link com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection#builder()}
 *    method, which is mostly suitable for programmatic instantiation. Alternatively, you can use
 *    {@link com.fortify.api.ssc.connection.SSCConnectionRetriever}, configuring the connection through 
 *    the {@link com.fortify.api.ssc.connection.SSCConnectionRetriever#getConfig()} method. This method 
 *    is mostly suitable for configuration-based instantiation, like Spring XML configuration, and allows
 *    you to auto-wire the {@link com.fortify.api.ssc.connection.SSCConnectionRetriever} instance to all
 *    classes that require access to SSC.</p>
 *    
 * <p>Once you have acquired an {@link com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection} instance,
 *    this instance provides low-level access to any SSC REST API endpoint, using the various executeRequest()
 *    methods. More high-level functionality is available through the
 *    {@link com.fortify.api.ssc.connection.SSCAuthenticatingRestConnection#api()} method; see
 *    {@link com.fortify.api.ssc.connection.api.SSCAPI} for more details.</p>
 */
package com.fortify.api.ssc.connection;
