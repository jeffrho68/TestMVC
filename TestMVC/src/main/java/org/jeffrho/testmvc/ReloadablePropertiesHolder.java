package org.jeffrho.testmvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

public class ReloadablePropertiesHolder implements ResourceLoaderAware
{
	
	private ResourceLoader resourceLoader; 
	
	//Map of properties files to load. The key is the alias used to
	//retrieve the Properties object.  
	Map<String, String> propertyFileLocations;
	
	//Spring's property persister
	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
	
	//Number of milliseconds to check cache for refresh
	private int checkCacheMillis = -1;
	
	//Cache of properties files
	private ConcurrentHashMap<String, CacheEntry> propertiesCache = 
			new ConcurrentHashMap<String,CacheEntry>();
	
	
	public void setPropertyFileLocations(Map<String, String> propertyFileLocations)
	{
		if(propertyFileLocations == null || propertyFileLocations.isEmpty())
		{
			throw new IllegalArgumentException("Property 'propertyFileLocations' cannot be empty.");
		}
		
		this.propertyFileLocations = propertyFileLocations;
	}
	
	public void setCheckCacheSeconds(int checkCacheSeconds)
	{
		this.checkCacheMillis = checkCacheSeconds * 1000;
	}
		
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader)
	{
		this.resourceLoader = resourceLoader;
	}
	
	public Properties getProperties(String alias)
	{
				
		CacheEntry ce = this.propertiesCache.get(alias);
		if( ce != null && (ce.getRefreshTimestamp() < 0 ||
				ce.getRefreshTimestamp() > ce.getRefreshTimestamp() - this.checkCacheMillis ) )
		{
			return ce.getProperties();
		}
		
		String fileName = this.propertyFileLocations.get(alias);
		return refreshProperties(alias, fileName, ce);
	}
	
	public void clearCache()
	{
		this.propertiesCache.clear();
	}
	
	protected Properties refreshProperties(String alias, String fileName, CacheEntry ce)
	{
		long refreshTimestamp = (this.checkCacheMillis < 0 ? -1 : System.currentTimeMillis());
		Resource res = null;
		if(fileName != null)
		{
			res = resourceLoader.getResource(fileName);
		}
		
		if(res!= null && res.exists())
		{
			long fileTimestamp = -1;
			if (this.checkCacheMillis >= 0) 
			{
				try
				{
					fileTimestamp = res.lastModified();
					if (ce != null && ce.getFileTimestamp() == fileTimestamp)
					{
						ce.setRefreshTimestamp(refreshTimestamp);
						return ce.getProperties();
					}
				}
				catch (IOException ex)
				{
					// Probably a class path resource: cache it forever.
					fileTimestamp = -1;
				}
				
				try
				{
					Properties props = loadProperties(res, fileName);
					ce = new CacheEntry(props, fileTimestamp);
				}
				catch (IOException ex)
				{
					// Empty holder representing "not valid".
					ce = new CacheEntry();
				}
			}
		}
		else
		{
			//Resource does not exist. create empty CacheEntry
			ce = new CacheEntry();
		}
		
		ce.setRefreshTimestamp(refreshTimestamp);
		propertiesCache.put(alias, ce);
		
		return ce.getProperties();
	}
	
	protected Properties loadProperties(Resource res, String fileName)
		throws IOException
	{
		InputStream is = res.getInputStream();
		Properties props = new Properties();
		
		try
		{
			this.propertiesPersister.load(props, is);
			return props;
			
		}
		finally
		{
			if(is != null)
			{
				is.close();
			}
		}
				
	}
	
	
	//Holds cache entries.
	protected class CacheEntry
	{
		private Properties props;
		private long fileTimestamp = -1;
		private long refreshTimestamp = -1;
		
		//Create an empty properties file. This is used if the property file
		//cannot be loaded from the resource.
		public CacheEntry()
		{
			this.props = new Properties();
		}
		
		public CacheEntry(Properties props, long fileTimestamp)
		{
			this.props = props;
			this.fileTimestamp = fileTimestamp;
		}
		
		public Properties getProperties() 
		{
			return props;
		}
		
		public long getFileTimestamp()
		{
			return fileTimestamp;
		}
		
		public void setRefreshTimestamp(long refreshTimestamp)
		{
			this.refreshTimestamp = refreshTimestamp;
		}

		public long getRefreshTimestamp()
		{
			return refreshTimestamp;
		}
	}
	

}
