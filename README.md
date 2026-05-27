# 食刻外卖 - 面向中小餐饮商家的轻量级外卖系统

## 项目概述

本系统是河北环境工程学院本科毕业设计项目，适用于中小餐饮商家的外卖系统，采用前后端分离的三层架构，专注于线上点餐、订单管理、菜品管理、基础信息维护及运营数据统计分析的一体化便捷管理。

## 技术栈

### 用户端（微信小程序）
- **框架**：微信小程序原生框架
- **语言**：JavaScript / WXML / WXSS
- **数据存储**：本地 Storage（Mock 数据模式）/ 后端 API

### 后端
- **框架**：Spring Boot 2.7
- **ORM**：MyBatis-Plus 3.5
- **数据库**：MySQL 8.0
- **认证**：JWT Token
- **实时通信**：WebSocket（订单实时推送）
- **API文档**：Knife4j（Swagger）
- **连接池**：Druid

### 商家端 / 管理员端（Web后台）
- **框架**：Vue 3 + Vite
- **UI库**：Element Plus
- **图表**：ECharts
- **HTTP**：Axios
- **状态管理**：Pinia
- **路由**：Vue Router 4

## 项目结构

```
├── miniprogram/                # 微信小程序用户端
│   ├── app.js / app.json / app.wxss
│   ├── custom-tab-bar/         # 自定义底部导航栏
│   ├── utils/                  # 工具函数 + Mock数据
│   └── pages/                  # 16个页面
│       ├── index/              # 首页
│       ├── shop/               # 点餐页
│       ├── confirm/            # 确认订单
│       ├── order/              # 订单列表
│       ├── order-detail/       # 订单详情
│       ├── mine/               # 个人中心
│       ├── login/              # 登录
│       ├── search/             # 搜索
│       ├── address/            # 地址列表
│       ├── address-edit/       # 地址编辑
│       ├── personal-info/      # 个人信息
│       ├── favorites/          # 我的收藏
│       ├── history/            # 浏览历史
│       ├── comment/            # 评价
│       ├── about/              # 关于我们
│       └── agreement/          # 用户协议
│
├── backend/                    # Spring Boot 后端
│   ├── pom.xml
│   ├── sql/init.sql            # 数据库初始化脚本(14张表)
│   └── src/main/java/com/koala/takeout/
│       ├── entity/             # 12个实体类
│       ├── mapper/             # 12个Mapper接口
│       ├── service/            # 7个业务Service
│       ├── controller/         # 11个REST Controller
│       ├── config/             # WebMvc/WebSocket/MyBatisPlus配置
│       ├── interceptor/        # JWT认证拦截器
│       ├── websocket/          # WebSocket订单推送
│       └── utils/              # JWT工具类
│
└── admin/                      # Vue3管理后台(商家端+管理员端)
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── router/             # 路由配置
        ├── utils/              # Axios请求封装
        └── views/              # 12个页面
            ├── Login.vue       # 登录(商家/管理员)
            ├── Layout.vue      # 布局框架(侧边栏导航)
            ├── Dashboard.vue   # 工作台(统计+图表)
            ├── OrderManage.vue # 订单管理
            ├── CategoryManage.vue # 类目管理
            ├── DishManage.vue  # 菜品管理
            ├── ComboManage.vue # 套餐管理
            ├── EmployeeManage.vue # 员工管理
            ├── Settings.vue    # 运营设置
            ├── MerchantAudit.vue  # 商家审核(管理员)
            ├── SystemConfig.vue   # 系统配置(管理员)
            └── DataMonitor.vue    # 数据监控(管理员)
```

## 功能清单

### 用户端功能（微信小程序）
- [x] **微信登录**：通过昵称+头像快速登录
- [x] **菜品浏览**：按分类展示菜品与套餐，支持关键词搜索
- [x] **购物车**：菜品添加、数量调整、单个删除、一键清空
- [x] **订单支付**：虚拟支付流程，模拟支付操作与结果反馈
- [x] **订单管理**：查看订单列表及状态（待支付/待接单/配送中/已完成）
- [x] **收货地址**：新增、编辑、删除、设置默认地址
- [x] **商家收藏**：收藏/取消收藏商家
- [x] **浏览历史**：记录浏览过的商家
- [x] **评价系统**：对已完成订单进行评分和评价
- [x] **个人信息**：修改头像和昵称
- [x] **订单操作**：支付、取消、退款、确认收货、删除

### 商家管理端功能（Web后台）
- [x] **员工管理**：新增、编辑、删除、启用/禁用员工
- [x] **类目管理**：菜品分类的增删改查和排序
- [x] **菜品管理**：菜品的增删改查、启售/停售、库存管理
- [x] **套餐管理**：套餐的创建、编辑、菜品关联
- [x] **订单管理**：接单、配送、退款、订单状态变更
- [x] **运营设置**：营业时间、配送费、商家信息维护
- [x] **工作台**：订单统计、营业额、近7日趋势图表

### 管理员端功能（Web后台）
- [x] **商家审核**：审核商家入驻申请、启用/停业操作
- [x] **系统配置**：平台参数维护（订单超时、默认配送费等）
- [x] **数据监控**：平台总览、商家数据对比、订单趋势、运营报表

## 使用说明

### 小程序端
1. 下载 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 导入 `miniprogram` 目录
3. 修改 `project.config.json` 中的 `appid`
4. 编译预览（当前使用Mock数据，无需后端）

### 后端
1. 安装 MySQL 8.0，执行 `backend/sql/init.sql` 初始化数据库
2. 修改 `application.yml` 中的数据库连接信息
3. 使用 Maven 编译运行：`mvn spring-boot:run`
4. 后端启动于 `http://localhost:8080`

### 管理后台
1. 进入 `admin` 目录
2. 安装依赖：`npm install`
3. 启动开发服务器：`npm run dev`
4. 访问 `http://localhost:3000`
5. 商家账号：merchant1 / 123456
6. 管理员账号：admin / 123456

## 开发者信息

- **姓名**：董呈旭
- **学号**：20220614316
- **院校**：河北环境工程学院
- **专业**：软件工程B223
- **指导教师**：李淑芳 教授
