<template>
  <div class="category-page">
    <h2 class="page-title">美食分类</h2>
    <div class="cat-layout">
      <aside class="filter-panel">
        <h3>筛选条件</h3>

        <div class="filter-group">
          <label>💰 价格区间</label>
          <div class="chip-group">
            <span v-for="p in priceOptions" :key="p.label" :class="{ active: priceRange[0] === p.min && priceRange[1] === p.max }" @click="setPrice(p)">{{ p.label }}</span>
          </div>
        </div>

        <div class="filter-group">
          <label>🌶 辣度</label>
          <el-radio-group v-model="spicy" class="radio-stack" @change="loadShops">
            <el-radio value="">不限</el-radio>
            <el-radio value="不辣">不辣</el-radio>
            <el-radio value="微辣">微辣</el-radio>
            <el-radio value="中辣">中辣</el-radio>
            <el-radio value="辣">辣</el-radio>
          </el-radio-group>
        </div>

        <div class="filter-group">
          <label>📍 食堂选择</label>
          <div class="chip-group">
            <span v-for="c in ['一食堂','二食堂','校外小吃','奶茶','减脂餐']" :key="c" :class="{ active: canteen === c }" @click="canteen = canteen === c ? '' : c; loadShops()">{{ c }}</span>
          </div>
        </div>

        <el-button type="primary" size="small" style="width:100%;margin-top:10px;border-radius:20px;background:#FF6600;border-color:#FF6600" @click="resetFilter">重置筛选</el-button>
      </aside>

      <div class="content-area">
        <div class="result-header">
          <span>共找到 {{ shops.length }} 个美食</span>
        </div>
        <div class="card-grid">
          <div v-for="shop in shops" :key="shop.id" class="food-card" @click="$router.push(`/detail/${shop.id}`)">
            <div class="food-card-img" :class="`c${shop.id % 6 + 1}`">🍽</div>
            <div class="food-card-body">
              <div class="food-card-name">{{ shop.name }}</div>
              <div class="food-card-shop">{{ shop.canteen }} · {{ shop.address }}</div>
              <div class="food-card-meta">
                <span class="food-card-stars">⭐ {{ shop.score }}</span>
                <span class="food-card-price">¥{{ shop.price }}</span>
              </div>
            </div>
            <button class="fav-btn" :class="{ faved: shop.faved }" @click.stop="handleFav(shop)">{{ shop.faved ? '♥' : '♡' }}</button>
          </div>
        </div>
        <div v-if="!shops.length" class="empty-state">
          <p>😕 没有找到符合条件的美食</p>
          <span>试试其他筛选条件</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShops, toggleFav, getFavorites } from '../api'

const route = useRoute()
const priceRange = ref([0, 50])
const priceOptions = [
  { label: '全部', min: 0, max: 50 },
  { label: '¥10以下', min: 0, max: 10 },
  { label: '¥10~15', min: 10, max: 15 },
  { label: '¥15~20', min: 15, max: 20 },
  { label: '¥20以上', min: 20, max: 50 },
]

function setPrice(p) {
  if (priceRange.value[0] === p.min && priceRange.value[1] === p.max) return
  priceRange.value = [p.min, p.max]
  loadShops()
}
const spicy = ref('')
const canteen = ref('')
const shops = ref([])
const favIds = ref(new Set())

onMounted(() => {
  if (route.query.canteen) canteen.value = route.query.canteen
  loadShops()
  loadFavorites()
})

async function loadShops() {
  try {
    const params = {
      canteen: canteen.value || undefined,
      spicy: spicy.value || undefined,
      minPrice: priceRange.value[0] > 0 ? priceRange.value[0] : undefined,
      maxPrice: priceRange.value[1] < 50 ? priceRange.value[1] : undefined,
    }
    const res = await getShops(params)
    shops.value = res.data.map(s => ({ ...s, faved: favIds.value.has(s.id) }))
  } catch (e) {
    console.error('Load shops failed:', e)
    shops.value = [
      { id: 1, name: '麻辣香锅', canteen: '一食堂', address: '二楼东侧', score: '4.8', price: 18, spicy: '辣', faved: false },
      { id: 2, name: '铁板牛肉饭', canteen: '二食堂', address: '一楼', score: '4.7', price: 16, spicy: '不辣', faved: false },
      { id: 3, name: '酸菜鱼米线', canteen: '校外小吃', address: '小吃街中段', score: '4.9', price: 15, spicy: '微辣', faved: false },
      { id: 4, name: '杨枝甘露', canteen: '奶茶', address: '奶茶专区', score: '4.6', price: 12, spicy: '不辣', faved: false },
      { id: 5, name: '鸡胸肉沙拉', canteen: '减脂餐', address: '专窗', score: '4.5', price: 20, spicy: '不辣', faved: false },
      { id: 6, name: '日式拉面', canteen: '二食堂', address: '二楼', score: '4.8', price: 15, spicy: '微辣', faved: false },
      { id: 7, name: '炸串卷饼', canteen: '校外小吃', address: '路口', score: '4.7', price: 10, spicy: '中辣', faved: false },
      { id: 8, name: '芝士葡萄', canteen: '奶茶', address: '奶茶专区', score: '4.4', price: 14, spicy: '不辣', faved: false },
    ].map(s => ({ ...s, faved: favIds.value.has(s.id) }))
  }
}

async function loadFavorites() {
  try {
    const res = await getFavorites()
    favIds.value = new Set(res.data.map(f => f.shop?.id))
    shops.value.forEach(s => s.faved = favIds.value.has(s.id))
  } catch (e) {
    console.error('Load favorites failed:', e)
  }
}

async function handleFav(shop) {
  const user = localStorage.getItem('user')
  if (!user) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await toggleFav(shop.id)
    shop.faved = res.data.faved
    if (shop.faved) {
      ElMessage.success('收藏成功')
    } else {
      ElMessage.info('已取消收藏')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

function resetFilter() {
  priceRange.value = [0, 50]
  spicy.value = ''
  canteen.value = ''
  loadShops()
}
</script>

<style scoped>
.cat-layout { display: flex; gap: 24px; align-items: flex-start; }
.filter-panel {
  width: 240px; flex-shrink: 0;
  background: #fff; border-radius: 12px; padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.filter-panel h3 { font-size: 16px; font-weight: 700; margin-bottom: 18px; }
.filter-group { margin-bottom: 20px; }
.filter-group label { font-size: 13px; font-weight: 600; color: #666; display: block; margin-bottom: 8px; }
.range-labels { display: flex; justify-content: space-between; font-size: 11px; color: #999; margin-top: 4px; }
.radio-stack { display: flex; flex-direction: column; gap: 8px; }
.chip-group { display: flex; flex-wrap: wrap; gap: 6px; }
.chip-group span {
  padding: 5px 14px; border-radius: 14px; font-size: 12px;
  background: #F0F0F0; color: #888; cursor: pointer; transition: all 0.2s;
}
.chip-group span.active { background: #FFF0E6; color: #FF6600; border: 1px solid #FFCCAA; }
.content-area { flex: 1; }
.result-header { font-size: 13px; color: #999; margin-bottom: 14px; }
.empty-state { text-align: center; padding: 60px 0; color: #999; }
.empty-state p { font-size: 18px; margin-bottom: 8px; }

@media (max-width: 768px) {
  .cat-layout { flex-direction: column; }
  .filter-panel { width: 100%; }
}
</style>