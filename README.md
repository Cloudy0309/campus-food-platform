---
AIGC:
    Label: "1"
    ContentProducer: 001191440300708461136T1XGW3
    ProduceID: 87c3d0db86386f77793d3df9a29d631e_5ea0574b783911f1a7da5254006c9bbf
    ReservedCode1: nUq/5Re0rKZadCwwMD0j5yi6Q7mJ+Wm+ceC6q60CzdmAU4/U/cup+UrsnSFARyPIduDCg1z43wQcNjjDKckM/ZJyRGfglqDxU8o8r2J3JM1Ln1vLFUhhbwzMLGRBW2wvldt3zlxu7A2b9k7XvD0tyrAWC5YKM5HsUbrzHPasQAviffccT93IgXFqQxQ=
    ContentPropagator: 001191440300708461136T1XGW3
    PropagateID: 87c3d0db86386f77793d3df9a29d631e_5ea0574b783911f1a7da5254006c9bbf
    ReservedCode2: nUq/5Re0rKZadCwwMD0j5yi6Q7mJ+Wm+ceC6q60CzdmAU4/U/cup+UrsnSFARyPIduDCg1z43wQcNjjDKckM/ZJyRGfglqDxU8o8r2J3JM1Ln1vLFUhhbwzMLGRBW2wvldt3zlxu7A2b9k7XvD0tyrAWC5YKM5HsUbrzHPasQAviffccT93IgXFqQxQ=
---



# 校园美食推荐平台

基于 Vue3 + SpringBoot 的全栈 Web 项目，为本校学生提供美食发现、探店分享、收藏评价的一站式平台。

## 项目结构

```
campus-food-platform/
├── frontend/              # Vue3 前端
│   ├── src/
│   │   ├── views/         # 7 个页面组件
│   │   ├── router/        # 路由配置
│   │   ├── api/           # 后端接口封装
│   │   ├── App.vue        # 根组件（导航栏+页脚）
│   │   ├── main.js        # 入口（Element Plus 注册）
│   │   └── style.css      # 全局样式
│   ├── index.html
│   ├── vite.config.js     # 代理配置（/api → 8080）
│   └── package.json
└── backend/               # SpringBoot 后端
    ├── pom.xml
    └── src/main/
        ├── java/com/campus/food/
        │   ├── Application.java
        │   ├── entity/    # User, Shop, Note, Comment, Favorite
        │   ├── controller/ # ShopController, UserController
        │   └── config/    # CorsConfig, DataInitializer
        └── resources/
            └── application.yml
```

## 技术栈

| 层 | 技术 |
|---|---|
| 前端框架 | Vue 3 + Vite |
| UI 组件库 | Element Plus |
| 路由 | Vue Router 4 |
| 后端框架 | Spring Boot 3.2 |
| 数据库 | H2（内置）/ MySQL |
| 缓存 | Redis |
| 构建工具 | Maven |

## 快速启动

### 1. 环境要求

- **Node.js** ≥ 18（[下载](https://nodejs.org)）
- **JDK** ≥ 17（[下载](https://adoptium.net)）
- **Maven** ≥ 3.8（或使用 IDE 内置）

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动在 `http://localhost:8080`，H2 控制台：`http://localhost:8080/h2-console`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动在 `http://localhost:3000`

## 页面路由

| 路由 | 页面 | 说明 |
|---|---|---|
| `/` | 首页 | 轮播图、分类导航、人气榜单、最新探店 |
| `/category` | 美食分类 | 多条件筛选 + 瀑布流卡片 |
| `/detail/:id` | 美食详情 | 大图轮播、店铺信息、评论互动、悬浮收藏 |
| `/publish` | 发布探店 | 图片上传、星级打分、标签选择 |
| `/search` | 搜索 | 模糊搜索店铺/菜品 |
| `/profile` | 个人中心 | 收藏/笔记/评论管理 |
| `/login` | 登录注册 | 账号密码认证 |

## 核心功能

- 用户登录注册（未登录拦截发布/收藏/评论）
- 多维度美食筛选（价格、辣度、食堂、用餐时段）
- 探店分享（多图上传 + 星级打分 + 标签）
- 收藏店铺、评论互动
- 模糊搜索店铺/菜品/笔记
- 人气榜单（按收藏数排序）
- 响应式布局（PC + 手机端适配）
*（内容由AI生成，仅供参考）*
*（内容由AI生成，仅供参考）*
