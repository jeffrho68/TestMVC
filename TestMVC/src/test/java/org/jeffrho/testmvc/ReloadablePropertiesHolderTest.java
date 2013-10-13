package org.jeffrho.testmvc;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Properties;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

public class ReloadablePropertiesHolderTest
{

	@Test
	public void test()
	{
		ReloadablePropertiesHolder rph = new ReloadablePropertiesHolder();
		rph.setResourceLoader(new DefaultResourceLoader());
		rph.setCheckCacheSeconds(1);
				
		HashMap<String, String> propertyLocations = new HashMap<String, String>();
		propertyLocations.put("test1", "/resources/test.props");
		propertyLocations.put("test2", "/resources/test2.props");
		
		rph.setPropertyFileLocations(propertyLocations);
		
		//Properties file 1; first access; should load properties
		Properties props1 = rph.getProperties("test1");
		assertNotNull(props1);
		String val = props1.getProperty("test1.key1");
		assertEquals("value1", val);
		
		//Properties file 2; first access should load properties
		Properties props2 = rph.getProperties("test2");
		assertNotNull(props2);
		val = props2.getProperty("test2.key2");
		assertEquals("value2", val);
		
		//Non-existent properties file, should return null
		Properties props3 = rph.getProperties("test3");
		assertNull(props3);
		
		//Should get from cache
		Properties prop1Cached = rph.getProperties("test1");
		assertEquals(prop1Cached, props1);
			
	}

}
