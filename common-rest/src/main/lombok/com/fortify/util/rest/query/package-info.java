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
/**
 * <p>This package contains functionality for querying REST API's. The main client API
 *    for retrieving data is provided by the {@link com.fortify.util.rest.query.IRestConnectionQuery}
 *    interface, for which {@link com.fortify.util.rest.query.AbstractRestConnectionQuery}
 *    provides an abstract implementation. Concrete implementations will need to implement/override 
 *    some methods to handle paging and processing responses. Clients usually will not instantiate
 *    query instances directly, but rather through builders (see below).</p>
 *    
 * <p>The main API for constructing queries is provided through concrete implementations
 *    of the {@link com.fortify.util.rest.query.AbstractRestConnectionQueryBuilder} class.
 *    Based on the builder pattern, these concrete implementations would allow for 
 *    configuring query criteria, and then building a corresponding 
 *    {@link com.fortify.util.rest.query.IRestConnectionQuery} implementation through
 *    the build() method.</p>
 *    
 * <p>As an example, suppose we have a system named 'MySystem', which provides query endpoints
 *    for books and cards. Usually you would develop a MySystemRestConnectionQuery class that
 *    extends from {@link com.fortify.util.rest.query.AbstractRestConnectionQuery},
 *    and implements the various methods for handling paging and REST responses. You would also have
 *    an AbstractMySystemQueryBuilder class that extends from 
 *    {@link com.fortify.util.rest.query.AbstractRestConnectionQueryBuilder},
 *    specifies the connection type, and adds a 
 *    <code>public MySystemRestConnectionQuery build() {return new MySystemRestConnectionQuery(this);}</code>
 *    method. This class is then used as a base class for MySystemBooksQueryBuilder and MySystemCardsQueryBuilder.
 *    These classes specify the target REST endpoint path, and allow users of your API to configure the queries, 
 *    for example books search criteria like title, ISBN or author. The implementation for these classes would 
 *    utilize the functionality provided in the {@link com.fortify.util.rest.webtarget} package to configure 
 *    the corresponding REST query parameters.</p>
 *    
 * <p>Please see the JavaDoc for the various classes for more details.</p>
 */
package com.fortify.util.rest.query;
