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
package com.fortify.util.spring.beans;

import java.beans.PropertyDescriptor;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * This {@link BeanWrapper} implementation allows for setting bean properties
 * from a {@link Map}, and generating a {@link Map} based on current bean
 * properties.
 * 
 * @author Ruud Senden
 *
 */
public class BeanWrapperWithMapSupport extends BeanWrapperImpl {

	public BeanWrapperWithMapSupport() {}

	public BeanWrapperWithMapSupport(boolean registerDefaultEditors) {
		super(registerDefaultEditors);
	}

	public BeanWrapperWithMapSupport(Object object) {
		super(object);
	}

	public BeanWrapperWithMapSupport(Class<?> clazz) {
		super(clazz);
	}

	public BeanWrapperWithMapSupport(Object object, String nestedPath, Object rootObject) {
		super(object, nestedPath, rootObject);
	}
	
	public void copyPropertiesFromMap(Map<String, Object> map, String prefix, boolean ignoreNonExisting) {
		for ( Map.Entry<String, Object> entry : map.entrySet() ) {
			String key = entry.getKey();
			String name = null;
			if ( StringUtils.isBlank(prefix) ) {
				name = key;
			} else if ( key.startsWith(prefix) ) {
				name = key.substring(prefix.length());
			}
			if ( StringUtils.isNotBlank(name) ) {
				setPropertyValue(name, entry.getValue(), ignoreNonExisting);
			}
		}
	}
	
	public void copyPropertiesToMap(Map<String, Object> map, String prefix, boolean overwriteExisting) {
		for ( PropertyDescriptor desc : getPropertyDescriptors() ) {
			if ( isReadableProperty(desc.getName()) && !"class".equals(desc.getName()) ) {
				if ( overwriteExisting || !map.containsKey(desc.getDisplayName()) ) {
					String key = StringUtils.capitalize(desc.getDisplayName());
					key = StringUtils.isBlank(prefix) ? key : (prefix+key);
					map.put(key, getPropertyValue(desc.getName()));
				}
			}
		}
	}
	
	public void setPropertyValue(String name, Object value, boolean ignoreNonExisting) {
		if ( isWritableProperty(name) ) {
			setPropertyValue(name, value);
		} else if ( !ignoreNonExisting ) {
			throw new IllegalArgumentException("Property "+name+" not writeable on "+this.getClass().getName());
		}
	}

}
