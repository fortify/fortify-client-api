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
 * <p>This package contains functionality for querying REST API's. The main client API
 *    is provided through {@link com.fortify.api.util.rest.query.IRestConnectionQuery},
 *    which provides methods for processing or retrieving data from a REST endpoint.
 *    {@link com.fortify.api.util.rest.query.AbstractRestConnectionQuery} provides an
 *    abstract implementation for this interface, and can be configured using a
 *    {@link com.fortify.api.util.rest.query.AbstractRestConnectionQueryConfig} instance.
 *    The corresponding {@link com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQuery}
 *    and {@link com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQueryConfig} classes
 *    add support for caching query results.</p>
 *    
 * <p>Concrete implementations of this API usually provide one or more classes extending from
 *    {@link com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQuery} to 
 *    handle paging and REST responses. For each specific REST endpoint, usually there is a 
 *    corresponding builder implementation that extends from {@link com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQueryConfig}.
 *    This builder is responsible for configuring the exact query to be executed (for example
 *    REST endpoint path and query parameters), and provides a build() method to instantiate
 *    the corresponding {@link com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQuery}
 *    implementation, passing itself to the constructor of the 
 *    {@link com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQuery} implementation.</p>
 *    
 * <p>As an example, suppose we have a system named 'MySystem', which provides query endpoints
 *    for books and cards. Usually you would develop a MySystemRestConnectionQuery class that
 *    extends from {@link com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQuery}
 *    and implements the various methods for handling paging and REST responses. In addition,
 *    you would develop MySystemBooksQueryBuilder and MySystemCardsQueryBuilder that both extend
 *    (directly or indirectly) from {@link com.fortify.api.util.rest.query.AbstractRestConnectionWithCacheQueryConfig}.
 *    These builders would allow a user of your API to configure the queries, for example by configuring
 *    books search criteria like title, ISBN or author. The implementation for MySystemBooksQueryBuilder
 *    would then utilize the functionality provided in the {@link com.fortify.api.util.rest.webtarget} package
 *    to configure the REST query parameters.</p>
 */
package com.fortify.api.util.rest.query;
