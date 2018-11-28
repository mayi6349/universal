/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.simple.kwA.plugin;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;

/**
 * jfinal2.0后移除了SpringPlugin,修改代码支持SpringPlugin
 * IocInterceptor.
 */
public class IocInterceptor implements Interceptor {
    
    static ApplicationContext ctx;
    
    // ActionInvocation 改为2.0的Invocation
    public void intercept(Invocation ai) {
        // 修改了这行 获取fields
        //重点也在这 追随2.0的AOP 不限于Controller 也可以在Service层注入
        Field[] fields = ai.getMethod().getDeclaringClass().getDeclaredFields();
        for (Field field : fields) {
            Object bean;
            if (field.isAnnotationPresent(Inject.BY_NAME.class))
                bean = ctx.getBean(field.getName());
            else if (field.isAnnotationPresent(Inject.BY_TYPE.class))
                bean = ctx.getBean(field.getType());
            else
                continue;
            
            try {
                if (bean != null) {
                    field.setAccessible(true);
                    field.set(ai.getTarget(), bean);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        ai.invoke();
    }
}
