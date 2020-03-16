package com.fortify.util.rest.json;
/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
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


import java.util.Date;

import org.junit.jupiter.api.Test;

public class JSONConversionServiceFactoryTest {
	@Test public void testParseDate1() {
		testParseDate("2017-09-12T11:24:28.000+0000"); // SSC token date format
	}
	
	@Test public void testParseDate2() {
		testParseDate("2019-11-26 15:01:01");
	}
	
	@Test public void testParseDate3() {
		testParseDate("2019-11-26T15:01:01.45"); // FoD scan date format
	}
	
	@Test public void testParseDate4() {
		//testParseDate("2019-11-26T15:01:01.4"); // This doesn't work yet
	}
	
	@Test public void testParseDate5() {
		testParseDate("2019-11-26");
	}
	
	private void testParseDate(String dateString) {
		System.out.println(parseDate(dateString));
	}
	
	private Date parseDate(String source) {
		return JSONConversionServiceFactory.getConversionService().convert(source, Date.class);
	}
}
