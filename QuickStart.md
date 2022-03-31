# 快速运行

鉴于很多朋友拿到项目后不知道怎么快速的开始运行，特产出此文档

以下操作按照`OS`进行区分

## Window

> 前提条件
>
> 1. MySQL
> 2. Redis
> 3. IDEA

`1.当前项目的exam-vue目录下,路径输入cmd进入命令行`

```cmd
npm install # 安装项目所需的依赖(如果速度过慢, 可以尝试cnpm)
npm run serve #启动项目服务
```

此时, 浏览器打开, 输入网址http://localhost:8080，前端项目就完全跑起来了!

`2.使用IDEA进入当前项目的exam-exam目录下`

> 使用项目sql目录下的sql文件进行导入，然后使用maven导入依赖，启动项目，后台即可运行起来

## Linux

### docker部署

#### 后端

> 前提条件
>
> 服务器有Docker

1. 部署数据库等环境

> Docker启动MySQL，Redis

2. `重要`，application.yml中的Mysql，Redis地址改成服务器服务地址
3. 将项目在本地使用Maven打包并上传到服务器
4. 在项目根目录下执行一下命令

```sh
# 构建docker镜像
docker build -t exam-backend:1.0 .
# 通过docker启动服务
docker run -d --name exam-backend -p 8888:8888 exam-backend:1.0
```

#### 前端

1. 更改前端项目中的请求baseUrl到服务器后端地址
2. 构建打包代码

> npm run build

3. 使用Nginx映射前端代码

