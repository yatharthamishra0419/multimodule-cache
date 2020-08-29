## Multi module aware cache library(Spring boot),support for aerospike,redis and jvm

Since in many applications , we use redis as well as aerospike as well as other caching systems, 
the idea was to create a generic aggregate cache library so that any modifications can be made in single library.
This library is module aware 

We created a single interface which is common to all the caches. 
Basic set, get,delete operations are supported by all the caching systems,
 difference comes in the data structures they have implemented. 
 Since mostly our requirement is read through cache(Cache backed db) , 
 we have also created an annotation which works around the function 
 and avoids writing boiler plate code for the end user of the library .
  Most of the customer facing applications could use this annotation .
 Also architecture is scalable to add cache dependent operations if we want to use them .

##Sample Aerospike properties 

{module-name}.aerospike.cluster.nodes=localhost:3000

{module-name}.aerospike.namespace=test

##Sample Redis properties

For redis , sentinel and cluster support both have been provided in library.

{module-name}.redis.endpoint=localhost:6379

For sentinel :- 

{module-name}.redis.endpoint=localhost:6380,localhost:6380

{module-name}.redis.master=testmaster

Other properties such as 

{module-name}.redis.password;
{module-name}.redis.max.total.connection;
{module-name}.redis.min.idle.connection;
{module-name}.redis.default.timeout.in.milliseconds;
{module-name}.redis.max.wait.in.milliseconds;

can also be specified but are optional .

##Sample JVM properties

For jvm cache , wrapper has been written over guava lrumap library.
Lru cache size needs to be defined by the client

{module-name}.jvm.map.size=2000

##Sample Code examples

##Basic Architecture

There can be multiple operations related to a cache system ,
 in case of redis , apis for list and set would be different as compared to list and set in aerospike 
 .So , there is service exposed to each of the cache systems, from which we can get corresponding operation 
 class. We have created a method in all of the cache implementations which returns objects of type
  AppAwareCommonCacheOperations. Most of the cases would come into this category and usage
   so this interface is highly recommended until some specific operations need to be performed.

##Example code

##Aerospike 

public class AppAwareCacheOperationsTest {

  private IAppAwareCommonCacheOperations aeroSpikeCommonCacheOperations;

  @Autowired
  private AppAwareAerospikeService appAwareAerospikeService;

  @Autowired
  private ServiceModuleNameProvider serviceModuleNameProvider;

  @PostConstruct
  public void postConstruct(){
    aeroSpikeCommonCacheOperations=
            appAwareAerospikeService.getAppAwareCacheOperations(serviceModuleNameProvider);
  }


  @Test
  public void testCommonOperations(){
    aeroSpikeCommonCacheOperations.set(OperationsConstant.SET_NAME+
                    CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR+
                    OperationsConstant.SAMPLE_KEY,
            OperationsConstant.SAMPLE_AEROSPIKE_DATA,2000, TimeUnit.SECONDS);
    String output = (String)aeroSpikeCommonCacheOperations.get(
            OperationsConstant.SET_NAME+CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR
                    +OperationsConstant.SAMPLE_KEY);
    aeroSpikeCommonCacheOperations.delete(OperationsConstant.SET_NAME
            +CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR
            +OperationsConstant.SAMPLE_KEY);
    Object afterDeletion
            = aeroSpikeCommonCacheOperations.get(OperationsConstant.SET_NAME
            + CacheConstants.AEROSPIKE_SET_KEY_SEPERATOR
            + OperationsConstant.SAMPLE_KEY);
    assertEquals(output,OperationsConstant.SAMPLE_AEROSPIKE_DATA);
    assertEquals(afterDeletion,null);
  }




}



##Redis

public class RedisAppAwareCacheOperationsTest {

    @Autowired
    private ServiceModuleNameProvider serviceModuleNameProvider;

    @Autowired
    private AppAwareRedisService appAwareRedisService;

    private IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations;



    @PostConstruct
    public void postConstruct(){
        iAppAwareCommonCacheOperations =
                appAwareRedisService.getAppAwareCacheOperations(serviceModuleNameProvider);
    }

    @Test
    public void testCommonOperations() throws InterruptedException {
        iAppAwareCommonCacheOperations.set(OperationsConstant.SAMPLE_KEY
                ,OperationsConstant.SAMPLE_REDIS_DATA,120, TimeUnit.SECONDS);
        String output = (String) iAppAwareCommonCacheOperations.get(OperationsConstant.SAMPLE_KEY);
        iAppAwareCommonCacheOperations.delete(OperationsConstant.SAMPLE_KEY);
        Object afterDeletion = iAppAwareCommonCacheOperations.get(OperationsConstant.SAMPLE_KEY);
        Assert.assertEquals(OperationsConstant.SAMPLE_REDIS_DATA,output);
        Thread.sleep(1000);
        Assert.assertEquals(null,afterDeletion);
    }


}


