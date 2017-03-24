package de.cismet.tools.configuration;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pd
 */
@ServiceProvider(service = ConfigAttrProvider.class, position=1, supersedes="Sirius.navigator.config.DefaultNavigatorConfigAttrProviderImpl")
    public class TestConfigAttrProvider implements ConfigAttrProvider {

    
        public TestConfigAttrProvider() 
        {
            
        }
        
        //~ Methods ------------------------------------------------------------
        @Override
        public String getUserConfigAttr(final String key) {
            return ConfigurationManagerTest.userConfig.get(key);
        }

        @Override
        public String getGroupConfigAttr(final String key) {
            return ConfigurationManagerTest.groupConfig.get(key);
        }

        @Override
        public String getDomainConfigAttr(final String key) {
            return ConfigurationManagerTest.domainConfig.get(key);
        }
    }
