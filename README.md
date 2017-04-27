# invesdwin-context-rintegrate
Integrate R functionality with these modules for invesdwin-context 

## Maven

Releases and snapshots are deployed to this maven repository:
```
http://invesdwin.de/artifactory/invesdwin-oss-remote
```

Dependency declaration:
```xml
<dependency>
	<groupId>de.invesdwin</groupId>
	<artifactId>invesdwin-context-rintegrate</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

## License Discussion

Please note that [Renjin](http://www.renjin.org/) (as a fast R-Engine for the JVM) and some [R](https://www.r-project.org/) packages that are used here are licensed under the GPL. Thus we have decided to publish our R modules here as GPL as well. Even though the rest of the invesdwin platform is still licensed under the LGPL (if not otherwise noted), you should be aware that by using these R modules in your main application you will have to follow the restrictions that are imposed by the GPL. If you do not want to make your whole application available under the terms of the GPL, you could go for a compromise by writing standalone applications for your R-scripts (which are licensed under the GPL then) and integrating them by calling them as separate processes via the command line from your main application. This at least will provide reusable (and hopefully high quality) command line applications for using R that the rest of the community can use and this will also allow you to leverage R functionality in a legally tolerated way (even though we are no lawyers and this is not any sort of legal advice, it still seems to be [what the FSF came to terms with](https://www.gnu.org/licenses/gpl-faq.html#GPLPlugins)). As long as there is no intimate relationship between the main application and the command line application this border case seems to be accepted. As an alternative you could integrate R directly via [JRI](https://rforge.net/JRI/) which seems to be licensed under the LGPL (even though this itself dynamically links to R libs and is thus legally questionable, even if R itself has an exception for allowing dynamic linking in its version of the GPL, most R packages don't have that). Also the idea of [using Renjin only behind the javax.script API](https://groups.google.com/forum/#!msg/renjin-dev/yoS1dTeJLm8/bVtVu_tGLck) seems to be legally questionable too in this regard since it still dynamically links to Renjin in the same JVM, just with an API layer in between. So maybe the best alternative would be to call the original R executable directly from your main application as a command line tool and let it run your R script. Though keep in mind that you might still have to make at least your R scripts open source if they use R packages that are licensed under the GPL. By using the R executable directly you would loose the performance benefit that Renjin brings to the table. But as long as Renjin is licensed under the GPL and is used in the same classpath as your main application (since there is no classpath exception in their version of the license, which might be imposed by R itself), you might essentially still be dynamically linking to a GPL library and are thus subjecting yourself to its license demands. So it might be better to stay fair by following the terms of the GPL and give something back to the open source community. That is at least the decision we are taking for our R modules. If you have any advice on this topic, we would be glad to hear more about it.
