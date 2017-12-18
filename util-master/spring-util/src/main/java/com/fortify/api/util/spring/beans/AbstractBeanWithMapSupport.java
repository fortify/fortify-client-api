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
package com.fortify.api.util.spring.beans;

import java.util.Map;

import org.springframework.beans.BeansException;

public class AbstractBeanWithMapSupport {
	private final BeanWrapperWithMapSupport beanWrapper = new BeanWrapperWithMapSupport(this);

	public void copyPropertiesFromMap(Map<String, Object> map, String prefix, boolean ignoreNonExisting) {
		beanWrapper.copyPropertiesFromMap(map, prefix, ignoreNonExisting);
	}

	public void copyPropertiesToMap(Map<String, Object> map, String prefix, boolean overwriteExisting) {
		beanWrapper.copyPropertiesToMap(map, prefix, overwriteExisting);
	}

	public void setPropertyValue(String name, Object value, boolean ignoreNonExisting) {
		beanWrapper.setPropertyValue(name, value, ignoreNonExisting);
	}

	public void setPropertyValue(String propertyName, Object value) throws BeansException {
		beanWrapper.setPropertyValue(propertyName, value);
	}

	public Object getPropertyValue(String propertyName) throws BeansException {
		return beanWrapper.getPropertyValue(propertyName);
	}
}
