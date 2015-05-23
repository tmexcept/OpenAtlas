# OpenAtlas
OpenAtlas ,a Android plugin framework，The MIT License (MIT) Copyright (c) 2015 Bunny Blue,achellies
This is A Plugin Framework run like Taobao.
  <br>至于资源这块处理仁者见仁智者见智。代码遵循MIT License，目前使用patch aapt进行资源分区，虽然现在这个项目带有插件的名字，严格意义上说这个已经从插件里面出来了，跟系统一样管理各个组件，叫做component更合适。对于项目的分析，正在进行，有bug的话可以在issue里面提交，QQ群不太适合讨论技术。</br>



## plugin start
download aapt from repo,and  you should use build-tool @R22,
write your plugin as normal app, ant build  with  hacked aapt.
### plugin resource notice
you can define your package id at Manifest by "versionName",such as versionName:"1.0.1" ,but as a plugin should be versionName:"1.0.10x7a",you will get apk which versionName is "1.0.0" but package id is 0x7a not 0x7f.you can use 0x2 to 0x7,
also you can define package change packageName "com.myapp.pkgname" to " com.myapp.pkgname0x7a".

##Sample & Art
<a href="https://github.com/bunnyblue/OpenAtlas/raw/bunny/Dist/AtlasLauncher.apk">
  Sample Apk,you can download from here
</a>

![Sample Gif](https://github.com/bunnyblue/OpenAtlas/raw/bunny/art/demo.gif)

## License
The MIT License (MIT) Copyright (c) 2015 Bunny Blue,achellies



Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
