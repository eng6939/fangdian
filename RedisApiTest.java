package com.redis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

public class RedisApiTest {

	private Jedis jedis;// 非切片额客户端连接

	@Before
	public void setUp() {
		RedisPool redisPool = new RedisPool();
		this.jedis = redisPool.getJedis();
	}

	/**
	 * 清空数据库
	 */
	@Test
	public void flushDb() {
		System.out.println("清空数据库测试--开始");
		getAllKey();
		jedis.flushDB();
		getAllKey();
		System.out.println("清空数据库测试--结束");
	}
	/**
	 * 判断key的值是否存在
	 */
	@Test
	public void isExist(){
		System.out.println("判断key001的值是否存在："+jedis.exists("key001"));
	}
	
	
	/**
	 * 查询所有key值
	 */
	@Test
	public void getAllKey() {
		System.out.println("查询所有key值--开始");
		Set<String> keySet = jedis.keys("*");
		Iterator<String> keys = keySet.iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			System.out.println(key);
		}
		System.out.println("查询所有key值--结束");
	}

	/**
	 * 单个新增
	 */
	@Test
	public void addValue() {
		System.out.println("单个新增--开始");
		jedis.set("key001", "value001");
		System.out.println("单个新增--结束");
	}
	
	/**
	 * 单个查询
	 */
	@Test
	public void getValue() {
		System.out.println("单个查询--开始");
		String value = jedis.get("key001");
		System.out.println("单个查询--结束：" + value);
	}

	/**
	 * 批量添加
	 */
	@Test
	public void batchAddValue() {
		System.out.println("批量添加--开始");
		jedis.mset("mkey001", "mvalue001"
				, "mkey002", "mvalue002"
				, "mkey003", "mvalue003");
		System.out.println("批量添加--结束");
	}
	/**
	 * 批量查询
	 */
	@Test
	public void batchGetValue(){
		System.out.println("批量查询--开始");
		List<String> valueList = jedis.mget("mkey001","mkey002","mkey003");
		for (int i = 0; i < valueList.size(); i++) {
			String value = valueList.get(i);
			System.out.println(value);
		}
		System.out.println("批量查询--结束");
	}
	
	/**
	 * 删除--支持批量删除
	 */
	@Test
	public void deletevalue(){
		System.out.println("删除--开始");
		long n = jedis.del("key001","mkey001","mkey002","mkey003");
		System.out.println("删除--结束,影响："+n);
	}
	
	/**
	 * 值保留时间添加,查询，剩余
	 */
	@Test
	public void retentionValue(){
		System.out.println("设置保留时间--开始");
		jedis.set("rkey001", "rvalue001");
		jedis.expire("rkey001", 5);
		 try{ 
			 System.out.println("当前保留5秒值:"+jedis.get("rkey001")+"程序睡眠2秒。");
             Thread.sleep(2000); 
         } 
         catch (InterruptedException e){ 
         } 
		 
		// 查看某个key的剩余生存时间,单位【秒】.永久生存或者不存在的都返回-1
         System.out.println("查看rkey001的剩余生存时间："+jedis.ttl("rkey001"));
         // 移除某个key的生存时间
         System.out.println("移除rkey001的生存时间："+jedis.persist("rkey001"));
         
         System.out.println("查看rkey001的剩余生存时间："+jedis.ttl("rkey001"));
         try{ 
             Thread.sleep(3000); 
         } 
         catch (InterruptedException e){ 
         } 
		System.out.println("睡眠五秒后保留值："+jedis.get("rkey001"));
		System.out.println("设置保留时间--完成");
	}
	
	/**
	 *  查看key所储存的值的类型
	 */
	@Test
	public void getValueType(){
        System.out.println("查看key所储存的值的类型："+jedis.type("key001"));
	}
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++hash++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * 添加hash值
	 */
	@Test
	public void addHash(){
		System.out.println("添加hash--开始");
		 System.out.println("keyhash001中添加hkey001和hvalue001键值对："+jedis.hset("keyhash001", "hkey001", "hvalue001")); 
         System.out.println("keyhash001中添加hkey002和hvalue002键值对："+jedis.hset("keyhash001", "hkey002", "hvalue002")); 
         System.out.println("keyhash001中添加hkey003和hvalue003键值对："+jedis.hset("keyhash001", "hkey003", "hvalue003"));
         System.out.println("新增hkey004和4的整型键值对："+jedis.hincrBy("keyhash001", "hkey004", 4l));
		System.out.println("添加hash--结束");
	}
	
	/**
	 * 判断hash中某个值是否存在
	 */
	@Test
	public void isHashExist(){
		System.out.println("判断keyhash001的中hkey001值是否存在："+jedis.hexists("keyhash001","hkey001"));
	}
	
	/**
	 * 获取hash中所有值结果Map类型
	 */
	@Test
	public void getHashsMap(){
		System.out.println("获取hash中所有值--开始");
		Map hashMap =  jedis.hgetAll("keyhash001");
		Set keySet = hashMap.keySet();
		Iterator<String> itkey = keySet.iterator();
		while(itkey.hasNext()){
			String key = itkey.next();
			Object value = hashMap.get(key);
			System.out.println("hash中key为"+key+"的值为"+value.toString());
		}
		System.out.println("获取hash中所有值--结束");
	}
	
	/**
	 * 获取hash中所有值结果List类型
	 */
	@Test
	public void getHashsList(){
		System.out.println("获取hash中所有值--开始");
		List list = jedis.hvals("keyhash001");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).toString());
		}
		System.out.println("获取hash中所有值--结束");
	}
	
	
	/**
	 * 删除hash中的值
	 */
	@Test
	public void deleteHash(){
		System.out.println("删除hash中hkey003的值--开始");
		long n = jedis.hdel("keyhash001", "hkey003");
		System.out.println("删除hash中hkey003的值--结束，影响："+n);
	}
	
	/**
	 * hash中整形值直接运算(运算)
	 */
	@Test
	public void plusIntValue(){
		
		System.out.println("hkey004整型键值的值"+jedis.hget("keyhash001", "hkey004"));
		 System.out.println("hkey004整型键值的值增加100："+jedis.hincrBy("keyhash001", "hkey004", 100l));
	}
	/**
	 * 获取hash中所有key
	 */
	@Test
	public void getHashsAllKeys(){
		System.out.println("获取hash中所有key--开始");
		Set<String> keySet =  jedis.hkeys("keyhash001");
		Iterator<String> itKey = keySet.iterator();
		while(itKey.hasNext()){
			String key = itKey.next();
			System.out.println(key);
		}
		System.out.println("获取hash中所有key--结束");
	}
	
	
	//+++++++++++++++++++++++++++++++++++++++++++LIST++++++++++++++++++++++++++++++++++++++++++++++
	
	
	/**
	 * 添加list值
	 */
	@Test
	public void addList(){
		System.out.println("添加list---开始");
		jedis.lpush("stringlists", "vector"); 
		jedis.lpush("stringlists", "ArrayList"); 
		jedis.lpush("stringlists", "vector");
		jedis.lpush("stringlists", "vector");
		jedis.lpush("stringlists", "LinkedList");
		jedis.lpush("stringlists", "MapList");
		jedis.lpush("stringlists", "SerialList");
		jedis.lpush("stringlists", "HashList");
		jedis.lpush("numberlists", "3");
		jedis.lpush("numberlists", "1");
		jedis.lpush("numberlists", "5");
		jedis.lpush("numberlists", "2");
		System.out.println("添加list---结束");
	}
	
	/**
	 * 查询所有list值
	 * 
	 */
	@Test
	public void getList(){
		System.out.println("查询所有list值--开始");
		List stringlist = jedis.lrange("stringlists", 0, -1);
		for (int i = 0; i < stringlist.size(); i++) {
			System.out.println(stringlist.get(i));
		}
		List numberlist = jedis.lrange("numberlists", 0, -1);
		for (int i = 0; i < numberlist.size(); i++) {
			System.out.println(numberlist.get(i));
		}
		System.out.println("查询所有list值--结束");
	}
	
	/**
	 * 删除list中指定的值
	 * 删除列表指定的值 ，第二个参数为删除的个数（有重复时），后add进去的值先被删，类似于出栈
	 */
	@Test
	public void deleteListPointValue(){
		// 删除列表指定的值 ，第二个参数为删除的个数（有重复时），后add进去的值先被删，类似于出栈
		System.out.println("删除list中指定的值 --开始");
		long  n = jedis.lrem("stringlists", 2, "vector");
		System.out.println("删除list中指定的值 --结束，影响："+n);
	}
	
	/**
	 * 删除区间外的值
	 */
	@Test
	public void deleteListoutPoitValue(){
		System.out.println("删除区间外的值--开始");
		System.out.println("删除下标0-3区间之外的元素："+jedis.ltrim("stringlists", 0, 3));
		System.out.println("删除区间外的值--结束");
	}
	
	/**
	 * 列表出桟
	 */
	@Test
	public void  deleteList(){
		
		  System.out.println("出栈元素："+jedis.lpop("stringlists")); 
          System.out.println("元素出栈后-stringlists："+jedis.lrange("stringlists", 0, -1));
		
	}
	
	/**
	 * 修改指定下标的值
	 */
	@Test
	public void updateListPointValue(){
		 // 修改列表中指定下标的值 
		jedis.lset("stringlists", 0, "hello list!"); 
        System.out.println("下标为0的值修改后-stringlists："+jedis.lrange("stringlists", 0, -1));
	}
	
	/**
	 * 查询list的size
	 */
	@Test
	public void getListSize(){
		 // 数组长度 
        System.out.println("长度-stringlists："+jedis.llen("stringlists"));
        System.out.println("长度-numberlists："+jedis.llen("numberlists"));
	}
	
	/**
	 * list排序
	 */
	@Test
	public void sortList(){
		 /*
         * list中存字符串时必须指定参数为alpha，如果不使用SortingParams，而是直接使用sort("list")，
         * 会出现"ERR One or more scores can't be converted into double"
         */
        SortingParams sortingParameters = new SortingParams();
        sortingParameters.alpha();
        sortingParameters.limit(0, 3);
        System.out.println("返回排序后的结果-stringlists："+jedis.sort("stringlists",sortingParameters)); 
        System.out.println("返回排序后的结果-numberlists："+jedis.sort("numberlists"));
        // 子串：  start为元素下标，end也为元素下标；-1代表倒数一个元素，-2代表倒数第二个元素
        System.out.println("子串-第二个开始到结束："+jedis.lrange("stringlists", 1, -1));
	}
	
	/**
	 * 获取列表指定下表的值
	 */
	@Test
	public void getListValueByIndex(){
		// 获取列表指定下标的值 
        System.out.println("获取下标为2的元素："+jedis.lindex("stringlists", 2)+"\n");
	}
	
	
	@Test
	public void SetOperate() {
  	  System.out.println("======================set=========================="); 
        // 清空数据 
        System.out.println("清空库中所有数据："+jedis.flushDB());
        
        System.out.println("=============增=============");
        System.out.println("向sets集合中加入元素element001："+jedis.sadd("sets", "element001")); 
        System.out.println("向sets集合中加入元素element002："+jedis.sadd("sets", "element002")); 
        System.out.println("向sets集合中加入元素element003："+jedis.sadd("sets", "element003"));
        System.out.println("向sets集合中加入元素element004："+jedis.sadd("sets", "element004"));
        System.out.println("查看sets集合中的所有元素:"+jedis.smembers("sets")); 
        System.out.println();
        
        System.out.println("=============删=============");
        System.out.println("集合sets中删除元素element003："+jedis.srem("sets", "element003"));
        System.out.println("查看sets集合中的所有元素:"+jedis.smembers("sets"));
        /*System.out.println("sets集合中任意位置的元素出栈："+jedis.spop("sets"));//注：出栈元素位置居然不定？--无实际意义
        System.out.println("查看sets集合中的所有元素:"+jedis.smembers("sets"));*/
        System.out.println();
        
        System.out.println("=============改=============");
        System.out.println();
        
        System.out.println("=============查=============");
        System.out.println("判断element001是否在集合sets中："+jedis.sismember("sets", "element001"));
        System.out.println("循环查询获取sets中的每个元素：");
        Set<String> set = jedis.smembers("sets");   
        Iterator<String> it=set.iterator() ;   
        while(it.hasNext()){   
            Object obj=it.next();   
            System.out.println(obj);   
        }  
        System.out.println();
        
        System.out.println("=============集合运算=============");
        System.out.println("sets1中添加元素element001："+jedis.sadd("sets1", "element001")); 
        System.out.println("sets1中添加元素element002："+jedis.sadd("sets1", "element002")); 
        System.out.println("sets1中添加元素element003："+jedis.sadd("sets1", "element003")); 
        System.out.println("sets1中添加元素element002："+jedis.sadd("sets2", "element002")); 
        System.out.println("sets1中添加元素element003："+jedis.sadd("sets2", "element003")); 
        System.out.println("sets1中添加元素element004："+jedis.sadd("sets2", "element004"));
        System.out.println("查看sets1集合中的所有元素:"+jedis.smembers("sets1"));
        System.out.println("查看sets2集合中的所有元素:"+jedis.smembers("sets2"));
        System.out.println("sets1和sets2交集："+jedis.sinter("sets1", "sets2"));
        System.out.println("sets1和sets2并集："+jedis.sunion("sets1", "sets2"));
        System.out.println("sets1和sets2差集："+jedis.sdiff("sets1", "sets2"));//差集：set1中有，set2中没有的元素
    }
	@Test
    public void SortedSetOperate() {
  	  System.out.println("======================zset=========================="); 
        // 清空数据 
        System.out.println(jedis.flushDB()); 
        
        System.out.println("=============增=============");
        System.out.println("zset中添加元素element001："+jedis.zadd("zset", 7.0, "element001")); 
        System.out.println("zset中添加元素element002："+jedis.zadd("zset", 8.0, "element002")); 
        System.out.println("zset中添加元素element003："+jedis.zadd("zset", 2.0, "element003")); 
        System.out.println("zset中添加元素element004："+jedis.zadd("zset", 3.0, "element004"));
        System.out.println("zset集合中的所有元素："+jedis.zrange("zset", 0, -1));//按照权重值排序
        System.out.println();
        
        System.out.println("=============删=============");
        System.out.println("zset中删除元素element002："+jedis.zrem("zset", "element002"));
        System.out.println("zset集合中的所有元素："+jedis.zrange("zset", 0, -1));
        System.out.println();
        
        System.out.println("=============改=============");
        System.out.println();
        
        System.out.println("=============查=============");
        System.out.println("统计zset集合中的元素中个数："+jedis.zcard("zset"));
        System.out.println("统计zset集合中权重某个范围内（1.0——5.0），元素的个数："+jedis.zcount("zset", 1.0, 5.0));
        System.out.println("查看zset集合中element004的权重："+jedis.zscore("zset", "element004"));
        System.out.println("查看下标1到2范围内的元素值："+jedis.zrange("zset", 1, 2));
    }
}
