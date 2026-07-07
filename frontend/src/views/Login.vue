<template>
  <div class="login-page">
    <div class="login-card">
      <h2>{{ isLogin ? '登录' : '注册' }}</h2>
      <p class="sub">校园美食推荐平台</p>

      <el-form style="margin-top:24px">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item v-if="!isLogin">
          <el-input v-model="form.confirmPwd" type="password" placeholder="确认密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;background:#FF6600;border-color:#FF6600;border-radius:22px" @click="handleSubmit" :loading="submitting">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <p class="switch" @click="isLogin = !isLogin">
        {{ isLogin ? '没有账号？立即注册' : '已有账号？去登录' }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../api'

const router = useRouter()
const isLogin = ref(true)
const submitting = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPwd: ''
})

async function handleSubmit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!isLogin.value && form.password !== form.confirmPwd) {
    ElMessage.warning('两次密码不一致')
    return
  }

  submitting.value = true
  try {
    const res = isLogin.value 
      ? await login({ username: form.username, password: form.password })
      : await register({ username: form.username, password: form.password, nickname: form.username })
    
    if (res.data.success) {
      localStorage.setItem('user', JSON.stringify(res.data.user))
      ElMessage.success(isLogin.value ? '登录成功' : '注册成功')
      router.push('/')
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    const msg = e.response?.data?.message || '网络错误，请检查后端是否启动'
    ElMessage.error(msg)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-page { display: flex; align-items: center; justify-content: center; min-height: 60vh; }
.login-card { width: 400px; background: #fff; border-radius: 16px; padding: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); text-align: center; }
.login-card h2 { font-size: 24px; font-weight: 700; }
.sub { font-size: 13px; color: #999; margin-top: 4px; }
.switch { margin-top: 16px; font-size: 13px; color: #FF6600; cursor: pointer; }
.switch:hover { text-decoration: underline; }

@media (max-width: 768px) {
  .login-card { width: 90%; padding: 24px; }
}
</style>