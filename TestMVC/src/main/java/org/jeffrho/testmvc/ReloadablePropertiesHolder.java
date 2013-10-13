package org.jeffrho.testmvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
	
	//List of property files to load
	List<String> propertyFileLocations;
	
	//TODO: Change this to a map and use the key as the key in the cache.  
	//Map<String, String> propertyFileLocations;
	
	//Spring's property persister
	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
	
	//Number of milliseconds to check cache for refresh
	int checkCacheMillis = -1;
	
	//Cache of properties files
	ConcurrentHashMap<String, CacheEntry> propertiesCache = 
			new ConcurrentHashMap<String,CacheEntry>();
	
	
	public void setPropertyFileLocations(List<String> propertyFileLocations)
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
	
	protected Properties getProperties(String fileName)
	{
		CacheEntry ce = this.propertiesCache.get(fileName);
		if( ce != null && (ce.getRefreshTimestamp() < 0 ||
				ce.getRefreshTimestamp() > ce.getRefreshTimestamp() - this.checkCacheMillis ) )
		{
			return ce.getProperties();
		}
		
		return refreshProperties(fileName, ce);
	}
	
	protected Properties refreshProperties(String fileName, CacheEntry ce)
	{
		long refreshTimestamp = (this.checkCacheMillis < 0 ? -1 : System.currentTimeMillis());
		Resource res = resourceLoader.getResource(fileName);
		
		if(res.exists())
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
		propertiesCache.put(fileName, ce);
		
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
		
		public CacheEntry()
		{
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
