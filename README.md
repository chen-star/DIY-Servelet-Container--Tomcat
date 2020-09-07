# DIY Servelet Container [Tomcat]
---

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)

[![ForTheBadge built-with-love](http://ForTheBadge.com/images/badges/built-with-love.svg)](https://GitHub.com/Naereen/)


[![Ask Me Anything !](https://img.shields.io/badge/Ask%20me-anything-1abc9c.svg)](https://GitHub.com/Naereen/ama)
[![Open Source? Yes!](https://badgen.net/badge/Open%20Source%20%3F/Yes%21/blue?icon=github)](https://github.com/Naereen/badges/)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)


> GitHub [@Alex Chen](https://github.com/chen-star) &nbsp;&middot;&nbsp;

---

## Overview

#### What is Tomcat

Simply put, Apache Tomcat is a web server and ***servlet container*** that is used to deploy and serve Java web applications.


#### Then What is Servlet & Web Container

A ***Servelt*** is a class that handles requests, processes them and reply back with a response. 

Servlets are under the control of another Java Application called a ***Servlet Container***. When an application running in a web server receives a request, the server hands the requst to the servlet container - which in turn passes it to the target Servlet. 

~~~
                                         -----------> Servelt 1
		  request                        |
Client ------------>  Servlet Container-
		                                 |
										 -----------> Servlet 2

~~~


## Tomcat Components

**server.xml structure**

~~~xml
<Server>
    <Service name="Catalina">
        <Engine defaultHost="localhost">
            <Host name="localhost">
                <Context path="/b" docBase="/Users/alexchen/programs/diytomcat/b"/>
            </Host>
        </Engine>
    </Service>
</Server>

~~~

* **Server**
	
	- A Server element represents the entire Catalina servlet container.
	- Therefore, it must be the single outermost element in the conf/server.xml
	- A Server can have ***mulitple*** Service
	
* **Service**
	
	- A Server can have ***1*** Service
	
* **Engine**


* **Host**

	- The Host element represents a virtual host, which is an association of a network name for a server (such as "www.alex.com" with the particular server on which Catalina is running.

	
* **Context**

	- The Context element represents a web application, which is run within a particular virtual host. 
	- Each web application is based on a Web Application Archive (WAR) file, or a corresponding directory containing the corresponding unpacked contents


Service --> one Engine ---> many hosts --> many context
