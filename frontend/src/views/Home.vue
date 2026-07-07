<template>
  <div class="home">
    <div class="carousel">
      <div class="carousel-inner">
        <div class="carousel-item" :class="{ active: currentSlide === i }" v-for="(item, i) in carouselData" :key="i">
          <div class="carousel-bg" :style="item.bg">
            <span class="carousel-icon">{{ item.icon }}</span>
            <h2>{{ item.title }}</h2>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </div>
      <div class="carousel-dots">
        <span v-for="i in carouselData.length" :key="i" :class="{ active: currentSlide === i-1 }" @click="currentSlide = i-1"></span>
      </div>
    </div>

    <div class="cat-buttons">
      <button v-for="cat in categories" :key="cat.key" :class="{ active: activeCat === cat.key }" @click="activeCat = cat.key; goCategory(cat.key)">
        {{ cat.icon }} {{ cat.label }}
      </button>
    </div>

    <div class="home-two-col">
      <div class="panel">
        <h3 class="panel-title">🔥 本周人气美食</h3>
        <div class="rank-list">
          <div v-for="(shop, idx) in hotList" :key="shop.id" class="rank-item" @click="$router.push(`/detail/${shop.id}`)">
            <span class="rank-num" :class="`top${idx+1}`">{{ idx+1 }}</span>
            <div class="rank-info">
              <div class="rank-name">{{ shop.name }}</div>
              <div class="rank-shop">{{ shop.canteen }} · {{ shop.address }}</div>
            </div>
            <span class="rank-score">⭐ {{ shop.score }}</span>
          </div>
        </div>
      </div>
      <div class="panel">
        <h3 class="panel-title">📝 最新学生探店</h3>
        <div class="note-list">
          <div v-for="note in notes" :key="note.id" class="note-item" @click="$router.push(`/detail/${note.shopId}`)">
            <div class="note-thumb" :class="`c${note.id % 6 + 1}`">🍽</div>
            <div class="note-info">
              <div class="note-title">{{ note.title || '探店分享' }}</div>
              <div class="note-desc">{{ note.content?.substring(0, 40) }}...</div>
              <div class="note-author">@{{ note.username || '匿名' }} · {{ formatTime(note.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotShops, getNotes } from '../api'

const router = useRouter()
const currentSlide = ref(0)
const activeCat = ref('')
const hotList = ref([])
const notes = ref([])

const categories = [
  { key: '一食堂', icon: '🍚', label: '一食堂' },
  { key: '二食堂', icon: '🍱', label: '二食堂' },
  { key: '校外小吃', icon: '🌭', label: '校外小吃' },
  { key: '奶茶', icon: '🧋', label: '奶茶' },
  { key: '减脂餐', icon: '🥗', label: '减脂餐' },
  { key: '夜宵', icon: '🌙', label: '夜宵' },
]

const carouselData = [
  { icon: '🍲', title: '校园美食大赏', desc: '发现你身边不可错过的美味', bg: 'background: linear-gradient(135deg, #FF6B35, #FF8C42);' },
  { icon: '🎉', title: '食堂新品上线', desc: '一食堂二楼全面升级，快来尝鲜', bg: 'background: linear-gradient(135deg, #43A047, #66BB6A);' },
  { icon: '🏆', title: '本周人气王', desc: '麻辣香锅蝉联榜首，你吃过了吗？', bg: 'background: linear-gradient(135deg, #1E88E5, #42A5F5);' },
]

onMounted(() => {
  setInterval(() => { currentSlide.value = (currentSlide.value + 1) % carouselData.length }, 4000)
  loadData()
})

async function loadData() {
  try {
    const [hotRes, noteRes] = await Promise.all([getHotShops(), getNotes()])
    hotList.value = hotRes.data.slice(0, 5)
    notes.value = noteRes.data.slice(0, 3)
  } catch (e) {
    console.error('Load home data failed:', e)
    hotList.value = [
      { id: 1, name: '麻辣香锅', canteen: '一食堂', address: '二楼东侧', score: '4.8' },
      { id: 2, name: '铁板牛肉饭', canteen: '二食堂', address: '一楼', score: '4.7' },
      { id: 3, name: '酸菜鱼米线', canteen: '校外小吃', address: '小吃街', score: '4.9' },
      { id: 4, name: '杨枝甘露', canteen: '奶茶', address: '奶茶专区', score: '4.6' },
      { id: 5, name: '鸡胸肉沙拉', canteen: '减脂餐', address: '专窗', score: '4.5' },
    ]
    notes.value = [
      { id: 1, shopId: 1, title: '二食堂新开日式拉面，绝了！', content: '汤底浓郁，叉烧厚切，人均15块', username: '吃货小王', createTime: new Date() },
      { id: 2, shopId: 2, title: '校外小吃街宝藏：炸串卷饼', content: '老板秘制酱料，一口下去满满的幸福感', username: '美食猎人', createTime: new Date() },
      { id: 3, shopId: 3, title: '奶茶新品测评：芝士葡萄爆珠', content: '清爽不腻，爆珠口感超Q弹', username: '奶茶患者', createTime: new Date() },
    ]
  }
}

function goCategory(cat) {
  router.push({ path: '/category', query: { canteen: cat } })
}

function formatTime(t) {
  if (!t) return '刚刚'
  const now = new Date()
  const time = new Date(t)
  const diff = now - time
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${Math.floor(diff / 86400000)}天前`
}
</script>

<style scoped>
.carousel { position: relative; border-radius: 16px; overflow: hidden; margin-bottom: 24px; }
.carousel-inner { position: relative; height: 300px; }
.carousel-item { position: absolute; inset: 0; opacity: 0; transition: opacity 0.6s; }
.carousel-item.active { opacity: 1; z-index: 1; }
.carousel-bg { height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #fff; }
.carousel-icon { font-size: 64px; margin-bottom: 12px; }
.carousel-bg h2 { font-size: 28px; margin-bottom: 8px; }
.carousel-bg p { font-size: 16px; opacity: 0.9; }
.carousel-dots { position: absolute; bottom: 16px; left: 50%; transform: translateX(-50%); display: flex; gap: 8px; z-index: 2; }
.carousel-dots span { width: 32px; height: 6px; background: rgba(255,255,255,0.4); border-radius: 3px; cursor: pointer; transition: all 0.3s; }
.carousel-dots span.active { background: #fff; width: 48px; }

.cat-buttons { display: flex; gap: 12px; justify-content: center; margin-bottom: 28px; flex-wrap: wrap; }
.cat-buttons button { padding: 10px 24px; border-radius: 22px; border: 1px solid #FFCCAA; background: #FFF5F0; color: #FF6600; font-size: 14px; cursor: pointer; transition: all 0.2s; }
.cat-buttons button:hover, .cat-buttons button.active { background: #FF6600; color: #fff; border-color: #FF6600; }

.home-two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.panel { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.panel-title { font-size: 18px; font-weight: 700; margin-bottom: 16px; }

.rank-list { display: flex; flex-direction: column; gap: 10px; }
.rank-item { display: flex; align-items: center; gap: 12px; padding: 10px; border-radius: 8px; background: #FAFAFA; cursor: pointer; transition: background 0.2s; }
.rank-item:hover { background: #FFF5F0; }
.rank-num { width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 700; color: #999; background: #E0E0E0; }
.rank-num.top1 { background: #FF6600; color: #fff; }
.rank-num.top2 { background: #FF9800; color: #fff; }
.rank-num.top3 { background: #FFC107; color: #fff; }
.rank-info { flex: 1; }
.rank-name { font-size: 14px; font-weight: 600; }
.rank-shop { font-size: 12px; color: #999; margin-top: 2px; }
.rank-score { color: #FF6600; font-weight: 600; font-size: 13px; }

.note-list { display: flex; flex-direction: column; gap: 10px; }
.note-item { display: flex; gap: 12px; padding: 10px; border-radius: 8px; background: #FAFAFA; cursor: pointer; transition: background 0.2s; }
.note-item:hover { background: #FFF5F0; }
.note-thumb { width: 72px; height: 56px; border-radius: 6px; display: flex; align-items: center; justify-content: center; font-size: 28px; flex-shrink: 0; }
.note-thumb.c1 { background: #FFE8D6; } .note-thumb.c2 { background: #E8F5E9; } .note-thumb.c3 { background: #E3F2FD; } .note-thumb.c4 { background: #FFF9C4; } .note-thumb.c5 { background: #F3E5F5; } .note-thumb.c6 { background: #E0F7FA; }
.note-info { flex: 1; min-width: 0; }
.note-title { font-size: 14px; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.note-desc { font-size: 12px; color: #888; margin-top: 3px; }
.note-author { font-size: 11px; color: #BBB; margin-top: 4px; }

@media (max-width: 768px) {
  .home-two-col { grid-template-columns: 1fr; }
  .carousel-inner { height: 200px; }
  .carousel-bg h2 { font-size: 20px; }
}
</style>