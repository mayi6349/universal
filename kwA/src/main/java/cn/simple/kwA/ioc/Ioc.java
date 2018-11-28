package cn.simple.kwA.ioc;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;

/**
 * JFinal的ioc插件<br>
 * 
 * 在JFinal添加IOC插件，通过add()方法和addPackage()方法添加bean<br>
 * bean可实现单例模式。可以自定义获取bean的key值<br>
 * 获取bean的方法getBean(String key)、getBean(Class<?> key)
 * @author songyang
 * 
 */
public class Ioc implements IPlugin{
	
	private static final Ioc me =  new Ioc();
	
	private Ioc(){
	}
	
	public static Ioc getIoc(){
		return me;
	}
	

	//零配置！！！
	//每次获取都是新的对象
	private Map<String, Class<?>> bean = new HashMap<>();
	//获取single单例对象
	private Map<String, Object> singleBean = new HashMap<>();
	
	//添加bean
	public Ioc add(Class<?> calss,boolean isSingle){
		add(calss.getName() , calss,isSingle);
		return this;
	}
	
	public Ioc add(String calss,boolean isSingle){
		try {
			add(calss , Class.forName(calss),isSingle);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public Ioc add(String key , String calss,boolean isSingle){
		try {
			add(key , Class.forName(calss),isSingle);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public Ioc add(String key,Class<?> calss,boolean isSingle){
		if(isSingle){
			try {
				singleBean.put(key, calss.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}else{
			bean.put(key , calss);
		}
		
		return this;
	}
	
	
	public Ioc addPackage(String pack , boolean isSingle){
		
		
		File dir = new File(this.getClass().getResource("/" + pack.replaceAll("\\.", "/")).getFile());
		
		//如果不存在或者 也不是目录就直接返回 
        if (!dir.exists() || !dir.isDirectory()) { 
            return null; 
        } 
        File[] dirfiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				 return pathname.getName().endsWith(".class"); 
			}
		}); 
		
        for (File file : dirfiles) { 
        	
        		
			String className = pack + "."
					+ file.getName().substring(0, file.getName().length() - 6);

			try {
				Class<?> calss = Class.forName(className);
				Service s = calss.getAnnotation(Service.class);
				if (s != null) {
					if (StrKit.isBlank(s.value())) {
						add(calss, isSingle);
					} else {
						add(s.value(), calss, isSingle);
					}
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
        	
        }
		
		return this;
	}
	
	
	public static Object getBean(String key){
		
		if(getIoc().bean.containsKey(key)){
			try {
				return getIoc().bean.get(key).newInstance();
			} catch (InstantiationException |IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} 
		}
			
		return getIoc().singleBean.get(key);
	}
	
	public static Object getBean(Class<?> key){
		return getBean(key.getName());
	}
	
	
	@Override
	public boolean start() {
		
		return true;
	}

	@Override
	public boolean stop() {
		bean.clear();
		return true;
	}

}
