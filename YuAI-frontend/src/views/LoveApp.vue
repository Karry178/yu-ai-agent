<template>
  <div class="chat-container">
    <div class="chat-header">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h2>💕 AI 恋爱大师</h2>
      <div class="spacer"></div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']"
      >
        <div class="message-content">
          <div class="avatar">{{ msg.role === 'user' ? '👤' : '💕' }}</div>
          <div class="text">{{ msg.content }}</div>
        </div>
      </div>
      <div v-if="loading" class="message ai-message">
        <div class="message-content">
          <div class="avatar">💕</div>
          <div class="text typing">正在思考...</div>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <input
        v-model="inputMessage"
        @keyup.enter="sendMessage"
        placeholder="输入你的问题..."
        :disabled="loading"
      />
      <button @click="sendMessage" :disabled="loading || !inputMessage.trim()">
        发送
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const chatId = ref('')

onMounted(() => {
  chatId.value = 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
})

const goBack = () => {
  router.push('/')
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  const userMessage = inputMessage.value.trim()
  messages.value.push({
    role: 'user',
    content: userMessage
  })
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const url = `/api/ai/love_app/chat/sse?message=${encodeURIComponent(userMessage)}&chatId=${chatId.value}`
    const eventSource = new EventSource(url)
    
    let aiMessage = ''
    let messageIndex = messages.value.length

    eventSource.onmessage = (event) => {
      const data = event.data
      if (data === '[DONE]') {
        eventSource.close()
        loading.value = false
        return
      }
      
      aiMessage += data
      
      if (messages.value[messageIndex]) {
        messages.value[messageIndex].content = aiMessage
      } else {
        messages.value.push({
          role: 'assistant',
          content: aiMessage
        })
      }
      scrollToBottom()
    }

    eventSource.onerror = (error) => {
      console.error('SSE错误:', error)
      eventSource.close()
      loading.value = false
      if (!aiMessage) {
        messages.value.push({
          role: 'assistant',
          content: '抱歉，连接出现问题，请稍后重试。'
        })
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    loading.value = false
    messages.value.push({
      role: 'assistant',
      content: '抱歉，发送消息失败，请稍后重试。'
    })
  }
}
</script>

<style scoped>
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: white;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.chat-header h2 {
  flex: 1;
  text-align: center;
  margin: 0;
}

.spacer {
  width: 80px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;
}

.message {
  margin-bottom: 20px;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message .message-content {
  justify-content: flex-end;
}

.ai-message .message-content {
  justify-content: flex-start;
}

.message-content {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  width: 100%;
}

.user-message .message-content {
  flex-direction: row-reverse;
  justify-content: flex-start;
}

.ai-message .message-content {
  justify-content: flex-start;
}

.avatar {
  font-size: 2rem;
  flex-shrink: 0;
}

.text {
  max-width: min(600px, 70%);
  min-width: 100px;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  word-break: break-word;
}

.user-message .text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin-left: auto;
}

.ai-message .text {
  background: white;
  color: #333;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  margin-right: auto;
}

@media (max-width: 768px) {
  .text {
    max-width: 80%;
  }
}

@media (max-width: 480px) {
  .text {
    max-width: 85%;
  }
  
  .avatar {
    font-size: 1.5rem;
  }
}

.typing {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.chat-input {
  display: flex;
  padding: 20px;
  background: white;
  border-top: 1px solid #e0e0e0;
  gap: 10px;
}

.chat-input input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s;
}

.chat-input input:focus {
  border-color: #667eea;
}

.chat-input button {
  padding: 12px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: opacity 0.3s;
}

.chat-input button:hover:not(:disabled) {
  opacity: 0.9;
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
