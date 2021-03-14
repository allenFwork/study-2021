# spring boot 源码

## 1. spring boot 使用

### 1.1 添加依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.study</groupId>
    <artifactId>spring-boot01</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.0.5.RELEASE</version>
        </dependency>
    </dependencies>

</project>
```

### 1.2 源码

```java
package com.study.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    @RequestMapping("/update")
    @ResponseBody
    public Map<String, String> update(@RequestParam("value") String value) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("v", value);
        return map;
    }

}
```

### 1.3 启动

```java
package com.study.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Test {

    public static void main(String[] args) {java
        SpringApplication.run(Test.class);
    }

}
```

## 2. springboot + websocket

### 2.1 图表制作 echart

- echart官网地址：https://echarts.apache.org/examples/zh/index.html#chart-type-bar

- 源码：

  ```html
  <!DOCTYPE html>
  <html>
  <head>
      <meta charset="utf-8"/>
      <!-- 引入 ECharts 文件 -->
      <script src="https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js">
  
      </script>
  </head>
  <body>
  <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
  <div id="main" style="width: 600px;height:400px;"></div>
  </body>
  <script type="text/javascript">
      // 基于准备好的dom，初始化echarts实例
      var myChart = echarts.init(document.getElementById('main'));
  
      // 指定图表的配置项和数据
      var option = {
          title: {
              text: 'ECharts 入门示例'
          },
          tooltip: {},
          legend: {
              data:['销量']
          },
          xAxis: {
              data: ["衬衫","羊毛衫","雪纺衫","裤子","高跟鞋","袜子"]
          },
          yAxis: {},
          series: [{
              name: '销量',
              type: 'bar',
              data: [5, 20, 36, 10, 10, 20]
          }]
      };
  
      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
      
  </script>
  </html>
  ```

### 2.2 websocket

#### 2.2.1 依赖：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.study</groupId>
    <artifactId>spring-boot01</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
            <version>2.0.5.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>webjars-locator-core</artifactId>
            <version>0.33</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>sockjs-client</artifactId>
            <version>1.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>stomp-websocket</artifactId>
            <version>2.3.3</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>bootstrap</artifactId>
            <version>3.3.7</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>3.1.1-1</version>
        </dependency>
    </dependencies>

</project>
```

#### 2.2.2 服务端

```java
package com.study.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {

		// 规定了客户端订阅的请求信息开头是以“/topic”开头
		config.enableSimpleBroker("/topic");

		// 客户端向服务端发出请求时，请求以 “/subscribeName” 开头，那么就会将这个请求跳转到对应的注解方法中处理
		// （订阅了以 “/subscribeName” 开头的请求）
		config.setApplicationDestinationPrefixes("/subscribeName");
	}

	/**
	 * 这里设置的 Endpoint 就是通过在 js 中定义的socket中设置的值
	 * 连接点的名字，用于进行websocket的客户端和服务端的连接
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/connectName").withSockJS();
	}

}
```

```java
package com.study.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GreetingController {

	@MessageMapping("/hello")
	@SendTo("/topic/echarts")
	public Integer update(Message message) {
		byte[] bytes = (byte[]) message.getPayload();
		String s = new String(bytes);
		System.out.println(s);
		return 90;
	}

}
```

#### 2.2.3 客户端

```html
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>

        <!-- 通过maven的依赖，将jquery的js打成jar包导入到项目中，这里直接使用 -->
        <script src="/webjars/jquery/3.1.1-1/jquery.min.js"></script>
        <script src="/webjars/sockjs-client/1.0.2/sockjs.min.js"></script>
        <script src="/webjars/stomp-websocket/2.3.3/stomp.min.js"></script>

        <!-- 引入 ECharts 文件 -->
        <script src="https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js">

        </script>
    </head>
    <body>
        <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
        <div id="main" style="width: 600px;height:400px;"></div>
    </body>
    <script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));

        // 指定图表的配置项和数据
        var option = {
            title: {
                text: 'ECharts 入门示例'
            },
            tooltip: {},
            legend: {
                data:['销量']
            },
            xAxis: {
                data: ["衬衫","羊毛衫","雪纺衫","裤子","高跟鞋","袜子"]
            },
            yAxis: {},
            series: [{
                name: '销量',
                type: 'bar',
                data: [5, 20, 36, 10, 10, 20]
            }]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);


        // 连接到哪台机子上
        // ws = connws("ws:localhost:8080/connectName");
        // ws.send("/app");
        // websocket的使用

        function connect() {
            // 1.链接
            var socket = new SockJS('/connectName');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);
                // 2.订阅
                stompClient.subscribe('/topic/echarts', function (data) {
                    console.log(data.body);
                    console.log(option.series[0].data[5] = data.body);
                    myChart.setOption(option);
                });
            });
        }

        function sendName() {
            stompClient.send("/subscribeName/hello", {}, JSON.stringify({'name': 'superman'}));
        }
        // 调用连接方法，刷新页面进行自动连接
        connect();

    </script>
    <a href="javascript:sendName()">sendMessage</a>
</html>
```

## 3. 