<template>
  <div class="detail-page">
    <div class="detail-carousel">
      <img v-if="shop.image" :src="shop.image" class="detail-img" alt="店铺图片" />
      <div v-else class="detail-carousel-bg">📸 暂无图片</div>
    </div>

    <div class="detail-body">
      <div class="detail-main">
        <div class="shop-header">
          <h1 class="shop-name">{{ shop.name }}</h1>
          <div class="shop-meta">
            <span class="stars">⭐ {{ shop.score }}</span>
            <span>💰 人均 ¥{{ shop.price }}</span>
            <span>📍 {{ shop.canteen }} · {{ shop.address }}</span>
            <span>🕐 {{ shop.hours }}</span>
            <span>❤️ {{ shop.favCount }}人收藏</span>
            <span>💬 {{ shop.commentCount }}条评论</span>
          </div>
        </div>

        <div class="rec-dishes">
          <h4>🍽 推荐菜品</h4>
          <div class="dish-tags">
            <span v-for="(d, i) in dishList" :key="i" class="dish-tag">{{ d }}</span>
          </div>
        </div>

        <div class="shop-desc">
          <h4>📝 店铺简介</h4>
          <p>{{ shop.description || '暂无简介' }}</p>
        </div>

        <div class="comment-section">
          <h3>💬 学生评价 ({{ comments.length }})</h3>
          <div v-if="comments.length">
            <div v-for="c in comments" :key="c.id" class="comment-item">
              <div class="comment-avatar">👤</div>
              <div class="comment-body">
                <div class="comment-user">{{ c.username || '匿名' }}</div>
                <div class="comment-text">{{ c.content }}</div>
                <div class="comment-time">{{ formatTime(c.createTime) }}</div>
              </div>
            </div>
          </div>
          <div v-else class="empty-comments">
            <p>暂无评论，快来发表第一条评价吧~</p>
          </div>
          <div class="comment-input-row">
            <input v-model="newComment" placeholder="写下你的评价…" class="comment-input" @keyup.enter="submitComment" />
            <button class="comment-submit" @click="submitComment">发布</button>
          </div>
        </div>
      </div>

      <button class="float-fav" :class="{ faved: isFaved }" @click="handleFav">{{ isFaved ? '♥' : '♡' }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShopDetail, getComments, postComment, toggleFav, getFavorites } from '../api'

const route = useRoute()
const shop = ref({})
const comments = ref([])
const newComment = ref('')
const isFaved = ref(false)

const dishList = computed(() => (shop.value.dishes || '').split(',').filter(d => d.trim()))

onMounted(() => {
  const id = route.params.id
  loadShop(id)
  loadComments(id)
  loadFavorites()
})

async function loadShop(id) {
  try {
    const res = await getShopDetail(id)
    shop.value = res.data
  } catch (e) {
    console.error('Load shop failed:', e)
    shop.value = {
      id: 1, name: '一食堂 · 麻辣香锅', score: '4.8', price: 18,
      canteen: '一食堂', address: '二楼东侧', hours: '10:00 - 21:00',
      dishes: '麻辣香锅套餐,酸辣土豆丝,番茄牛腩锅,麻辣烫',
      description: '一食堂人气最高的窗口之一，香锅自选食材丰富，荤素搭配自由，秘制酱料是老板的独门配方。每到饭点必排长队，建议错峰前往。',
      favCount: 128, commentCount: 45
    }
  }
}

async function loadComments(id) {
  try {
    const res = await getComments(id)
    comments.value = res.data
  } catch (e) {
    console.error('Load comments failed:', e)
    comments.value = [
      { id: 1, username: '吃货小王', content: '这家麻辣香锅真的绝了！分量足，味道正宗，每次来都要排队，但值得等！', createTime: new Date() },
      { id: 2, username: '美食猎人', content: '人均不到20就能吃撑，性价比超高。强烈推荐牛腩锅，番茄汤底浓郁。', createTime: new Date() },
      { id: 3, username: '大胃王阿杰', content: '自选食材种类很多，酱料是灵魂！加辣版本更过瘾。', createTime: new Date() },
    ]
  }
}

