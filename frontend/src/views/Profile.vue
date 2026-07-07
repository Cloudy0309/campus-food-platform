<template>
  <div class="profile-page" v-if="user">
    <div class="profile-header">
      <div class="avatar">👤</div>
      <div>
        <div class="nickname">{{ user.nickname }}</div>
        <div class="uid">用户名: {{ user.username }}</div>
      </div>
    </div>

    <div class="menu-grid">
      <div v-for="m in menus" :key="m.label" class="menu-item" :class="{ active: activeMenu === m.key }" @click="activeMenu = m.key">
        <div class="menu-icon">{{ m.icon }}</div>
        <div class="menu-label">{{ m.label }}</div>
      </div>
    </div>

    <div class="content-panel">
      <h3>{{ activeMenuLabel }}</h3>
      
      <div v-if="activeMenu === 'fav'" class="profile-list">
        <div v-if="favorites.length">
          <div v-for="f in favorites" :key="f.favoriteId" class="profile-card" @click="$router.push(`/detail/${f.shop.id}`)">
            <div class="card-img" :class="`c${f.shop.id % 6 + 1}`">🍽</div>
            <div class="card-info">
              <div class="card-name">{{ f.shop.name }}</div>
              <div class="card-meta">{{ f.shop.canteen }} · {{ f.shop.address }}</div>
              <div class="card-bottom">
                <span>⭐ {{ f.shop.score }}</span>
                <span>¥{{ f.shop.price }}</span>
              </div>
            </div>
            <button class="card-fav" @click.stop="handleRemoveFav(f)">✕</button>
          </div>
        </div>
        <div v-else class="empty-list">
          <p>暂无收藏</p>
          <span>去发现美食并收藏吧~</span>
        </div>
      </div>

      <div v-else-if="activeMenu === 'notes'" class="profile-list">
        <div v-if="notes.length">
          <div v-for="n in notes" :key="n.id" class="profile-card">
            <div class="card-img" :class="`c${n.id % 6 + 1}`">📝</div>
            <div class="card-info">
              <div class="card-name">{{ n.title || '探店分享' }}</div>
              <div class="card-meta">{{ formatTime(n.createTime) }}</div>
              <div class="card-bottom">
                <span>⭐ {{ n.stars }}.0</span>
                <span>{{ n.tags || '无标签' }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-list">
          <p>暂无探店笔记</p>
          <span><a href="/publish">去发布第一篇探店笔记</a></span>
        </div>
      </div>

      <div v-else-if="activeMenu === 'comments'" class="profile-list">
        <div v-if="comments.length">
          <div v-for="c in comments" :key="c.id" class="profile-card">
            <div class="card-img c1">💬</div>
            <div class="card-info">
              <div class="card-name">{{ c.content }}</div>
              <div class="card-meta">{{ formatTime(c.createTime) }}</div>
            </div>
          </div>
        </div>
        <div v-else class="empty-list">
          <p>暂无评论</p>
          <span>去美食详情页发表评论吧~</span>
        </div>
      </div>

      <div v-else-if="activeMenu === 'edit'" class="edit-form">
        <el-form label-width="80px">
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input :value="user.username" disabled />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="background:#FF6600;border-color:#FF6600;border-radius:20px" @click="saveProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <el-button type="danger" style="margin-top:20px;border-radius:20px" @click="logout">退出登录</el-button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFavorites, getNotes, toggleFav } from '../api'

const router = useRouter()
const user = ref(null)
const activeMenu = ref('fav')
const favorites = ref([])
const notes = ref([])
const comments = ref([])

const editForm = ref({ nickname: '' })

const menus = [
  { key: 'fav', icon: '♥', label: '我的收藏' },
  { key: 'notes', icon: '📝', label: '我的笔记' },
  { key: 'comments', icon: '💬', label: '我的评论' },
  { key: 'edit', icon: '⚙', label: '修改资料' },
]

const activeMenuLabel = computed(() => menus.find(m => m.key === activeMenu.value)?.label || '')

onMounted(() => {
  const u = localStorage.getItem('user')
  if (!u) { router.push('/login'); return }
  user.value = JSON.parse(u)
  editForm.value.nickname = user.value.nickname
  loadData()
})

watch(activeMenu, (newMenu) => {
  if (newMenu === 'fav') loadFavorites()
  if (newMenu === 'notes') loadNotes()
})

async function loadData() {
  await Promise.all([loadFavorites(), loadNotes()])
}

async function loadFavorites() {
  try {
    const res = await getFavorites()
    favorites.value = res.data
  } catch (e) {
    console.error('Load favorites failed:', e)
    favorites.value = []
  }
}

async function loadNotes() {
  try {
    const res = await getNotes({ userId: user.value.id })
    notes.value = res.data
  } catch (e) {
    console.error('Load notes failed:', e)
    notes.value = []
  }
}

async function handleRemoveFav(f) {
  try {
    await toggleFav(f.shop.id)
    favorites.value = favorites.value.filter(item => item.favoriteId !== f.favoriteId)
    ElMessage.success('已取消收藏')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

function saveProfile() {
  if (!editForm.value.nickname.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }
  user.value.nickname = editForm.value.nickname
  localStorage.setItem('user', JSON.stringify(user.value))
  ElMessage.success('修改成功')
}

function logout() {
  localStorage.removeItem('user')
  router.push('/login')
}

function formatTime(t) {
  if (!t) return ''
  const now = new Date()
  const time = new Date(t)
  const diff = now - time
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return time.toLocaleDateString()
}
</script>

<style scoped>
.profile-header { display: flex; align-items: center; gap: 16px; padding: 24px; background: #fff; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); margin-bottom: 20px; }
.avatar { width: 64px; height: 64px; border-radius: 50%; background: #FF6600; display: flex; align-items: center; justify-content: center; font-size: 32px; color: #fff; }
.nickname { font-size: 20px; font-weight: 700; }
.uid { font-size: 12px; color: #AAA; margin-top: 2px; }

.menu-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 20px; }
.menu-item { text-align: center; padding: 18px 8px; border-radius: 10px; background: #fff; cursor: pointer; box-shadow: 0 2px 8px rgba(0,0,0,0.04); transition: all 0.2s; }
.menu-item:hover, .menu-item.active { background: #FFF5F0; border: 1px solid #FFCCAA; }
.menu-icon { font-size: 24px; margin-bottom: 6px; }
.menu-label { font-size: 13px; color: #555; }

.content-panel { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.content-panel h3 { font-size: 16px; margin-bottom: 14px; }

.profile-list { display: flex; flex-direction: column; gap: 12px; }
.profile-card { display: flex; gap: 12px; padding: 14px; background: #FAFAFA; border-radius: 10px; cursor: pointer; position: relative; }
.profile-card:hover { background: #F0F0F0; }
.card-img { width: 64px; height: 64px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px; flex-shrink: 0; }
.card-img.c1 { background: #FFE8D6; } .card-img.c2 { background: #E8F5E9; } .card-img.c3 { background: #E3F2FD; } .card-img.c4 { background: #FFF9C4; } .card-img.c5 { background: #F3E5F5; } .card-img.c6 { background: #E0F7FA; }
.card-info { flex: 1; min-width: 0; }
.card-name { font-size: 14px; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.card-meta { font-size: 12px; color: #999; margin-top: 4px; }
.card-bottom { font-size: 12px; color: #666; margin-top: 6px; display: flex; gap: 12px; }
.card-fav { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); width: 28px; height: 28px; border-radius: 50%; background: #fff; border: 1px solid #DDD; font-size: 12px; color: #999; cursor: pointer; }
.card-fav:hover { background: #FFEBEB; color: #FF4466; border-color: #FF4466; }

.empty-list { text-align: center; padding: 48px 0; color: #BBB; }
.empty-list p { font-size: 16px; margin-bottom: 8px; }
.empty-list a { color: #FF6600; text-decoration: none; }

.edit-form { max-width: 400px; }
</style>