package org.uengine.social;

import org.metaworks.multitenancy.ClassManager;
import org.metaworks.multitenancy.DefaultMetadataService;
import org.metaworks.multitenancy.MetadataService;
import org.metaworks.multitenancy.tenantawarefilter.TenantAwareFilter;
import org.metaworks.springboot.configuration.Metaworks4WebConfig;
import org.metaworks.multitenancy.persistence.MultitenantRepositoryImpl;
import org.metaworks.rest.MetaworksRestService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.uengine.five.InstanceDataAppendingActivityFilter;
import org.uengine.five.JPAProcessInstance;
import org.uengine.five.JPAWorkList;
import org.uengine.five.ProcessDefinitionFactory;
import org.uengine.kernel.ActivityFilter;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;
import org.uengine.modeling.resource.CachedResourceManager;
import org.uengine.modeling.resource.LocalFileStorage;
import org.uengine.modeling.resource.ResourceManager;
import org.uengine.modeling.resource.Storage;
import org.uengine.social.entity.ProcessInstanceEntity;
import org.uengine.social.repository.ProcessInstanceRepository;
import org.uengine.social.service.DefinitionService;
import org.uengine.webservices.worklist.WorkList;

import javax.servlet.Filter;
import java.util.Map;

@EnableWebMvc
@Configuration
@ComponentScan(basePackageClasses = {DefinitionService.class, ProcessInstanceEntity.class, MetaworksRestService.class, ClassManager.class, MetadataService.class, MultitenantRepositoryImpl.class})
@EnableJpaRepositories(basePackageClasses = {MultitenantRepositoryImpl.class, ProcessInstanceRepository.class})
public class WebConfig extends Metaworks4WebConfig {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.setOrder(10);
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/dev/dist/");
//        super.addResourceHandlers(registry);
//    }
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("forward:/index.html");
//        super.addViewControllers(registry);
//    }

    @Bean
    public ResourceManager resourceManager() {
        ResourceManager resourceManager = new CachedResourceManager();
        resourceManager.setStorage(storage());
        return resourceManager;
    }

//    @Bean
//    public DataSource dataSource() {
//        final Properties pool = new Properties();
//        pool.put("driverClassName", "com.mysql.jdbc.Driver");
//        pool.put("url", "jdbc:mysql://localhost:3306/uengine?useUnicode=true&characterEncoding=UTF8&useOldAliasMetadataBehavior=true");
//        pool.put("username", "root");
//        pool.put("password", "");
//        pool.put("minIdle", 1);
//        try {
//            return new org.apache.tomcat.jdbc.pool.DataSourceFactory().createDataSource(pool);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Bean
    /**
     *
     * <bean class="CouchbaseStorage">
     *    <property name="basePath" value="/"/>
     <property name="bucketName" value="default"/>
     <property name="serverIp" value="localhost"/>
     </bean>
     */
    public Storage storage() {
        LocalFileStorage storage = new LocalFileStorage();
        storage.setBasePath("/oce/repository");

        return storage;
    }

//    @Bean
//    public TenantAwareFilter tenantAwareFilter(){
//        return new TenantAwareFilter();
//    }

    @Bean
    public MetadataService metadataService() {
        DefaultMetadataService metadataService = new DefaultMetadataService();
        metadataService.setResourceManager(resourceManager());

        return metadataService;
    }


    @Bean
    @Scope("prototype")
    public ProcessInstance processInstance(ProcessDefinition procDefinition, String instanceId, Map options) throws Exception {
        return new JPAProcessInstance(procDefinition, instanceId, options);
    }

    @Bean
    public ActivityFilter instanceDataAppendingFilter(){
        return new InstanceDataAppendingActivityFilter();
    }

    @Bean
    public WorkList workList() {
        return new JPAWorkList();
    }

    @Bean
    public Filter webFilter() {
        return new TenantAwareFilter();
    }

    @Bean
    public ProcessDefinitionFactory processDefinitionFactory(){
        return new ProcessDefinitionFactory();
    }
}


