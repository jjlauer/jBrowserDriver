/* 
 * jBrowserDriver (TM)
 * Copyright (C) 2014-2016 Machine Publishers, LLC
 * 
 * Sales and support: ops@machinepublishers.com
 * Updates: https://github.com/MachinePublishers/jBrowserDriver
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.machinepublishers.jbrowserdriver;

import java.lang.reflect.Field;
import java.net.URL;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.webkit.network.CookieManager;
import java.io.File;

class JBrowserMain {
  
    public static void main(String[] args) {
    try {
      CookieManager.setDefault(new CookieStore());
      try {
        URL.setURLStreamHandlerFactory(new StreamHandler());
      } catch (Throwable t) {
        Field factory = null;
        try {
          factory = URL.class.getDeclaredField("factory");
          factory.setAccessible(true);
          Object curFac = factory.get(null);

          //assume we're in the Eclipse jar-in-jar loader
          Field chainedFactory = curFac.getClass().getDeclaredField("chainFac");
          chainedFactory.setAccessible(true);
          chainedFactory.set(curFac, new StreamHandler());
        } catch (Throwable t2) {
          //this should work regardless
          factory.set(null, new StreamHandler());
        }
      }
      
      JBrowserDriverServer server = new JBrowserDriverServer();
      
      Settings settings = new Settings.Builder()
          .headless(false)
          .quickRender(false)
          .cache(true)
          .cacheDir(new File("target", "cache"))
          .logTrace(true)
          .build();
      
      server.setUp(settings);
      server.init();
      server.get("http://jet.com");
      
      System.out.println("Next stuff...");

    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }
}