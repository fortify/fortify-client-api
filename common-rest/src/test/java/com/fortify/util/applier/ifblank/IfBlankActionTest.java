package com.fortify.util.applier.ifblank;
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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

public class IfBlankActionTest {
	@Test public void testSkipBlankString() {
		String value = "";
		assertNotInvoked(IfBlank.SKIP(), value);
	}
	
	@Test public void testApplyBlankString() {
		String value = "";
		assertInvoked(value, IfBlank.APPLY(), value);
	}
	
	@Test public void testErrorBlankString() {
		String value = "";
		try {
			assertNotInvoked(IfBlank.ERROR(), value);
			fail("No exeption thrown");
		} catch ( IllegalArgumentException expected ) {}
	}
	
	@Test public void testDefaultBlankString() {
		String value = "";
		String defaultValue = UUID.randomUUID().toString();
		assertInvoked(defaultValue, IfBlank.DEFAULT(defaultValue), value);
	}
	
	@Test public void testSkipNonBlankString() {
		String value = UUID.randomUUID().toString();
		assertInvoked(value, IfBlank.SKIP(), value);
	}
	
	@Test public void testApplyNonBlankString() {
		String value = UUID.randomUUID().toString();
		assertInvoked(value, IfBlank.APPLY(), value);
	}
	
	@Test public void testErrorNonBlankString() {
		String value = UUID.randomUUID().toString();
		assertInvoked(value, IfBlank.ERROR(), value);
	}
	
	@Test public void testDefaultNonBlankString() {
		String value = UUID.randomUUID().toString();
		assertInvoked(value, IfBlank.APPLY(), value);
	}
	
	private void assertInvoked(String expectedValue, IfBlankAction applier, String inputValue) {
		final List<String> values = new ArrayList<>();
		applier.apply("test", inputValue, StringUtils::isBlank, values::add);
		assertTrue(values.size()==1, "Lambda not called");
		assertEquals(expectedValue, values.get(0));
	}
	
	private void assertNotInvoked(IfBlankAction applier, String inputValue) {
		applier.apply("test", inputValue, StringUtils::isBlank, v->fail("Consumer shouldn't be invoked"));
	}
	
}