async function loadFavorites() {
  try {
    const res = await getFavorites()
    const favIds = new Set(res.data.map(f => f.shop?.id))
    isFaved.value = favIds.has(shop.value.id)
  } catch (e) {
    console.error('Load favorites failed:', e)
  }
}

async function submitComment() {
  const user = localStorage.getItem('user')
  if (!user) {
    ElMessage.warning('请先登录')
    return
  }
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    const u = JSON.parse(user)
    const res = await postComment({
      shopId: shop.value.id,
      userId: u.id,
      username: u.nickname,
      content: newComment.value.trim()
    })
    if (res.data.success) {
      comments.value.unshift(res.data.comment)
      newComment.value = ''
      ElMessage.success('评论发布成功')
      shop.value.commentCount = (shop.value.commentCount || 0) + 1
    }
  } catch (e) {
    ElMessage.error('发布失败')
  }
}

async function handleFav() {
  const user = localStorage.getItem('user')
  if (!user) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await toggleFav(shop.value.id)
    isFaved.value = res.data.faved
    if (isFaved.value) {
      ElMessage.success('收藏成功')
      shop.value.favCount = (shop.value.favCount || 0) + 1
    } else {
      ElMessage.info('已取消收藏')
      shop.value.favCount = Math.max(0, (shop.value.favCount || 0) - 1)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

function formatTime(t) {
  if (!t) return '刚刚'
  const now = new Date()
  const time = new Date(t)
  const diff = now - time
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return time.toLocaleDateString()
}
</script>

<style scoped>
.detail-carousel { height: 280px; background: linear-gradient(135deg, #FFF3E0, #FFE0B2); border-radius: 16px; display: flex; align-items: center; justify-content: center; color: #BF6B04; font-size: 18px; margin-bottom: 20px; overflow: hidden; }
.detail-img { width: 100%; height: 100%; object-fit: cover; }
.detail-body { position: relative; }
.shop-header { margin-bottom: 20px; }
.shop-name { font-size: 26px; font-weight: 700; }
.shop-meta { display: flex; gap: 16px; margin-top: 10px; font-size: 14px; color: #888; flex-wrap: wrap; }
.shop-meta .stars { color: #FFB300; }
.rec-dishes { margin-bottom: 20px; }
.rec-dishes h4, .shop-desc h4 { font-size: 15px; font-weight: 600; margin-bottom: 8px; }
.dish-tags { display: flex; gap: 8px; flex-wrap: wrap; }
.dish-tag { padding: 6px 16px; background: #FFF0E6; color: #FF6600; border-radius: 14px; font-size: 13px; }
.shop-desc { margin-bottom: 20px; }
.shop-desc p { font-size: 14px; color: #666; line-height: 1.8; }

.comment-section { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.comment-section h3 { font-size: 16px; margin-bottom: 16px; }
.comment-item { display: flex; gap: 10px; padding: 10px 0; border-bottom: 1px solid #F5F5F5; }
.comment-item:last-child { border-bottom: none; }
.comment-avatar { width: 36px; height: 36px; border-radius: 50%; background: #E0E0E0; display: flex; align-items: center; justify-content: center; font-size: 14px; flex-shrink: 0; }
.comment-body { flex: 1; }
.comment-user { font-size: 13px; font-weight: 600; }
.comment-text { font-size: 13px; color: #666; margin-top: 3px; }
.comment-time { font-size: 11px; color: #BBB; margin-top: 3px; }
.empty-comments { text-align: center; padding: 24px; color: #BBB; font-size: 13px; }
.comment-input-row { display: flex; gap: 10px; margin-top: 14px; }
.comment-input { flex: 1; padding: 10px 16px; border: 1px solid #E0E0E0; border-radius: 20px; font-size: 13px; outline: none; }
.comment-submit { padding: 10px 22px; background: #FF6600; color: #fff; border: none; border-radius: 20px; cursor: pointer; font-size: 13px; }

.float-fav {
  position: fixed; right: 40px; bottom: 80px;
  width: 52px; height: 52px; border-radius: 50%;
  background: #fff; color: #FF4466; border: 2px solid #FF4466;
  font-size: 22px; cursor: pointer; box-shadow: 0 4px 16px rgba(255,68,102,0.2);
  transition: all 0.3s; z-index: 50;
}
.float-fav.faved { background: #FF4466; color: #fff; transform: scale(1.15); }
</style>