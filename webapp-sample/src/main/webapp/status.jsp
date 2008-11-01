
<%@page import="org.sakaiproject.kernel.api.Kernel"%>
<%@page import="org.sakaiproject.kernel.api.KernelManager"%>
<%@page import="org.sakaiproject.kernel.api.ServiceSpec"%>
<%@page import="org.sakaiproject.kernel.api.PackageRegistryService"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.io.InputStream"%>
<%@page import="org.sakaiproject.kernel.api.ClassExporter"%><html>
<!--
  Copyright 2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<body bgcolor="white">
<h1>K2 Exported Packages Information</h1>
<font size="4"> <%
   KernelManager km = new KernelManager();
   Kernel kernel = km.getKernel();
   PackageRegistryService packageRegistryService = km
       .getService(PackageRegistryService.class);
   Map<String, String> exports = packageRegistryService.getExports();
 %> <br>
<ul>
<%
  for (Map.Entry<String, String> e : exports.entrySet()) {
%> <li>
<pre>
Key <%=e.getKey()%>,
Classloader <%=e.getValue()%> 
</pre>
</li><%
   }
 %> <ul>


<h1>Test 1</h1>
<p>
Classloader for org.sakaiproject.componentsample.api.HelloWorldService is
</p>
<pre>
<%= packageRegistryService.findClassloader("org.sakaiproject.componentsample.api.HelloWorldService") %>
</pre>
<h1>Test 2</h1>
<p>
Classloader for org.sakaiproject.componentsample.api.HelloWorldService is
</p>
<pre>
<% 
Class c = this.getClass().getClassLoader().loadClass("org.sakaiproject.componentsample.api.HelloWorldService");
%>
Loaded Class: 
<%= c %>
<h1>Test 2</h1>
<p>
Classloader for org.sakaiproject.componentsample.api.HelloWorldService is
</p>
<pre>
<% 
InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/sakaiproject/componentsample/api/HelloWorldService.class");
%>
Loaded Object: 
<%= in %>
</pre>
</font>
</body>
</html>
