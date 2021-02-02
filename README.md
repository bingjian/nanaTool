# nana-tool
娜娜工具类开发宗旨是打造一套兼顾 SpringBoot 和 SpringCloud 项目的公共工具类

#检查是否有效maven pom配置
mvn help:effective-settings 

# 打包命令，跳过测试
mvn clean package -Dmaven.test.skip=true

# 发布命令
mvn clean deploy

# 统一版本修改
mvn versions:set -DnewVersion=1.0.1-SNAPSHOP

## 工程结构
````
nana-tool
├─nana-core-oss
└─nana-core-tool
````

## 鸣谢
* mica（[Mica](https://github.com/lets-mica/mica)）
* 如梦技术（[DreamLu](https://www.dreamlu.net/)）
* pigx（[Pig Microservice](https://www.pig4cloud.com/zh-cn/)）
* avue（[avue](https://avue.top/)）
* gitee.ltd（[gitee.ltd](https://gitee.ltd/)）
* 鲸宵（<a href="https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/jx.png" target="_blank">鲸宵</a>）

