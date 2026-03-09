# YuAI Frontend

基于 Vue3 + Vite 的 AI 智能助手前端项目

## 功能特性

- 🏠 主页：应用选择界面
- 💕 AI 恋爱大师：恋爱咨询聊天应用
- 🤖 AI 超级智能体：全能AI助手

## 技术栈

- Vue 3
- Vue Router
- Axios
- Vite

## 安装依赖

```bash
npm install
```

## 开发运行

```bash
npm run dev
```

访问 http://localhost:3000

## 构建生产版本

```bash
npm run build
```

## 注意事项

- 确保后端服务运行在 http://localhost:8123
- 前端通过 Vite 代理转发 /api 请求到后端
- 使用 SSE (Server-Sent Events) 实现实时消息推送
