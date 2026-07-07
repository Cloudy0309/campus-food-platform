<template>
  <div id="app-root">
    <nav class="main-nav">
      <div class="nav-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">🍽</span>
          <span class="logo-text">校园美食</span>
        </router-link>
        
        <div class="nav-center">
          <router-link to="/">首页</router-link>
          <router-link to="/category">美食分类</router-link>
          <router-link to="/search" class="search-link">🔍 搜索</router-link>
        </div>

        <div class="nav-right">
          <router-link to="/publish" v-if="user">✏ 发布探店</router-link>
          <router-link to="/profile" v-if="user" class="user-link">👤 {{ user.nickname }}</router-link>
          <router-link to="/login" v-else>登录 / 注册</router-link>
        </div>

        <button class="mobile-menu-btn" @click="mobileMenuOpen = !mobileMenuOpen">☰</button>
      </div>

      <div class="mobile-menu" v-if="mobileMenuOpen">
        <router-link to="/" @click="mobileMenuOpen = false">首页</router-link>
        <router-link to="/category" @click="mobileMenuOpen = false">美食分类</router-link>
        <router-link to="/search" @click="mobileMenuOpen = false">搜索</router-link>
        <router-link to="/publish" v-if="user" @click="mobileMenuOpen = false">发布探店</router-link>
        <router-link to="/profile" v-if="user" @click="mobileMenuOpen = false">个人中心</router-link>
        <router-link to="/login" v-else @click="mobileMenuOpen = false">登录 / 注册</router-link>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
    
    <footer class="main-footer">
      <p>校园美食发现平台 — 汇聚全校吃货的真实评价</p>
      <p class="feedback">📧 意见反馈 / 联系我们</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const user = ref(null)
const mobileMenuOpen = ref(false)

function updateUser() {
  const u = localStorage.getItem('user')
  user.value = u ? JSON.parse(u) : null
}

updateUser()

watch(() => route.path, () => {
  updateUser()
})

window.addEventListener('storage', (e) => {
  if (e.key === 'user') updateUser()
})
</script>

<style scoped>
.main-nav { position: sticky; top: 0; z-index: 100; background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.nav-inner { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; height: 56px; }
.logo { display: flex; align-items: center; gap: 8px; font-size: 20px; font-weight: 700; color: #FF6600; flex-shrink: 0; }
.logo-icon { font-size: 28px; }

.nav-center { display: flex; gap: 24px; align-items: center; font-size: 14px; }
.nav-center a { color: #666; padding: 6px 0; border-bottom: 2px solid transparent; transition: all 0.2s; }
.nav-center a:hover, .nav-center a.router-link-active { color: #FF6600; border-bottom-color: #FF6600; }

.nav-right { display: flex; gap: 16px; align-items: center; font-size: 14px; }
.nav-right a { color: #666; padding: 6px 12px; transition: all 0.2s; }
.nav-right a:hover { color: #FF6600; }
.user-link { background: #FFF5F0; border-radius: 16px; padding: 6px 16px; }

.mobile-menu-btn { display: none; font-size: 20px; background: none; border: none; cursor: pointer; }
.mobile-menu { display: none; background: #fff; border-top: 1px solid #EEE; padding: 12px; }
.mobile-menu a { display: block; padding: 10px 16px; color: #666; font-size: 14px; border-radius: 6px; }
.mobile-menu a:hover { background: #F5F5F5; }

.main-content { max-width: 1200px; margin: 0 auto; padding: 24px; min-height: calc(100vh - 120px); }

.main-footer { text-align: center; padding: 24px; color: #999; font-size: 13px; background: #fff; border-top: 1px solid #EEE; }
.main-footer .feedback { color: #FF6600; margin-top: 4px; cursor: pointer; }

@media (max-width: 768px) {
  .nav-center, .nav-right { display: none; }
  .mobile-menu-btn { display: block; }
  .mobile-menu { display: block; }
  .nav-inner { height: auto; padding: 12px; }
  .main-content { padding: 12px; }
}
</style>