##JVM

public class JvmAppAwareCacheOperations {


    @Autowired
    private ServiceModuleNameProvider serviceModuleNameProvider;

    @Autowired
    private AppAwareJvmService appAwareJvmService;

    private IAppAwareCommonCacheOperations iAppAwareCommonCacheOperations;



    @PostConstruct
    public void postConstruct(){
        iAppAwareCommonCacheOperations =
                appAwareJvmService.getAppAwareCacheOperations(serviceModuleNameProvider);
    }

    @Test
    public void testCommonOperations(){
        iAppAwareCommonCacheOperations.set(OperationsConstant.SAMPLE_KEY,
                OperationsConstant.SAMPLE_JVM_DATA,120, TimeUnit.SECONDS);
        String output = (String) iAppAwareCommonCacheOperations.get(OperationsConstant.SAMPLE_KEY);
        iAppAwareCommonCacheOperations.delete(OperationsConstant.SAMPLE_KEY);
        Object afterDeletion = iAppAwareCommonCacheOperations.get(OperationsConstant.SAMPLE_KEY);
        Assert.assertEquals(OperationsConstant.SAMPLE_JVM_DATA,output);
        Assert.assertEquals(afterDeletion,null);
    }
}


##Injecting Operations class 

There is yet another way to inject client operations class :- 

public class SampleTest {

    @Autowired
    private TestService testService;

    @Autowired
    @Qualifier("{module-name}-{cachetype}")// cachetype can be aerospike,jvm,redis
    private AppAwareCommonCacheOperations aeroSpikeCommonCacheOperations;

}

cachetype can be aerospike,jvm or redis

##Annotation(MultiModuleCachable) 

MultiModuleCachable named annotation has been made with its structure .There usage is given in comments.

public @interface MultiModuleCachable {

  String cacheName() default "";

  String cacheType() default CacheConstants.AEROSPIKE_CLIENT;

  long ttlInSeconds() default CacheConstants.CACHE_DEFAULT_TTL_SECONDS;

  TimeUnit timeUnit() default TimeUnit.SECONDS;

  Class serviceModuleNameProvider();

  String setName() default CacheConstants.DEFAULT_SET_NAME;

  String cacheKey() default "";



}



##Sample code 

public class TestService {

    @Autowired
    private TestWorkerEntity testWorkerEntity;

    @MultiModuleCachable(cacheName = "test",
            serviceModuleNameProvider = TestModuleNameProvider.class,
            cacheType = CacheConstants.REDIS_CLIENT,
    cacheKey = "[0].getSampleAnnotationKey()")
    public String testRedisAnnotation(Counter counter){
        return testWorkerEntity.testRedisWorker(counter);
    }

    @MultiModuleCachable(cacheName = "test",
            serviceModuleNameProvider = TestModuleNameProvider.class,
            cacheType = CacheConstants.AEROSPIKE_CLIENT,
            cacheKey = "[0].getSampleAnnotationKey()")
    public String testAeroSpike(Counter counter){
        return testWorkerEntity.testAeroSpikeWorker(counter);
    }

    @MultiModuleCachable(cacheName = "test",
            serviceModuleNameProvider =
                    TestModuleNameProvider.class,
            cacheType = CacheConstants.JVM_CLIENT,
            cacheKey = "[0].getSampleAnnotationKey()")
    public String testJvm(Counter counter){
        return testWorkerEntity.testJvmWorker(counter);
    }
}

 class Counter implements Serializable {

    int count=0;

    String sampleAnnotationKey="hello";
    public void increment(){
        this.count++;
    }

    public int getCount() {
        return count;
    }

    public String getSampleAnnotationKey() {
        return sampleAnnotationKey;
    }
}

This annotation works around the function , checks if data is present in the cache specified , 
if not executes the function otherwise returns the data from cache itself (read through cache)

##Bucketing support(cache names)

Bucketing support for cache names has also been added .
There comes a need when application wants to bucketize certain sets of keys which are having same properties 
.Properties can be key-prefix ,ttl , setname in case of aerospike .

cache-aerospike-redis-jvm.cache.names=test
cache-aerospike-redis-jvm.cache.test.ttlinseconds=3600
cache-aerospike-redis-jvm.cache.test.setname=jobsearch
cache-aerospike-redis-jvm.cache.test.prefix=job
cache-aerospike-redis-jvm.cache.test.timeunit=SECONDS

If  “test“ is specified in MultiModuleCachable annotation as cacheName , then all keys made by
 annotation would be having the same properties specified. 

##Dependency

  <dependency>
            <groupId>com.multimodule</groupId>
            <artifactId>cache</artifactId>
            <version>1.0.1-rc1</version>
  </dependency>

##Add "com.multimodule" in scan packages  for Applicationconfig for spring boot

Please refer to test cases for further integration issues.For test redis 
and aerospike are assumed to be installed in locahost
