# what is jj1 #
jj1 is an json-rpc implementation for java. json-rpc is a lightweight rpc protocol which uses json for messages. as json serializer and parser i use stringtree json, a lightweight json implementation.
  * json-rpc specification: http://json-rpc.org/
  * json specification: http://www.json.org/
  * stringtree json: http://www.stringtree.org/stringtree-json.html
jj1 contains a service handler and a service proxy. the service handler is used by the server side to provide a json service. service proxy can be used from the client to access a service.

# installation #
download the latest jj1 release from [downloads](http://code.google.com/p/jj1/downloads/list). it comes as a jar file and has to be in the classpath. additionally you need the stringtree json jar from [the stringtree json download page](https://sourceforge.net/project/showfiles.php?group_id=80689&package_id=226281&release_id=496375).

# service proxy #
if we have a service on http://jsolait.net/services/test.jsonrpc and we want to call the method echo which has a string as parameter and returns a string, we can use following code:
```
ServiceProxy proxy = new ServiceProxy("http://jsolait.net/services/test.jsonrpc");
String result = (String)proxy.call("echo", "hello you!");
System.out.println(result);
```
the above example should print "hello you!". if an error occurs a JsonRpcException will be thrown.

# service handler #
when you create a service handler you have to provide a context object. this object should provide the methods you want to call.
## servlet ##
the service handler is best used with a servlet. a simple one is provided and can be configured into a webapps web.xml like this:
```
<web-app>
  <servlet>
    <servlet-name>Jj1Servlet</servlet-name>
    <servlet-class>com.googlecode.server.jj1.Jj1Servlet</servlet-class>
    <init-param>
      <param-name>services</param-name>
      <param-value>test=com.googlecode.jj1.test.TestServices</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>Jj1Servlet</servlet-name>
    <url-pattern>/jj1/*</url-pattern>
  </servlet-mapping>
</web-app>
```
to configure the servlet you have to set the services parameter. it's a comma separated list of name = value pairs. the name is the contextname of the service, value is the name of the class. the contextname must be appended to the url. if a context named root is defined this service is called if you call the url without a context.
if the context path of the webapp is "webservice", the url to call a method from the com.googlecode.jj1.crap.TestServices class is like "http://domain.xx/webservice/jj1/test".

## context objects ##
to define a service you need to create a common java object. you can make some methods accessible from json-rpc by adding a @JsonRpc annotation. you can't call methods with no @JsonRpc annotation. if you use this context-object with the servlet it must have an empty constructor and must be listed in the services parameter.

```
/**
 * this method cannot be called as a json service
 */
public void doSomethingDangerous(){
  //...
}
	
@JsonRpc
public String echo(String in) {
  return in;
}
```