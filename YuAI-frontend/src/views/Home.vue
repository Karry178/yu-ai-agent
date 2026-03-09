<template>
  <div class="home-container">
    <div class="content">
      <h1 class="title">YuAI 智能助手</h1>
      <p class="subtitle">选择你需要的AI应用</p>
      
      <div class="app-cards">
        <div class="app-card" @click="goToApp('/love')">
          <div class="card-icon">💕</div>
          <h2>AI 恋爱大师</h2>
          <p>专业的恋爱咨询和情感建议</p>
        </div>
        
        <div class="app-card" @click="goToApp('/manus')">
          <div class="card-icon">🤖</div>
          <h2>AI 超级智能体</h2>
          <p>全能AI助手，解决各种问题</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToApp = (path) => {
  router.push(path)
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.content {
  text-align: center;
  max-width: 900px;
  width: 100%;
}

.title {
  font-size: 3rem;
  color: white;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 50px;
}

.app-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.app-card {
  background: white;
  border-radius: 20px;
  padding: 40px 30px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.app-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
}

.card-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.app-card h2 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 10px;
}

.app-card p {
  color: #666;
  font-size: 1rem;
  line-height: 1.6;
}
</style>
